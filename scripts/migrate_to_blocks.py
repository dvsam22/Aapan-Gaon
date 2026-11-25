#!/usr/bin/env python3
"""
Safe, non-destructive Firestore migration script for Apna Gaon:
Copies existing records from `villages/{villageId}/{service}` and legacy top-level
collections (`banners`, `mandi`, `news`, `education`, `legal_panchayat`)
into `/blocks/block_paniyara/{service}`.

Usage:
  python3 scripts/migrate_to_blocks.py --key ~/Downloads/firebase-key.json          # DRY RUN
  python3 scripts/migrate_to_blocks.py --key ~/Downloads/firebase-key.json --write  # WRITE
"""

import argparse
import glob
import os
import re
import sys

import google.auth
from google.cloud import firestore

PROJECT_ID = "apna-gaon-f57f6"
BLOCK_ID = "block_paniyara"

SUBCOLLECTIONS = [
    "banners",
    "construction",
    "labour",
    "transport",
    "mandi",
    "health",
    "education",
    "family_functions",
    "legal_panchayat",
    "news",
    "notifications",
]

CENTRAL_COLLECTIONS = {"banners", "mandi", "news", "education", "legal_panchayat"}
LOCATION_FIELD_COLLECTIONS = {"labour", "transport"}
ADDRESS_FIELD_COLLECTIONS = {
    "construction",
    "health",
    "education",
    "family_functions",
    "legal_panchayat",
    "mandi",
}


def init_firestore_client(key_path: str | None = None) -> firestore.Client:
    """Initializes Firestore client via --key, env var, local service-account JSON, or ADC."""
    candidates = []
    if key_path:
        candidates.append(os.path.expanduser(key_path.strip()))
    env_sa = os.environ.get("GOOGLE_APPLICATION_CREDENTIALS", "").strip()
    if env_sa:
        candidates.append(os.path.expanduser(env_sa))
    candidates.extend([
        "service-account.json",
        "serviceAccountKey.json",
    ])
    candidates.extend(sorted(glob.glob("*firebase-adminsdk*.json")))

    for path in candidates:
        if path and os.path.exists(path):
            print(f"[INFO] Authenticating with service account: {path}")
            return firestore.Client.from_service_account_json(path, project=PROJECT_ID)

    try:
        creds, _ = google.auth.default(scopes=["https://www.googleapis.com/auth/datastore"])
        print(f"[INFO] Authenticating with Application Default Credentials for project '{PROJECT_ID}'...")
        return firestore.Client(project=PROJECT_ID, credentials=creds)
    except Exception:
        pass

    print("\n[ERROR] Valid Firebase Service Account key nahi mili.")
    print("Please Firebase Console se 1-time Service Account key download karein:")
    print("  1. Open Firebase Console -> apna-gaon-f57f6 -> Project Settings (gear icon) -> Service accounts")
    print("  2. Click 'Generate new private key' -> Download JSON file")
    print("  3. Run:")
    print("     python3 scripts/migrate_to_blocks.py --key ~/Downloads/APNA_GAON_KEY.json\n")
    sys.exit(1)


def normalize_text(value) -> str:
    if not value:
        return ""
    if isinstance(value, str):
        return value.strip().lower()
    if isinstance(value, dict):
        parts = [
            v.strip().lower()
            for v in value.values()
            if isinstance(v, str) and v.strip()
        ]
        return "|".join(parts)
    return str(value).strip().lower()


def is_empty_localized(value) -> bool:
    if not value:
        return True
    if isinstance(value, str):
        return len(value.strip()) == 0
    if isinstance(value, dict):
        return all(not isinstance(v, str) or len(v.strip()) == 0 for v in value.values())
    return False


def build_dedupe_key(subcol: str, data: dict):
    if subcol == "banners":
        img = (data.get("imageUrl") or "").strip()
        return img or f"{normalize_text(data.get('title'))}__{data.get('link') or ''}"
    if subcol == "news":
        url = (data.get("url") or "").strip()
        return url or f"{normalize_text(data.get('title'))}__{data.get('date') or 0}"
    if subcol == "notifications":
        return f"{normalize_text(data.get('title'))}__{normalize_text(data.get('message'))}__{data.get('date') or 0}"
    if subcol == "mandi" and data.get("categoryId") != "buyers":
        return f"rate__{normalize_text(data.get('cropName'))}__{normalize_text(data.get('market'))}__{data.get('date') or 0}"

    cat = (data.get("categoryId") or "").strip().lower()
    title_or_name = normalize_text(
        data.get("shopName") or data.get("name") or data.get("buyerName") or data.get("title")
    )
    contact = re.sub(r"\D", "", str(data.get("contact") or ""))
    if title_or_name and contact:
        return f"{cat}__{title_or_name}__{contact}"
    return None


def main():
    parser = argparse.ArgumentParser(description="Migrate Apna Gaon Firestore data into /blocks/block_paniyara.")
    parser.add_argument("--key", default=None, help="Path to Firebase Service Account JSON key.")
    parser.add_argument("--write", action="store_true", help="Write changes to Firestore (default is dry-run).")
    args = parser.parse_args()

    mode = "WRITE MODE" if args.write else "DRY RUN (PREVIEW ONLY)"
    print(f"\n=== Apna Gaon Migration -> /blocks/{BLOCK_ID} [{mode}] ===\n")

    db = init_firestore_client(args.key)

    village_docs = list(db.collection("villages").stream())
    village_meta = {}
    village_names = []

    for v_doc in village_docs:
        v_data = v_doc.to_dict() or {}
        village_meta[v_doc.id] = v_data
        raw_name = v_data.get("villageName")
        if isinstance(raw_name, str):
            label = raw_name
        elif isinstance(raw_name, dict):
            label = raw_name.get("en") or raw_name.get("hi") or v_doc.id
        else:
            label = v_doc.id
        village_names.append(label)

    print(f"[INFO] Found {len(village_docs)} villages: {', '.join(village_names)}")

    block_ref = db.collection("blocks").document(BLOCK_ID)
    block_payload = {
        "id": BLOCK_ID,
        "blockName": {"en": "Paniyara Block", "hi": "पनियरा ब्लॉक"},
        "district": {"en": "Maharajganj", "hi": "महराजगंज"},
        "state": {"en": "Uttar Pradesh", "hi": "उत्तर प्रदेश"},
        "lat": 27.00,
        "lng": 83.42,
        "active": True,
        "isActive": True,
        "order": 1,
        "villages": village_names,
    }

    pending_writes = [(block_ref, block_payload)]

    for subcol in SUBCOLLECTIONS:
        raw_docs = []

        for v_doc in village_docs:
            snap = v_doc.reference.collection(subcol).stream()
            for doc in snap:
                raw_docs.append({
                    "doc_id": doc.id,
                    "village_id": v_doc.id,
                    "data": doc.to_dict() or {},
                })

        if subcol in CENTRAL_COLLECTIONS:
            for doc in db.collection(subcol).stream():
                d = doc.to_dict() or {}
                raw_docs.append({
                    "doc_id": doc.id,
                    "village_id": d.get("villageId") or "",
                    "data": d,
                })

        seen_dedupe_keys = set()
        used_doc_ids = set()
        copied_count = 0
        skipped_dupes = 0

        for entry in raw_docs:
            data = dict(entry["data"])
            dedupe_key = build_dedupe_key(subcol, data)
            if dedupe_key and dedupe_key in seen_dedupe_keys:
                skipped_dupes += 1
                continue
            if dedupe_key:
                seen_dedupe_keys.add(dedupe_key)

            v_id = entry["village_id"]
            v_info = village_meta.get(v_id, {})
            data["blockId"] = BLOCK_ID

            if v_id and not data.get("villageId"):
                data["villageId"] = v_id
            if v_info.get("villageName") and not data.get("villageName"):
                data["villageName"] = v_info["villageName"]

            if subcol in LOCATION_FIELD_COLLECTIONS:
                if is_empty_localized(data.get("location")) and v_info.get("villageName"):
                    data["location"] = v_info["villageName"]
            elif subcol in ADDRESS_FIELD_COLLECTIONS:
                if is_empty_localized(data.get("address")) and v_info.get("villageName"):
                    data["address"] = v_info["villageName"]

            target_id = entry["doc_id"]
            if target_id in used_doc_ids:
                prefix = v_id or "central"
                target_id = f"{prefix}__{entry['doc_id']}"
                counter = 1
                while target_id in used_doc_ids:
                    target_id = f"{prefix}__{entry['doc_id']}_{counter}"
                    counter += 1

            used_doc_ids.add(target_id)
            data["id"] = target_id

            target_ref = block_ref.collection(subcol).document(target_id)
            pending_writes.append((target_ref, data))
            copied_count += 1

        print(
            f"  - {subcol.ljust(18)}: {len(raw_docs):3d} source -> {copied_count:3d} copied ({skipped_dupes} duplicates skipped)"
        )

    print(f"\n[INFO] Total Firestore writes queued: {len(pending_writes)}")

    if not args.write:
        print("\n[DRY RUN] No changes written yet. Re-run with `--write` to copy data into Firestore:\n")
        print("  python3 scripts/migrate_to_blocks.py --write\n")
        return

    batch_size = 400
    for i in range(0, len(pending_writes), batch_size):
        batch = db.batch()
        chunk = pending_writes[i : i + batch_size]
        for ref, payload in chunk:
            batch.set(ref, payload, merge=True)
        batch.commit()
        print(f"[INFO] Committed batch {i // batch_size + 1} ({len(chunk)} docs)")

    print("\n[SUCCESS] Migration copy completed into /blocks/block_paniyara!")
    print("[NOTE] Existing `villages` and root collections were untouched; verify in Firebase Console before deleting old ones.\n")


if __name__ == "__main__":
    main()
