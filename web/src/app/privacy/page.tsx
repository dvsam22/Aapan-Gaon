import type { Metadata } from "next";
import Link from "next/link";
import { ArrowLeft, Shield, Lock, Eye, FileCheck } from "lucide-react";
import { SITE_CONFIG } from "@/lib/seo";

export const metadata: Metadata = {
  title: "प्राइवेसी पॉलिसी (Privacy Policy)",
  description:
    "आपन गांव (Aapan Gaon) ऐप की आधिकारिक प्राइवेसी पॉलिसी। हम आपके डेटा की गोपनीयता और सुरक्षा को प्राथमिकता देते हैं।",
};

export default function PrivacyPage() {
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
            <Shield className="w-7 h-7" />
          </div>
          <div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900">
              प्राइवेसी पॉलिसी (Privacy Policy)
            </h1>
            <p className="text-xs text-slate-500 mt-1">
              अंतिम अपडेट: 24 अक्टूबर 2026 • आपन गांव (Aapan Gaon)
            </p>
          </div>
        </div>

        <div className="space-y-6 text-sm text-slate-700 leading-relaxed">
          <section>
            <h2 className="text-lg font-bold text-slate-900 mb-2">1. प्रस्तावना</h2>
            <p>
              आपन गांव (&ldquo;Aapan Gaon&rdquo;, &ldquo;हम&rdquo;, या &ldquo;ऐप&rdquo;) आपके व्यक्तिगत डेटा की गोपनीयता और सुरक्षा का पूरा सम्मान करता है। यह प्राइवेसी पॉलिसी बताती है कि जब आप हमारे मोबाइल एप्लिकेशन का उपयोग करते हैं, तो हम किस प्रकार की जानकारी एकत्र करते हैं और उसका उपयोग कैसे किया जाता है।
            </p>
          </section>

          <section>
            <h2 className="text-lg font-bold text-slate-900 mb-2">2. एकत्र की जाने वाली जानकारी</h2>
            <p className="mb-2">
              हम केवल वही जानकारी एकत्र करते हैं जो ऐप की आवश्यक सुविधाओं को सही ढंग से संचालित करने के लिए ज़रूरी है:
            </p>
            <ul className="list-disc pl-5 space-y-1.5 text-slate-600">
              <li>
                <strong>चयनित जिला एवं गाँव:</strong> आपके क्षेत्र के अनुसार सही मंडी भाव (खलीलाबाद, नौगढ़) और स्थानीय खबरें दिखाने के लिए।
              </li>
              <li>
                <strong>भाषा प्राथमिकता:</strong> ऐप की सामग्री को आपकी पसंदीदा भाषा (हिंदी/अंग्रेजी) में प्रदर्शित करने के लिए।
              </li>
              <li>
                <strong>डिवाइस टोकन (FCM):</strong> रोज़ाना सुबह 07:45 बजे ताज़ा मंडी भाव और आपातकालीन नोटिस का नोटिफिकेशन भेजने के लिए।
              </li>
            </ul>
          </section>

          <section>
            <h2 className="text-lg font-bold text-slate-900 mb-2">3. जानकारी का उपयोग</h2>
            <p>
              हम आपकी किसी भी व्यक्तिगत जानकारी को किसी तीसरे पक्ष (Third Party) को नहीं बेचते और न ही इसका दुरुपयोग करते हैं। एकत्र की गई जानकारी का उपयोग केवल ऐप की कार्यक्षमता को बेहतर बनाने और आपको सही समय पर सूचनाएं उपलब्ध कराने के लिए किया जाता है।
            </p>
          </section>

          <section>
            <h2 className="text-lg font-bold text-slate-900 mb-2">4. डेटा सुरक्षा (Data Security)</h2>
            <p>
              हम आपके डेटा को Google Firebase के सुरक्षित क्लाउड सर्वर पर एन्क्रिप्टेड रूप में रखते हैं। हम उद्योग-मानक सुरक्षा प्रोटोकॉल का पालन करते हैं ताकि किसी भी अनाधिकृत पहुँच को रोका जा सके।
            </p>
          </section>

          <section>
            <h2 className="text-lg font-bold text-slate-900 mb-2">5. संपर्क करें</h2>
            <p>
              यदि आपके पास इस प्राइवेसी पॉलिसी से संबंधित कोई प्रश्न या सुझाव है, तो आप हमसे सीधे संपर्क कर सकते हैं:
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
