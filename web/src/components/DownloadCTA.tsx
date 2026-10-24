"use client";

import React from "react";
import Image from "next/image";
import { Download, QrCode, ShieldCheck, Smartphone, CheckCircle } from "lucide-react";
import { SITE_CONFIG } from "@/lib/seo";

export default function DownloadCTA({ onOpenDownload }: { onOpenDownload: () => void }) {
  return (
    <section className="py-16 sm:py-24 bg-gradient-to-br from-emerald-900 via-emerald-800 to-teal-950 text-white relative overflow-hidden">
      {/* Background radial glow */}
      <div className="absolute top-0 right-0 w-96 h-96 bg-emerald-500/20 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-0 left-0 w-96 h-96 bg-teal-500/15 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="bg-white/10 backdrop-blur-xl rounded-3xl p-8 sm:p-12 border border-white/15 shadow-2xl">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
            {/* Left Column: Heading and CTAs */}
            <div className="lg:col-span-8 text-center lg:text-left space-y-6">
              <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-white/20 text-emerald-200 text-xs font-bold border border-white/20 shadow-xs">
                <Smartphone className="w-3.5 h-3.5" />
                <span>आज ही अपने गाँव को डिजिटल बनाएं</span>
              </div>

              <h2 className="text-3xl sm:text-4xl lg:text-5xl font-black text-white tracking-tight leading-tight">
                अभी डाउनलोड करें आपन गांव ऐप और पाएं रोज़ाना सबसे पहले मंडी भाव!
              </h2>

              <p className="text-base sm:text-lg text-emerald-100 max-w-2xl leading-relaxed">
                खलीलाबाद, नौगढ़ व महराजगंज के हज़ारों किसानों से जुड़ें। 100% मुफ़्त, सुरक्षित और आपकी अपनी भाषा में।
              </p>

              {/* Checkmarks */}
              <div className="flex flex-wrap items-center justify-center lg:justify-start gap-4 text-xs sm:text-sm font-semibold text-emerald-100">
                <div className="flex items-center gap-1.5">
                  <CheckCircle className="w-4 h-4 text-emerald-400" />
                  <span>100% नि:शुल्क</span>
                </div>
                <div className="flex items-center gap-1.5">
                  <CheckCircle className="w-4 h-4 text-emerald-400" />
                  <span>सुबह 7:45 नोटिफिकेशन</span>
                </div>
                <div className="flex items-center gap-1.5">
                  <CheckCircle className="w-4 h-4 text-emerald-400" />
                  <span>कोई विज्ञापन रुकावट नहीं</span>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="pt-2 flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-4">
                <a
                  href={SITE_CONFIG.playStoreUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="w-full sm:w-auto inline-flex items-center justify-center gap-3 px-8 py-4 rounded-2xl bg-white hover:bg-slate-100 text-slate-900 font-bold text-base shadow-xl transition-all transform hover:-translate-y-0.5 active:scale-98"
                >
                  <svg viewBox="0 0 24 24" className="w-6 h-6 fill-current text-slate-900">
                    <path d="M3.609 1.814L13.792 12 3.61 22.186a2.41 2.41 0 01-.61-1.636V3.45c0-.64.223-1.218.609-1.636zm11.238 11.241l2.457-2.457-11.89-6.8a1.868 1.868 0 00-.85-.248l10.283 9.505zm0 1.89l-10.283 9.505c.25.04.54-.04.85-.248l11.89-6.8-2.457-2.457zm2.464-1.464l3.193 1.823c1.026.586 1.026 1.543 0 2.129l-3.193 1.823-2.18-2.18 2.18-2.18z"/>
                  </svg>
                  <div className="text-left">
                    <span className="block text-[10px] uppercase tracking-wider text-slate-500 font-medium">Download on</span>
                    <span className="block text-base leading-tight font-bold">Google Play Store</span>
                  </div>
                </a>

                <button
                  onClick={onOpenDownload}
                  className="w-full sm:w-auto inline-flex items-center justify-center gap-2.5 px-6 py-4 rounded-2xl bg-emerald-800/80 hover:bg-emerald-800 text-white font-bold text-base border border-white/20 shadow-xs transition"
                >
                  <QrCode className="w-5 h-5 text-emerald-300" />
                  <span>QR कोड से स्कैन करें</span>
                </button>
              </div>
            </div>

            {/* Right Column: QR Code Box */}
            <div className="lg:col-span-4 flex justify-center">
              <div className="bg-white rounded-3xl p-6 shadow-2xl text-slate-900 text-center max-w-[240px] w-full border border-white/50">
                <div className="relative w-44 h-44 mx-auto mb-3 flex items-center justify-center bg-slate-50 rounded-2xl border border-slate-100 p-2">
                  <svg viewBox="0 0 100 100" className="w-full h-full text-slate-900">
                    <rect x="0" y="0" width="30" height="30" fill="none" stroke="currentColor" strokeWidth="6" rx="4"/>
                    <rect x="8" y="8" width="14" height="14" fill="currentColor" rx="2"/>
                    <rect x="70" y="0" width="30" height="30" fill="none" stroke="currentColor" strokeWidth="6" rx="4"/>
                    <rect x="78" y="8" width="14" height="14" fill="currentColor" rx="2"/>
                    <rect x="0" y="70" width="30" height="30" fill="none" stroke="currentColor" strokeWidth="6" rx="4"/>
                    <rect x="8" y="78" width="14" height="14" fill="currentColor" rx="2"/>
                    <circle cx="45" cy="15" r="3" fill="currentColor"/>
                    <circle cx="55" cy="15" r="3" fill="currentColor"/>
                    <circle cx="45" cy="45" r="4" fill="#16a34a"/>
                    <circle cx="65" cy="45" r="3" fill="currentColor"/>
                    <circle cx="75" cy="45" r="3" fill="currentColor"/>
                    <circle cx="45" cy="65" r="3" fill="currentColor"/>
                    <circle cx="75" cy="75" r="3" fill="currentColor"/>
                  </svg>
                </div>
                <p className="text-xs font-bold text-slate-800">
                  फोन कैमरे से स्कैन करें
                </p>
                <p className="text-[10px] text-slate-500 mt-0.5">
                  तुरंत Play Store डाउनलोड लिंक खुलेगा
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
