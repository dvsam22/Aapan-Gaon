#!/usr/bin/env python3
"""
Sync Daily Mandi & Market Prices from data.gov.in (Agmarknet) to Firebase Firestore for Apna Gaon.

Target Districts:
- Sant Kabir Nagar (Khalilabad APMC)
- Siddharth Nagar (Naugarh APMC)

Features:
1. Fetches official daily wholesale mandi prices from Ministry of Agriculture & Farmers Welfare.
2. Translates commodities into Hindi & English bilingual names.
3. Automatically categorizes into:
   - 'market': Vegetables & Fruits (ताज़ा बाजार भाव)
   - 'prices': Crops, Grains, Cereals & Bulk Produce (मंडी भाव)
4. Calculates trends (up, down, stable) from min/max/modal prices.
5. Saves into Firestore `mandi` collection.
6. Cleans up expired mandi prices older than 7 days.
7. Runs via GitHub Actions 4 times a day (07:45 AM, 11:45 AM, 03:45 PM, 07:45 PM IST).
"""

import os
import sys
import json
import hashlib
import argparse
from datetime import datetime, timezone, timedelta
import urllib.request
import urllib.parse
import urllib.error
import ssl

try:
    import firebase_admin
    from firebase_admin import credentials, firestore, messaging
    FIREBASE_AVAILABLE = True
except ImportError:
    FIREBASE_AVAILABLE = False


DATA_GOV_API_URL = "https://api.data.gov.in/resource/9ef84268-d588-465a-a308-a864a43d0070"
DEFAULT_API_KEY = "579b464db66ec23bdd000001d53e0115502e4a9f65f1012b123ada90"
TARGET_DISTRICTS = ["Sant Kabir Nagar", "Siddharth Nagar"]
RETENTION_DAYS = 7

# Commodity Hindi Translation Mapping
COMMODITY_TRANSLATIONS = {
    "potato": {"hi": "आलू (Potato)", "en": "Potato"},
    "onion": {"hi": "प्याज (Onion)", "en": "Onion"},
    "tomato": {"hi": "टमाटर (Tomato)", "en": "Tomato"},
    "brinjal": {"hi": "बैंगन (Brinjal)", "en": "Brinjal"},
    "bottle gourd": {"hi": "लौकी (Bottle Gourd)", "en": "Bottle Gourd"},
    "bitter gourd": {"hi": "करेला (Bitter Gourd)", "en": "Bitter Gourd"},
    "pumpkin": {"hi": "कद्दू / कोहड़ा (Pumpkin)", "en": "Pumpkin"},
    "cauliflower": {"hi": "फूलगोभी (Cauliflower)", "en": "Cauliflower"},
    "cabbage": {"hi": "पत्तागोभी (Cabbage)", "en": "Cabbage"},
    "bhindi(ladies finger)": {"hi": "भिंडी (Bhindi)", "en": "Lady Finger"},
    "pointed gourd(parval)": {"hi": "परवल (Parval)", "en": "Pointed Gourd (Parval)"},
    "apple": {"hi": "सेब (Apple)", "en": "Apple"},
    "banana": {"hi": "केला (Banana)", "en": "Banana"},
    "banana - green": {"hi": "कच्चा केला (Green Banana)", "en": "Green Banana"},
    "pomegranate": {"hi": "अनार (Pomegranate)", "en": "Pomegranate"},
    "mousambi(sweet lime)": {"hi": "मौसमी (Sweet Lime)", "en": "Sweet Lime"},
    "capsicum": {"hi": "शिमला मिर्च (Capsicum)", "en": "Capsicum"},
    "carrot": {"hi": "गाजर (Carrot)", "en": "Carrot"},
    "cucumbar(kheera)": {"hi": "खीरा (Cucumber)", "en": "Cucumber"},
    "green chilli": {"hi": "हरी मिर्च (Green Chilli)", "en": "Green Chilli"},
    "garlic": {"hi": "लहसुन (Garlic)", "en": "Garlic"},
    "ginger(green)": {"hi": "अदरक (Ginger)", "en": "Ginger"},
    "lemon": {"hi": "नींबू (Lemon)", "en": "Lemon"},
    "papaya": {"hi": "पपीता (Papaya)", "en": "Papaya"},
    "radish": {"hi": "मूली (Radish)", "en": "Radish"},
    "raddish": {"hi": "मूली (Radish)", "en": "Radish"},
    "sponge gourd": {"hi": "नेनुआ / तोरई (Sponge Gourd)", "en": "Sponge Gourd"},
    "ridgeguard(tori)": {"hi": "तोरई (Ridge Gourd)", "en": "Ridge Gourd"},
    "sweet potato": {"hi": "शकरकंद (Sweet Potato)", "en": "Sweet Potato"},
    "wheat": {"hi": "गेहूं (Wheat)", "en": "Wheat"},
    "paddy(common)": {"hi": "धान साधारण (Paddy)", "en": "Paddy (Common)"},
    "paddy(basmati)": {"hi": "धान बासमती (Paddy)", "en": "Paddy (Basmati)"},
    "paddy(dhan)(common)": {"hi": "धान (Paddy/Dhan)", "en": "Paddy (Common)"},
    "bajra(pearl millet/cumbu)": {"hi": "बाजरा (Bajra)", "en": "Bajra"},
    "maize": {"hi": "मक्का (Maize)", "en": "Maize"},
    "mustard": {"hi": "सरसों (Mustard)", "en": "Mustard"},
    "gram raw(chholia)": {"hi": "हरा चना (Chana)", "en": "Gram"},
    "bengal gram(gram)(whole)": {"hi": "चना (Bengal Gram)", "en": "Bengal Gram"},
    "arhar (tur/red gram)(whole)": {"hi": "अरहर / तुअर (Arhar)", "en": "Arhar/Tur"},
    "masur(whole)": {"hi": "मसूर (Masur)", "en": "Masur"},
    "moong(whole)": {"hi": "मूंग (Moong)", "en": "Moong"},
    "urad(whole)": {"hi": "उड़द (Urad)", "en": "Urad"},
    "peas(dry)": {"hi": "सूखा मटर (Dry Peas)", "en": "Dry Peas"},
    "peas wet": {"hi": "हरी मटर (Green Peas)", "en": "Green Peas"},
    "sugarcane": {"hi": "गन्ना (Sugarcane)", "en": "Sugarcane"},
}

CROP_KEYWORDS = [
    "wheat", "paddy", "dhan", "rice", "maize", "mustard", "bajra", 
    "gram", "arhar", "tur", "masur", "moong", "urad", "barley", 
    "jowar", "sugarcane", "cotton", "soyabean", "sesamum", "linseed"
]

# Bulk produce staples that are also relevant in Mandi Bhav (Crop Prices)
BULK_STAPLE_KEYWORDS = ["potato", "pumpkin", "bottle gourd", "cauliflower", "onion"]


def get_localized_commodity(commodity_raw: str):
    clean = commodity_raw.strip().lower()
    if clean in COMMODITY_TRANSLATIONS:
        return COMMODITY_TRANSLATIONS[clean]
    # Fallback to Title Cased English
    title = commodity_raw.strip()
    return {"hi": title, "en": title}


def parse_arrival_date(arrival_date_str: str) -> int:
    """Parses 'DD/MM/YYYY' into epoch milliseconds."""
    try:
        cleaned = arrival_date_str.strip().replace("\\", "")
        dt = datetime.strptime(cleaned, "%d/%m/%Y").replace(tzinfo=timezone.utc)
        return int(dt.timestamp() * 1000)
    except Exception:
        return int(datetime.now(timezone.utc).timestamp() * 1000)


def calculate_trend(min_p, max_p, modal_p) -> str:
    """Calculates trend based on where modal price stands between min and max."""
    try:
        min_p = float(min_p)
        max_p = float(max_p)
        modal_p = float(modal_p)
        if max_p > min_p:
            mid = (min_p + max_p) / 2.0
            if modal_p > mid:
                return "up"
            elif modal_p < mid:
                return "down"
    except Exception:
        pass
    return "stable"


DEFAULT_SCRAPINGANT_KEY = "2cb696ad7ca040be83bdec1966d7ed5e"


def _fetch_via_proxy_gateway(target_url: str):
    """Fallback to Indian proxy gateway when direct cloud connection is blocked by NIC firewall."""
    raw_key = os.getenv("SCRAPINGANT_API_KEY", "").strip()
    scrapingant_key = raw_key if raw_key and raw_key.lower() not in ("none", "null", "false") else DEFAULT_SCRAPINGANT_KEY
    scraperapi_key = os.getenv("SCRAPERAPI_KEY", "").strip()

    # 1. Try ScrapingAnt with Indian residential IP
    if scrapingant_key:
        print(f"[INFO] Routing via ScrapingAnt gateway (India proxy)...")
        proxy_url = f"https://api.scrapingant.com/v2/general?url={urllib.parse.quote(target_url)}&x-api-key={scrapingant_key}&proxy_country=IN&browser=false"
        try:
            req = urllib.request.Request(proxy_url, headers={"User-Agent": "Mozilla/5.0"})
            with urllib.request.urlopen(req, timeout=35) as r:
                return json.loads(r.read().decode("utf-8"))
        except Exception as e:
            print(f"[WARNING] ScrapingAnt gateway error: {e}", file=sys.stderr)

    # 2. Try ScraperAPI with Indian IP
    if scraperapi_key and scraperapi_key.lower() not in ("none", "null", "false"):
        print(f"[INFO] Routing via ScraperAPI gateway (India proxy)...")
        proxy_url = f"https://api.scraperapi.com?api_key={scraperapi_key}&url={urllib.parse.quote(target_url)}&country_code=in"
        try:
            req = urllib.request.Request(proxy_url, headers={"User-Agent": "Mozilla/5.0"})
            with urllib.request.urlopen(req, timeout=35) as r:
                return json.loads(r.read().decode("utf-8"))
        except Exception as e:
            print(f"[WARNING] ScraperAPI gateway error: {e}", file=sys.stderr)

    return None


def _query_data_gov_api(api_key: str, district: str):
    encoded_district = urllib.parse.quote(district)
    url = f"{DATA_GOV_API_URL}?api-key={api_key}&format=json&limit=100&filters%5Bstate.keyword%5D=Uttar%20Pradesh&filters%5Bdistrict%5D={encoded_district}"
    print(f"[INFO] Fetching Mandi data for district: '{district}'...")

    req = urllib.request.Request(
        url,
        headers={"User-Agent": "Mozilla/5.0 (ApnaGaon-MandiSync/1.0)"}
    )

    ctx = ssl.create_default_context()
    try:
        with urllib.request.urlopen(req, timeout=30, context=ctx) as r:
            payload = json.loads(r.read().decode("utf-8"))
            if "error" in payload:
                print(f"[ERROR] API returned error for {district}: {payload.get('error')}", file=sys.stderr)
                return []
            records = payload.get("records", [])
            print(f"[INFO] Fetched {len(records)} records for {district}.")
            return records
    except urllib.error.URLError as e:
        print(f"[WARNING] Direct connection failed ({e}). Checking proxy gateway fallback...", file=sys.stderr)
        gateway_data = _fetch_via_proxy_gateway(url)
        if gateway_data and "records" in gateway_data:
            recs = gateway_data.get("records", [])
            print(f"[INFO] Fetched {len(recs)} records for {district} via Indian proxy gateway.")
            return recs

        if "CERTIFICATE_VERIFY_FAILED" in str(e):
            unverified_ctx = ssl._create_unverified_context()
            try:
                with urllib.request.urlopen(req, timeout=30, context=unverified_ctx) as r:
                    payload = json.loads(r.read().decode("utf-8"))
                    if "error" in payload:
                        print(f"[ERROR] API returned error for {district}: {payload.get('error')}", file=sys.stderr)
                        return []
                    records = payload.get("records", [])
                    print(f"[INFO] Fetched {len(records)} records for {district} (unverified SSL).")
                    return records
            except Exception as e2:
                print(f"[ERROR] Failed to fetch data for {district} (unverified SSL): {e2}", file=sys.stderr)
                return []
        print(f"[ERROR] Failed to fetch data for {district}: {e}", file=sys.stderr)
        return []
    except Exception as e:
        gateway_data = _fetch_via_proxy_gateway(url)
        if gateway_data and "records" in gateway_data:
            recs = gateway_data.get("records", [])
            print(f"[INFO] Fetched {len(recs)} records for {district} via Indian proxy gateway.")
            return recs
        print(f"[ERROR] Failed to fetch data for {district}: {e}", file=sys.stderr)
        return []


def fetch_district_mandi(api_key: str, district: str):
    clean_key = api_key.strip() if api_key and api_key.strip().lower() not in ("", "true", "none", "null") else DEFAULT_API_KEY
    records = _query_data_gov_api(clean_key, district)
    if not records and clean_key != DEFAULT_API_KEY:
        print(f"[WARNING] API key '{clean_key[:8]}...' returned 0 records. Retrying with DEFAULT_API_KEY...")
        records = _query_data_gov_api(DEFAULT_API_KEY, district)
    return records


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
            if sa_env.strip().startswith("{"):
                sa_json = json.loads(sa_env)
                cred = credentials.Certificate(sa_json)
                print("[INFO] Initialized Firebase from service account JSON string.")
            elif os.path.exists(sa_env):
                cred = credentials.Certificate(sa_env)
                print(f"[INFO] Initialized Firebase from service account file: {sa_env}")
        except Exception as e:
            print(f"[ERROR] Failed to parse FIREBASE_SERVICE_ACCOUNT: {e}", file=sys.stderr)
            return None
    elif os.path.exists("service-account.json"):
        cred = credentials.Certificate("service-account.json")
        print("[INFO] Initialized Firebase from service-account.json.")
    else:
        try:
            cred = credentials.ApplicationDefault()
            print("[INFO] Using Application Default Credentials.")
        except Exception as e:
            print(f"[ERROR] No Firebase credentials found: {e}", file=sys.stderr)
            return None

    firebase_admin.initialize_app(cred)
    return firestore.client()


def process_records(all_records):
    """Processes and categorizes records into market and prices items."""
    seen_keys = set()
    processed_items = []

    for rec in all_records:
        district = rec.get("district", "").strip()
        market = rec.get("market", "").strip()
        commodity_raw = rec.get("commodity", "").strip()
        variety = rec.get("variety", "").strip()
        arrival_date = rec.get("arrival_date", "").strip()
        modal_price = float(rec.get("modal_price", 0))
        min_price = float(rec.get("min_price", modal_price))
        max_price = float(rec.get("max_price", modal_price))

        if modal_price <= 0:
            continue

        # Unique key across district, market, commodity, date
        dedup_key = f"{district}_{market}_{commodity_raw}_{arrival_date}".lower()
        if dedup_key in seen_keys:
            continue
        seen_keys.add(dedup_key)

        date_ms = parse_arrival_date(arrival_date)
        trend = calculate_trend(min_price, max_price, modal_price)
        crop_name = get_localized_commodity(commodity_raw)
        unit = {"hi": "1 क्विंटल", "en": "1 Quintal"}
        market_localized = {"hi": market, "en": market}

        is_crop = any(kw in commodity_raw.lower() for kw in CROP_KEYWORDS)
        is_bulk_staple = any(kw in commodity_raw.lower() for kw in BULK_STAPLE_KEYWORDS)

        # 1. Add to 'market' (ताज़ा बाजार भाव / Vegetables & Fruits) if not purely grain
        # All fruits and vegetables belong in 'market'
        if not is_crop:
            doc_id_market = hashlib.md5(f"market_{dedup_key}".encode("utf-8")).hexdigest()
            processed_items.append({
                "id": doc_id_market,
                "categoryId": "market",
                "cropName": crop_name,
                "price": modal_price,
                "unit": unit,
                "trend": trend,
                "date": date_ms,
                "market": market_localized,
                "district": district,
                "variety": variety
            })

        # 2. Add to 'prices' (मंडी भाव / Crops & Bulk Produce)
        # Any actual crop or bulk agricultural staple belongs in 'prices'
        if is_crop or is_bulk_staple:
            doc_id_price = hashlib.md5(f"prices_{dedup_key}".encode("utf-8")).hexdigest()
            processed_items.append({
                "id": doc_id_price,
                "categoryId": "prices",
                "cropName": crop_name,
                "price": modal_price,
                "unit": unit,
                "trend": trend,
                "date": date_ms,
                "market": market_localized,
                "district": district,
                "variety": variety
            })

    return processed_items


def sync_mandi_to_firestore(db, items, dry_run=False):
    """Saves mandi items to central `mandi` collection in Firestore and purges expired entries."""
    if db is None:
        print("[WARNING] Firestore client is not available. Skipping sync.")
        return 0, 0

    mandi_col = db.collection("mandi")
    saved_count = 0

    print(f"[INFO] Saving {len(items)} mandi items into Firestore 'mandi' collection...")
    for item in items:
        doc_id = item["id"]
        doc_ref = mandi_col.document(doc_id)
        if not dry_run:
            item_data = dict(item)
            item_data["updatedAt"] = firestore.SERVER_TIMESTAMP
            doc_ref.set(item_data)
        saved_count += 1
        cat = item["categoryId"]
        name = item["cropName"].get("hi", "")
        price = item["price"]
        market = item["market"].get("en", "")
        print(f"  [+] [{cat.upper()}] {name} - ₹{price}/क्विंटल ({market})")

    # 7-day retention cleanup
    cutoff_dt = datetime.now(timezone.utc) - timedelta(days=RETENTION_DAYS)
    cutoff_ms = int(cutoff_dt.timestamp() * 1000)
    print(f"[INFO] Cleaning up Mandi records older than {RETENTION_DAYS} days (cutoff: {cutoff_dt.isoformat()})...")

    deleted_count = 0
    try:
        old_docs = mandi_col.where("date", "<", cutoff_ms).stream()
        for doc in old_docs:
            if not dry_run:
                doc.reference.delete()
            deleted_count += 1
            print(f"  [-] Deleted expired mandi doc: {doc.id}")
    except Exception as e:
        print(f"[WARNING] Could not execute deletion query: {e}")

    return saved_count, deleted_count


def send_mandi_notification(db, arrival_date_str: str, force: bool = False, dry_run: bool = False):
    """Sends a push notification to FCM topic 'mandi' when fresh rates arrive."""
    if not FIREBASE_AVAILABLE or dry_run:
        print(f"[DRY-RUN/SKIP] Would send FCM Mandi notification for date: {arrival_date_str}")
        return

    meta_ref = db.collection("settings").document("mandi_sync_meta")

    if not force:
        try:
            meta_doc = meta_ref.get()
            if meta_doc.exists:
                last_notified = meta_doc.to_dict().get("last_notified_date")
                if last_notified == arrival_date_str:
                    print(f"[INFO] Notification already sent today for arrival date '{arrival_date_str}'. Skipping duplicate.")
                    return
        except Exception as e:
            print(f"[WARNING] Could not check mandi sync metadata: {e}")

    try:
        title = "🌾 आज का ताज़ा मंडी भाव अपडेट हो गया है!"
        body = "खलीलाबाद व नौगढ़ मंडी के आलू, टमाटर, बैंगन, गोभी व फसलों के नए रेट देखने के लिए टैप करें।"

        msg = messaging.Message(
            notification=messaging.Notification(
                title=title,
                body=body
            ),
            data={
                "id": "mandi",
                "type": "mandi"
            },
            topic="mandi"
        )
        response = messaging.send(msg)
        print(f"[INFO] FCM Mandi notification successfully sent to topic 'mandi': {response}")

        meta_ref.set({
            "last_notified_date": arrival_date_str,
            "last_notification_sent_at": firestore.SERVER_TIMESTAMP
        }, merge=True)
    except Exception as e:
        print(f"[ERROR] Failed to send FCM Mandi notification: {e}", file=sys.stderr)


def main():
    parser = argparse.ArgumentParser(description="Sync daily Mandi prices for Sant Kabir Nagar & Siddharth Nagar.")
    env_key = os.environ.get("DATA_GOV_API_KEY", "").strip()
    default_key = env_key if env_key and env_key.lower() not in ("true", "false", "none", "null") else DEFAULT_API_KEY
    parser.add_argument("--api-key", default=default_key, help="data.gov.in API Key")
    parser.add_argument("--dry-run", action="store_true", help="Run without writing to Firebase")
    parser.add_argument("--force-notify", action="store_true", help="Force send FCM notification even if already sent today")
    args = parser.parse_args()

    api_key = args.api_key.strip() if args.api_key and args.api_key.strip().lower() not in ("true", "false", "none", "null") else DEFAULT_API_KEY

    print("==========================================")
    print("   Apna Gaon - Daily Mandi Sync Service   ")
    print("==========================================")
    print(f"Timestamp: {datetime.now(timezone.utc).isoformat()}")
    print(f"Districts: {TARGET_DISTRICTS} | Dry Run: {args.dry_run}")

    all_records = []
    for dist in TARGET_DISTRICTS:
        recs = fetch_district_mandi(api_key, dist)
        all_records.extend(recs)

    if not all_records:
        print("[ERROR] No records returned for target districts. Exiting with failure.", file=sys.stderr)
        sys.exit(1)

    processed_items = process_records(all_records)
    print(f"[INFO] Processed {len(processed_items)} total mandi entries across categories.")

    if args.dry_run:
        print("[INFO] Dry-run preview:")
        for it in processed_items[:10]:
            print(f"  [{it['categoryId']}] {it['cropName']['hi']} : ₹{it['price']} ({it['market']['en']})")
        send_mandi_notification(None, "TODAY", force=args.force_notify, dry_run=True)
        return

    db = init_firebase()
    if not db:
        print("[ERROR] Firebase could not be initialized.")
        sys.exit(1)

    saved, deleted = sync_mandi_to_firestore(db, processed_items, dry_run=args.dry_run)
    print(f"[SUMMARY] Saved/Updated: {saved} | Expired deleted: {deleted}")

    # Determine latest arrival date string (e.g. 23/10/2026)
    arrival_dates = [rec.get("arrival_date", "").replace("\\", "").strip() for rec in all_records if rec.get("arrival_date")]
    latest_arrival_date = arrival_dates[0] if arrival_dates else datetime.now(timezone.utc).strftime("%d/%m/%Y")

    if saved > 0 or args.force_notify:
        send_mandi_notification(db, latest_arrival_date, force=args.force_notify, dry_run=args.dry_run)


if __name__ == "__main__":
    main()
