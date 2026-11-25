package com.dv.apna.feature.kya_banega.data.datasource

import com.dv.apna.feature.kya_banega.domain.model.*

object CateringDatabase {

    private val rawDishes: List<CateringDish> = listOf(
        // ==================== 1. PANEER & VEGETABLES ====================
        CateringDish(
            id = "shahi_paneer",
            nameHindi = "शाही पनीर",
            englishName = "Shahi Paneer",
            category = DishCategory.PANEER_SABZI,
            description = "काजू, मलाई व टमाटर की रिच मखमली ग्रेवी में पनीर",
            descriptionEnglish = "Soft paneer simmered in rich creamy tomato and cashew gravy",
            cookedServingPerPerson = 150f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("पनीर (Paneer)", IngredientCategory.DAIRY, 75f, "g", 380f),
                RawIngredientRatio("प्याज़ (Onion)", IngredientCategory.SABZI_MANDI, 45f, "g", 30f),
                RawIngredientRatio("टमाटर (Tomato)", IngredientCategory.SABZI_MANDI, 55f, "g", 35f),
                RawIngredientRatio("ताज़ा मलाई / क्रीम", IngredientCategory.DAIRY, 15f, "g", 280f),
                RawIngredientRatio("काजू व मगज़", IngredientCategory.MASALA_DRY, 6f, "g", 850f),
                RawIngredientRatio("रिफाइंड तेल / घी", IngredientCategory.KIRANA, 18f, "ml", 140f),
                RawIngredientRatio("गरम मसाला व कस्तूरी मेथी", IngredientCategory.MASALA_DRY, 8f, "g", 350f)
            ),
            chefTips = "पनीर को बहुत ज़्यादा न तलें, हल्का गुनगुने पानी में रखने से पनीर एकदम नरम रहता है।"
        ),
        CateringDish(
            id = "matar_paneer",
            nameHindi = "मटर पनीर (तरीदार)",
            englishName = "Matar Paneer",
            category = DishCategory.PANEER_SABZI,
            description = "पारंपरिक उत्तर भारतीय तरीदार मटर पनीर",
            descriptionEnglish = "Classic North Indian curry with paneer cubes and green peas",
            cookedServingPerPerson = 150f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("पनीर (Paneer)", IngredientCategory.DAIRY, 60f, "g", 380f),
                RawIngredientRatio("हरी मटर (Green Peas)", IngredientCategory.SABZI_MANDI, 45f, "g", 50f),
                RawIngredientRatio("प्याज़ (Onion)", IngredientCategory.SABZI_MANDI, 40f, "g", 30f),
                RawIngredientRatio("टमाटर (Tomato)", IngredientCategory.SABZI_MANDI, 50f, "g", 35f),
                RawIngredientRatio("कुकिंग तेल", IngredientCategory.KIRANA, 15f, "ml", 140f),
                RawIngredientRatio("अदरक, लहसुन व मसाले", IngredientCategory.MASALA_DRY, 10f, "g", 250f)
            ),
            chefTips = "मटर को ग्रेवी में डालने से पहले 2 मिनट भाप में पका लें।"
        ),
        CateringDish(
            id = "aloo_gobhi",
            nameHindi = "हलवाई स्टाइल आलू गोभी",
            englishName = "Aloo Gobhi",
            category = DishCategory.PANEER_SABZI,
            description = "शादी-ब्याह व भंडारे वाली भुनी हुई लटपटी आलू गोभी",
            descriptionEnglish = "Halwai-style roasted cauliflower and spiced potatoes",
            cookedServingPerPerson = 140f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("फूलगोभी (Cauliflower)", IngredientCategory.SABZI_MANDI, 80f, "g", 35f),
                RawIngredientRatio("आलू (Potato)", IngredientCategory.SABZI_MANDI, 60f, "g", 25f),
                RawIngredientRatio("टमाटर (Tomato)", IngredientCategory.SABZI_MANDI, 30f, "g", 35f),
                RawIngredientRatio("सरसों / रिफाइंड तेल", IngredientCategory.KIRANA, 16f, "ml", 140f),
                RawIngredientRatio("अदरक, हरी मिर्च व मसाले", IngredientCategory.MASALA_DRY, 10f, "g", 220f)
            ),
            chefTips = "गोभी के बड़े टुकड़े काटें ताकि वो भूनते समय टूटे नहीं।"
        ),
        CateringDish(
            id = "mix_veg",
            nameHindi = "मिक्स वेज तरीदार",
            englishName = "Mix Veg Curry",
            category = DishCategory.PANEER_SABZI,
            description = "गाजर, बीन्स, गोभी, मटर व आलू की स्वादिष्ट मिली-जुली सब्ज़ी",
            descriptionEnglish = "Mixed vegetable curry with carrots, beans, peas and potatoes",
            cookedServingPerPerson = 150f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("गाजर व बीन्स", IngredientCategory.SABZI_MANDI, 50f, "g", 40f),
                RawIngredientRatio("फूलगोभी व मटर", IngredientCategory.SABZI_MANDI, 50f, "g", 40f),
                RawIngredientRatio("आलू (Potato)", IngredientCategory.SABZI_MANDI, 35f, "g", 25f),
                RawIngredientRatio("टमाटर (Tomato)", IngredientCategory.SABZI_MANDI, 35f, "g", 35f),
                RawIngredientRatio("कुकिंग तेल", IngredientCategory.KIRANA, 15f, "ml", 140f),
                RawIngredientRatio("मसाले व धनिया पत्ती", IngredientCategory.MASALA_DRY, 10f, "g", 220f)
            ),
            chefTips = "सब्जियों को एक ही आकार में काटें जिससे सभी समान रूप से पकें।"
        ),
        CateringDish(
            id = "chhole_masala",
            nameHindi = "पंजाबी चना मसाला / छोले",
            englishName = "Chhole Masala",
            category = DishCategory.PANEER_SABZI,
            description = "पूरी और चावल के साथ परोसे जाने वाले चटपटे काबुली छोले",
            descriptionEnglish = "Spiced tangy chickpea curry ideal with poori or steamed rice",
            cookedServingPerPerson = 160f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("काबुली चना (Kabuli Chana)", IngredientCategory.KIRANA, 50f, "g", 120f),
                RawIngredientRatio("प्याज़ (Onion)", IngredientCategory.SABZI_MANDI, 35f, "g", 30f),
                RawIngredientRatio("टमाटर (Tomato)", IngredientCategory.SABZI_MANDI, 45f, "g", 35f),
                RawIngredientRatio("कुकिंग तेल", IngredientCategory.KIRANA, 15f, "ml", 140f),
                RawIngredientRatio("छोले मसाला व खड़े मसाले", IngredientCategory.MASALA_DRY, 10f, "g", 300f)
            ),
            chefTips = "छोलों को रातभर भिगोएं और उबालते समय 1 चुटकी बेकिंग सोडा व चायपत्ती पोटली डालें।"
        ),
        CateringDish(
            id = "dum_aloo",
            nameHindi = "कश्मीरी दम आलू",
            englishName = "Kashmiri Dum Aloo",
            category = DishCategory.PANEER_SABZI,
            description = "धीमी आंच पर पके छोटे आलू तीखी-खट्टी लाल तरी में",
            descriptionEnglish = "Slow-cooked baby potatoes in flavorful spiced tomato-curd gravy",
            cookedServingPerPerson = 150f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("छोटे आलू (Baby Potatoes)", IngredientCategory.SABZI_MANDI, 110f, "g", 25f),
                RawIngredientRatio("ताज़ा दही (Curd)", IngredientCategory.DAIRY, 35f, "g", 70f),
                RawIngredientRatio("टमाटर (Tomato)", IngredientCategory.SABZI_MANDI, 30f, "g", 35f),
                RawIngredientRatio("कुकिंग तेल", IngredientCategory.KIRANA, 18f, "ml", 140f),
                RawIngredientRatio("सौंफ, कश्मीरी मिर्च व मसाले", IngredientCategory.MASALA_DRY, 10f, "g", 280f)
            ),
            chefTips = "आलू में कांटे से छेद करके तलें ताकि तरी का स्वाद अंदर तक समा जाए।"
        ),

        // ==================== 2. DAL ====================
        CateringDish(
            id = "dal_tadka",
            nameHindi = "अरहर दाल तड़का",
            englishName = "Dal Tadka",
            category = DishCategory.DAL,
            description = "देसी घी, हींग, जीरा व लहसुन के तड़के वाली पीली दाल",
            descriptionEnglish = "Yellow arhar lentils tempered with desi ghee, cumin, hing and garlic",
            cookedServingPerPerson = 160f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("अरहर / तूअर दाल", IngredientCategory.KIRANA, 45f, "g", 140f),
                RawIngredientRatio("टमाटर (Tomato)", IngredientCategory.SABZI_MANDI, 25f, "g", 35f),
                RawIngredientRatio("प्याज़ (Onion)", IngredientCategory.SABZI_MANDI, 20f, "g", 30f),
                RawIngredientRatio("शुद्ध देसी घी", IngredientCategory.DAIRY, 10f, "g", 650f),
                RawIngredientRatio("हींग, जीरा, लहसुन व मसाले", IngredientCategory.MASALA_DRY, 6f, "g", 320f)
            ),
            chefTips = "दाल बनने के बाद ऊपर से कश्मीरी लाल मिर्च और हींग का दूसरा तड़का लगाएं।"
        ),
        CateringDish(
            id = "dal_makhani",
            nameHindi = "शाही दाल मखनी",
            englishName = "Dal Makhani",
            category = DishCategory.DAL,
            description = "साबुत उड़द और राजमा से धीमी आंच पर मक्खन-मलाई से तैयार",
            descriptionEnglish = "Slow-simmered black urad lentils finished with butter and cream",
            cookedServingPerPerson = 160f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("साबुत उड़द दाल", IngredientCategory.KIRANA, 40f, "g", 130f),
                RawIngredientRatio("चित्रा राजमा", IngredientCategory.KIRANA, 10f, "g", 140f),
                RawIngredientRatio("सफेद मक्खन / बटर", IngredientCategory.DAIRY, 20f, "g", 500f),
                RawIngredientRatio("दूध व मलाई", IngredientCategory.DAIRY, 30f, "ml", 80f),
                RawIngredientRatio("टमाटर प्यूरी", IngredientCategory.SABZI_MANDI, 40f, "g", 35f),
                RawIngredientRatio("मसाले व कस्तूरी मेथी", IngredientCategory.MASALA_DRY, 8f, "g", 350f)
            ),
            chefTips = "दाल मखनी जितनी धीमी आंच पर पकेगी, उतना ही मखमली स्वाद आएगा।"
        ),
        CateringDish(
            id = "chana_dal_fry",
            nameHindi = "चना दाल फ्राई",
            englishName = "Chana Dal Fry",
            category = DishCategory.DAL,
            description = "गाढ़ी व खुशबूदार चना दाल, पूरी और चावल के साथ सर्वोत्तम",
            descriptionEnglish = "Fragrant thick split-chickpea dal, great with poori and rice",
            cookedServingPerPerson = 150f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("चना दाल (Chana Dal)", IngredientCategory.KIRANA, 45f, "g", 90f),
                RawIngredientRatio("टमाटर (Tomato)", IngredientCategory.SABZI_MANDI, 25f, "g", 35f),
                RawIngredientRatio("प्याज़ (Onion)", IngredientCategory.SABZI_MANDI, 20f, "g", 30f),
                RawIngredientRatio("देसी घी / तेल", IngredientCategory.DAIRY, 10f, "g", 650f),
                RawIngredientRatio("मसाले, हींग व अदरक", IngredientCategory.MASALA_DRY, 6f, "g", 250f)
            ),
            chefTips = "चना दाल को 1 घंटा पहले भिगोने से दाल जल्दी और मुलायम गलती है।"
        ),

        // ==================== 3. ROTI & BREADS ====================
        CateringDish(
            id = "tawa_roti",
            nameHindi = "गरमा-गरम तवा रोटी",
            englishName = "Tawa Roti",
            category = DishCategory.ROTI_POORI,
            description = "शुद्ध गेहूं के आटे की मुलायम फुल्का रोटी",
            descriptionEnglish = "Soft whole-wheat phulka flatbread cooked fresh on tawa",
            cookedServingPerPerson = 3.5f,
            servingUnit = "रोटी",
            ingredients = listOf(
                RawIngredientRatio("गेहूं का आटा (Wheat Flour)", IngredientCategory.KIRANA, 120f, "g", 35f),
                RawIngredientRatio("देसी घी (चुपड़ने हेतु)", IngredientCategory.DAIRY, 6f, "g", 650f)
            ),
            chefTips = "आटा थोड़ा नरम गूंधें और 15 मिनट ढक कर रख दें।"
        ),
        CateringDish(
            id = "poori",
            nameHindi = "खस्ता हलवाई पूरी",
            englishName = "Crispy Poori",
            category = DishCategory.ROTI_POORI,
            description = "शादी व भंडारे वाली फूली-फूली सुनहरी खस्ता पूरी",
            descriptionEnglish = "Golden puffed crispy fried flatbreads for feasts and celebrations",
            cookedServingPerPerson = 5f,
            servingUnit = "पूरी",
            ingredients = listOf(
                RawIngredientRatio("गेहूं का आटा / मैदा", IngredientCategory.KIRANA, 140f, "g", 35f),
                RawIngredientRatio("कुकिंग तेल (तलने हेतु)", IngredientCategory.KIRANA, 35f, "ml", 140f),
                RawIngredientRatio("अजवाइन व नमक", IngredientCategory.MASALA_DRY, 4f, "g", 200f)
            ),
            chefTips = "पूरी का आटा थोड़ा सख्त गूंधें ताकि तेल कम सोखे और पूरी कुरकुरी बने।"
        ),
        CateringDish(
            id = "tandoori_roti",
            nameHindi = "तंदूरी रोटी / नान",
            englishName = "Tandoori Roti",
            category = DishCategory.ROTI_POORI,
            description = "ढाबा व शादी स्टाइल तंदूरी रोटी बटर के साथ",
            descriptionEnglish = "Dhaba-style clay-oven flatbread brushed lightly with butter",
            cookedServingPerPerson = 3f,
            servingUnit = "रोटी",
            ingredients = listOf(
                RawIngredientRatio("मैदा / आटा मिक्स", IngredientCategory.KIRANA, 130f, "g", 40f),
                RawIngredientRatio("मक्खन / घी", IngredientCategory.DAIRY, 8f, "g", 500f)
            ),
            chefTips = "तंदूर या तवे पर सेकते समय एक तरफ हल्का पानी लगाएं।"
        ),
        CateringDish(
            id = "kachori",
            nameHindi = "उड़द दाल खस्ता कचौड़ी",
            englishName = "Khasta Kachori",
            category = DishCategory.ROTI_POORI,
            description = "हलवाई स्टाइल मसालेदार उड़द दाल भरी कुरकुरी कचौड़ी",
            descriptionEnglish = "Crispy spiced urad dal stuffed kachori prepared halwai-style",
            cookedServingPerPerson = 2f,
            servingUnit = "कचौड़ी",
            ingredients = listOf(
                RawIngredientRatio("मैदा (All-purpose flour)", IngredientCategory.KIRANA, 80f, "g", 40f),
                RawIngredientRatio("धुली उड़द दाल (भिगोई)", IngredientCategory.KIRANA, 25f, "g", 140f),
                RawIngredientRatio("कुकिंग तेल (तलने हेतु)", IngredientCategory.KIRANA, 30f, "ml", 140f),
                RawIngredientRatio("सौंफ, हींग व मसाले", IngredientCategory.MASALA_DRY, 6f, "g", 300f)
            ),
            chefTips = "कचौड़ी को हमेशा धीमी आंच पर तलें ताकि परत अंदर तक खस्ता बने।"
        ),

        // ==================== 4. RICE & PULAO ====================
        CateringDish(
            id = "jeera_rice",
            nameHindi = "बासमती जीरा राइस",
            englishName = "Jeera Rice",
            category = DishCategory.RICE,
            description = "देसी घी व भुने जीरे की खुशबूदार बासमती चावल",
            descriptionEnglish = "Fragrant basmati rice tempered with desi ghee and roasted cumin",
            cookedServingPerPerson = 160f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("बासमती चावल (Basmati Rice)", IngredientCategory.KIRANA, 65f, "g", 90f),
                RawIngredientRatio("शुद्ध देसी घी", IngredientCategory.DAIRY, 8f, "g", 650f),
                RawIngredientRatio("जीरा व साबुत खड़े मसाले", IngredientCategory.MASALA_DRY, 4f, "g", 380f)
            ),
            chefTips = "चावल को 20 मिनट पहले धोकर भिगोएं, दाने खिले-खिले बनेंगे।"
        ),
        CateringDish(
            id = "veg_pulao",
            nameHindi = "शाही मटर वेज पुलाव",
            englishName = "Veg Pulao",
            category = DishCategory.RICE,
            description = "मटर, गाजर, काजू व खड़े मसालों से युक्त शानदार पुलाव",
            descriptionEnglish = "Aromatic basmati pulao cooked with peas, carrots and cashews",
            cookedServingPerPerson = 170f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("बासमती चावल (Basmati Rice)", IngredientCategory.KIRANA, 65f, "g", 90f),
                RawIngredientRatio("हरी मटर व गाजर", IngredientCategory.SABZI_MANDI, 35f, "g", 45f),
                RawIngredientRatio("देसी घी", IngredientCategory.DAIRY, 10f, "g", 650f),
                RawIngredientRatio("काजू व किशमिश", IngredientCategory.MASALA_DRY, 5f, "g", 800f),
                RawIngredientRatio("लौंग, दालचीनी, तेजपत्ता", IngredientCategory.MASALA_DRY, 4f, "g", 450f)
            ),
            chefTips = "पुलाव बनने के बाद 5 मिनट ढक्कन न खोलें ताकि भाप में दाना सेट हो सके।"
        ),
        CateringDish(
            id = "plain_rice",
            nameHindi = "सादा बासमती चावल",
            englishName = "Steamed Rice",
            category = DishCategory.RICE,
            description = "दाल और छोले के साथ परोसा जाने वाला सादा उबला चावल",
            descriptionEnglish = "Fluffy steamed basmati rice served alongside dal or curries",
            cookedServingPerPerson = 160f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("बासमती चावल (Basmati Rice)", IngredientCategory.KIRANA, 65f, "g", 90f)
            ),
            chefTips = "उबालते समय आधा चम्मच नींबू का रस डालने से चावल एकदम सफेद व खिला बनता है।"
        ),

        // ==================== 5. SWEETS & DESSERTS ====================
        CateringDish(
            id = "gulab_jamun",
            nameHindi = "गरमा-गरम गुलाब जामुन",
            englishName = "Gulab Jamun",
            category = DishCategory.SWEETS,
            description = "चाशनी में डूबे रसीले व मुलायम गुलाब जामुन (2 पीस प्रति व्यक्ति)",
            descriptionEnglish = "Warm soft khoya dumplings soaked in cardamom sugar syrup",
            cookedServingPerPerson = 2f,
            servingUnit = "पीस",
            ingredients = listOf(
                RawIngredientRatio("मावा / गुलाब जामुन मिक्स", IngredientCategory.DAIRY, 45f, "g", 350f),
                RawIngredientRatio("चीनी / शक्कर (चाशनी हेतु)", IngredientCategory.KIRANA, 60f, "g", 44f),
                RawIngredientRatio("रिफाइंड तेल / घी (तलने हेतु)", IngredientCategory.KIRANA, 15f, "ml", 140f),
                RawIngredientRatio("इलायची पाउडर", IngredientCategory.MASALA_DRY, 1f, "g", 1800f)
            ),
            chefTips = "चाशनी गुनगुनी होनी चाहिए, बहुत तेज़ गरम चाशनी में जामुन पिचक जाते हैं।"
        ),
        CateringDish(
            id = "kheer",
            nameHindi = "चावल की मलाईदार खीर",
            englishName = "Rice Kheer",
            category = DishCategory.SWEETS,
            description = "गाढ़े दूध, बासमती चावल व मेवों से सजी पारंपरिक खीर",
            descriptionEnglish = "Creamy slow-cooked basmati rice pudding garnished with dry fruits",
            cookedServingPerPerson = 150f,
            servingUnit = "मिली",
            ingredients = listOf(
                RawIngredientRatio("फुल क्रीम ताज़ा दूध", IngredientCategory.DAIRY, 160f, "ml", 65f),
                RawIngredientRatio("खीर बासमती चावल (टुकड़ा)", IngredientCategory.KIRANA, 20f, "g", 70f),
                RawIngredientRatio("चीनी (Sugar)", IngredientCategory.KIRANA, 30f, "g", 44f),
                RawIngredientRatio("काजू, बादाम व चिरौंजी", IngredientCategory.MASALA_DRY, 5f, "g", 900f),
                RawIngredientRatio("हरी इलायची", IngredientCategory.MASALA_DRY, 1f, "g", 1800f)
            ),
            chefTips = "चावल को पहले 1 चम्मच घी में भून लें, खीर में लाजवाब सोंधी खुशबू आएगी।"
        ),
        CateringDish(
            id = "sooji_halwa",
            nameHindi = "रवा / सूजी का हलवा",
            englishName = "Sooji Halwa",
            category = DishCategory.SWEETS,
            description = "भंडारा व प्रसाद स्पेशल दानेदार देसी घी का हलवा",
            descriptionEnglish = "Ghee-roasted semolina dessert topped with roasted dry fruits",
            cookedServingPerPerson = 120f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("सूजी / रवा (Sooji)", IngredientCategory.KIRANA, 35f, "g", 45f),
                RawIngredientRatio("शुद्ध देसी घी", IngredientCategory.DAIRY, 30f, "g", 650f),
                RawIngredientRatio("चीनी (Sugar)", IngredientCategory.KIRANA, 35f, "g", 44f),
                RawIngredientRatio("काजू, बादाम व किशमिश", IngredientCategory.MASALA_DRY, 5f, "g", 850f)
            ),
            chefTips = "सूजी को धीमी आंच पर बादामी होने तक भूनें और गरम चाशनी ही डालें।"
        ),
        CateringDish(
            id = "gajar_halwa",
            nameHindi = "गाजर का हलवा",
            englishName = "Gajar Halwa",
            category = DishCategory.SWEETS,
            description = "लाल मीठी गाजर, मावा और देसी घी का सर्दियों का खास हलवा",
            descriptionEnglish = "Sweet winter carrot pudding cooked slowly with khoya and ghee",
            cookedServingPerPerson = 120f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("लाल गाजर (Carrot)", IngredientCategory.SABZI_MANDI, 140f, "g", 40f),
                RawIngredientRatio("खोया / मावा (Khoya)", IngredientCategory.DAIRY, 30f, "g", 350f),
                RawIngredientRatio("शुद्ध देसी घी", IngredientCategory.DAIRY, 18f, "g", 650f),
                RawIngredientRatio("चीनी (Sugar)", IngredientCategory.KIRANA, 25f, "g", 44f),
                RawIngredientRatio("मेवे व इलायची", IngredientCategory.MASALA_DRY, 4f, "g", 900f)
            ),
            chefTips = "गाजर को पहले दूध में पकाएं, बाद में मावा और घी डालकर भूनें।"
        ),

        // ==================== 6. RAITA & SALAD ====================
        CateringDish(
            id = "boondi_raita",
            nameHindi = "तड़का बूंदी रायता",
            englishName = "Boondi Raita",
            category = DishCategory.RAITA_SALAD,
            description = "ताज़ा दही में कुरकुरी बूंदी, भुना जीरा व पुदीना",
            descriptionEnglish = "Fresh whipped curd with crispy boondi, roasted cumin and mint",
            cookedServingPerPerson = 130f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("ताज़ा दही (Fresh Curd)", IngredientCategory.DAIRY, 100f, "g", 70f),
                RawIngredientRatio("रायता बूंदी (Raita Boondi)", IngredientCategory.KIRANA, 20f, "g", 160f),
                RawIngredientRatio("भुना जीरा, काला नमक व पुदीना", IngredientCategory.MASALA_DRY, 3f, "g", 300f)
            ),
            chefTips = "बूंदी को 2 मिनट गुनगुने पानी में भिगोकर निचोड़ लें, रायता गाढ़ा और मुलायम बनेगा।"
        ),
        CateringDish(
            id = "mix_veg_raita",
            nameHindi = "खीरा-टमाटर वेज रायता",
            englishName = "Mix Veg Raita",
            category = DishCategory.RAITA_SALAD,
            description = "बारीक कटे खीरे, टमाटर और हरी मिर्च का ताज़ा रायता",
            descriptionEnglish = "Cooling yogurt raita mixed with finely chopped cucumber and tomato",
            cookedServingPerPerson = 130f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("ताज़ा दही (Curd)", IngredientCategory.DAIRY, 100f, "g", 70f),
                RawIngredientRatio("खीरा व टमाटर", IngredientCategory.SABZI_MANDI, 30f, "g", 35f),
                RawIngredientRatio("भुना जीरा व नमक", IngredientCategory.MASALA_DRY, 3f, "g", 250f)
            ),
            chefTips = "सब्जियां परोसने से थोड़ी देर पहले ही मिलाएं ताकि रायता पानी न छोड़े।"
        ),
        CateringDish(
            id = "green_salad",
            nameHindi = "सलाद (खीरा, प्याज, टमाटर)",
            englishName = "Green Salad",
            category = DishCategory.RAITA_SALAD,
            description = "ककड़ी, प्याज, टमाटर, हरी मिर्च और नींबू का ताज़ा सलाद",
            descriptionEnglish = "Crisp cucumber, onion and tomato salad with lemon and green chilli",
            cookedServingPerPerson = 100f,
            servingUnit = "ग्राम",
            ingredients = listOf(
                RawIngredientRatio("खीरा / ककड़ी (Cucumber)", IngredientCategory.SABZI_MANDI, 45f, "g", 30f),
                RawIngredientRatio("टमाटर (Tomato)", IngredientCategory.SABZI_MANDI, 30f, "g", 35f),
                RawIngredientRatio("प्याज़ (Onion)", IngredientCategory.SABZI_MANDI, 25f, "g", 30f),
                RawIngredientRatio("नींबू व हरी मिर्च", IngredientCategory.SABZI_MANDI, 10f, "g", 80f)
            ),
            chefTips = "सलाद पर नमक परोसते समय ही छिड़कें।"
        ),
        CateringDish(
            id = "papad_achar",
            nameHindi = "पापड़ व आम का अचार",
            englishName = "Papad & Pickle",
            category = DishCategory.RAITA_SALAD,
            description = "उड़द का कुरकुरा पापड़ और चटपटा आम का अचार",
            descriptionEnglish = "Crispy roasted urad papad served alongside tangy mango pickle",
            cookedServingPerPerson = 1.2f,
            servingUnit = "पापड़",
            ingredients = listOf(
                RawIngredientRatio("उड़द पापड़ (Papad)", IngredientCategory.KIRANA, 18f, "g", 220f),
                RawIngredientRatio("आम का अचार (Pickle)", IngredientCategory.KIRANA, 15f, "g", 180f),
                RawIngredientRatio("रिफाइंड तेल (सिकाई/तलाई)", IngredientCategory.KIRANA, 5f, "ml", 140f)
            ),
            chefTips = "पापड़ को तलने के तुरंत बाद जाली पर रखें ताकि अतिरिक्त तेल निकल जाए।"
        )
    )

    private val ingredientEnglishNames: Map<String, String> = mapOf(
        "अजवाइन व नमक" to "Carom Seeds & Salt",
        "अदरक, लहसुन व मसाले" to "Ginger, Garlic & Spices",
        "अदरक, हरी मिर्च व मसाले" to "Ginger, Green Chilli & Spices",
        "अरहर / तूअर दाल" to "Toor Dal (Yellow Lentils)",
        "आम का अचार (Pickle)" to "Mango Pickle",
        "आलू (Potato)" to "Potato",
        "इलायची पाउडर" to "Cardamom Powder",
        "उड़द पापड़ (Papad)" to "Urad Papad",
        "काजू व किशमिश" to "Cashews & Raisins",
        "काजू व मगज़" to "Cashews & Melon Seeds",
        "काजू, बादाम व किशमिश" to "Cashews, Almonds & Raisins",
        "काजू, बादाम व चिरौंजी" to "Cashews, Almonds & Chironji",
        "काबुली चना (Kabuli Chana)" to "Chickpeas (Kabuli Chana)",
        "कुकिंग तेल" to "Cooking Oil",
        "कुकिंग तेल (तलने हेतु)" to "Cooking Oil (for frying)",
        "खीर बासमती चावल (टुकड़ा)" to "Basmati Rice (broken)",
        "खीरा / ककड़ी (Cucumber)" to "Cucumber",
        "खीरा व टमाटर" to "Cucumber & Tomato",
        "खोया / मावा (Khoya)" to "Khoya / Mawa",
        "गरम मसाला व कस्तूरी मेथी" to "Garam Masala & Kasuri Methi",
        "गाजर व बीन्स" to "Carrots & French Beans",
        "गेहूं का आटा (Wheat Flour)" to "Whole Wheat Flour (Atta)",
        "गेहूं का आटा / मैदा" to "Wheat Flour / Maida",
        "चना दाल (Chana Dal)" to "Chana Dal (Split Chickpeas)",
        "चित्रा राजमा" to "Kidney Beans (Rajma)",
        "चीनी (Sugar)" to "Sugar",
        "चीनी / शक्कर (चाशनी हेतु)" to "Sugar (for syrup)",
        "छोटे आलू (Baby Potatoes)" to "Baby Potatoes",
        "छोले मसाला व खड़े मसाले" to "Chhole Masala & Whole Spices",
        "जीरा व साबुत खड़े मसाले" to "Cumin & Whole Spices",
        "टमाटर (Tomato)" to "Tomato",
        "टमाटर प्यूरी" to "Tomato Puree",
        "ताज़ा दही (Curd)" to "Fresh Curd (Yogurt)",
        "ताज़ा दही (Fresh Curd)" to "Fresh Curd (Yogurt)",
        "ताज़ा मलाई / क्रीम" to "Fresh Cream (Malai)",
        "दूध व मलाई" to "Milk & Cream",
        "देसी घी" to "Desi Ghee",
        "देसी घी (चुपड़ने हेतु)" to "Desi Ghee (for brushing)",
        "देसी घी / तेल" to "Desi Ghee / Cooking Oil",
        "धुली उड़द दाल (भिगोई)" to "Split White Urad Dal",
        "नींबू व हरी मिर्च" to "Lemon & Green Chilli",
        "पनीर (Paneer)" to "Paneer (Cottage Cheese)",
        "प्याज़ (Onion)" to "Onion",
        "फुल क्रीम ताज़ा दूध" to "Full-Cream Milk",
        "फूलगोभी (Cauliflower)" to "Cauliflower",
        "फूलगोभी व मटर" to "Cauliflower & Green Peas",
        "बासमती चावल (Basmati Rice)" to "Basmati Rice",
        "भुना जीरा व नमक" to "Roasted Cumin & Salt",
        "भुना जीरा, काला नमक व पुदीना" to "Roasted Cumin, Black Salt & Mint",
        "मक्खन / घी" to "Butter / Ghee",
        "मसाले व कस्तूरी मेथी" to "Spices & Kasuri Methi",
        "मसाले व धनिया पत्ती" to "Spices & Fresh Coriander",
        "मसाले, हींग व अदरक" to "Spices, Asafoetida & Ginger",
        "मावा / गुलाब जामुन मिक्स" to "Khoya / Gulab Jamun Mix",
        "मेवे व इलायची" to "Dry Fruits & Cardamom",
        "मैदा (All-purpose flour)" to "All-Purpose Flour (Maida)",
        "मैदा / आटा मिक्स" to "Maida / Wheat Flour Mix",
        "रायता बूंदी (Raita Boondi)" to "Raita Boondi",
        "रिफाइंड तेल (सिकाई/तलाई)" to "Refined Oil (for frying/roasting)",
        "रिफाइंड तेल / घी" to "Refined Oil / Ghee",
        "रिफाइंड तेल / घी (तलने हेतु)" to "Refined Oil / Ghee (for frying)",
        "लाल गाजर (Carrot)" to "Red Carrots",
        "लौंग, दालचीनी, तेजपत्ता" to "Cloves, Cinnamon & Bay Leaf",
        "शुद्ध देसी घी" to "Pure Desi Ghee",
        "सफेद मक्खन / बटर" to "White Butter",
        "सरसों / रिफाइंड तेल" to "Mustard / Refined Oil",
        "साबुत उड़द दाल" to "Whole Black Urad Dal",
        "सूजी / रवा (Sooji)" to "Semolina (Sooji / Rava)",
        "सौंफ, कश्मीरी मिर्च व मसाले" to "Fennel, Kashmiri Chilli & Spices",
        "सौंफ, हींग व मसाले" to "Fennel, Asafoetida & Spices",
        "हरी इलायची" to "Green Cardamom",
        "हरी मटर (Green Peas)" to "Green Peas",
        "हरी मटर व गाजर" to "Green Peas & Carrots",
        "हींग, जीरा, लहसुन व मसाले" to "Asafoetida, Cumin, Garlic & Spices"
    )

    private val chefTipEnglishByDishId: Map<String, String> = mapOf(
        "shahi_paneer" to "Do not over-fry paneer; soaking in lukewarm water keeps it super soft.",
        "matar_paneer" to "Steam green peas for 2 minutes before adding them to the gravy.",
        "aloo_gobhi" to "Cut cauliflower into medium-large florets so they stay intact while roasting.",
        "mix_veg" to "Chop all vegetables into uniform sizes so everything cooks evenly.",
        "chhole_masala" to "Soak chickpeas overnight and boil with a pinch of baking soda and tea bag.",
        "dum_aloo" to "Prick baby potatoes with a fork before frying so the gravy absorbs well.",
        "dal_tadka" to "Finish with a second tempering of desi ghee, Kashmiri chilli and asafoetida.",
        "dal_makhani" to "The slower you simmer Dal Makhani, the richer and creamier it tastes.",
        "chana_dal_fry" to "Soak chana dal 1 hour ahead so it cooks faster and stays soft.",
        "tawa_roti" to "Knead a soft dough and let it rest covered for 15 minutes.",
        "poori" to "Knead a stiff dough so pooris stay crisp and absorb minimal oil.",
        "tandoori_roti" to "Brush lightly with water on one side before cooking so it puffs crisp.",
        "kachori" to "Fry kachoris on low heat so the crust becomes flaky all the way through.",
        "jeera_rice" to "Rinse and soak rice 20 minutes before cooking for fluffy separated grains.",
        "veg_pulao" to "Rest covered for 5 minutes after cooking so steam sets the grains cleanly.",
        "plain_rice" to "Add half a spoon of lemon juice while boiling for bright white, non-sticky rice.",
        "gulab_jamun" to "Keep sugar syrup lukewarm; overly hot syrup makes gulab jamuns flatten.",
        "kheer" to "Lightly roast the broken rice in 1 tsp ghee first for rich aroma.",
        "sooji_halwa" to "Roast semolina slowly until golden brown and pour hot sugar syrup gradually.",
        "gajar_halwa" to "Simmer grated carrots in milk first before adding khoya and desi ghee.",
        "boondi_raita" to "Soak boondi in lukewarm water for 2 minutes and squeeze gently before mixing.",
        "mix_veg_raita" to "Mix chopped vegetables just before serving so the raita stays thick.",
        "green_salad" to "Sprinkle salt only right before serving so salad stays crisp and fresh.",
        "papad_achar" to "Place fried papads upright on a wire rack right away to drain excess oil."
    )

    val allDishes: List<CateringDish> = rawDishes.map { dish ->
        dish.copy(
            chefTipsEnglish = chefTipEnglishByDishId[dish.id] ?: dish.chefTips,
            ingredients = dish.ingredients.map { ing ->
                ing.copy(nameEnglish = ingredientEnglishNames[ing.name] ?: ing.name)
            }
        )
    }

    // Quick Recommended Thalis
    val popularPartyThaliDishIds = listOf(
        "shahi_paneer", "dal_makhani", "poori", "jeera_rice", "gulab_jamun", "boondi_raita", "green_salad"
    )

    val bhandaraThaliDishIds = listOf(
        "aloo_gobhi", "chana_dal_fry", "poori", "plain_rice", "sooji_halwa"
    )

    val dailyHomeThaliDishIds = listOf(
        "matar_paneer", "dal_tadka", "tawa_roti", "jeera_rice", "green_salad"
    )
}
