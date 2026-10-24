"use client";

import React, { useState } from "react";
import Image from "next/image";
import { X, Smartphone, Download, QrCode, ShieldCheck, CheckCircle2 } from "lucide-react";
import { SITE_CONFIG } from "@/lib/seo";

interface DownloadModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function DownloadModal({ isOpen, onClose }: DownloadModalProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm animate-in fade-in duration-200">
      <div 
        className="relative w-full max-w-md bg-white rounded-3xl p-6 sm:p-8 shadow-2xl border border-slate-100 overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Close Button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100 transition"
          aria-label="Close modal"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Header */}
        <div className="text-center">
          <div className="inline-flex p-3 rounded-2xl bg-emerald-50 text-emerald-700 mb-3 shadow-inner">
            <Smartphone className="w-8 h-8" />
          </div>
          <h3 className="text-2xl font-bold text-slate-900">
            आपन गांव ऐप इंस्टॉल करें
          </h3>
          <p className="text-sm text-slate-600 mt-1">
            अपने मोबाइल पर ताज़ा मंडी भाव और गाँव की हर खबर पाएं
          </p>
        </div>

        {/* QR Code Presentation */}
        <div className="my-6 p-5 bg-gradient-to-br from-emerald-50 to-teal-50/50 rounded-2xl border border-emerald-100 text-center flex flex-col items-center">
          <div className="bg-white p-3 rounded-2xl shadow-md border border-emerald-100/80 mb-3">
            {/* SVG Dynamic QR Code placeholder with Google Play destination */}
            <div className="relative w-40 h-40 flex items-center justify-center bg-white">
              <svg viewBox="0 0 100 100" className="w-full h-full text-slate-900">
                {/* QR Finder patterns */}
                <rect x="0" y="0" width="30" height="30" fill="none" stroke="currentColor" strokeWidth="6" rx="4"/>
                <rect x="8" y="8" width="14" height="14" fill="currentColor" rx="2"/>
                <rect x="70" y="0" width="30" height="30" fill="none" stroke="currentColor" strokeWidth="6" rx="4"/>
                <rect x="78" y="8" width="14" height="14" fill="currentColor" rx="2"/>
                <rect x="0" y="70" width="30" height="30" fill="none" stroke="currentColor" strokeWidth="6" rx="4"/>
                <rect x="8" y="78" width="14" height="14" fill="currentColor" rx="2"/>
                {/* QR Data Dots */}
                <circle cx="45" cy="15" r="3" fill="currentColor"/>
                <circle cx="55" cy="15" r="3" fill="currentColor"/>
                <circle cx="45" cy="25" r="3" fill="currentColor"/>
                <circle cx="15" cy="45" r="3" fill="currentColor"/>
                <circle cx="25" cy="45" r="3" fill="currentColor"/>
                <circle cx="35" cy="45" r="3" fill="currentColor"/>
                <circle cx="45" cy="45" r="4" fill="#16a34a"/>
                <circle cx="55" cy="45" r="3" fill="currentColor"/>
                <circle cx="65" cy="45" r="3" fill="currentColor"/>
                <circle cx="75" cy="45" r="3" fill="currentColor"/>
                <circle cx="85" cy="45" r="3" fill="currentColor"/>
                <circle cx="45" cy="55" r="3" fill="currentColor"/>
                <circle cx="55" cy="55" r="3" fill="currentColor"/>
                <circle cx="65" cy="55" r="3" fill="currentColor"/>
                <circle cx="75" cy="55" r="3" fill="currentColor"/>
                <circle cx="45" cy="65" r="3" fill="currentColor"/>
                <circle cx="55" cy="65" r="3" fill="currentColor"/>
                <circle cx="45" cy="75" r="3" fill="currentColor"/>
                <circle cx="55" cy="75" r="3" fill="currentColor"/>
                <circle cx="75" cy="75" r="3" fill="currentColor"/>
                <circle cx="85" cy="85" r="3" fill="currentColor"/>
              </svg>
            </div>
          </div>
          <div className="flex items-center gap-1.5 text-xs font-semibold text-emerald-800">
            <QrCode className="w-4 h-4 text-emerald-600" />
            <span>कैमरा या Google Lens से स्कैन करें</span>
          </div>
        </div>

        {/* Feature Badges */}
        <div className="grid grid-cols-2 gap-2 text-xs text-slate-600 mb-6 font-medium">
          <div className="flex items-center gap-1.5">
            <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
            <span>100% मुफ़्त व सुरक्षित</span>
          </div>
          <div className="flex items-center gap-1.5">
            <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
            <span>रोज़ाना सुबह 7:45 भाव</span>
          </div>
          <div className="flex items-center gap-1.5">
            <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
            <span>2G/3G पर भी तेज़</span>
          </div>
          <div className="flex items-center gap-1.5">
            <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
            <span>कोई गुप्त शुल्क नहीं</span>
          </div>
        </div>

        {/* Direct Action Buttons */}
        <div className="space-y-3">
          <a
            href={SITE_CONFIG.playStoreUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center justify-center gap-3 w-full py-3.5 px-4 bg-slate-900 hover:bg-black text-white font-semibold rounded-2xl shadow-lg transition active:scale-[0.98]"
          >
            <svg viewBox="0 0 24 24" className="w-5 h-5 fill-current">
              <path d="M3.609 1.814L13.792 12 3.61 22.186a2.41 2.41 0 01-.61-1.636V3.45c0-.64.223-1.218.609-1.636zm11.238 11.241l2.457-2.457-11.89-6.8a1.868 1.868 0 00-.85-.248l10.283 9.505zm0 1.89l-10.283 9.505c.25.04.54-.04.85-.248l11.89-6.8-2.457-2.457zm2.464-1.464l3.193 1.823c1.026.586 1.026 1.543 0 2.129l-3.193 1.823-2.18-2.18 2.18-2.18z"/>
            </svg>
            <span>Google Play Store पर जाएं</span>
          </a>

          <p className="text-center text-[11px] text-slate-400 flex items-center justify-center gap-1">
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
            Google Play Protect द्वारा सुरक्षित एवं सत्यापित
          </p>
        </div>
      </div>
    </div>
  );
}
