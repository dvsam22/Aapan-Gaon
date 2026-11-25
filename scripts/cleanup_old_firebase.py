#!/usr/bin/env python3
"""
Safe cleanup script to remove legacy village subcollections and root service collections
after migrating data into `/blocks/block_paniyara/{service}`.

PROTECTED (NEVER TOUCHED):
  - `blocks` (`/blocks/block_paniyara/...`)
  - `business_registrations`
  - `settings`

DELETED (when `--delete` is passed):
  - All subcollections under `villages/{villageId}/...`
  - All root `villages/{villageId}` docs (unless `--keep-villages-list` is passed)
  - Legacy root collections: `banners`, `mandi`, `news`, `education`, `legal_panchayat`,
    `construction`, `labour`, `transport`, `health`, `family_functions`, `notifications`

Usage:
  python3 scripts/cleanup_old_firebase.py           # DRY RUN (preview counts only)
  python3 scripts/cleanup_old_firebase.py --delete  # Permanently delete legacy data
"""

import argparse
import glob
import os
import sys

import google.auth
from google.cloud import firestore

PROJECT_ID = "apna-gaon-f57f6"
BLOCK_ID = "block_paniyara"

LEGACY_ROOT_COLLECTIONS = [
    "banners",
    "mandi",
    "news",
    "education",
    "legal_panchayat",
    "construction",
    "labour",
    "transport",
    "health",
    "family_functions",
    "notifications",
]


def init_firestore_client(key_path: str | None = None) -> firestore.Client:
    candidates = []
    if key_path:
        candidates.append(os.path.expanduser(key_path.strip()))
    env_sa = os.environ.get("GOOGLE_APPLICATION_CREDENTIALS", "").strip()
    if env_sa:
        candidates.append(os.path.expanduser(env_sa))
    candidates.extend(["service-account.json", "serviceAccountKey.json"])
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

    print("[ERROR] No Firebase Service Account JSON found. Pass `--key path/to/key.json`.")
    sys.exit(1)


def verify_new_block_exists(db: firestore.Client):
    """Safety guard: abort if `/blocks/block_paniyara` does not exist yet."""
    block_ref = db.collection("blocks").document(BLOCK_ID)
    if not block_ref.get().exists:
        print(f"[ABORT] Safety check failed: `/blocks/{BLOCK_ID}` does not exist! Run migration first.")
        sys.exit(1)

    has_data = any(
        len(list(block_ref.collection(sub).limit(1).stream())) > 0
        for sub in ("labour", "mandi", "construction", "news")
    )
    if not has_data:
        print(f"[ABORT] Safety check failed: `/blocks/{BLOCK_ID}` looks empty! Aborting cleanup.")
        sys.exit(1)
    print(f"[OK] Verified `/blocks/{BLOCK_ID}` is populated and safe.")


def commit_deletes(db: firestore.Client, doc_refs: list, batch_size: int = 400):
    for i in range(0, len(doc_refs), batch_size):
        batch = db.batch()
        chunk = doc_refs[i : i + batch_size]
        for ref in chunk:
            batch.delete(ref)
        batch.commit()
        print(f"[INFO] Deleted batch {i // batch_size + 1} ({len(chunk)} docs)")


def main():
    parser = argparse.ArgumentParser(description="Clean up legacy Firebase collections after Option 1 migration.")
    parser.add_argument("--key", default=None, help="Path to Firebase Service Account JSON key.")
    parser.add_argument("--delete", action="store_true", help="Actually delete legacy data (default is dry-run).")
    parser.add_argument(
        "--keep-villages-list",
        action="store_true",
        help="Delete only subcollections under `villages/*`, keeping the parent `villages` metadata docs.",
    )
    args = parser.parse_args()

    mode = "DELETE MODE" if args.delete else "DRY RUN (PREVIEW ONLY)"
    print(f"\n=== Apna Gaon Legacy Cleanup [{mode}] ===\n")

    db = init_firestore_client(args.key)
    verify_new_block_exists(db)

    refs_to_delete = []

    # 1. Collect all subcollection docs inside every `villages/{id}` (and optionally the village doc itself)
    village_docs = list(db.collection("villages").stream())
    village_sub_count = 0
    for v_doc in village_docs:
        for sub_col in v_doc.reference.collections():
            sub_docs = list(sub_col.stream())
            for s_doc in sub_docs:
                refs_to_delete.append(s_doc.reference)
                village_sub_count += 1
        if not args.keep_villages_list:
            refs_to_delete.append(v_doc.reference)

    print(
        f"  - villages/* subcollections : {village_sub_count} docs"
        + (f" (+ {len(village_docs)} village docs)" if not args.keep_villages_list else f" (keeping {len(village_docs)} village metadata docs)")
    )

    # 2. Collect legacy top-level service collections (explicit allowlist only; never touches blocks/business_registrations/settings)
    for root_col in LEGACY_ROOT_COLLECTIONS:
        docs = list(db.collection(root_col).stream())
        for d in docs:
            refs_to_delete.append(d.reference)
        if docs:
            print(f"  - root `{root_col}`".ljust(32) + f": {len(docs)} docs")

    print(f"\n[INFO] Total legacy documents queued for deletion: {len(refs_to_delete)}")
    print("[SAFE] Untouched collections: `blocks`, `business_registrations`, `settings`\n")

    if not args.delete:
        print("[DRY RUN] Nothing deleted. Re-run with `--delete` to permanently remove legacy docs:\n")
        print("  python3 scripts/cleanup_old_firebase.py --delete\n")
        return

    if not refs_to_delete:
        print("[INFO] Database is already clean — 0 legacy documents found.\n")
        return

    commit_deletes(db, refs_to_delete)
    print("\n[SUCCESS] Legacy Firebase cleanup complete! Only clean Option 1 data remains.\n")


if __name__ == "__main__":
    main()
