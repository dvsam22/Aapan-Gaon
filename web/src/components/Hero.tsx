"use client";

import React from "react";
import Image from "next/image";
import { Download, QrCode, Star, ShieldCheck, TrendingUp, Bell, Sparkles, MapPin, Check } from "lucide-react";
import { SITE_CONFIG } from "@/lib/seo";

export default function Hero({ onOpenDownload }: { onOpenDownload: () => void }) {
  return (
    <section className="relative pt-28 sm:pt-36 pb-16 sm:pb-24 overflow-hidden">
      {/* Background radial gradients */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-full max-w-7xl h-[600px] pointer-events-none overflow-hidden -z-10">
        <div className="absolute top-10 left-1/4 w-96 h-96 bg-emerald-400/20 rounded-full blur-3xl animate-pulse-slow" />
        <div className="absolute top-32 right-1/4 w-96 h-96 bg-amber-300/20 rounded-full blur-3xl" />
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 w-[650px] h-64 bg-teal-400/15 rounded-full blur-3xl" />
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-8 items-center">
          {/* Left Column: Headlines & Call to Actions */}
          <div className="lg:col-span-7 text-center lg:text-left">
            {/* Live Status Pill */}
            <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-emerald-50 border border-emerald-200/80 shadow-xs mb-6 text-xs sm:text-sm font-semibold text-emerald-800">
              <span className="flex h-2.5 w-2.5 relative">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75" />
                <span className="relative inline-flex rounded-full h-2.5 w-2.5 bg-emerald-600" />
              </span>
              <span>🌾 पूर्वांचल व उत्तर प्रदेश के किसानों का सच्चा साथी</span>
            </div>

            {/* Main SEO H1 */}
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-black text-slate-900 tracking-tight leading-[1.15]">
              गाँव का समाचार और ताज़ा{" "}
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-700 via-emerald-600 to-teal-700">
                मंडी भाव
              </span>
              , अब आपके फोन में!
            </h1>

            {/* Subheading */}
            <p className="mt-5 text-base sm:text-lg text-slate-600 max-w-2xl mx-auto lg:mx-0 leading-relaxed">
              <strong>खलीलाबाद (संत कबीर नगर)</strong> व <strong>नौगढ़ (सिद्धार्थ नगर)</strong> के आलू, टमाटर, गेहूं, धान के रोज़ाना सरकारी रेट, <strong>महराजगंज</strong> की स्थानीय खबरें और ग्राम पंचायत नोटिस — सीधे अपने मोबाइल पर पाएं, <strong>100% मुफ़्त!</strong>
            </p>

            {/* Key Value Bullets */}
            <div className="mt-6 flex flex-wrap items-center justify-center lg:justify-start gap-4 text-xs sm:text-sm font-semibold text-slate-700">
              <div className="flex items-center gap-1.5 bg-white px-3 py-1.5 rounded-lg border border-slate-200/80 shadow-2xs">
                <Check className="w-4 h-4 text-emerald-600" />
                <span>सरकारी Agmarknet वेरिफाइड रेट</span>
              </div>
              <div className="flex items-center gap-1.5 bg-white px-3 py-1.5 rounded-lg border border-slate-200/80 shadow-2xs">
                <Check className="w-4 h-4 text-emerald-600" />
                <span>रोज़ सुबह 7:45 नोटिफिकेशन</span>
              </div>
              <div className="flex items-center gap-1.5 bg-white px-3 py-1.5 rounded-lg border border-slate-200/80 shadow-2xs">
                <Check className="w-4 h-4 text-emerald-600" />
                <span>2G/3G पर भी सुपरफ़ास्ट</span>
              </div>
            </div>

            {/* CTA Buttons */}
            <div className="mt-8 flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-4">
              <a
                href={SITE_CONFIG.playStoreUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="w-full sm:w-auto inline-flex items-center justify-center gap-3 px-8 py-4 rounded-2xl bg-slate-900 hover:bg-black text-white font-bold text-base shadow-xl shadow-slate-900/20 transition-all transform hover:-translate-y-0.5 active:scale-98"
              >
                <svg viewBox="0 0 24 24" className="w-6 h-6 fill-current text-white">
                  <path d="M3.609 1.814L13.792 12 3.61 22.186a2.41 2.41 0 01-.61-1.636V3.45c0-.64.223-1.218.609-1.636zm11.238 11.241l2.457-2.457-11.89-6.8a1.868 1.868 0 00-.85-.248l10.283 9.505zm0 1.89l-10.283 9.505c.25.04.54-.04.85-.248l11.89-6.8-2.457-2.457zm2.464-1.464l3.193 1.823c1.026.586 1.026 1.543 0 2.129l-3.193 1.823-2.18-2.18 2.18-2.18z"/>
                </svg>
                <div className="text-left">
                  <span className="block text-[10px] uppercase tracking-wider text-slate-300 font-medium">Download on</span>
                  <span className="block text-base leading-tight font-bold">Google Play</span>
                </div>
              </a>

              <button
                onClick={onOpenDownload}
                className="w-full sm:w-auto inline-flex items-center justify-center gap-2.5 px-6 py-4 rounded-2xl bg-white hover:bg-slate-50 text-slate-800 font-bold text-base border-2 border-slate-200/80 shadow-xs transition hover:border-emerald-500/50"
              >
                <QrCode className="w-5 h-5 text-emerald-700" />
                <span>QR कोड स्कैन करें</span>
              </button>
            </div>

            {/* Social Trust Metrics */}
            <div className="mt-8 pt-6 border-t border-slate-200/60 flex items-center justify-center lg:justify-start gap-6 text-xs text-slate-500 font-medium">
              <div className="flex items-center gap-1.5">
                <div className="flex text-amber-500">
                  {[...Array(5)].map((_, i) => (
                    <Star key={i} className="w-4 h-4 fill-current" />
                  ))}
                </div>
                <span className="font-bold text-slate-800 text-sm">4.8 / 5</span>
              </div>
              <div className="h-4 w-px bg-slate-200" />
              <div className="flex items-center gap-1.5">
                <ShieldCheck className="w-4 h-4 text-emerald-600" />
                <span>100% सेफ व सुरक्षित</span>
              </div>
              <div className="h-4 w-px bg-slate-200" />
              <span>गाँवों का विश्वसनीय मंच</span>
            </div>
          </div>

          {/* Right Column: Realistic 3D App Mockup Preview */}
          <div className="lg:col-span-5 flex justify-center relative">
            {/* Floating Top Badge */}
            <div className="absolute -top-4 -left-4 sm:-left-8 z-20 bg-white/95 backdrop-blur-md p-3.5 rounded-2xl shadow-xl border border-emerald-100 flex items-center gap-3 animate-bounce duration-1000">
              <div className="w-10 h-10 rounded-xl bg-emerald-100 flex items-center justify-center text-emerald-700 font-bold">
                <TrendingUp className="w-5 h-5" />
              </div>
              <div>
                <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">आज का मंडी भाव</p>
                <p className="text-sm font-black text-slate-900">आलू: ₹1,450 / क्विंटल</p>
              </div>
            </div>

            {/* Floating Bottom Badge */}
            <div className="absolute -bottom-4 -right-4 sm:-right-8 z-20 bg-white/95 backdrop-blur-md p-3.5 rounded-2xl shadow-xl border border-amber-100 flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-amber-100 flex items-center justify-center text-amber-700 font-bold">
                <Bell className="w-5 h-5" />
              </div>
              <div>
                <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">ताज़ा सूचना</p>
                <p className="text-sm font-bold text-slate-900">🌾 आज सुबह का भाव अपडेटेड</p>
              </div>
            </div>

            {/* Phone Frame */}
            <div className="relative w-[300px] sm:w-[320px] rounded-[44px] p-3.5 bg-slate-900 shadow-2xl shadow-slate-900/40 border-4 border-slate-800">
              {/* Dynamic Island / Notch */}
              <div className="absolute top-6 left-1/2 -translate-x-1/2 w-28 h-5 bg-slate-950 rounded-full z-30" />

              {/* Screen Content */}
              <div className="relative bg-slate-50 rounded-[34px] overflow-hidden border border-slate-200 pt-7 pb-4 text-left">
                {/* App Status Header */}
                <div className="px-4 py-2 bg-gradient-to-r from-emerald-800 to-teal-900 text-white flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <div className="w-7 h-7 rounded-lg relative overflow-hidden bg-white/20">
                      <Image src="/logo.png" alt="Logo" fill sizes="28px" className="object-cover" />
                    </div>
                    <div>
                      <span className="text-xs font-bold block leading-none">आपन गांव</span>
                      <span className="text-[9px] text-emerald-200">संत कबीर नगर</span>
                    </div>
                  </div>
                  <span className="text-[10px] px-2 py-0.5 rounded-full bg-emerald-700 font-bold">लाइव</span>
                </div>

                {/* Banner inside Phone */}
                <div className="relative h-28 mx-3 my-2.5 rounded-xl overflow-hidden shadow-xs">
                  <Image
                    src="/banner_mandi_rates.jpg"
                    alt="Mandi Banner"
                    fill
                    sizes="300px"
                    className="object-cover"
                    priority
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent p-2.5 flex flex-col justify-end">
                    <span className="text-[10px] font-bold text-amber-300">दैनिक मंडी भाव</span>
                    <span className="text-xs font-bold text-white leading-tight">खलीलाबाद व नौगढ़ के नए रेट</span>
                  </div>
                </div>

                {/* Sample Rate Cards Inside Screen */}
                <div className="px-3 space-y-2">
                  <div className="p-2.5 bg-white rounded-xl border border-slate-200/80 shadow-2xs flex items-center justify-between">
                    <div>
                      <span className="text-xs font-bold text-slate-800 block">आलू (Potato)</span>
                      <span className="text-[10px] text-slate-500">खलीलाबाद APMC</span>
                    </div>
                    <div className="text-right">
                      <span className="text-xs font-black text-slate-900 block">₹1,450</span>
                      <span className="text-[9px] font-bold text-emerald-700">▲ ₹50 तेज़</span>
                    </div>
                  </div>

                  <div className="p-2.5 bg-white rounded-xl border border-slate-200/80 shadow-2xs flex items-center justify-between">
                    <div>
                      <span className="text-xs font-bold text-slate-800 block">टमाटर (Tomato)</span>
                      <span className="text-[10px] text-slate-500">खलीलाबाद APMC</span>
                    </div>
                    <div className="text-right">
                      <span className="text-xs font-black text-slate-900 block">₹1,800</span>
                      <span className="text-[9px] font-bold text-amber-700">● स्थिर</span>
                    </div>
                  </div>

                  <div className="p-2.5 bg-white rounded-xl border border-slate-200/80 shadow-2xs flex items-center justify-between">
                    <div>
                      <span className="text-xs font-bold text-slate-800 block">गेहूं (Wheat)</span>
                      <span className="text-[10px] text-slate-500">नौगढ़ APMC</span>
                    </div>
                    <div className="text-right">
                      <span className="text-xs font-black text-slate-900 block">₹2,425</span>
                      <span className="text-[9px] font-bold text-emerald-700">▲ ₹25 तेज़</span>
                    </div>
                  </div>
                </div>

                {/* Mini News Ticker Inside Phone */}
                <div className="mx-3 mt-2.5 p-2 bg-emerald-50/70 border border-emerald-100 rounded-xl">
                  <span className="text-[10px] font-bold text-emerald-800 block">📰 ताज़ा खबर: महराजगंज</span>
                  <span className="text-[10px] text-slate-600 line-clamp-1">त्योहार के बाद मंडियों में आवक में भारी उछाल...</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
