"use client";

import React from "react";
import { XCircle, CheckCircle2, TrendingUp, Sparkles } from "lucide-react";

export default function ComparisonSection({ onOpenDownload }: { onOpenDownload: () => void }) {
  const COMPARISONS = [
    {
      problem: "मंडी का भाव जानने के लिए आढ़तियों या व्यापारियों के फोन का इंतज़ार करना पड़ता था।",
      solution: "रोज़ सुबह 07:45 बजे सरकारी Agmarknet पोर्टल से सीधा ताज़ा भाव आपके फोन पर मिल जाता है।",
    },
    {
      problem: "बिचौलिये कम दाम बताकर किसानों की मेहनत की कमाई में भारी कटौती कर लेते थे।",
      solution: "न्यूनतम, अधिकतम और मॉडल भाव जानकर किसान अपनी उपज का सही और पूरा दाम तय कर सकते हैं।",
    },
    {
      problem: "गाँव में कौन सी सरकारी योजना या नोटिस आया, ब्लॉक जाए बिना पता नहीं चलता था।",
      solution: "पीएम किसान, केसीसी, राशन और ग्राम पंचायत की हर सूचना ऐप पर तुरंत दिख जाती है।",
    },
    {
      problem: "इमरजेंसी में डॉक्टर, एम्बुलेंस या ट्रैक्टर वाले का नंबर खोजने में समय बर्बाद होता था।",
      solution: "गाँव के सभी आवश्यक नंबर सत्यापित डायरेक्टरी में 24 घंटे एक क्लिक पर उपलब्ध रहते हैं।",
    },
  ];

  return (
    <section className="py-16 sm:py-24 bg-white relative">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full bg-amber-100 text-amber-900 text-xs font-bold mb-3">
            <span>पारदर्शिता और सीधा फायदा</span>
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight">
            पहले क्या दिक्कत थी और आपन गांव से क्या बदला?
          </h2>
          <p className="mt-3 text-base text-slate-600">
            डिजिटल तकनीक से हमने गाँव के किसानों और परिवारों को बिचौलियों से आज़ाद कर आत्मनिर्भर बनाया है।
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 items-stretch">
          {/* Left Column: Old Way */}
          <div className="bg-rose-50/50 rounded-3xl p-6 sm:p-8 border border-rose-100 shadow-sm flex flex-col justify-between">
            <div>
              <div className="flex items-center gap-2 mb-6 text-rose-800">
                <XCircle className="w-6 h-6 text-rose-600" />
                <h3 className="text-xl font-bold">पहले की परेशानियां (बिना ऐप)</h3>
              </div>

              <div className="space-y-4">
                {COMPARISONS.map((item, idx) => (
                  <div key={idx} className="flex items-start gap-3 p-3.5 rounded-2xl bg-white/80 border border-rose-100/80">
                    <XCircle className="w-5 h-5 text-rose-500 flex-shrink-0 mt-0.5" />
                    <p className="text-sm font-medium text-slate-700 leading-relaxed">
                      {item.problem}
                    </p>
                  </div>
                ))}
              </div>
            </div>

            <div className="mt-6 pt-4 border-t border-rose-200/50 text-xs text-rose-700 font-semibold text-center">
              समय की बर्बादी और फसल का कम दाम मिलता था
            </div>
          </div>

          {/* Right Column: With Aapan Gaon App */}
          <div className="bg-emerald-50/60 rounded-3xl p-6 sm:p-8 border-2 border-emerald-500/40 shadow-xl flex flex-col justify-between relative overflow-hidden">
            <div className="absolute top-0 right-0 w-32 h-32 bg-emerald-300/20 rounded-full blur-2xl pointer-events-none" />

            <div>
              <div className="flex items-center gap-2 mb-6 text-emerald-900">
                <CheckCircle2 className="w-6 h-6 text-emerald-600" />
                <h3 className="text-xl font-bold">आपन गांव ऐप के साथ (नया डिजिटल गाँव)</h3>
              </div>

              <div className="space-y-4">
                {COMPARISONS.map((item, idx) => (
                  <div key={idx} className="flex items-start gap-3 p-3.5 rounded-2xl bg-white border border-emerald-200 shadow-xs">
                    <CheckCircle2 className="w-5 h-5 text-emerald-600 flex-shrink-0 mt-0.5" />
                    <p className="text-sm font-bold text-slate-800 leading-relaxed">
                      {item.solution}
                    </p>
                  </div>
                ))}
              </div>
            </div>

            <div className="mt-6 pt-4 border-t border-emerald-200 flex flex-col sm:flex-row items-center justify-between gap-3">
              <span className="text-xs text-emerald-800 font-bold">
                100% मुफ़्त एवं किसानों के लिए समर्पित
              </span>
              <button
                onClick={onOpenDownload}
                className="w-full sm:w-auto px-5 py-2.5 rounded-xl bg-emerald-700 hover:bg-emerald-800 text-white font-bold text-xs shadow-md transition"
              >
                फायदा उठाएं (डाउनलोड करें)
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
