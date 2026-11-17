#!/usr/bin/env python3
"""
Script to delete all dummy testing records inside `education` and `legal_panchayat`
subcollections across all villages in Firebase Firestore.
"""

import json
import os
import sys
from google.oauth2.credentials import Credentials
from google.cloud import firestore

def main():
    token_path = os.path.expanduser("~/.config/configstore/firebase-tools.json")
    if not os.path.exists(token_path):
        print(f"[ERROR] Token file not found at {token_path}")
        sys.exit(1)

    with open(token_path) as f:
        cfg = json.load(f)
        tok = cfg.get("tokens", {})

    creds = Credentials(
        token=tok.get("access_token"),
        refresh_token=tok.get("refresh_token"),
        token_uri="https://oauth2.googleapis.com/token",
        client_id="563584335869-fgrhgmd47bqnekij5i8b5pr03ho85qd6.apps.googleusercontent.com"
    )

    db = firestore.Client(project="apna-gaon-f57f6", credentials=creds)

    print("[INFO] Starting cleanup of dummy data in education & legal_panchayat...")

    total_edu_deleted = 0
    total_legal_deleted = 0

    villages = list(db.collection("villages").stream())
    print(f"[INFO] Found {len(villages)} villages to process.")

    for v in villages:
        v_id = v.id
        v_ref = db.collection("villages").document(v_id)

        # 1. Clean education subcollection
        edu_docs = list(v_ref.collection("education").stream())
        if edu_docs:
            batch = db.batch()
            for doc in edu_docs:
                batch.delete(doc.reference)
                total_edu_deleted += 1
            batch.commit()
            print(f"  [-] Village {v_id}: Deleted {len(edu_docs)} education docs.")

        # 2. Clean legal_panchayat subcollection
        legal_docs = list(v_ref.collection("legal_panchayat").stream())
        if legal_docs:
            batch = db.batch()
            for doc in legal_docs:
                batch.delete(doc.reference)
                total_legal_deleted += 1
            batch.commit()
            print(f"  [-] Village {v_id}: Deleted {len(legal_docs)} legal_panchayat docs.")

    # 3. Clean root collections if any
    for col_name in ["education", "legal_panchayat"]:
        root_docs = list(db.collection(col_name).stream())
        if root_docs:
            batch = db.batch()
            for doc in root_docs:
                batch.delete(doc.reference)
            batch.commit()
            print(f"  [-] Root {col_name}: Deleted {len(root_docs)} docs.")

    print("\n[SUCCESS] Cleanup Completed!")
    print(f"  Total Education records deleted: {total_edu_deleted}")
    print(f"  Total Legal & Panchayat records deleted: {total_legal_deleted}")
    print(f"  Total dummy records removed: {total_edu_deleted + total_legal_deleted}")

if __name__ == "__main__":
    main()
