"""
Apna Gaon - 2nd Gen Firebase Cloud Functions (Mumbai Region: asia-south1)
Automated Daily Mandi & Market Prices Sync + Local News Sync.
"""

import os
import sys
import json
import hashlib
import ssl
import urllib.request
import urllib.parse
from datetime import datetime, timezone, timedelta

from firebase_functions import scheduler_fn, https_fn
from firebase_admin import initialize_app, firestore, messaging

# Initialize Firebase Admin using default in-cloud credentials
initialize_app()

DATA_GOV_API_URL = "https://api.data.gov.in/resource/9ef84268-d588-465a-a308-a864a43d0070"
DEFAULT_DATA_GOV_KEY = "579b464db66ec23bdd000001d53e0115502e4a9f65f1012b123ada90"
TARGET_DISTRICTS = ["Sant Kabir Nagar", "Siddharth Nagar"]
MANDI_RETENTION_DAYS = 7

GNEWS_API_URL = "https://gnews.io/api/v4/search"
DEFAULT_GNEWS_KEY = "7f3a60695976d579cb056394df4743fb"
NEWS_RETENTION_DAYS = 7

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

BULK_STAPLE_KEYWORDS = ["potato", "pumpkin", "bottle gourd", "cauliflower", "onion"]


def get_localized_commodity(commodity_raw: str):
    clean = commodity_raw.strip().lower()
    if clean in COMMODITY_TRANSLATIONS:
        return COMMODITY_TRANSLATIONS[clean]
    title = commodity_raw.strip()
    return {"hi": title, "en": title}


def parse_arrival_date(arrival_date_str: str) -> int:
    try:
        cleaned = arrival_date_str.strip().replace("\\", "")
        dt = datetime.strptime(cleaned, "%d/%m/%Y").replace(tzinfo=timezone.utc)
        return int(dt.timestamp() * 1000)
    except Exception:
        return int(datetime.now(timezone.utc).timestamp() * 1000)


def calculate_trend(min_p, max_p, modal_p) -> str:
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


def _query_data_gov_api(api_key: str, district: str):
    encoded_district = urllib.parse.quote(district)
    url = f"{DATA_GOV_API_URL}?api-key={api_key}&format=json&limit=100&filters%5Bstate.keyword%5D=Uttar%20Pradesh&filters%5Bdistrict%5D={encoded_district}"
    print(f"[INFO] Fetching Mandi data for district: '{district}'...")

    headers = {
        "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36",
        "Accept": "application/json, text/plain, */*",
        "Accept-Language": "en-IN,en-GB;q=0.9,en-US;q=0.8,en;q=0.7,hi;q=0.6",
        "Connection": "keep-alive"
    }
    req = urllib.request.Request(url, headers=headers)

    ctx = ssl.create_default_context()
    try:
        with urllib.request.urlopen(req, timeout=10, context=ctx) as r:
            payload = json.loads(r.read().decode("utf-8"))
            if "error" in payload:
                err = f"API error for {district}: {payload.get('error')}"
                print(f"[ERROR] {err}", file=sys.stderr)
                return [], err
            records = payload.get("records", [])
            print(f"[INFO] Fetched {len(records)} records for {district}.")
            return records, None
    except urllib.error.URLError as e:
        if "CERTIFICATE_VERIFY_FAILED" in str(e):
            unverified_ctx = ssl._create_unverified_context()
            try:
                with urllib.request.urlopen(req, timeout=10, context=unverified_ctx) as r:
                    payload = json.loads(r.read().decode("utf-8"))
                    if "error" in payload:
                        err = f"API error for {district} (unverified SSL): {payload.get('error')}"
                        return [], err
                    records = payload.get("records", [])
                    return records, None
            except Exception as e2:
                err = f"Failed for {district} (unverified SSL): {e2}"
                return [], err
        err = f"Failed for {district}: {e}"
        print(f"[ERROR] {err}", file=sys.stderr)
        return [], err
    except Exception as e:
        err = f"Failed for {district}: {e}"
        print(f"[ERROR] {err}", file=sys.stderr)
        return [], err


def fetch_district_mandi(api_key: str, district: str):
    clean_key = api_key.strip() if api_key and api_key.strip().lower() not in ("", "true", "none", "null") else DEFAULT_DATA_GOV_KEY
    records, err = _query_data_gov_api(clean_key, district)
    if not records and clean_key != DEFAULT_DATA_GOV_KEY:
        print(f"[WARNING] Key '{clean_key[:8]}...' returned 0 records. Retrying with default...")
        records, err = _query_data_gov_api(DEFAULT_DATA_GOV_KEY, district)
    return records, err


def process_mandi_records(all_records):
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

        # 1. Market (Vegetables & Fruits)
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

        # 2. Prices (Crops & Bulk Produce)
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


def send_mandi_fcm_notification(db, arrival_date_str: str, force: bool = False):
    meta_ref = db.collection("settings").document("mandi_sync_meta")
    if not force:
        try:
            meta_doc = meta_ref.get()
            if meta_doc.exists and meta_doc.to_dict().get("last_notified_date") == arrival_date_str:
                print(f"[INFO] FCM notification already sent today for '{arrival_date_str}'.")
                return
        except Exception as e:
            print(f"[WARNING] Could not check meta: {e}")

    try:
        msg = messaging.Message(
            notification=messaging.Notification(
                title="🌾 आज का ताज़ा मंडी भाव अपडेट हो गया है!",
                body="खलीलाबाद व नौगढ़ मंडी के आलू, टमाटर, बैंगन, गोभी व फसलों के नए रेट देखने के लिए टैप करें।"
            ),
            data={"id": "mandi", "type": "mandi"},
            topic="mandi"
        )
        resp = messaging.send(msg)
        print(f"[INFO] FCM Notification sent: {resp}")
        meta_ref.set({
            "last_notified_date": arrival_date_str,
            "last_notification_sent_at": firestore.SERVER_TIMESTAMP
        }, merge=True)
    except Exception as e:
        print(f"[ERROR] Failed to send FCM: {e}", file=sys.stderr)


def execute_mandi_sync(force_notify=False):
    db = firestore.client()
    all_records = []
    errors = []
    for dist in TARGET_DISTRICTS:
        recs, err = fetch_district_mandi(DEFAULT_DATA_GOV_KEY, dist)
        if err:
            errors.append(err)
        all_records.extend(recs)

    if not all_records:
        err_summary = "; ".join(errors) if errors else "No records returned from data.gov.in"
        return 0, 0, err_summary

    items = process_mandi_records(all_records)
    mandi_col = db.collection("mandi")
    saved_count = 0

    for it in items:
        doc_ref = mandi_col.document(it["id"])
        data = dict(it)
        data["updatedAt"] = firestore.SERVER_TIMESTAMP
        doc_ref.set(data)
        saved_count += 1

    # Purge expired > 7 days
    cutoff_ms = int((datetime.now(timezone.utc) - timedelta(days=MANDI_RETENTION_DAYS)).timestamp() * 1000)
    deleted_count = 0
    try:
        for doc in mandi_col.where("date", "<", cutoff_ms).stream():
            doc.reference.delete()
            deleted_count += 1
    except Exception as e:
        print(f"[WARNING] Cleanup error: {e}")

    arrival_dates = [r.get("arrival_date", "").replace("\\", "").strip() for r in all_records if r.get("arrival_date")]
    latest_date = arrival_dates[0] if arrival_dates else datetime.now(timezone.utc).strftime("%d/%m/%Y")

    if saved_count > 0 or force_notify:
        send_mandi_fcm_notification(db, latest_date, force=force_notify)

    return saved_count, deleted_count, latest_date


# ==============================================================================
# Cloud Functions 2nd Gen - Deployed in Mumbai (asia-south1)
# ==============================================================================

@scheduler_fn.on_schedule(
    schedule="45 7,11,15,19 * * *",
    region="asia-south1",
    timezone="Asia/Kolkata"
)
def sync_mandi_scheduled(event: scheduler_fn.ScheduledEvent) -> None:
    """Runs 4 times daily at 07:45 AM, 11:45 AM, 03:45 PM, 07:45 PM IST."""
    saved, deleted, arrival_date = execute_mandi_sync()
    print(f"[SCHEDULED SUCCESS] Saved: {saved}, Deleted: {deleted}, Date: {arrival_date}")


HTML_SYNC_PAGE = """<!DOCTYPE html>
<html lang="hi">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>🌾 आपन गांव - मंडी सिंक</title>
  <style>
    * { box-sizing: border-box; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
      background: #f0f4f0;
      color: #1b381e;
      margin: 0;
      padding: 20px;
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
    }
    .card {
      background: #ffffff;
      border-radius: 20px;
      padding: 28px 24px;
      max-width: 440px;
      width: 100%;
      box-shadow: 0 10px 30px rgba(0,0,0,0.08);
      text-align: center;
    }
    .badge {
      display: inline-block;
      background: #e8f5e9;
      color: #2e7d32;
      font-weight: 600;
      font-size: 13px;
      padding: 6px 14px;
      border-radius: 20px;
      margin-bottom: 12px;
    }
    h1 {
      font-size: 22px;
      margin: 8px 0 16px;
      color: #1b5e20;
    }
    p {
      font-size: 14px;
      color: #555;
      line-height: 1.5;
      margin: 0 0 20px;
    }
    .status-box {
      background: #f9fbf9;
      border: 1px solid #e0e0e0;
      border-radius: 12px;
      padding: 16px;
      margin-bottom: 20px;
      min-height: 70px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      line-height: 1.4;
      text-align: center;
    }
    .btn {
      background: #2e7d32;
      color: white;
      border: none;
      border-radius: 12px;
      padding: 14px;
      font-size: 16px;
      font-weight: 600;
      width: 100%;
      cursor: pointer;
      box-shadow: 0 4px 12px rgba(46,125,50,0.25);
      transition: all 0.2s ease;
    }
    .btn:active { transform: scale(0.98); }
    .btn:disabled { background: #a5d6a7; cursor: not-allowed; }
    .footer {
      margin-top: 18px;
      font-size: 12px;
      color: #888;
    }
  </style>
</head>
<body>
  <div class="card">
    <div class="badge">📡 मोबाइल सिंक नेटवर्क</div>
    <h1>🌾 आपन गांव - मंडी भाव सिंक</h1>
    <p>यह पेज आपके फोन के नेटवर्क (Jio/Airtel/Wi-Fi) से सरकारी पोर्टल से आज का ताज़ा भाव लेकर Google Cloud पर अपडेट करता है।</p>
    
    <div id="statusBox" class="status-box">
      <span>⏳ शुरू हो रहा है...</span>
    </div>

    <button id="syncBtn" class="btn" onclick="startSync()">🔄 अभी सिंक करें (Sync Now)</button>
    <div class="footer">संत कबीर नगर (खलीलाबाद) व सिद्धार्थ नगर (नौगढ़)</div>
  </div>

  <script>
    const API_KEY = "579b464db66ec23bdd000001d53e0115502e4a9f65f1012b123ada90";
    const RESOURCE_ID = "9ef84268-d588-465a-a308-a864a43d0070";
    const DISTRICTS = ["Sant Kabir Nagar", "Siddharth Nagar"];

    async function startSync() {
      const btn = document.getElementById("syncBtn");
      const box = document.getElementById("statusBox");
      btn.disabled = true;
      box.style.borderColor = "#81c784";
      box.innerHTML = "<span>⏳ सरकारी पोर्टल (data.gov.in) से भाव प्राप्त किया जा रहा है...</span>";

      let allRecords = [];
      let errors = [];

      for (const dist of DISTRICTS) {
        try {
          const url = `https://api.data.gov.in/resource/${RESOURCE_ID}?api-key=${API_KEY}&format=json&limit=100&filters[state]=Uttar%20Pradesh&filters[district]=${encodeURIComponent(dist)}`;
          const resp = await fetch(url);
          if (!resp.ok) {
            errors.push(`${dist}: HTTP ${resp.status}`);
            continue;
          }
          const json = await resp.json();
          if (json.records && json.records.length > 0) {
            allRecords.push(...json.records);
          }
        } catch (e) {
          errors.push(`${dist}: ${e.message}`);
        }
      }

      if (allRecords.length === 0) {
        box.style.borderColor = "#ffb74d";
        box.innerHTML = `<span style="color:#e65100;">⚠️ आज का कोई नया डेटा नहीं मिला।<br><small>${errors.join("; ") || "0 records"}</small></span>`;
        btn.disabled = false;
        return;
      }

      box.innerHTML = `<span>📥 ${allRecords.length} रिकॉर्ड मिले!<br>Google Cloud पर सुरक्षित सेव किया जा रहा है...</span>`;

      try {
        const postResp = await fetch(window.location.origin + window.location.pathname, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ records: allRecords })
        });
        const result = await postResp.json();
        box.style.borderColor = "#4caf50";
        box.innerHTML = `<span style="color:#2e7d32; font-weight:600;">🎉 सफलतापूर्वक सिंक हुआ!<br>✅ ${result.saved} मंडी भाव सेव हुए (${result.arrival_date})<br>🔔 सभी ग्रामीणों को नोटिफिकेशन भेज दिया गया।</span>`;
      } catch (err) {
        box.style.borderColor = "#ef5350";
        box.innerHTML = `<span style="color:#c62828;">❌ Cloud Save त्रुटि: ${err.message}</span>`;
      } finally {
        btn.disabled = false;
      }
    }

    // Auto-run on page load
    window.addEventListener("DOMContentLoaded", () => {
      startSync();
    });
  </script>
</body>
</html>
"""


@https_fn.on_request(region="asia-south1")
def sync_mandi_http(req: https_fn.Request) -> https_fn.Response:
    """Handles browser visits with auto-client sync UI, or direct POST records, or server GET."""
    cors_headers = {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
        "Access-Control-Allow-Headers": "Content-Type, Authorization",
    }

    # Handle CORS preflight
    if req.method == "OPTIONS":
        return https_fn.Response("", status=204, headers=cors_headers)

    # 1. POST from phone browser / Android app / script with pre-fetched records
    if req.method == "POST":
        body = req.get_json(silent=True) or {}
        records = body.get("records", [])
        if records:
            db = firestore.client()
            items = process_mandi_records(records)
            mandi_col = db.collection("mandi")
            saved_count = 0
            for it in items:
                doc_ref = mandi_col.document(it["id"])
                data = dict(it)
                data["updatedAt"] = firestore.SERVER_TIMESTAMP
                doc_ref.set(data)
                saved_count += 1

            # Purge expired > 7 days
            cutoff_ms = int((datetime.now(timezone.utc) - timedelta(days=MANDI_RETENTION_DAYS)).timestamp() * 1000)
            deleted_count = 0
            try:
                for doc in mandi_col.where("date", "<", cutoff_ms).stream():
                    doc.reference.delete()
                    deleted_count += 1
            except Exception as e:
                print(f"[WARNING] Cleanup error: {e}")

            arrival_dates = [r.get("arrival_date", "").replace("\\", "").strip() for r in records if r.get("arrival_date")]
            latest_date = arrival_dates[0] if arrival_dates else datetime.now(timezone.utc).strftime("%d/%m/%Y")
            send_mandi_fcm_notification(db, latest_date, force=True)
            return https_fn.Response(
                json.dumps({
                    "status": "success",
                    "saved": saved_count,
                    "deleted": deleted_count,
                    "source": "device_direct",
                    "arrival_date": latest_date,
                    "timestamp": datetime.now(timezone.utc).isoformat()
                }, indent=2),
                status=200,
                mimetype="application/json",
                headers=cors_headers
            )

    # 2. Browser GET request -> serve interactive 1-click Auto-Sync Web UI
    accept_header = req.headers.get("Accept", "")
    is_browser = "text/html" in accept_header and not req.args.get("raw")
    if is_browser and req.method == "GET":
        return https_fn.Response(
            HTML_SYNC_PAGE,
            status=200,
            mimetype="text/html; charset=utf-8",
            headers=cors_headers
        )

    # 3. Server-side fetch fallback (for curl / automated triggers)
    force = req.args.get("force", "false").lower() == "true"
    saved, deleted, arrival_date = execute_mandi_sync(force_notify=force)
    return https_fn.Response(
        json.dumps({
            "status": "success" if saved > 0 else "warning",
            "saved": saved,
            "deleted": deleted,
            "arrival_date": arrival_date,
            "timestamp": datetime.now(timezone.utc).isoformat()
        }, indent=2),
        status=200,
        mimetype="application/json",
        headers=cors_headers
    )
