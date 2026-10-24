"use client";

import React, { useState, useEffect } from "react";
import Image from "next/image";
import Link from "next/link";
import { Menu, X, Download, Smartphone, Sparkles, TrendingUp, Newspaper, HelpCircle } from "lucide-react";
import { SITE_CONFIG } from "@/lib/seo";

interface NavbarProps {
  onOpenDownload: () => void;
}

export default function Navbar({ onOpenDownload }: NavbarProps) {
  const [isScrolled, setIsScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 20);
    };
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  const navLinks = [
    { name: "ताज़ा मंडी भाव", href: "#mandi", icon: TrendingUp },
    { name: "गाँव समाचार", href: "#news", icon: Newspaper },
    { name: "मुख्य सुविधाएं", href: "#features", icon: Sparkles },
    { name: "सवाल-जवाब", href: "#faq", icon: HelpCircle },
  ];

  return (
    <header
      className={`fixed top-0 left-0 right-0 z-40 transition-all duration-300 ${
        isScrolled
          ? "bg-white/90 backdrop-blur-md shadow-sm border-b border-slate-100 py-3"
          : "bg-transparent py-4 sm:py-5"
      }`}
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between">
          {/* Logo & Brand */}
          <Link href="/" className="flex items-center gap-3 group">
            <div className="relative w-11 h-11 rounded-2xl overflow-hidden shadow-md border-2 border-emerald-500/20 group-hover:scale-105 transition">
              <Image
                src="/logo.png"
                alt="Aapan Gaon Logo"
                fill
                sizes="44px"
                className="object-cover"
                priority
              />
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xl sm:text-2xl font-black tracking-tight text-slate-900 group-hover:text-emerald-700 transition">
                  आपन गांव
                </span>
                <span className="text-[10px] font-bold uppercase tracking-wider px-1.5 py-0.5 rounded-md bg-emerald-100 text-emerald-800 border border-emerald-200">
                  App
                </span>
              </div>
              <p className="text-xs text-slate-500 hidden sm:block font-medium">
                खलीलाबाद, नौगढ़ व पूर्वांचल
              </p>
            </div>
          </Link>

          {/* Desktop Navigation Links */}
          <nav className="hidden md:flex items-center gap-1 lg:gap-2">
            {navLinks.map((link) => {
              const Icon = link.icon;
              return (
                <a
                  key={link.name}
                  href={link.href}
                  className="flex items-center gap-1.5 px-3.5 py-2 rounded-xl text-sm font-semibold text-slate-600 hover:text-emerald-700 hover:bg-emerald-50/70 transition"
                >
                  <Icon className="w-4 h-4 text-emerald-600/70" />
                  <span>{link.name}</span>
                </a>
              );
            })}
          </nav>

          {/* Download CTA Button */}
          <div className="flex items-center gap-3">
            <button
              onClick={onOpenDownload}
              className="inline-flex items-center gap-2 px-4 sm:px-5 py-2.5 rounded-full bg-gradient-to-r from-emerald-600 to-teal-700 hover:from-emerald-700 hover:to-teal-800 text-white text-xs sm:text-sm font-bold shadow-md shadow-emerald-600/20 transition active:scale-95"
            >
              <Download className="w-4 h-4" />
              <span>ऐप डाउनलोड करें</span>
            </button>

            {/* Mobile Menu Toggle */}
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="md:hidden p-2 rounded-xl text-slate-700 hover:bg-slate-100 transition"
              aria-label="Toggle menu"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>

        {/* Mobile Dropdown Menu */}
        {mobileMenuOpen && (
          <div className="md:hidden mt-3 p-4 bg-white/95 backdrop-blur-md rounded-2xl border border-slate-100 shadow-xl animate-in slide-in-from-top-2 duration-200">
            <div className="flex flex-col gap-2">
              {navLinks.map((link) => {
                const Icon = link.icon;
                return (
                  <a
                    key={link.name}
                    href={link.href}
                    onClick={() => setMobileMenuOpen(false)}
                    className="flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-semibold text-slate-700 hover:bg-emerald-50 hover:text-emerald-800 transition"
                  >
                    <Icon className="w-4 h-4 text-emerald-600" />
                    <span>{link.name}</span>
                  </a>
                );
              })}
              <div className="pt-2 border-t border-slate-100 mt-1">
                <button
                  onClick={() => {
                    setMobileMenuOpen(false);
                    onOpenDownload();
                  }}
                  className="w-full flex items-center justify-center gap-2 py-3 rounded-xl bg-emerald-600 text-white font-bold text-sm shadow-md"
                >
                  <Download className="w-4 h-4" />
                  <span>Google Play Store से इंस्टॉल करें</span>
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </header>
  );
}
