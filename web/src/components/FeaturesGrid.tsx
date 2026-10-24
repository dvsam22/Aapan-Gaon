"use client";

import React from "react";
import Image from "next/image";
import { TrendingUp, Newspaper, Megaphone, Users, Bell, Zap, Check, ArrowRight } from "lucide-react";

const FEATURES = [
  {
    title: "दैनिक सरकारी मंडी भाव",
    subtitle: "Agmarknet द्वारा 100% सत्यापित",
    description:
      "खलीलाबाद (संत कबीर नगर) और नौगढ़ (सिद्धार्थ नगर) मंडी के आलू, टमाटर, प्याज, गेहूं, धान के न्यूनतम, अधिकतम और मॉडल भाव। पिछले 7 दिनों का ट्रेंड चार्ट भी उपलब्ध।",
    icon: TrendingUp,
    badge: "लाइव रेट्स",
    image: "/banner_mandi_rates.jpg",
    color: "from-emerald-500 to-teal-700",
  },
  {
    title: "स्थानीय ग्रामीण समाचार",
    subtitle: "महराजगंज व आसपास का हर अपडेट",
    description:
      "गाँव, तहसील और जिले की हर प्रमाणित ताज़ा खबर। मौसम की चेतावनी, नहर में पानी, बिजली कटौती और स्थानीय आयोजनों की सूचना सीधे अपने फोन पर पढ़ें।",
    icon: Newspaper,
    badge: "हर 2 घंटे में",
    image: "/banner_local_news.jpg",
    color: "from-blue-500 to-indigo-700",
  },
  {
    title: "सरकारी योजनाएं व नोटिस",
    subtitle: "ग्राम पंचायत की हर घोषणा",
    description:
      "पीएम किसान सम्मान निधि, किसान क्रेडिट कार्ड (KCC), राशन कार्ड सूची, फसल बीमा और ग्राम पंचायत द्वारा जारी सभी आवश्यक नोटिस अब एक ही जगह पर।",
    icon: Megaphone,
    badge: "सरकारी सहायता",
    image: "/banner_fresh_produce.jpg",
    color: "from-amber-500 to-orange-700",
  },
  {
    title: "गाँव की इमरजेंसी डायरेक्टरी",
    subtitle: "ज़रूरी संपर्क हमेशा हाथ में",
    description:
      "ग्राम प्रधान, लेखपाल, पंचायत सचिव, नज़दीकी स्वास्थ्य केंद्र, डॉक्टर, एम्बुलेंस, पुलिस थाना और ट्रैक्टर/हार्वेस्टर किराए वाले भाइयों के सीधे फोन नंबर।",
    icon: Users,
    badge: "सहयोग नेटवर्क",
    image: "/banner_emergency_health.jpg",
    color: "from-rose-500 to-red-700",
  },
  {
    title: "रोज़ सुबह 7:45 AM नोटिफिकेशन",
    subtitle: "मंडी जाने से पहले रेट पता करें",
    description:
      "बिना कोई ऐप खोले, जैसे ही सुबह मंडी खुलती है, आपके फोन पर ताज़ा भाव का नोटिफिकेशन आ जाता है ताकि किसान भाई घर बैठे ही सही कीमत तय कर सकें।",
    icon: Bell,
    badge: "ऑटोमैटिक",
    image: "/banner_register_business.jpg",
    color: "from-purple-500 to-violet-700",
  },
  {
    title: "लो-इंटरनेट पर भी सुपरफ़ास्ट",
    subtitle: "2G / 3G / 4G के लिए अनुकूलित",
    description:
      "बेहद हल्का ऐप जो खराब नेटवर्क में भी बिना अटके चलता है। एक बार लोड होने के बाद ऑफलाइन भी पिछला भाव और आवश्यक संपर्क नंबर देख सकते हैं।",
    icon: Zap,
    badge: "सुपर-लाइट",
    image: "/banner_mandi_rates.jpg",
    color: "from-teal-500 to-emerald-700",
  },
];

export default function FeaturesGrid({ onOpenDownload }: { onOpenDownload: () => void }) {
  return (
    <section id="features" className="py-16 sm:py-24 bg-white relative">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section Heading */}
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full bg-slate-100 text-slate-800 text-xs font-bold mb-3 border border-slate-200">
            <span>आपन गांव ऐप की खास सुविधाएं</span>
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight">
            गाँव के विकास और किसानों के मुनाफे के लिए जरूरी हर फीचर
          </h2>
          <p className="mt-4 text-base text-slate-600">
            आपन गांव केवल एक ऐप नहीं है, यह आपके पूरे गाँव को डिजिटल बनाकर हर परिवार को सशक्त करने का एक विश्वसनीय माध्यम है।
          </p>
        </div>

        {/* Feature Cards Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {FEATURES.map((feature, index) => {
            const Icon = feature.icon;
            return (
              <div
                key={index}
                className="group relative bg-slate-50 rounded-3xl overflow-hidden border border-slate-200/80 hover:border-emerald-500/50 shadow-xs hover:shadow-xl transition-all duration-300 flex flex-col justify-between"
              >
                <div>
                  {/* Card Visual Header with Image */}
                  <div className="relative h-44 w-full overflow-hidden bg-slate-100">
                    <Image
                      src={feature.image}
                      alt={feature.title}
                      fill
                      sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
                      className="object-cover group-hover:scale-105 transition-transform duration-500"
                    />
                    <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-slate-950/30 to-transparent" />
                    
                    {/* Badge */}
                    <div className="absolute top-3.5 left-3.5">
                      <span className="px-3 py-1 rounded-full bg-white/90 backdrop-blur-md text-[11px] font-bold text-slate-900 shadow-xs">
                        {feature.badge}
                      </span>
                    </div>

                    {/* Icon Pill */}
                    <div className="absolute bottom-3.5 left-3.5 flex items-center gap-2 text-white">
                      <div className={`p-2 rounded-xl bg-gradient-to-br ${feature.color} text-white shadow-md`}>
                        <Icon className="w-5 h-5" />
                      </div>
                      <span className="text-xs font-semibold text-slate-200">{feature.subtitle}</span>
                    </div>
                  </div>

                  {/* Body Content */}
                  <div className="p-6">
                    <h3 className="text-xl font-bold text-slate-900 group-hover:text-emerald-700 transition">
                      {feature.title}
                    </h3>
                    <p className="mt-3 text-sm text-slate-600 leading-relaxed">
                      {feature.description}
                    </p>
                  </div>
                </div>

                <div className="px-6 pb-6 pt-2">
                  <button
                    onClick={onOpenDownload}
                    className="inline-flex items-center gap-1.5 text-xs font-bold text-emerald-700 hover:text-emerald-800 transition group-hover:translate-x-1"
                  >
                    <span>मुफ़्त में ऐप डाउनलोड करें</span>
                    <ArrowRight className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
}
