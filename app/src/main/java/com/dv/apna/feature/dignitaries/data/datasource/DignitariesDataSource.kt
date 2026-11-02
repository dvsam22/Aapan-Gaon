package com.dv.apna.feature.dignitaries.data.datasource

import com.dv.apna.core.firestore.FirestoreDataSource
import com.dv.apna.feature.dignitaries.data.model.DignitaryDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DignitariesDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : FirestoreDataSource(firestore) {

    suspend fun getDignitaries(villageId: String, categoryId: String): List<DignitaryDto> {
        // 1. Try village-specific subcollection
        val villageItems = try {
            getCollection("villages")
                .document(villageId)
                .collection("dignitaries")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()
                .toObjects(DignitaryDto::class.java)
        } catch (e: Exception) {
            emptyList()
        }

        if (villageItems.isNotEmpty()) return villageItems

        // 2. Try global central dignitaries collection
        val centralItems = try {
            getCollection("dignitaries")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()
                .toObjects(DignitaryDto::class.java)
        } catch (e: Exception) {
            emptyList()
        }

        if (centralItems.isNotEmpty()) return centralItems

        // 3. Built-in defaults for instant utility in village
        return getDefaultDignitaries(categoryId, villageId)
    }

    private fun getDefaultDignitaries(categoryId: String, villageId: String): List<DignitaryDto> {
        return when (categoryId) {
            "lawyers" -> listOf(
                DignitaryDto(
                    id = "law_1",
                    name = mapOf("hi" to "एडवोकेट सूर्य प्रकाश सिंह", "en" to "Advocate Surya Prakash Singh"),
                    categoryId = "lawyers",
                    designation = mapOf("hi" to "वरिष्ठ अधिवक्ता (तहसील बार)", "en" to "Senior Advocate (Tehsil Bar)"),
                    contact = "9415123456",
                    whatsapp = "9415123456",
                    location = mapOf("hi" to "निवासी: ग्राम रामपुर", "en" to "Res: Village Rampur"),
                    officeAddress = mapOf("hi" to "चैम्बर नं. 14, तहसील परिसर, फरेन्दा", "en" to "Chamber No. 14, Tehsil Campus"),
                    specialization = mapOf("hi" to "भूमि विवाद, बैनामा (रजिस्ट्री), दाखिल खारिज, विरासत एवं खतौनी संशोधन।", "en" to "Land disputes, Property Registry, Mutation & Revenue cases."),
                    timings = mapOf("hi" to "तहसील: 10 AM - 4 PM | घर पर: 6 PM - 8 PM", "en" to "Tehsil: 10 AM - 4 PM | Home: 6 PM - 8 PM"),
                    villageId = villageId
                ),
                DignitaryDto(
                    id = "law_2",
                    name = mapOf("hi" to "एडवोकेट अनवर अली", "en" to "Advocate Anwar Ali"),
                    categoryId = "lawyers",
                    designation = mapOf("hi" to "अधिवक्ता - दीवानी एवं फौजदारी", "en" to "Advocate - Civil & Criminal"),
                    contact = "9839456789",
                    whatsapp = "9839456789",
                    location = mapOf("hi" to "कस्बा मुख्य चौराहा", "en" to "Main Town Square"),
                    officeAddress = mapOf("hi" to "सिविल कोर्ट परिसर, कलेक्ट्रेट रोड", "en" to "Civil Court Campus, Collectorate Road"),
                    specialization = mapOf("hi" to "पारिवारिक विवाद, बेल (जमानत), सिविल अपील व शपथ पत्र प्रमाणन।", "en" to "Family disputes, Bail, Civil appeals & Affidavits."),
                    timings = mapOf("hi" to "सोमवार से शनिवार: 10:30 AM - 04:30 PM", "en" to "Mon to Sat: 10:30 AM - 04:30 PM"),
                    villageId = villageId
                )
            )
            "representatives" -> listOf(
                DignitaryDto(
                    id = "rep_1",
                    name = mapOf("hi" to "श्री रामनरेश यादव", "en" to "Shri Ram Naresh Yadav"),
                    categoryId = "representatives",
                    designation = mapOf("hi" to "ग्राम प्रधान (Gram Pradhan)", "en" to "Village Head (Gram Pradhan)"),
                    contact = "9451122334",
                    whatsapp = "9451122334",
                    location = mapOf("hi" to "ग्राम पंचायत भवन", "en" to "Gram Panchayat Bhawan"),
                    officeAddress = mapOf("hi" to "पंचायत सचिवालय, ग्राम सभा", "en" to "Panchayat Secretariat"),
                    specialization = mapOf("hi" to "प्रधानमंत्री आवास, शौचालय योजना, नाली व खड़ंजा निर्माण, मनरेगा जॉब कार्ड।", "en" to "PM Awas Yojana, Sanitation, Drainage, MNREGA Job Cards."),
                    timings = mapOf("hi" to "प्रतिदिन: प्रातः 8:00 AM से 11:00 AM", "en" to "Daily: 8:00 AM to 11:00 AM"),
                    villageId = villageId
                ),
                DignitaryDto(
                    id = "rep_2",
                    name = mapOf("hi" to "श्री विनीत कुमार श्रीवास्तव", "en" to "Shri Vineet Srivastava"),
                    categoryId = "representatives",
                    designation = mapOf("hi" to "ग्राम पंचायत अधिकारी / सचिव (VDO)", "en" to "Gram Panchayat Officer (Secretary)"),
                    contact = "9936554433",
                    location = mapOf("hi" to "विकास खंड कार्यालय", "en" to "Block Development Office"),
                    officeAddress = mapOf("hi" to "पंचायत सचिवालय (बुधवार व शनिवार)", "en" to "Panchayat Secretariat (Wed & Sat)"),
                    specialization = mapOf("hi" to "परिवार रजिस्टर नकल, जन्म/मृत्यु प्रमाण पत्र सत्यापन, पेंशन स्वीकृति।", "en" to "Parivar Register copy, Birth/Death cert verification, Pensions."),
                    timings = mapOf("hi" to "बुधवार व शनिवार: 10:00 AM से 2:00 PM", "en" to "Wed & Sat: 10:00 AM to 2:00 PM"),
                    villageId = villageId
                ),
                DignitaryDto(
                    id = "rep_3",
                    name = mapOf("hi" to "श्री रामप्रसाद चौरसिया", "en" to "Shri Ram Prasad Chaurasia"),
                    categoryId = "representatives",
                    designation = mapOf("hi" to "उचित दर विक्रेता / कोटेदार", "en" to "Fair Price Shop / Kotedar"),
                    contact = "9838776655",
                    location = mapOf("hi" to "पश्चिम टोला, राशन गोदाम", "en" to "West Tola, Ration Warehouse"),
                    officeAddress = mapOf("hi" to "सरकारी राशन दुकान", "en" to "Govt. Ration Store"),
                    specialization = mapOf("hi" to "पात्र गृहस्थी व अंत्योदय राशन वितरण, ई-पॉस मशीन द्वारा निशुल्क राशन।", "en" to "NFSA Ration distribution via e-POS machine."),
                    timings = mapOf("hi" to "वितरण दिवस: 05 से 20 तारीख, सुबह 8 - 12", "en" to "Distribution: 5th to 20th, 8 AM - 12 PM"),
                    villageId = villageId
                )
            )
            "elders" -> listOf(
                DignitaryDto(
                    id = "eld_1",
                    name = mapOf("hi" to "कैप्टन (मानद) सूबेदार वी. पी. मिश्रा (रिटायर्ड)", "en" to "Subedar V. P. Mishra (Retd. Army)"),
                    categoryId = "elders",
                    designation = mapOf("hi" to "पूर्व सैनिक / रक्षा मार्गदर्शक", "en" to "Ex-Servicemen / Defence Mentor"),
                    contact = "9415887766",
                    whatsapp = "9415887766",
                    location = mapOf("hi" to "पूर्व सैनिक कॉलोनी", "en" to "Ex-Servicemen Colony"),
                    officeAddress = mapOf("hi" to "गाँव मुख्य चौपाल", "en" to "Village Chaupal"),
                    specialization = mapOf("hi" to "सेना, अर्धसैनिक बल (CRPF/BSF/ITBP) व पुलिस भर्ती हेतु युवाओं को निशुल्क मार्गदर्शन।", "en" to "Free physical & recruitment guidance for Army, CRPF & Police aspirants."),
                    timings = mapOf("hi" to "सुबह 6-8 बजे (ग्राउंड पर) व शाम को 5-7 बजे", "en" to "Morning 6-8 AM (Ground) & Evening 5-7 PM"),
                    villageId = villageId
                ),
                DignitaryDto(
                    id = "eld_2",
                    name = mapOf("hi" to "पंडित दीनदयाल उपाध्याय (रिटायर्ड हेडमास्टर)", "en" to "Pt. Deendayal Upadhyay (Retd. Headmaster)"),
                    categoryId = "elders",
                    designation = mapOf("hi" to "वरिष्ठ नागरिक एवं सामाजिक परामर्शदाता", "en" to "Senior Citizen & Community Counselor"),
                    contact = "9794112233",
                    location = mapOf("hi" to "मध्य टोला, बरगद चौराहा", "en" to "Bargad Chauraha, Middle Tola"),
                    officeAddress = mapOf("hi" to "आवास", "en" to "Residence"),
                    specialization = mapOf("hi" to "गाँव के पारिवारिक व सामाजिक विवादों का आपसी सुलह-समझौता, शिक्षा संस्कार।", "en" to "Family dispute mediation, social harmony & educational guidance."),
                    timings = mapOf("hi" to "दोपहर 03:00 PM से शाम 07:00 PM", "en" to "03:00 PM to 07:00 PM"),
                    villageId = villageId
                )
            )
            "documents" -> listOf(
                DignitaryDto(
                    id = "doc_1",
                    name = mapOf("hi" to "मुंशी कन्हैया लाल (दस्तावेज़ नवीस)", "en" to "Munshi Kanhaiya Lal (Deed Writer)"),
                    categoryId = "documents",
                    designation = mapOf("hi" to "लाइसेंसशुदा दस्तावेज़ लेखक / कातिब", "en" to "Licensed Deed Writer"),
                    contact = "9838990011",
                    whatsapp = "9838990011",
                    location = mapOf("hi" to "तहसील गेट के सामने", "en" to "Opp. Tehsil Gate"),
                    officeAddress = mapOf("hi" to "बस्ता नं. 08, सब-रजिस्ट्रार कार्यालय परिसर", "en" to "Sub-Registrar Office Campus"),
                    specialization = mapOf("hi" to "बैनामा (सेल डीड), दानपत्र, वसीयतनामा, किरायानामा, शपथ पत्र (हलफनामा) लेखन।", "en" to "Sale Deed, Gift Deed, Will, Rent Agreement & Affidavits."),
                    timings = mapOf("hi" to "सोमवार से शनिवार: 10:00 AM से 5:00 PM", "en" to "Mon to Sat: 10:00 AM to 5:00 PM"),
                    villageId = villageId
                ),
                DignitaryDto(
                    id = "doc_2",
                    name = mapOf("hi" to "अपना जन सेवा केंद्र (CSC VLE)", "en" to "Apna Jan Seva Kendra (CSC VLE)"),
                    categoryId = "documents",
                    designation = mapOf("hi" to "ई-डिस्ट्रिक्ट एवं सीएससी संचालक", "en" to "e-District & CSC Operator"),
                    contact = "9455332211",
                    whatsapp = "9455332211",
                    location = mapOf("hi" to "मुख्य बाजार, स्टेट बैंक के बगल में", "en" to "Main Market, Beside SBI"),
                    officeAddress = mapOf("hi" to "जन सेवा केंद्र दुकान", "en" to "CSC Center Shop"),
                    specialization = mapOf("hi" to "आय, जाति, निवास प्रमाण पत्र, खतौनी नकल, पैन कार्ड, आधार सुधार व आयुष्मान कार्ड।", "en" to "Income, Caste, Domicile certificates, Khatauni, PAN & Ayushman Card."),
                    timings = mapOf("hi" to "प्रतिदिन: 8:30 AM से 7:30 PM", "en" to "Daily: 8:30 AM to 7:30 PM"),
                    villageId = villageId
                )
            )
            "finance" -> listOf(
                DignitaryDto(
                    id = "fin_1",
                    name = mapOf("hi" to "सुरेश चंद्र अग्रहरि (बैंक मित्र)", "en" to "Suresh Chandra Agrahari (Bank Mitr)"),
                    categoryId = "finance",
                    designation = mapOf("hi" to "SBI ग्राहक सेवा केंद्र (CSP)", "en" to "SBI Customer Service Point (CSP)"),
                    contact = "9415998877",
                    whatsapp = "9415998877",
                    location = mapOf("hi" to "ग्राम पंचायत सचिवालय के निकट", "en" to "Near Panchayat Secretariat"),
                    officeAddress = mapOf("hi" to "एसबीआई सीएसपी सेंटर", "en" to "SBI CSP Center"),
                    specialization = mapOf("hi" to "आधार से पैसा निकासी (AEPS), मिनी स्टेटमेंट, खाता खोलना, किसान सम्मान निधि चेक।", "en" to "Aadhaar Cash Withdrawal (AEPS), Account Opening & PM Kisan status."),
                    timings = mapOf("hi" to "सुबह 9:00 AM से शाम 6:00 PM", "en" to "9:00 AM to 6:00 PM"),
                    villageId = villageId
                ),
                DignitaryDto(
                    id = "fin_2",
                    name = mapOf("hi" to "महेंद्र प्रताप वर्मा (LIC सलाहकार)", "en" to "Mahendra Pratap Verma (LIC Advisor)"),
                    categoryId = "finance",
                    designation = mapOf("hi" to "वरिष्ठ बीमा व पेंशन सलाहकार", "en" to "Senior Insurance & Pension Advisor"),
                    contact = "9838445566",
                    whatsapp = "9838445566",
                    location = mapOf("hi" to "कस्बा रोड", "en" to "Town Road"),
                    officeAddress = mapOf("hi" to "एलआईसी कार्यालय शाखा के पास", "en" to "Near LIC Branch"),
                    specialization = mapOf("hi" to "जीवन बीमा (LIC), सुकन्या समृद्धि योजना, प्रधानमंत्री जीवन ज्योति बीमा, पेंशन प्लान।", "en" to "Life Insurance, Sukanya Samriddhi, PMJJBY & Pension plans."),
                    timings = mapOf("hi" to "प्रतिदिन उपलब्ध (फोन पर संपर्क करें)", "en" to "Available Daily (Contact on phone)"),
                    villageId = villageId
                )
            )
            else -> emptyList()
        }
    }
}
