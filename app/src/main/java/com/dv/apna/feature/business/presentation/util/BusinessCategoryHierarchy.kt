package com.dv.apna.feature.business.presentation.util

object BusinessCategoryHierarchy {
    const val OTHER_CATEGORY = "अन्य दुकान / सेवा (Other Shop / Service)"

    val HIERARCHY: Map<String, List<String>> = linkedMapOf(
        "🌾 किसान व मंडी (Farmers & Mandi)" to listOf(
            "फसल व सब्जी खरीदार (Mandi Buyer)"
        ),
        "🩺 स्वास्थ्य व डॉक्टर (Health & Medical)" to listOf(
            "डॉक्टर व क्लिनिक (Doctor / Clinic)",
            "मेडिकल स्टोर (Pharmacy / Medical)",
            "अस्पताल (Hospital)",
            "एम्बुलेंस सेवा (Ambulance)"
        ),
        "🏗️ मकान व निर्माण (Building & Construction)" to listOf(
            "ईंट भट्ठा सप्लायर (Bricks Supplier)",
            "सीमेंट, बालू व मटेरियल (Building Material)",
            "हार्डवेयर स्टोर (Hardware Shop)",
            "लिन्टर मशीन (Linter Machine)",
            "सेंटरिंग व शटरिंग (Shuttering)"
        ),
        "🔨 मिस्त्री व लेबर (Craftsmen & Labour)" to listOf(
            "राजमिस्त्री (Rajmistri / Mason)",
            "बिजली मिस्त्री (Electrician)",
            "प्लंबर (Plumber)",
            "बढ़ई (Carpenter)",
            "पेंटर (Painter)",
            "मजदूर / लेबर (Labour)"
        ),
        "🚜 वाहन व ट्रैक्टर किराया (Transport & Rentals)" to listOf(
            "ट्रैक्टर व ट्रॉली किराया (Tractor Rental)",
            "पिकअप व लोडर वाहन (Pickup / Loader)",
            "जेसीबी मशीन (JCB Machine)",
            "कार व टैक्सी किराया (Car / Taxi)"
        ),
        "🎪 शादी व पारिवारिक कार्यक्रम (Family Function)" to listOf(
            "टेंट हाउस व सजावट (Tent House)",
            "हलवाई व कैटरिंग (Halwai & Catering)",
            "फोटो व वीडियोग्राफी (Photography)",
            "डीजे व बैंड बाजा (DJ Sound & Band)",
            "मैरिज हॉल व वाटिका (Marriage Hall)"
        ),
        "📚 स्कूल, कोचिंग व शिक्षा (Education & Schools)" to listOf(
            "प्राइवेट स्कूल व कॉन्वेंट (School / Convent)",
            "कोचिंग व ट्यूशन सेंटर (Coaching Center)",
            "शिक्षक व होम ट्यूटर (Home Tutor / Teacher)",
            "कंप्यूटर व स्किल सेंटर (Skill / Computer Center)"
        ),
        "⚖️ वकील व ग्राम पंचायत (Legal & Panchayat)" to listOf(
            "तहसील व कचहरी के वकील (Advocate / Lawyer)",
            "दस्तावेज़ लेखक व नोटरी मुंशी (Document Writer / Munshi)",
            "ग्राम प्रधान व पंचायत प्रतिनिधि (Panchayat Representative)",
            "वरिष्ठ व सम्मानित नागरिक (Respected Senior Citizen)"
        ),
        OTHER_CATEGORY to emptyList()
    )

    val mainCategories: List<String> = HIERARCHY.keys.toList()

    fun getSubcategories(mainCategory: String): List<String> {
        return HIERARCHY[mainCategory] ?: emptyList()
    }

    fun hasSubcategories(mainCategory: String): Boolean {
        return (HIERARCHY[mainCategory]?.isNotEmpty() == true)
    }
}
