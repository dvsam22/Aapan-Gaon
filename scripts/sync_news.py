#!/usr/bin/env python3
"""
Sync Local News from GNews API to Firebase Firestore for Apna Gaon.

Features:
1. Fetches top 10-15 local news articles for Maharajganj in Hindi from GNews.
2. Deduplicates and saves new articles to Firestore `news` collection.
3. Automatically deletes articles older than 7 days (7-day retention policy).
4. Sends an FCM push notification to topic 'news' when a new top article is found.
5. Runs via GitHub Actions cron 10 times a day (consuming only 10 requests/day of the 100/day free quota).
"""

import os
import sys
import json
import hashlib
import argparse
from datetime import datetime, timezone, timedelta
import urllib.request
import urllib.error
import ssl

# Optional Firebase Admin SDK (graceful fallback for dry-run or testing)
try:
    import firebase_admin
    from firebase_admin import credentials, firestore, messaging
    FIREBASE_AVAILABLE = True
except ImportError:
    FIREBASE_AVAILABLE = False


GNEWS_API_URL = "https://gnews.io/api/v4/search"
DEFAULT_QUERY = "Maharajganj"
DEFAULT_LANG = "hi"
DEFAULT_COUNTRY = "in"
DEFAULT_MAX = 15
DEFAULT_API_KEY = "7f3a60695976d579cb056394df4743fb"
RETENTION_DAYS = 7


def parse_iso_to_epoch_ms(iso_str: str) -> int:
    """Converts ISO 8601 string (e.g. 2026-10-21T21:12:24Z) to epoch milliseconds."""
    if not iso_str:
        return int(datetime.now(timezone.utc).timestamp() * 1000)
    try:
        # Handle 'Z' suffix
        cleaned = iso_str.replace("Z", "+00:00")
        dt = datetime.fromisoformat(cleaned)
        return int(dt.timestamp() * 1000)
    except Exception:
        return int(datetime.now(timezone.utc).timestamp() * 1000)


def fetch_news(api_key: str, query: str = DEFAULT_QUERY, max_results: int = DEFAULT_MAX):
    """Fetches articles from GNews API."""
    encoded_query = urllib.parse.quote(query)
    url = f"{GNEWS_API_URL}?q={encoded_query}&lang={DEFAULT_LANG}&country={DEFAULT_COUNTRY}&max={max_results}&apikey={api_key}"
    print(f"[INFO] Requesting GNews API for query: '{query}'...")

    req = urllib.request.Request(
        url,
        headers={"User-Agent": "ApnaGaon-NewsSync/1.0"}
    )

    # SSL context with fallback for local macOS environments
    ctx = ssl.create_default_context()
    try:
        with urllib.request.urlopen(req, timeout=30, context=ctx) as response:
            payload = json.loads(response.read().decode("utf-8"))
            articles = payload.get("articles", [])
            print(f"[INFO] Fetched {len(articles)} articles from GNews API.")
            return articles
    except urllib.error.URLError as e:
        if "CERTIFICATE_VERIFY_FAILED" in str(e):
            print("[WARNING] SSL verify failed, retrying with unverified context...")
            unverified_ctx = ssl._create_unverified_context()
            with urllib.request.urlopen(req, timeout=30, context=unverified_ctx) as response:
                payload = json.loads(response.read().decode("utf-8"))
                articles = payload.get("articles", [])
                print(f"[INFO] Fetched {len(articles)} articles from GNews API.")
                return articles
        raise
    except urllib.error.HTTPError as e:
        error_body = e.read().decode("utf-8", errors="ignore")
        print(f"[ERROR] GNews HTTP Error {e.code}: {error_body}", file=sys.stderr)
        return []
    except Exception as e:
        print(f"[ERROR] Failed to fetch news from GNews: {e}", file=sys.stderr)
        return []


def init_firebase():
    """Initializes Firebase Admin SDK using service account or environment credentials."""
    if not FIREBASE_AVAILABLE:
        print("[WARNING] firebase-admin package is not installed.")
        return None

    if firebase_admin._apps:
        return firestore.client()

    sa_env = os.environ.get("FIREBASE_SERVICE_ACCOUNT")
    cred = None

    if sa_env:
        try:
            # Check if it's a file path
            if os.path.exists(sa_env):
                cred = credentials.Certificate(sa_env)
                print(f"[INFO] Initialized Firebase from service account file: {sa_env}")
            else:
                # Parse as JSON string
                sa_json = json.loads(sa_env)
                cred = credentials.Certificate(sa_json)
                print("[INFO] Initialized Firebase from service account JSON string.")
        except Exception as e:
            print(f"[ERROR] Failed to parse FIREBASE_SERVICE_ACCOUNT: {e}", file=sys.stderr)
            return None
    elif os.path.exists("service-account.json"):
        cred = credentials.Certificate("service-account.json")
        print("[INFO] Initialized Firebase from service-account.json.")
    else:
        try:
            # Try application default credentials
            cred = credentials.ApplicationDefault()
            print("[INFO] Using Application Default Credentials.")
        except Exception as e:
            print(f"[ERROR] No Firebase credentials found: {e}", file=sys.stderr)
            return None

    firebase_admin.initialize_app(cred)
    return firestore.client()


def sync_articles_to_firestore(db, articles, dry_run=False):
    """
    Saves articles to Firestore `news` collection and cleans up items older than 7 days.
    Returns: (newly_added_articles, deleted_count)
    """
    if db is None:
        print("[WARNING] Firestore client is not available. Skipping sync.")
        return [], 0

    news_collection = db.collection("news")
    new_articles = []

    print("[INFO] Checking and saving articles into Firestore 'news' collection...")
    for item in articles:
        url = item.get("url", "").strip()
        raw_id = item.get("id")
        if not raw_id:
            raw_id = hashlib.md5(url.encode("utf-8")).hexdigest()
        article_id = str(raw_id)

        title = item.get("title", "").strip()
        description = item.get("description", "").strip()
        content = item.get("content", "").strip()
        image = item.get("image", "").strip()
        source_name = item.get("source", {}).get("name", "Local News").strip()
        date_ms = parse_iso_to_epoch_ms(item.get("publishedAt", ""))

        doc_ref = news_collection.document(article_id)

        # Check if already exists
        doc_snap = doc_ref.get()
        if not doc_snap.exists:
            news_data = {
                "id": article_id,
                "title": title,
                "description": description or content,
                "image": image,
                "source": source_name,
                "url": url,
                "date": date_ms,
                "createdAt": firestore.SERVER_TIMESTAMP
            }
            if not dry_run:
                doc_ref.set(news_data)
            new_articles.append(news_data)
            print(f"  [+] Added new article: {title[:50]}... ({source_name})")
        else:
            print(f"  [=] Already exists: {title[:50]}...")

    # 7-day retention cleanup
    cutoff_dt = datetime.now(timezone.utc) - timedelta(days=RETENTION_DAYS)
    cutoff_ms = int(cutoff_dt.timestamp() * 1000)
    print(f"[INFO] Cleaning up news older than {RETENTION_DAYS} days (cutoff: {cutoff_dt.isoformat()})...")

    deleted_count = 0
    try:
        old_docs = news_collection.where("date", "<", cutoff_ms).stream()
        for doc in old_docs:
            if not dry_run:
                doc.reference.delete()
            deleted_count += 1
            print(f"  [-] Deleted expired news doc: {doc.id}")
    except Exception as e:
        print(f"[WARNING] Could not execute deletion query: {e}")

    return new_articles, deleted_count


def clean_news_text(text: str) -> str:
    if not text:
        return ""
    import re
    # Remove 'Maharajganj News:' or similar redundant publisher prefixes
    text = re.sub(r'(?i)maharajganj\s*news\s*[:\-\|]\s*', '', text)
    text = re.sub(r'(?i)maharajganj\s*[:\-\|]\s*', '', text)
    text = re.sub(r'महराजगंज\s*(न्यूज़|समाचार)?\s*[:\-\|]\s*', '', text)
    text = re.sub(r'(?i)read\s+(latest\s+)?maharajganj\s+news\s+(today\s+)?in\s+hindi\s*[-—–]?\s*', '', text)
    text = re.sub(r'(?i)read\s+latest\s+news\s+in\s+hindi\s*[-—–]?\s*', '', text)
    text = re.sub(r'^(महराजगंज|महाराजगंज)\s*[।|:\-—–]\s*', '', text)
    return text.strip()


def send_fcm_notification(top_article, dry_run=False):
    """Sends a push notification to FCM topic 'news' for a top new article."""
    if not FIREBASE_AVAILABLE or dry_run:
        print(f"[DRY-RUN/SKIP] Would send FCM notification for: {top_article['title']}")
        return

    try:
        raw_title = top_article.get("title", "ताज़ा खबर")
        clean_title = clean_news_text(raw_title)
        if len(clean_title) > 60:
            clean_title = clean_title[:57] + "..."

        raw_body = top_article.get("description", "खबर विस्तार से पढ़ने के लिए टैप करें")
        clean_body = clean_news_text(raw_body)
        if len(clean_body) > 120:
            clean_body = clean_body[:117] + "..."

        image_url = top_article.get("image") or None

        msg = messaging.Message(
            notification=messaging.Notification(
                title=f"ताज़ा खबर: {clean_title}",
                body=clean_body,
                image=image_url
            ),
            data={
                "id": str(top_article.get("id")),
                "type": "news"
            },
            topic="news"
        )
        response = messaging.send(msg)
        print(f"[INFO] FCM notification successfully sent to topic 'news': {response}")
    except Exception as e:
        print(f"[ERROR] Failed to send FCM notification: {e}", file=sys.stderr)


def main():
    parser = argparse.ArgumentParser(description="Sync GNews to Firebase Firestore for Apna Gaon.")
    parser.add_argument("--api-key", default=os.environ.get("GNEWS_API_KEY", DEFAULT_API_KEY), help="GNews API Key")
    parser.add_argument("--query", default=os.environ.get("NEWS_QUERY", DEFAULT_QUERY), help="Search query")
    parser.add_argument("--max", type=int, default=DEFAULT_MAX, help="Max articles to fetch")
    parser.add_argument("--dry-run", action="store_true", help="Run without writing to Firebase or sending notifications")
    args = parser.parse_args()

    print("==========================================")
    print("   Apna Gaon - Local News Sync Service   ")
    print("==========================================")
    print(f"Timestamp: {datetime.now(timezone.utc).isoformat()}")
    print(f"Query: {args.query} | Max: {args.max} | Dry Run: {args.dry_run}")

    articles = fetch_news(args.api_key, query=args.query, max_results=args.max)
    if not articles:
        print("[INFO] No articles returned. Exiting.")
        return

    if args.dry_run:
        print(f"[INFO] Dry-run completed. Fetched {len(articles)} articles.")
        for idx, art in enumerate(articles, 1):
            source = art.get("source", {}).get("name", "Unknown")
            print(f"{idx}. [{source}] {art.get('title')} ({art.get('publishedAt')})")
        return

    db = init_firebase()
    if not db:
        print("[ERROR] Firebase could not be initialized. Please configure credentials.")
        sys.exit(1)

    new_articles, deleted_count = sync_articles_to_firestore(db, articles, dry_run=args.dry_run)
    print(f"[SUMMARY] Total fetched: {len(articles)} | New added: {len(new_articles)} | Expired deleted: {deleted_count}")

    # Send push notification for the newest article if new articles were added
    if new_articles:
        top_new = new_articles[0]
        send_fcm_notification(top_new, dry_run=args.dry_run)
    else:
        print("[INFO] No new articles to notify.")


if __name__ == "__main__":
    main()
