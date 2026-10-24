"use client";

import React from "react";
import Image from "next/image";
import Link from "next/link";
import { Mail, MapPin, Heart, Shield, FileText, Smartphone } from "lucide-react";
import { SITE_CONFIG } from "@/lib/seo";

export default function Footer({ onOpenDownload }: { onOpenDownload: () => void }) {
  return (
    <footer className="bg-slate-950 text-slate-400 text-sm pt-16 pb-12 border-t border-slate-900">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-10 pb-12 border-b border-slate-900">
          {/* Brand Info */}
          <div className="lg:col-span-2 space-y-4">
            <Link href="/" className="flex items-center gap-3">
              <div className="relative w-10 h-10 rounded-2xl overflow-hidden border border-emerald-500/30">
                <Image src="/logo.png" alt="Logo" fill sizes="40px" className="object-cover" />
              </div>
              <span className="text-xl font-bold text-white tracking-tight">
                आपन गांव (Aapan Gaon)
              </span>
            </Link>
            <p className="text-xs sm:text-sm text-slate-400 leading-relaxed max-w-sm">
              ग्रामीण भारत और किसानों के लिए एक समर्पित डिजिटल मंच। खलीलाबाद व नौगढ़ मंडी के ताज़ा भाव, गाँव का समाचार, सरकारी योजनाएं और इमरजेंसी डायरेक्टरी।
            </p>
            <div className="flex items-center gap-2 text-xs text-slate-400">
              <Mail className="w-4 h-4 text-emerald-400" />
              <a href={`mailto:${SITE_CONFIG.supportEmail}`} className="hover:text-emerald-400 transition">
                {SITE_CONFIG.supportEmail}
              </a>
            </div>
          </div>

          {/* District Coverage for Local SEO */}
          <div>
            <h4 className="text-white text-sm font-bold uppercase tracking-wider mb-4">
              कवर्ड जिले व मंडियां
            </h4>
            <ul className="space-y-2 text-xs">
              <li className="flex items-center gap-1.5 text-slate-300">
                <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />
                <span>संत कबीर नगर (खलीलाबाद APMC)</span>
              </li>
              <li className="flex items-center gap-1.5 text-slate-300">
                <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />
                <span>सिद्धार्थ नगर (नौगढ़ APMC)</span>
              </li>
              <li className="flex items-center gap-1.5 text-slate-300">
                <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />
                <span>महराजगंज (स्थानीय समाचार)</span>
              </li>
              <li className="flex items-center gap-1.5 text-slate-400">
                <span className="w-1.5 h-1.5 rounded-full bg-slate-600" />
                <span>गोरखपुर व बस्ती (जल्द आ रहा है)</span>
              </li>
            </ul>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="text-white text-sm font-bold uppercase tracking-wider mb-4">
              महत्वपूर्ण लिंक्स
            </h4>
            <ul className="space-y-2 text-xs">
              <li>
                <a href="#mandi" className="hover:text-emerald-400 transition">
                  आज का मंडी भाव
                </a>
              </li>
              <li>
                <a href="#news" className="hover:text-emerald-400 transition">
                  गाँव समाचार
                </a>
              </li>
              <li>
                <a href="#features" className="hover:text-emerald-400 transition">
                  ऐप की सुविधाएं
                </a>
              </li>
              <li>
                <a href="#faq" className="hover:text-emerald-400 transition">
                  सवाल और जवाब
                </a>
              </li>
            </ul>
          </div>

          {/* Legal & Trust */}
          <div>
            <h4 className="text-white text-sm font-bold uppercase tracking-wider mb-4">
              नीति व शर्तें
            </h4>
            <ul className="space-y-2 text-xs">
              <li>
                <Link href="/privacy" className="hover:text-emerald-400 transition flex items-center gap-1.5">
                  <Shield className="w-3.5 h-3.5" />
                  <span>प्राइवेसी पॉलिसी (Privacy)</span>
                </Link>
              </li>
              <li>
                <Link href="/terms" className="hover:text-emerald-400 transition flex items-center gap-1.5">
                  <FileText className="w-3.5 h-3.5" />
                  <span>नियम व शर्तें (Terms)</span>
                </Link>
              </li>
              <li className="pt-2">
                <button
                  onClick={onOpenDownload}
                  className="px-3 py-1.5 rounded-lg bg-emerald-900/80 hover:bg-emerald-800 text-emerald-200 text-xs font-bold transition flex items-center gap-1.5"
                >
                  <Smartphone className="w-3.5 h-3.5" />
                  <span>गूगल प्ले स्टोर से डाउनलोड</span>
                </button>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom Disclaimer & Copyright */}
        <div className="pt-8 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-slate-500">
          <p>© 2026 आपन गांव (Aapan Gaon). सर्वाधिकार सुरक्षित।</p>
          <p className="flex items-center gap-1">
            <span>किसानों और गाँव के विकास के लिए समर्पित</span>
            <Heart className="w-3.5 h-3.5 text-rose-500 fill-current" />
          </p>
        </div>
      </div>
    </footer>
  );
}
