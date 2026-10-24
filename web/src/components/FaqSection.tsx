"use client";

import React, { useState } from "react";
import { ChevronDown, HelpCircle } from "lucide-react";

interface FaqItem {
  question: string;
  answer: string;
}

const FAQS: FaqItem[] = [
  {
    question: "खलीलाबाद और नौगढ़ मंडी का आज का ताज़ा भाव कैसे देखें?",
    answer:
      "आपन गांव (Aapan Gaon) ऐप डाउनलोड करके आप खलीलाबाद (संत कबीर नगर) और नौगढ़ (सिद्धार्थ नगर) APMC मंडी के आलू, टमाटर, प्याज, गोभी, गेहूं, धान आदि सभी 25+ फसलों के आज के न्यूनतम, अधिकतम और मॉडल भाव रोज़ाना सुबह सबसे पहले देख सकते हैं।",
  },
  {
    question: "क्या आपन गांव ऐप बिल्कुल मुफ़्त (Free) है?",
    answer:
      "हाँ! आपन गांव ऐप हमारे गाँव के सभी किसान भाइयों, व्यापारियों और ग्रामीण परिवारों के लिए 100% मुफ़्त है। इसके उपयोग या किसी भी फीचर के लिए कोई भी गुप्त शुल्क नहीं लिया जाता।",
  },
  {
    question: "मंडी भाव रोज़ाना कितने बजे अपडेट होता है?",
    answer:
      "आपन गांव ऐप में सरकारी एगमार्कनेट (Agmarknet) पोर्टल से रोज़ाना 4 बार भाव स्वतः अपडेट होता है—सुबह 07:45 AM, 11:45 AM, 03:45 PM और 07:45 PM पर। सुबह ताज़ा भाव आते ही आपके फोन पर तुरंत नोटिफिकेशन आ जाता है।",
  },
  {
    question: "क्या ऐप में अपने गाँव की स्थानीय खबरें भी मिलती हैं?",
    answer:
      "हाँ, ऐप में महराजगंज, संत कबीर नगर, सिद्धार्थ नगर और आसपास के ग्रामीण क्षेत्रों की प्रमाणित ताज़ा स्थानीय खबरें, मौसम अलर्ट, ग्राम पंचायत की घोषणाएं और नोटिस हर 2 घंटे में अपडेट की जाती हैं।",
  },
  {
    question: "क्या ऐप स्लो या 2G/3G इंटरनेट पर भी काम करेगा?",
    answer:
      "बिल्कुल! आपन गांव ऐप को खासतौर पर ग्रामीण इंटरनेट परिस्थितियों को ध्यान में रखकर बेहद हल्का और सुपरफ़ास्ट बनाया गया है, जो 2G/3G नेटवर्क पर भी बिना अटके बहुत तेज़ी से खुलता है और डेटा कम खर्च करता है।",
  },
  {
    question: "आपन गांव ऐप को कैसे डाउनलोड और इंस्टॉल करें?",
    answer:
      "आप इस वेबसाइट पर दिए गए 'Google Play Store से डाउनलोड करें' बटन पर क्लिक करके सीधे प्ले स्टोर से इंस्टॉल कर सकते हैं, या ऊपर दिए गए QR कोड को अपने मोबाइल कैमरे से स्कैन करके भी 10 सेकंड में डाउनलोड कर सकते हैं।",
  },
];

export default function FaqSection() {
  const [openIndex, setOpenIndex] = useState<number | null>(0);

  const toggleFaq = (idx: number) => {
    setOpenIndex(openIndex === idx ? null : idx);
  };

  return (
    <section id="faq" className="py-16 sm:py-24 bg-white relative">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-14">
          <div className="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full bg-slate-100 text-slate-800 text-xs font-bold mb-3 border border-slate-200">
            <HelpCircle className="w-3.5 h-3.5 text-emerald-600" />
            <span>अक्सर पूछे जाने वाले सवाल</span>
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight">
            सामान्य प्रश्न एवं उत्तर (FAQs)
          </h2>
          <p className="mt-3 text-base text-slate-600">
            आपन गांव ऐप और मंडी भाव से जुड़े आपके सभी सवालों के आसान जवाब।
          </p>
        </div>

        {/* Accordion List */}
        <div className="space-y-4">
          {FAQS.map((faq, idx) => {
            const isOpen = openIndex === idx;
            return (
              <div
                key={idx}
                className={`rounded-2xl border transition-all duration-200 overflow-hidden ${
                  isOpen
                    ? "bg-emerald-50/40 border-emerald-300 shadow-sm"
                    : "bg-slate-50/70 border-slate-200 hover:border-slate-300"
                }`}
              >
                <button
                  onClick={() => toggleFaq(idx)}
                  className="w-full py-4 sm:py-5 px-5 sm:px-6 text-left flex items-center justify-between gap-4 font-bold text-slate-900 focus:outline-hidden"
                  aria-expanded={isOpen}
                >
                  <span className="text-base sm:text-lg leading-snug">
                    {faq.question}
                  </span>
                  <div
                    className={`p-1.5 rounded-full bg-white text-slate-600 shadow-xs transition-transform duration-300 flex-shrink-0 ${
                      isOpen ? "rotate-180 text-emerald-700 bg-emerald-100" : ""
                    }`}
                  >
                    <ChevronDown className="w-4 h-4" />
                  </div>
                </button>

                {isOpen && (
                  <div className="px-5 sm:px-6 pb-5 pt-1 text-sm sm:text-base text-slate-600 leading-relaxed border-t border-emerald-100/60 animate-in fade-in duration-200">
                    {faq.answer}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
}
