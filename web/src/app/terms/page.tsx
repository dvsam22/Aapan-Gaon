import type { Metadata } from "next";
import Link from "next/link";
import { ArrowLeft, FileText, CheckCircle2 } from "lucide-react";
import { SITE_CONFIG } from "@/lib/seo";

export const metadata: Metadata = {
  title: "नियम एवं शर्तें (Terms & Conditions)",
  description: "आपन गांव (Aapan Gaon) ऐप के उपयोग की आधिकारिक नियम एवं शर्तें।",
};

export default function TermsPage() {
  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto bg-white rounded-3xl p-6 sm:p-10 border border-slate-200 shadow-sm">
        <Link
          href="/"
          className="inline-flex items-center gap-2 text-xs font-bold text-emerald-700 hover:text-emerald-800 mb-6 transition"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>मुख्य पृष्ठ पर वापस जाएं</span>
        </Link>

        <div className="flex items-center gap-3 mb-6 pb-6 border-b border-slate-100">
          <div className="p-3 rounded-2xl bg-emerald-50 text-emerald-700">
            <FileText className="w-7 h-7" />
          </div>
          <div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900">
              नियम एवं शर्तें (Terms & Conditions)
            </h1>
            <p className="text-xs text-slate-500 mt-1">
              अंतिम अपडेट: 24 अक्टूबर 2026 • आपन गांव (Aapan Gaon)
            </p>
          </div>
        </div>

        <div className="space-y-6 text-sm text-slate-700 leading-relaxed">
          <section>
            <h2 className="text-lg font-bold text-slate-900 mb-2">1. सेवा की शर्तें</h2>
            <p>
              आपन गांव ऐप को डाउनलोड, इंस्टॉल या उपयोग करके, आप इन नियमों और शर्तों का पूर्ण रूप से पालन करने के लिए सहमत होते हैं। यदि आप इन शर्तों से सहमत नहीं हैं, तो कृपया ऐप का उपयोग न करें।
            </p>
          </section>

          <section>
            <h2 className="text-lg font-bold text-slate-900 mb-2">2. सूचना का स्रोत (Disclaimer on Mandi & News)</h2>
            <p className="mb-2">
              ऐप में प्रदर्शित मंडी भाव भारत सरकार के आधिकारिक एगमार्कनेट (Agmarknet / data.gov.in) पोर्टल से प्राप्त किए जाते हैं। समाचार प्रमाणित मीडिया स्रोतों से संकलित किए जाते हैं।
            </p>
            <p>
              हम सूचना की सटीकता सुनिश्चित करने का हर संभव प्रयास करते हैं, परंतु स्थानीय बाज़ार परिस्थितियों के अनुसार कीमतों में तात्कालिक भिन्नता हो सकती है। किसी भी बड़े वित्तीय सौदे से पहले स्थानीय आढ़त या मंडी समिति से सीधे पुष्टि कर लें।
            </p>
          </section>

          <section>
            <h2 className="text-lg font-bold text-slate-900 mb-2">3. बौद्धिक संपदा (Intellectual Property)</h2>
            <p>
              आपन गांव ऐप का लोगो, डिज़ाइन, सॉफ्टवेयर कोड और ब्रांड नाम हमारे स्वामित्व में हैं। किसी भी अनाधिकृत प्रतिलिपि या रिवर्स-इंजीनियरिंग की अनुमति नहीं है।
            </p>
          </section>

          <section>
            <h2 className="text-lg font-bold text-slate-900 mb-2">4. संपर्क एवं सहायता</h2>
            <p>
              किसी भी सहायता, शिकायत या सुझाव के लिए आप हमारे सपोर्ट पते पर ईमेल कर सकते हैं:
            </p>
            <p className="mt-2 font-semibold text-emerald-800">
              ईमेल: <a href={`mailto:${SITE_CONFIG.supportEmail}`} className="underline">{SITE_CONFIG.supportEmail}</a>
            </p>
          </section>
        </div>
      </div>
    </div>
  );
}
