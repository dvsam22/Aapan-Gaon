export const SITE_CONFIG = {
  name: "Aapan Gaon (आपन गांव)",
  tagline: "आपका अपना ग्रामीण डिजिटल साथी",
  description:
    "आपन गांव ऐप - खलीलाबाद (संत कबीर नगर) व नौगढ़ (सिद्धार्थ नगर) का आज का ताज़ा सरकारी मंडी भाव, महाराजगंज की स्थानीय खबरें, सरकारी योजनाएं और गाँव की हर सुविधा। 100% मुफ़्त!",
  url: "https://aapangaon.in",
  playStoreUrl: "https://play.google.com/store/apps/details?id=com.dv.apna",
  ogImage: "/banner_mandi_rates.jpg",
  author: "Aapan Gaon Digital Village Team",
  keywords: [
    "आपन गांव",
    "Aapan Gaon",
    "apna gaon app",
    "mandi bhav today",
    "khalilabad mandi bhav",
    "naugarh mandi rate",
    "sant kabir nagar mandi",
    "siddharth nagar mandi bhav",
    "gramin samachar",
    "maharajganj news in hindi",
    "uttar pradesh mandi rates",
    "kisan mandi app",
    "daily mandi rate notification",
    "gram panchayat notice",
    "village directory app",
  ],
  supportEmail: "developersm22@gmail.com",
};

export const SCHEMA_ORGANIZATION = {
  "@context": "https://schema.org",
  "@type": "Organization",
  name: "Aapan Gaon",
  url: SITE_CONFIG.url,
  logo: `${SITE_CONFIG.url}/logo.png`,
  description: SITE_CONFIG.description,
  sameAs: [SITE_CONFIG.playStoreUrl],
  contactPoint: {
    "@type": "ContactPoint",
    email: SITE_CONFIG.supportEmail,
    contactType: "Customer Support",
    areaServed: "IN",
    availableLanguage: ["Hindi", "English"],
  },
};

export const SCHEMA_APPLICATION = {
  "@context": "https://schema.org",
  "@type": "SoftwareApplication",
  name: "Aapan Gaon",
  operatingSystem: "Android",
  applicationCategory: "BusinessApplication, NewsApplication",
  offers: {
    "@type": "Offer",
    price: "0",
    priceCurrency: "INR",
  },
  aggregateRating: {
    "@type": "AggregateRating",
    ratingValue: "4.8",
    ratingCount: "1280",
    bestRating: "5",
    worstRating: "1",
  },
  downloadUrl: SITE_CONFIG.playStoreUrl,
  featureList: [
    "दैनिक सरकारी मंडी भाव (खलीलाबाद व नौगढ़)",
    "स्थानीय ग्रामीण समाचार (महराजगंज व पूर्वांचल)",
    "ग्राम पंचायत नोटिस व सरकारी योजनाएं",
    "ग्रामीण आपातकालीन व आवश्यक डायरेक्टरी",
    "रोज़ाना सुबह 7:45 बजे ताज़ा भाव का नोटिफिकेशन",
  ],
};

export const SCHEMA_FAQ = {
  "@context": "https://schema.org",
  "@type": "FAQPage",
  mainEntity: [
    {
      "@type": "Question",
      name: "खलीलाबाद और नौगढ़ मंडी का आज का ताज़ा भाव कैसे देखें?",
      acceptedAnswer: {
        "@type": "Answer",
        text: "आपन गांव (Aapan Gaon) ऐप डाउनलोड करके आप खलीलाबाद (संत कबीर नगर) और नौगढ़ (सिद्धार्थ नगर) APMC मंडी के आलू, टमाटर, प्याज, गोभी, गेहूं, धान आदि सभी फसलों के आज के न्यूनतम, अधिकतम और मॉडल भाव रोज़ाना सुबह सबसे पहले देख सकते हैं।",
      },
    },
    {
      "@type": "Question",
      name: "क्या आपन गांव ऐप बिल्कुल मुफ़्त (Free) है?",
      acceptedAnswer: {
        "@type": "Answer",
        text: "हाँ, आपन गांव ऐप हमारे गाँव के सभी किसान भाइयों, व्यापारियों और ग्रामीणों के लिए 100% मुफ़्त है। इसके उपयोग के लिए कोई शुल्क नहीं लिया जाता।",
      },
    },
    {
      "@type": "Question",
      name: "मंडी भाव रोज़ाना कितने बजे अपडेट होता है?",
      acceptedAnswer: {
        "@type": "Answer",
        text: "आपन गांव ऐप में सरकारी एगमार्कनेट (Agmarknet) पोर्टल से रोज़ाना 4 बार भाव स्वतः अपडेट होता है—सुबह 07:45 AM, 11:45 AM, 03:45 PM और 07:45 PM पर, और सुबह ताज़ा भाव आते ही आपके फोन पर तुरंत नोटिफिकेशन आ जाता है।",
      },
    },
    {
      "@type": "Question",
      name: "क्या ऐप में अपने गाँव की स्थानीय खबरें भी मिलती हैं?",
      acceptedAnswer: {
        "@type": "Answer",
        text: "हाँ, ऐप में महराजगंज और आसपास के ग्रामीण क्षेत्रों की प्रमाणित ताज़ा स्थानीय खबरें, ग्राम पंचायत की घोषणाएं, सरकारी योजनाएं और नोटिस हर 2 घंटे में अपडेट की जाती हैं।",
      },
    },
    {
      "@type": "Question",
      name: "क्या ऐप स्लो या 2G/3G इंटरनेट पर भी काम करेगा?",
      acceptedAnswer: {
        "@type": "Answer",
        text: "बिल्कुल! आपन गांव ऐप को खासतौर पर ग्रामीण इंटरनेट परिस्थितियों को ध्यान में रखकर बेहद हल्का और सुपरफ़ास्ट बनाया गया है, जो धीमे इंटरनेट पर भी बिना अटके चलता है।",
      },
    },
  ],
};
