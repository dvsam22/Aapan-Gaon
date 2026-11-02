package com.dv.apna.feature.education.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.education.data.model.EducationDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EducationDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getEducationItems(villageId: String, categoryId: String): List<EducationDto> {
        // 1. Try village-specific subcollection
        val villageItems = try {
            getCollection("villages")
                .document(villageId)
                .collection("education")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()
                .toObjects(EducationDto::class.java)
        } catch (e: Exception) {
            emptyList()
        }

        if (villageItems.isNotEmpty()) return villageItems

        // 2. Try global central education collection
        val centralItems = try {
            getCollection("education")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()
                .toObjects(EducationDto::class.java)
        } catch (e: Exception) {
            emptyList()
        }

        if (centralItems.isNotEmpty()) return centralItems

        // 3. Built-in defaults for instant demonstration and village utility
        return getDefaultEducationItems(categoryId, villageId)
    }

    private fun getDefaultEducationItems(categoryId: String, villageId: String): List<EducationDto> {
        return when (categoryId) {
            "schools" -> listOf(
                EducationDto(
                    id = "sch_1",
                    name = mapOf("hi" to "राजकीय उच्च प्राथमिक विद्यालय", "en" to "Govt. Upper Primary School"),
                    categoryId = "schools",
                    roleOrType = mapOf("hi" to "कक्षा 1 से 8 (UP Board)", "en" to "Class 1 to 8 (UP Board)"),
                    contact = "9838012345",
                    location = mapOf("hi" to "ग्राम पंचायत भवन के पास", "en" to "Near Gram Panchayat Bhawan"),
                    description = mapOf("hi" to "मुफ्त मिड-डे मील, ड्रेस, किताबें व खेल मैदान उपलब्ध।", "en" to "Free Mid-Day Meal, Uniforms, Books & Playground."),
                    extraInfo = mapOf("hi" to "समय: प्रातः 09:00 AM से 03:00 PM", "en" to "Timing: 09:00 AM to 03:00 PM"),
                    villageId = villageId
                ),
                EducationDto(
                    id = "sch_2",
                    name = mapOf("hi" to "सरस्वती विद्या मंदिर इंटर कॉलेज", "en" to "Saraswati Vidya Mandir Inter College"),
                    categoryId = "schools",
                    roleOrType = mapOf("hi" to "कक्षा 6 से 12 (विज्ञान/कला)", "en" to "Class 6 to 12 (Science/Arts)"),
                    contact = "9450098765",
                    location = mapOf("hi" to "कस्बा मुख्य मार्ग, बस स्टैंड", "en" to "Main Town Road, Bus Stand"),
                    description = mapOf("hi" to "अनुभवी शिक्षक, कंप्यूटर लैब, भौतिक-रसायन प्रयोगशाला।", "en" to "Experienced faculty, Computer Lab & Science Labs."),
                    extraInfo = mapOf("hi" to "बस सुविधा उपलब्ध", "en" to "Bus Facility Available"),
                    villageId = villageId
                )
            )
            "teachers" -> listOf(
                EducationDto(
                    id = "tch_1",
                    name = mapOf("hi" to "अमित कुमार मौर्या (M.Sc, B.Ed)", "en" to "Amit Kumar Maurya (M.Sc, B.Ed)"),
                    categoryId = "teachers",
                    roleOrType = mapOf("hi" to "गणित व विज्ञान विशेषज्ञ (कक्षा 8-12)", "en" to "Maths & Science Specialist (Class 8-12)"),
                    contact = "9839123456",
                    whatsapp = "9839123456",
                    location = mapOf("hi" to "उत्तर टोला, शिव मंदिर के पास", "en" to "North Tola, Near Shiv Mandir"),
                    description = mapOf("hi" to "UP Board और CBSE के विद्यार्थियों के लिए विशेष बैच। कमजोर छात्रों पर विशेष ध्यान।", "en" to "Special batches for UP Board & CBSE. Focus on weak students."),
                    extraInfo = mapOf("hi" to "समय: सुबह 6-8 AM, शाम 4-7 PM", "en" to "Timing: 6-8 AM, 4-7 PM"),
                    villageId = villageId
                ),
                EducationDto(
                    id = "tch_2",
                    name = mapOf("hi" to "राजेश त्रिपाठी (M.A. English)", "en" to "Rajesh Tripathi (M.A. English)"),
                    categoryId = "teachers",
                    roleOrType = mapOf("hi" to "अंग्रेजी ग्रामर व स्पोकन ट्यूटर", "en" to "English Grammar & Spoken Tutor"),
                    contact = "9415678901",
                    whatsapp = "9415678901",
                    location = mapOf("hi" to "बाजार रोड, पानी टंकी के सामने", "en" to "Bazar Road, Opp. Water Tank"),
                    description = mapOf("hi" to "हाई स्कूल व इंटरमीडिएट अंग्रेजी ग्रामर, ट्रांसलेशन व राइटिंग स्किल।", "en" to "High School & Intermediate English Grammar & Writing skills."),
                    extraInfo = mapOf("hi" to "होम ट्यूशन भी उपलब्ध", "en" to "Home tuition available"),
                    villageId = villageId
                )
            )
            "notes" -> listOf(
                EducationDto(
                    id = "nts_1",
                    name = mapOf("hi" to "कक्षा 10वीं - गणित व विज्ञान फॉर्मूला नोट्स", "en" to "Class 10th - Maths & Science Formulas"),
                    categoryId = "notes",
                    roleOrType = mapOf("hi" to "हस्तलिखित नोट्स (Handwritten PDF)", "en" to "Handwritten Notes (PDF)"),
                    contact = "9838012345",
                    location = mapOf("hi" to "डिजिटल लाइब्रेरी", "en" to "Digital Library"),
                    description = mapOf("hi" to "UP Board 10वीं परीक्षा के सभी महत्वपूर्ण सूत्र, प्रमेय और 5 साल के हल प्रश्न पत्र।", "en" to "Formulas, theorems and 5-year solved board questions."),
                    extraInfo = mapOf("hi" to "मुफ्त डाउनलोड / अध्ययन हेतु", "en" to "Free Download / Study"),
                    fileUrl = "https://upmsp.edu.in",
                    villageId = villageId
                ),
                EducationDto(
                    id = "nts_2",
                    name = mapOf("hi" to "कक्षा 12वीं - भौतिक विज्ञान व रसायन विज्ञान नोट्स", "en" to "Class 12th - Physics & Chemistry Notes"),
                    categoryId = "notes",
                    roleOrType = mapOf("hi" to "अध्यायवार संक्षिप्त नोट्स", "en" to "Chapter-wise Short Notes"),
                    contact = "9838012345",
                    location = mapOf("hi" to "डिजिटल लाइब्रेरी", "en" to "Digital Library"),
                    description = mapOf("hi" to "बोर्ड परीक्षा 2025-26 के नए सिलेबस अनुसार तैयार किए गए त्वरित रिवीजन नोट्स।", "en" to "Quick revision notes based on latest 2025-26 syllabus."),
                    extraInfo = mapOf("hi" to "मुफ्त डाउनलोड / अध्ययन हेतु", "en" to "Free Download / Study"),
                    fileUrl = "https://upmsp.edu.in",
                    villageId = villageId
                )
            )
            "scholarships" -> listOf(
                EducationDto(
                    id = "schlp_1",
                    name = mapOf("hi" to "जवाहर नवोदय विद्यालय (JNV) प्रवेश परीक्षा", "en" to "Jawaharlal Navodaya Vidyalaya Entrance"),
                    categoryId = "scholarships",
                    roleOrType = mapOf("hi" to "कक्षा 6 एवं 9 प्रवेश परीक्षा", "en" to "Class 6 & 9 Admission Test"),
                    contact = "18001805522",
                    location = mapOf("hi" to "नवोदय विद्यालय समिति", "en" to "Navodaya Vidyalaya Samiti"),
                    description = mapOf("hi" to "ग्रामीण क्षेत्र के प्रतिभावान विद्यार्थियों के लिए पूर्णतः निःशुल्क आवासीय शिक्षा।", "en" to "Free residential education for talented rural students."),
                    extraInfo = mapOf("hi" to "आवेदन ऑनलाइन जारी", "en" to "Online Application Open"),
                    fileUrl = "https://navodaya.gov.in",
                    villageId = villageId
                ),
                EducationDto(
                    id = "schlp_2",
                    name = mapOf("hi" to "उत्तर प्रदेश छात्रवृत्ति एवं शुल्क प्रतिपूर्ति योजना", "en" to "UP Post-Matric & Pre-Matric Scholarship"),
                    categoryId = "scholarships",
                    roleOrType = mapOf("hi" to "कक्षा 9, 10, 11, 12 व उच्च शिक्षा", "en" to "Class 9, 10, 11, 12 & Higher Ed"),
                    contact = "05222209270",
                    location = mapOf("hi" to "समाज कल्याण विभाग, उत्तर प्रदेश", "en" to "Social Welfare Dept, UP"),
                    description = mapOf("hi" to "SC, ST, OBC, सामान्य व अल्पसंख्यक वर्ग के विद्यार्थियों के लिए सरकारी छात्रवृत्ति।", "en" to "Government scholarship for SC, ST, OBC, Gen & Minority students."),
                    extraInfo = mapOf("hi" to "आधार बैंक सीडिंग अनिवार्य", "en" to "Aadhaar Bank Seeding Required"),
                    fileUrl = "https://scholarship.up.gov.in",
                    villageId = villageId
                )
            )
            "stationery" -> listOf(
                EducationDto(
                    id = "stn_1",
                    name = mapOf("hi" to "श्री कृष्णा बुक डिपो एवं स्टेशनर्स", "en" to "Shri Krishna Book Depot & Stationers"),
                    categoryId = "stationery",
                    roleOrType = mapOf("hi" to "स्कूल किताबें, कॉपियां व बैग", "en" to "School Books, Notebooks & Bags"),
                    contact = "9792011223",
                    whatsapp = "9792011223",
                    location = mapOf("hi" to "चौराहा मार्केट, पोस्ट ऑफिस के सामने", "en" to "Chauraha Market, Opp. Post Office"),
                    description = mapOf("hi" to "कक्षा 1 से 12 तक NCERT व UP Board की किताबें, पेन, ज्योमेट्री बॉक्स व बैग।", "en" to "NCERT & UP Board books from class 1 to 12, pens, geometry boxes."),
                    extraInfo = mapOf("hi" to "उचित मूल्य की दुकान", "en" to "Fair Price Shop"),
                    villageId = villageId
                ),
                EducationDto(
                    id = "stn_2",
                    name = mapOf("hi" to "डिजिटल सेवा कैफे एवं फोटोकॉपी", "en" to "Digital Seva Cafe & Photocopy"),
                    categoryId = "stationery",
                    roleOrType = mapOf("hi" to "ऑनलाइन फॉर्म, प्रिंट व लेमिनेशन", "en" to "Online Forms, Print & Lamination"),
                    contact = "9935123477",
                    whatsapp = "9935123477",
                    location = mapOf("hi" to "तहसील तिराहा, मुख्य मार्ग", "en" to "Tehsil Tiraha, Main Road"),
                    description = mapOf("hi" to "स्कॉलरशिप फॉर्म, बोर्ड एडमिट कार्ड प्रिंट, फोटोकॉपी, पासपोर्ट साइज फोटो।", "en" to "Scholarship form filling, admit card prints, photocopies."),
                    extraInfo = mapOf("hi" to "सुबह 8 बजे से रात 8 बजे तक", "en" to "Open 8 AM to 8 PM"),
                    villageId = villageId
                )
            )
            else -> emptyList()
        }
    }
}
