"use client";

import React, { useState } from "react";
import Image from "next/image";
import { TrendingUp, Newspaper, Megaphone, Users, Check, Sparkles, Smartphone } from "lucide-react";

interface TabItem {
  id: string;
  name: string;
  icon: any;
  headline: string;
  description: string;
  points: string[];
  bannerImage: string;
  screenContent: React.ReactNode;
}

export default function InteractiveShowcase({ onOpenDownload }: { onOpenDownload: () => void }) {
  const [activeTab, setActiveTab] = useState<string>("mandi");

  const TABS: TabItem[] = [
    {
      id: "mandi",
      name: "🌾 मंडी भाव",
      icon: TrendingUp,
      headline: "दैनिक मंडी भाव: फसल बेचने से पहले सही दाम जानें",
      description:
        "खलीलाबाद व नौगढ़ मंडी के रोज़ाना सत्यापित न्यूनतम, अधिकतम और मॉडल रेट। आलू, टमाटर, प्याज, गेहूं, धान, सरसों व अन्य 25+ फसलों के सटीक आंकड़े।",
      points: [
        "सरकारी Agmarknet पोर्टल से 100% वेरिफाइड डाटा",
        "तेज़ी और मंदी का स्पष्ट संकेत (Trend Arrow)",
        "पिछले 7 दिनों का ऐतिहासिक मूल्य रिकॉर्ड",
      ],
      bannerImage: "/banner_mandi_rates.jpg",
      screenContent: (
        <div className="space-y-2 p-3">
          <div className="p-3 bg-white rounded-xl border border-slate-200/80 shadow-2xs flex items-center justify-between">
            <div>
              <span className="text-xs font-bold text-slate-800 block">आलू (Potato)</span>
              <span className="text-[10px] text-slate-500">खलीलाबाद APMC • 24 Oct</span>
            </div>
            <div className="text-right">
              <span className="text-xs font-black text-slate-900 block">₹1,450/क्विं.</span>
              <span className="text-[9px] font-bold text-emerald-700">▲ ₹50 तेज़</span>
            </div>
          </div>
          <div className="p-3 bg-white rounded-xl border border-slate-200/80 shadow-2xs flex items-center justify-between">
            <div>
              <span className="text-xs font-bold text-slate-800 block">टमाटर (Tomato)</span>
              <span className="text-[10px] text-slate-500">खलीलाबाद APMC • 24 Oct</span>
            </div>
            <div className="text-right">
              <span className="text-xs font-black text-slate-900 block">₹1,800/क्विं.</span>
              <span className="text-[9px] font-bold text-amber-700">● स्थिर</span>
            </div>
          </div>
          <div className="p-3 bg-white rounded-xl border border-slate-200/80 shadow-2xs flex items-center justify-between">
            <div>
              <span className="text-xs font-bold text-slate-800 block">गेहूं (Wheat)</span>
              <span className="text-[10px] text-slate-500">नौगढ़ APMC • 24 Oct</span>
            </div>
            <div className="text-right">
              <span className="text-xs font-black text-slate-900 block">₹2,425/क्विं.</span>
              <span className="text-[9px] font-bold text-emerald-700">▲ ₹25 तेज़</span>
            </div>
          </div>
        </div>
      ),
    },
    {
      id: "news",
      name: "📰 गाँव समाचार",
      icon: Newspaper,
      headline: "महराजगंज व पूर्वांचल की स्थानीय खबरें",
      description:
        "जिले और गाँव के विश्वसनीय समाचार। कृषि मेले, मौसम अलर्ट, सड़क व नहर निर्माण, और ग्रामीण विकास से जुड़ी हर ज़रूरी जानकारी बिना किसी फेक न्यूज़ के।",
      points: [
        "हर 2 घंटे में ऑटोमैटिक ताज़ा अपडेट",
        "प्रमाणित न्यूज़ पोर्टल्स द्वारा सत्यापित खबरें",
        "ज़रूरी सूचनाओं का तुरंत मोबाइल नोटिफिकेशन",
      ],
      bannerImage: "/banner_local_news.jpg",
      screenContent: (
        <div className="space-y-2 p-3">
          <div className="p-3 bg-white rounded-xl border border-slate-200/80 shadow-2xs">
            <span className="text-[9px] font-bold text-emerald-700 uppercase">महराजगंज • आज</span>
            <h4 className="text-xs font-bold text-slate-800 line-clamp-2 mt-0.5">
              कृषि विभाग द्वारा रबी फसलों के उन्नत बीजों का वितरण शुरू
            </h4>
            <p className="text-[10px] text-slate-500 mt-1 line-clamp-2">
              किसान भाइयों को 50% अनुदान पर गेहूं व सरसों के प्रमाणित बीज उपलब्ध कराए जा रहे हैं...
            </p>
          </div>
          <div className="p-3 bg-white rounded-xl border border-slate-200/80 shadow-2xs">
            <span className="text-[9px] font-bold text-blue-700 uppercase">मौसम सूचना</span>
            <h4 className="text-xs font-bold text-slate-800 line-clamp-2 mt-0.5">
              अगले 3 दिनों तक मौसम शुष्क रहने का अनुमान, कटाई में तेज़ी
            </h4>
          </div>
        </div>
      ),
    },
    {
      id: "notices",
      name: "📢 पंचायत नोटिस",
      icon: Megaphone,
      headline: "ग्राम पंचायत की हर घोषणा और सरकारी योजनाएं",
      description:
        "ब्लॉक या तहसील के चक्कर लगाए बिना जानें कि गाँव में कौन सी नई योजना आई है। पीएम आवास सूची, शौचालय अनुदान, पेंशन और राशन कार्ड वितरण की जानकारी।",
      points: [
        "ग्राम प्रधान व सचिव द्वारा जारी आधिकारिक सूचनाएं",
        "सरकारी योजनाओं के लिए पात्रता व आवेदन नियम",
        "सीधे अपने गाँव से जुड़ी डिजिटल चौपाल",
      ],
      bannerImage: "/banner_fresh_produce.jpg",
      screenContent: (
        <div className="space-y-2 p-3">
          <div className="p-3 bg-white rounded-xl border-l-4 border-l-amber-500 border border-slate-200/80 shadow-2xs">
            <span className="text-[9px] font-bold text-amber-700 uppercase">आवश्यक सूचना</span>
            <h4 className="text-xs font-bold text-slate-800 mt-0.5">
              ग्राम पंचायत में ई-केवाईसी शिविर कल सुबह 10 बजे
            </h4>
            <p className="text-[10px] text-slate-500 mt-1">
              पीएम किसान लाभार्थी अपना आधार व बैंक पासबुक साथ लाएं।
            </p>
          </div>
          <div className="p-3 bg-white rounded-xl border-l-4 border-l-emerald-500 border border-slate-200/80 shadow-2xs">
            <span className="text-[9px] font-bold text-emerald-700 uppercase">विकास कार्य</span>
            <h4 className="text-xs font-bold text-slate-800 mt-0.5">
              मुख्य मार्ग से प्राथमिक विद्यालय तक खड़ंजा निर्माण कार्य स्वीकृत
            </h4>
          </div>
        </div>
      ),
    },
    {
      id: "directory",
      name: "👥 ग्रामीण डायरेक्टरी",
      icon: Users,
      headline: "गाँव के सभी आवश्यक नंबर हमेशा आपकी जेब में",
      description:
        "किसी भी आपातकालीन स्थिति में इधर-उधर भागने की ज़रूरत नहीं। लेखपाल, आशा बहू, एम्बुलेंस, पशु चिकित्सक, ट्रैक्टर-ट्रॉली और मिस्त्री का सीधा संपर्क नंबर।",
      points: [
        "एक क्लिक में सीधा फ़ोन कॉल",
        "ग्राम पंचायत स्तर पर सत्यापित संपर्क सूची",
        "बिना इंटरनेट के भी सुरक्षित ऑफलाइन एक्सेस",
      ],
      bannerImage: "/banner_emergency_health.jpg",
      screenContent: (
        <div className="space-y-2 p-3">
          <div className="p-3 bg-white rounded-xl border border-slate-200/80 shadow-2xs flex items-center justify-between">
            <div>
              <span className="text-xs font-bold text-slate-800 block">सामुदायिक स्वास्थ्य केंद्र</span>
              <span className="text-[10px] text-slate-500">24x7 आपातकालीन सेवा</span>
            </div>
            <span className="text-[10px] font-bold px-2 py-1 rounded-md bg-emerald-100 text-emerald-800">
              📞 108 / कॉल
            </span>
          </div>
          <div className="p-3 bg-white rounded-xl border border-slate-200/80 shadow-2xs flex items-center justify-between">
            <div>
              <span className="text-xs font-bold text-slate-800 block">हल्का लेखपाल</span>
              <span className="text-[10px] text-slate-500">भूमि व अभिलेख शाखा</span>
            </div>
            <span className="text-[10px] font-bold px-2 py-1 rounded-md bg-slate-100 text-slate-700">
              संपर्क उपलब्ध
            </span>
          </div>
          <div className="p-3 bg-white rounded-xl border border-slate-200/80 shadow-2xs flex items-center justify-between">
            <div>
              <span className="text-xs font-bold text-slate-800 block">कृषि यंत्र व ट्रैक्टर सेवा</span>
              <span className="text-[10px] text-slate-500">जुताई व थ्रेशर उपलब्ध</span>
            </div>
            <span className="text-[10px] font-bold px-2 py-1 rounded-md bg-emerald-100 text-emerald-800">
              कॉल करें
            </span>
          </div>
        </div>
      ),
    },
  ];

  const currentTab = TABS.find((t) => t.id === activeTab) || TABS[0];

  return (
    <section className="py-16 sm:py-24 bg-gradient-to-b from-slate-50 to-white relative overflow-hidden">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section Heading */}
        <div className="text-center max-w-3xl mx-auto mb-12">
          <div className="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full bg-emerald-100 text-emerald-800 text-xs font-bold mb-3">
            <Smartphone className="w-3.5 h-3.5" />
            <span>लाइव ऐप इंटरफेस अनुभव</span>
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight">
            देखें आपन गांव ऐप फोन पर कैसा दिखता है
          </h2>
          <p className="mt-3 text-base text-slate-600">
            विभिन्न सुविधाओं पर टैप करके ऐप की स्क्रीन और फीचर्स का सीधा अनुभव करें।
          </p>
        </div>

        {/* Tab Buttons */}
        <div className="flex items-center justify-center gap-2 sm:gap-4 mb-12 overflow-x-auto pb-2">
          {TABS.map((tab) => {
            const Icon = tab.icon;
            const isActive = tab.id === activeTab;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-2 px-5 py-3 rounded-2xl text-xs sm:text-sm font-bold transition whitespace-nowrap shadow-xs ${
                  isActive
                    ? "bg-emerald-700 text-white shadow-lg shadow-emerald-700/25 scale-105"
                    : "bg-white text-slate-700 hover:bg-slate-100 border border-slate-200"
                }`}
              >
                <span>{tab.name}</span>
              </button>
            );
          })}
        </div>

        {/* Interactive Showcase Box */}
        <div className="bg-white rounded-3xl p-6 sm:p-10 border border-slate-200 shadow-xl">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-center">
            {/* Left Column: Feature Narrative */}
            <div className="lg:col-span-7 space-y-6">
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-lg bg-emerald-50 text-emerald-800 text-xs font-bold">
                <Sparkles className="w-4 h-4 text-emerald-600" />
                <span>विशेष फीचर</span>
              </div>

              <h3 className="text-2xl sm:text-3xl font-bold text-slate-900 leading-snug">
                {currentTab.headline}
              </h3>

              <p className="text-slate-600 text-base leading-relaxed">
                {currentTab.description}
              </p>

              <div className="space-y-3 pt-2">
                {currentTab.points.map((point, idx) => (
                  <div key={idx} className="flex items-start gap-3">
                    <div className="p-1 rounded-full bg-emerald-100 text-emerald-700 mt-0.5">
                      <Check className="w-3.5 h-3.5" />
                    </div>
                    <span className="text-sm font-semibold text-slate-800">{point}</span>
                  </div>
                ))}
              </div>

              <div className="pt-4">
                <button
                  onClick={onOpenDownload}
                  className="px-6 py-3 rounded-xl bg-slate-900 hover:bg-black text-white font-bold text-sm shadow-md transition"
                >
                  इस फीचर का लाभ उठाने के लिए ऐप इंस्टॉल करें
                </button>
              </div>
            </div>

            {/* Right Column: Phone Screen Preview */}
            <div className="lg:col-span-5 flex justify-center">
              <div className="relative w-[280px] sm:w-[300px] rounded-[40px] p-3 bg-slate-900 shadow-2xl border-4 border-slate-800">
                {/* Notch */}
                <div className="absolute top-5 left-1/2 -translate-x-1/2 w-24 h-4 bg-slate-950 rounded-full z-20" />

                {/* Display Area */}
                <div className="relative bg-slate-50 rounded-[32px] overflow-hidden border border-slate-200 pt-7 pb-4 min-h-[440px] flex flex-col justify-between">
                  <div>
                    {/* Header bar */}
                    <div className="px-4 py-2 bg-gradient-to-r from-emerald-800 to-teal-900 text-white flex items-center justify-between">
                      <span className="text-xs font-bold">{currentTab.name}</span>
                      <span className="text-[10px] text-emerald-200">आपन गांव</span>
                    </div>

                    {/* Banner Image */}
                    <div className="relative h-28 mx-3 my-2 rounded-xl overflow-hidden shadow-xs">
                      <Image
                        src={currentTab.bannerImage}
                        alt="Feature Banner"
                        fill
                        sizes="280px"
                        className="object-cover"
                      />
                    </div>

                    {/* Dynamic Screen Content */}
                    {currentTab.screenContent}
                  </div>

                  {/* App Bottom Bar simulation */}
                  <div className="mx-3 mt-2 pt-2 border-t border-slate-200 flex items-center justify-around text-[10px] text-slate-500 font-semibold">
                    <span className="text-emerald-700 font-bold">🌾 होम</span>
                    <span>📰 समाचार</span>
                    <span>📢 नोटिस</span>
                    <span>👥 डायरेक्टरी</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
