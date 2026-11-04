#!/usr/bin/env python3
"""
Seed script to push 2 dummy records per subcategory for Education and Legal & Panchayat to Firebase Firestore.
Supports both root collection structure (`/education`, `/legal_panchayat`) and village-specific subcollections (`/villages/{village_id}/...`).
"""

import os
import sys

try:
    import firebase_admin
    from firebase_admin import credentials, firestore
    FIREBASE_AVAILABLE = True
except ImportError:
    FIREBASE_AVAILABLE = False


EDUCATION_DATA = [
    # 1. Private Schools & Convent
    {
        "id": "school_1",
        "categoryId": "private_schools",
        "name": {"hi": "सरस्वती ज्ञान मंदिर कॉन्वेंट स्कूल", "en": "Saraswati Gyan Mandir Convent School"},
        "contact": "+919876543201",
        "address": {"hi": "मेन मार्केट रोड, बस स्टैंड के पास", "en": "Main Market Road, Near Bus Stand"},
        "details": {"hi": "कक्षा नर्सरी से 12वीं (हिंदी व इंग्लिश मीडियम), स्मार्ट क्लास", "en": "Nursery to 12th (Hindi & English Medium), Smart Classes"},
        "extraInfo": {"hi": "वाहन व कंप्यूटर लैब सुविधा उपलब्ध", "en": "Transport & Computer Lab Available"}
    },
    {
        "id": "school_2",
        "categoryId": "private_schools",
        "name": {"hi": "होली एंजल्स पब्लिक स्कूल", "en": "Holy Angels Public School"},
        "contact": "+919876543202",
        "address": {"hi": "तहसील रोड, ब्लॉक के सामने", "en": "Tehsil Road, In front of Block"},
        "details": {"hi": "CBSE पैटर्न, अनुभवी शिक्षक, खेलकूद मैदान व लाइब्रेरी", "en": "CBSE Pattern, Experienced Teachers, Playground & Library"},
        "extraInfo": {"hi": "प्रवेश प्रारंभ (नर्सरी से 10वीं)", "en": "Admission Open (Nursery to 10th)"}
    },

    # 2. Coaching & Tuition Centers
    {
        "id": "coaching_1",
        "categoryId": "coaching_centers",
        "name": {"hi": "लक्ष्य कैरियर एकेडमी", "en": "Lakshya Career Academy"},
        "contact": "+919876543203",
        "address": {"hi": "पुराना पावर हाउस चौराहा", "en": "Old Power House Crossing"},
        "details": {"hi": "कक्षा 9वीं से 12वीं (गणित, विज्ञान, अंग्रेजी) स्पेशल बैच", "en": "Class 9th to 12th (Maths, Science, English) Special Batch"},
        "extraInfo": {"hi": "साप्ताहिक टेस्ट व डाउट क्लास", "en": "Weekly Tests & Doubt Sessions"}
    },
    {
        "id": "coaching_2",
        "categoryId": "coaching_centers",
        "name": {"hi": "सफलता कोचिंग संस्थान", "en": "Safalta Coaching Institute"},
        "contact": "+919876543204",
        "address": {"hi": "थाना रोड, नियर शिव मंदिर", "en": "Thana Road, Near Shiv Temple"},
        "details": {"hi": "नवोदय, सैनिक स्कूल व पॉलिटेक्निक प्रवेश परीक्षा तैयारी", "en": "Navodaya, Sainik School & Polytechnic Exam Prep"},
        "extraInfo": {"hi": "समय: सुबह 6:00 से शाम 7:00 तक", "en": "Timing: 6:00 AM to 7:00 PM"}
    },

    # 3. Teachers & Home Tutors
    {
        "id": "tutor_1",
        "categoryId": "home_tutors",
        "name": {"hi": "अमित कुमार वर्मा (गणित विशेषज्ञ)", "en": "Amit Kumar Verma (Maths Specialist)"},
        "contact": "+919876543205",
        "address": {"hi": "गांव व नजदीकी 5 किमी क्षेत्र में", "en": "In village & nearby 5 km area"},
        "details": {"hi": "कक्षा 6वीं से 10वीं तक होम ट्यूशन (गणित व विज्ञान)", "en": "Class 6th to 10th Home Tuition (Maths & Science)"},
        "extraInfo": {"hi": "5 वर्ष का शिक्षण अनुभव", "en": "5 Years Teaching Experience"}
    },
    {
        "id": "tutor_2",
        "categoryId": "home_tutors",
        "name": {"hi": "सुनील कुमार (अंग्रेजी व सामाजिक विज्ञान)", "en": "Sunil Kumar (English & Social Studies)"},
        "contact": "+919876543206",
        "address": {"hi": "वार्ड नं. 3, प्राथमिक विद्यालय के पास", "en": "Ward No. 3, Near Primary School"},
        "details": {"hi": "कक्षा 1 से 8वीं तक सभी विषय एवं बोर्ड परीक्षा गाइडेंस", "en": "Class 1 to 8th all subjects & Board Exam Guidance"},
        "extraInfo": {"hi": "व्यक्तिगत ध्यान व कमजोर छात्रों पर विशेष फोकस", "en": "Personal attention & Special focus on weak students"}
    },

    # 4. Computer & Skill Centers
    {
        "id": "skill_1",
        "categoryId": "skill_centers",
        "name": {"hi": "डिजिटल भारत कंप्यूटर इंस्टीट्यूट", "en": "Digital Bharat Computer Institute"},
        "contact": "+919876543207",
        "address": {"hi": "मुख्य बाजार, यूनियन बैंक के ऊपर", "en": "Main Market, Above Union Bank"},
        "details": {"hi": "CCC, DCA, ADCA, Tally Prime एवं हिंदी/इंग्लिश टाइपिंग", "en": "CCC, DCA, ADCA, Tally Prime & Typing"},
        "extraInfo": {"hi": "सरकारी मान्यता प्राप्त सर्टिफिकेट", "en": "Government Recognized Certificate"}
    },
    {
        "id": "skill_2",
        "categoryId": "skill_centers",
        "name": {"hi": "हुनर विकास एवं सिलाई-कढ़ाई प्रशिक्षण केंद्र", "en": "Hunar Skill & Sewing Training Center"},
        "contact": "+919876543208",
        "address": {"hi": "पंचायत भवन के पास", "en": "Near Panchayat Bhawan"},
        "details": {"hi": "सिलाई, कटाई, कढ़ाई, ब्यूटी पार्लर व हुनर प्रशिक्षण", "en": "Sewing, Tailoring, Embroidery & Skill Training"},
        "extraInfo": {"hi": "महिलाओं व बालिकाओं के लिए विशेष बैच", "en": "Special batches for women & girls"}
    }
]

LEGAL_PANCHAYAT_DATA = [
    # 1. Tehsil & Court Lawyers
    {
        "id": "lawyer_1",
        "categoryId": "court_lawyers",
        "name": {"hi": "एडवोकेट राजेश कुमार सिंह", "en": "Advocate Rajesh Kumar Singh"},
        "contact": "+919876543209",
        "address": {"hi": "सिविल कोर्ट / तहसील परिसर, चैंबर नं. 14", "en": "Civil Court / Tehsil Compound, Chamber No. 14"},
        "details": {"hi": "दीवानी, फौजदारी व ज़मीन/राजस्व मामलों के वरिष्ठ अधिवक्ता", "en": "Senior Advocate for Civil, Criminal & Revenue Land Cases"},
        "extraInfo": {"hi": "15+ वर्ष का कानूनी अनुभव", "en": "15+ Years Legal Experience"}
    },
    {
        "id": "lawyer_2",
        "categoryId": "court_lawyers",
        "name": {"hi": "एडवोकेट अनवर अली सिद्दीकी", "en": "Advocate Anwar Ali Siddiqui"},
        "contact": "+919876543210",
        "address": {"hi": "कलेक्ट्रेट कचहरी, गेट नं. 2 के पास", "en": "Collectorate Court, Near Gate No. 2"},
        "details": {"hi": "ज़मीन विवाद, दाखिल-खारिज, पैमाइश व कानूनी सलाह", "en": "Land Disputes, Mutation, Demarcation & Legal Advice"},
        "extraInfo": {"hi": "तहसील व जिला अदालत में नियमित वकालत", "en": "Regular Tehsil & District Court Practice"}
    },

    # 2. Panchayat Leaders & Officials
    {
        "id": "panchayat_1",
        "categoryId": "panchayat_officials",
        "name": {"hi": "ग्राम प्रधान प्रतिनिधि (रामू वर्मा)", "en": "Gram Pradhan Representative (Ramu Verma)"},
        "contact": "+919876543211",
        "address": {"hi": "ग्राम सचिवालय / पंचायत भवन", "en": "Gram Secretariat / Panchayat Bhawan"},
        "details": {"hi": "गांव विकास कार्य, आवास योजना, स्वच्छता व सरकारी योजनाएं", "en": "Village Development, Awas Yojana, Sanitation & Schemes"},
        "extraInfo": {"hi": "समय: प्रतिदिन सुबह 9:00 से 12:00 तक", "en": "Timing: Daily 9:00 AM to 12:00 PM"}
    },
    {
        "id": "panchayat_2",
        "categoryId": "panchayat_officials",
        "name": {"hi": "पंचायत सहायक (संजय कुमार मौर्य)", "en": "Panchayat Sahayak (Sanjay Kumar Maurya)"},
        "contact": "+919876543212",
        "address": {"hi": "पंचायत भवन, कंप्यूटर कक्ष", "en": "Panchayat Bhawan, Computer Room"},
        "details": {"hi": "आय-जाति-निवास प्रमाण पत्र, पेंशन व परिवार रजिस्टर नकल", "en": "Caste/Income Certificates, Pension & Family Register"},
        "extraInfo": {"hi": "सोमवार से शनिवार सुबह 10:00 से शाम 4:00", "en": "Mon to Sat 10:00 AM to 4:00 PM"}
    },

    # 3. Respected Village Elders
    {
        "id": "senior_1",
        "categoryId": "senior_citizens",
        "name": {"hi": "सूबेदार (रिटायर्ड) रामप्रसाद यादव", "en": "Subedar (Retd.) Ram Prasad Yadav"},
        "contact": "+919876543213",
        "address": {"hi": "पुराना टोला, गांव के मध्य", "en": "Purana Tola, Village Center"},
        "details": {"hi": "भूतपूर्व सैनिक कल्याण, युवा सेना भर्ती मार्गदर्शन व सुलह-समझौता", "en": "Ex-Servicemen Welfare, Youth Army Recruitment Guide & Mediation"},
        "extraInfo": {"hi": "वरिष्ठ सम्मानित नागरिक व समाजसेवी", "en": "Respected Senior Citizen & Social Guide"}
    },
    {
        "id": "senior_2",
        "categoryId": "senior_citizens",
        "name": {"hi": "पंडित दीनदयाल शर्मा (सेवानिवृत्त शिक्षक)", "en": "Pt. Deendayal Sharma (Retd. Teacher)"},
        "contact": "+919876543214",
        "address": {"hi": "वार्ड नं. 2, मंदिर के पास", "en": "Ward No. 2, Near Temple"},
        "details": {"hi": "ग्राम विवाद निपटारा समिति सदस्य व सामाजिक मार्गदर्शक", "en": "Village Dispute Resolution Member & Cultural Guide"},
        "extraInfo": {"hi": "शिक्षा एवं समाज सुधारक", "en": "Educationist & Social Reformer"}
    }
]


def seed_data(db):
    print("[INFO] Seeding Education records to Firestore...")
    for item in EDUCATION_DATA:
        db.collection("education").document(item["id"]).set(item, merge=True)
        print(f"  [+] /education/{item['id']} saved")

    print("[INFO] Seeding Legal & Panchayat records to Firestore...")
    for item in LEGAL_PANCHAYAT_DATA:
        db.collection("legal_panchayat").document(item["id"]).set(item, merge=True)
        print(f"  [+] /legal_panchayat/{item['id']} saved")

    # Also seed into village subcollections if villages exist
    try:
        villages = db.collection("villages").stream()
        for v in villages:
            v_id = v.id
            print(f"[INFO] Seeding into village: {v_id}...")
            for item in EDUCATION_DATA:
                db.collection("villages").document(v_id).collection("education").document(item["id"]).set(item, merge=True)
            for item in LEGAL_PANCHAYAT_DATA:
                db.collection("villages").document(v_id).collection("legal_panchayat").document(item["id"]).set(item, merge=True)
    except Exception as e:
        print(f"[WARNING] Village seeding skipped: {e}")

    print("[SUCCESS] All records seeded to Firestore successfully!")


def main():
    if not FIREBASE_AVAILABLE:
        print("[ERROR] firebase-admin is not installed. Install via: pip install firebase-admin")
        sys.exit(1)

    try:
        if not firebase_admin._apps:
            firebase_admin.initialize_app()
        db = firestore.client()
        seed_data(db)
    except Exception as e:
        print(f"[ERROR] Could not connect to Firestore: {e}")


if __name__ == "__main__":
    main()
