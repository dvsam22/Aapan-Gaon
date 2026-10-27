package com.dv.apna.core.notification.local

import java.util.Calendar

object LocalNotificationSchedule {

    const val MORNING_HOUR = 8
    const val MORNING_MINUTE = 30

    const val EVENING_HOUR = 19
    const val EVENING_MINUTE = 0

    fun getNotificationFor(dayOfWeek: Int, slot: LocalNotificationSlot): LocalNotificationItem {
        return when (dayOfWeek) {
            Calendar.MONDAY -> {
                if (slot == LocalNotificationSlot.MORNING) {
                    LocalNotificationItem(
                        id = 101,
                        title = "🌾 सोमवार के ताज़ा मंडी भाव जारी!",
                        body = "आज महाराजगंज मंडी में धान, गेहूं और सब्जियों के नए रेट चेक करें।",
                        targetType = "mandi"
                    )
                } else {
                    LocalNotificationItem(
                        id = 102,
                        title = "घर में शादी या कोई कार्यक्रम है? टेंट ढूंढ रहे हैं? 🎪",
                        body = "टेंट-पंडाल, स्टेज और लाइटिंग वाले का नंबर ढूंढ रहे हैं? अपने गांव के टॉप टेंट वालों की लिस्ट यहां देखें।",
                        targetType = "family_tent"
                    )
                }
            }
            Calendar.TUESDAY -> {
                if (slot == LocalNotificationSlot.MORNING) {
                    LocalNotificationItem(
                        id = 201,
                        title = "घर का काम रुका है? लेबर या राजमिस्त्री चाहिए? 🧱",
                        body = "परेशान न हों! अपने गांव और नजदीकी इलाके के राजमिस्त्री और मजदूरों से तुरंत बात करें।",
                        targetType = "labour"
                    )
                } else {
                    LocalNotificationItem(
                        id = 202,
                        title = "शादी या भोज में लज़ीज़ खाना? बेस्ट हलवाई ढूंढें 🍲",
                        body = "तिलक, शादी या मुंडन के लिए नजदीकी कैटरर्स और हलवाईयों से सीधा संपर्क करें।",
                        targetType = "family_catering"
                    )
                }
            }
            Calendar.WEDNESDAY -> {
                if (slot == LocalNotificationSlot.MORNING) {
                    LocalNotificationItem(
                        id = 301,
                        title = "📰 आपके क्षेत्र और महराजगंज की ताज़ा खबरें",
                        body = "जिले और ब्लॉक की आज की बड़ी हलचल व सरकारी योजनाओं की जानकारी अभी पढ़ें।",
                        targetType = "news"
                    )
                } else {
                    LocalNotificationItem(
                        id = 302,
                        title = "सस्ता सीमेंट, बालू, सरिया या ईंट मंगवाना है? 🚚",
                        body = "नजदीकी ईंट-भट्ठों और बिल्डिंग मटेरियल दुकानों के फोन नंबर व रेट्स सीधे ऐप में देखें।",
                        targetType = "construction"
                    )
                }
            }
            Calendar.THURSDAY -> {
                if (slot == LocalNotificationSlot.MORNING) {
                    LocalNotificationItem(
                        id = 401,
                        title = "माल ढुलाई या खेत जुताई के लिए ट्रैक्टर/पिकअप चाहिए? 🚜",
                        body = "किराए के ट्रैक्टर, पिकअप और जेसीबी वालों को सीधे घर बैठे फोन लगाएं।",
                        targetType = "transport"
                    )
                } else {
                    LocalNotificationItem(
                        id = 402,
                        title = "कार्यक्रम के लिए डीजे, साउंड या फोटोग्राफर बुक करना है? 🎵📸",
                        body = "शादी और पार्टियों के लिए इलाके के बेस्ट डीजे और वीडियो वालों के नंबर पाएं।",
                        targetType = "family_dj"
                    )
                }
            }
            Calendar.FRIDAY -> {
                if (slot == LocalNotificationSlot.MORNING) {
                    LocalNotificationItem(
                        id = 501,
                        title = "नजदीकी डॉक्टर या मेडिकल स्टोर का नंबर चाहिए? 🏥💊",
                        body = "इमरजेंसी या बीमारी में नजदीकी डॉक्टर, अस्पताल और दवा दुकानों की लिस्ट हमेशा पास रखें।",
                        targetType = "health"
                    )
                } else {
                    LocalNotificationItem(
                        id = 502,
                        title = "शादी या पार्टी के लिए मैरिज हॉल / लॉन ढूंढ रहे हैं? 🏛️",
                        body = "अपने इलाके के खूबसूरत मैरिज हॉल, लॉन और धर्मशालाओं की लिस्ट व फोन नंबर देखें।",
                        targetType = "family_halls"
                    )
                }
            }
            Calendar.SATURDAY -> {
                if (slot == LocalNotificationSlot.MORNING) {
                    LocalNotificationItem(
                        id = 601,
                        title = "घर में पंखा, वायरिंग, नल या लकड़ी का काम है? 💡🪚",
                        body = "गांव के भरोसेमंद इलेक्ट्रीशियन, प्लम्बर, बढ़ई और पेंटर को एक कॉल में घर बुलाएं।",
                        targetType = "labour"
                    )
                } else {
                    LocalNotificationItem(
                        id = 602,
                        title = "क्या आप भी गांव में कोई दुकान या काम करते हैं? 🏪",
                        body = "अपना व्यापार, गाड़ी या मिस्त्री का नंबर 'अपना गांव' ऐप में फ्री में जोड़ें और ग्राहक बढ़ाएं!",
                        targetType = "add_business"
                    )
                }
            }
            Calendar.SUNDAY -> {
                if (slot == LocalNotificationSlot.MORNING) {
                    LocalNotificationItem(
                        id = 701,
                        title = "फसल का सही दाम चाहिए? लोकल खरीदारों से जुड़ें 🌾💰",
                        body = "आसपास के गल्ला व्यापारियों और खरीदारों के सीधे नंबर देखें और सही रेट पर फसल बेचें।",
                        targetType = "mandi_buyers"
                    )
                } else {
                    LocalNotificationItem(
                        id = 702,
                        title = "इस हफ्ते आपके इलाके में क्या हुआ? बड़ी खबरें 🗞️",
                        body = "महराजगंज और गांव-देहात की हफ्ते भर की प्रमुख खबरें एक साथ पढ़ें।",
                        targetType = "news"
                    )
                }
            }
            else -> {
                LocalNotificationItem(
                    id = 999,
                    title = "गांव की हर नई जानकारी और सुविधाएं 🌾",
                    body = "आज के मंडी भाव, खबरें और गांव के कारीगरों की सूची देखने के लिए टैप करें।",
                    targetType = "home"
                )
            }
        }
    }

    /**
     * Determines current slot based on hour of day.
     * Before 14:00 (2 PM) is considered morning slot, otherwise evening slot.
     */
    fun getCurrentSlot(calendar: Calendar = Calendar.getInstance()): LocalNotificationSlot {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return if (hour < 14) LocalNotificationSlot.MORNING else LocalNotificationSlot.EVENING
    }
}
