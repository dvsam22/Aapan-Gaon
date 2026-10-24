"use client";

import React from "react";
import { Star, Quote, CheckCircle2 } from "lucide-react";

const REVIEWS = [
  {
    name: "रमेश कुमार वर्मा",
    role: "प्रगतिशील किसान",
    location: "खलीलाबाद, संत कबीर नगर",
    rating: 5,
    text: "खलीलाबाद मंडी में आलू और गोभी बेचने से पहले अब मैं रोज़ सुबह आपन गांव ऐप पर भाव देख लेता हूँ। व्यापारी अब मुझे कम दाम नहीं दे पाते, इस ऐप से मेरी कमाई में हर क्विंटल पर ₹150-200 का सीधा फायदा हुआ है।",
  },
  {
    name: "धर्मेन्द्र सिंह",
    role: "धान व गेहूं उत्पादक",
    location: "नौगढ़, सिद्धार्थ नगर",
    rating: 5,
    text: "ऐप बहुत ही आसान है और बिना नेट के भी फोन नंबर दिख जाते हैं। पिछले महीने जब हमारे खेत में थ्रेशर की ज़रूरत थी, ऐप की डायरेक्टरी से तुरंत नंबर मिल गया। गाँव के हर किसान के मोबाइल में यह ऐप होना चाहिए।",
  },
  {
    name: "सुरेश यादव",
    role: "ग्राम पंचायत सदस्य",
    location: "सिसवा, महराजगंज",
    rating: 5,
    text: "गाँव की जो भी सरकारी योजना या ब्लॉक की सूचना आती है, हम तुरंत ऐप पर डाल देते हैं और पूरे गाँव को नोटिफिकेशन मिल जाता है। अब किसी को सूचना के लिए लाउडस्पीकर की ज़रूरत नहीं पड़ती।",
  },
];

export default function Testimonials() {
  return (
    <section className="py-16 sm:py-24 bg-slate-50 relative overflow-hidden">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full bg-emerald-100 text-emerald-800 text-xs font-bold mb-3">
            <span>किसान भाइयों का अनुभव</span>
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight">
            हमारे गाँव के लोग क्या कहते हैं?
          </h2>
          <p className="mt-3 text-base text-slate-600">
            पूर्वांचल के सैकड़ों किसान और ग्रामीण परिवार आपन गांव ऐप के साथ रोज़ाना डिजिटल सुविधा का लाभ ले रहे हैं।
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {REVIEWS.map((review, idx) => (
            <div
              key={idx}
              className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm hover:shadow-xl transition-all duration-300 flex flex-col justify-between"
            >
              <div>
                {/* Rating Stars */}
                <div className="flex items-center gap-1 text-amber-500 mb-4">
                  {[...Array(review.rating)].map((_, i) => (
                    <Star key={i} className="w-4 h-4 fill-current" />
                  ))}
                </div>

                {/* Review Text */}
                <p className="text-sm text-slate-700 leading-relaxed italic">
                  &ldquo;{review.text}&rdquo;
                </p>
              </div>

              {/* Author Info */}
              <div className="mt-6 pt-4 border-t border-slate-100 flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-emerald-100 text-emerald-800 font-bold flex items-center justify-center text-sm">
                  {review.name.charAt(0)}
                </div>
                <div>
                  <h4 className="text-sm font-bold text-slate-900">{review.name}</h4>
                  <p className="text-xs text-slate-500">{review.role} • {review.location}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
