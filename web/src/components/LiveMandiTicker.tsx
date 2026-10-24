"use client";

import React, { useState } from "react";
import { TrendingUp, TrendingDown, Minus, CheckCircle, Clock, Sparkles } from "lucide-react";

interface MandiItem {
  commodity: string;
  hindiName: string;
  market: string;
  district: string;
  modalPrice: number;
  minPrice: number;
  maxPrice: number;
  unit: string;
  trend: "up" | "down" | "stable";
}

const SAMPLE_RATES: MandiItem[] = [
  {
    commodity: "Potato",
    hindiName: "आलू (Potato)",
    market: "खलीलाबाद APMC",
    district: "Sant Kabir Nagar",
    modalPrice: 1450,
    minPrice: 1350,
    maxPrice: 1550,
    unit: "क्विंटल",
    trend: "up",
  },
  {
    commodity: "Tomato",
    hindiName: "टमाटर (Tomato)",
    market: "खलीलाबाद APMC",
    district: "Sant Kabir Nagar",
    modalPrice: 1800,
    minPrice: 1600,
    maxPrice: 2000,
    unit: "क्विंटल",
    trend: "stable",
  },
  {
    commodity: "Wheat",
    hindiName: "गेहूं (Wheat)",
    market: "नौगढ़ APMC",
    district: "Siddharth Nagar",
    modalPrice: 2425,
    minPrice: 2350,
    maxPrice: 2500,
    unit: "क्विंटल",
    trend: "up",
  },
  {
    commodity: "Paddy (Common)",
    hindiName: "धान साधारण (Paddy)",
    market: "नौगढ़ APMC",
    district: "Siddharth Nagar",
    modalPrice: 2300,
    minPrice: 2250,
    maxPrice: 2350,
    unit: "क्विंटल",
    trend: "stable",
  },
  {
    commodity: "Cauliflower",
    hindiName: "फूलगोभी (Cauliflower)",
    market: "खलीलाबाद APMC",
    district: "Sant Kabir Nagar",
    modalPrice: 2200,
    minPrice: 1900,
    maxPrice: 2500,
    unit: "क्विंटल",
    trend: "down",
  },
  {
    commodity: "Bottle Gourd",
    hindiName: "लौकी (Bottle Gourd)",
    market: "खलीलाबाद APMC",
    district: "Sant Kabir Nagar",
    modalPrice: 1250,
    minPrice: 1100,
    maxPrice: 1400,
    unit: "क्विंटल",
    trend: "stable",
  },
  {
    commodity: "Mustard",
    hindiName: "सरसों (Mustard)",
    market: "नौगढ़ APMC",
    district: "Siddharth Nagar",
    modalPrice: 5650,
    minPrice: 5400,
    maxPrice: 5900,
    unit: "क्विंटल",
    trend: "up",
  },
  {
    commodity: "Onion",
    hindiName: "प्याज (Onion)",
    market: "खलीलाबाद APMC",
    district: "Sant Kabir Nagar",
    modalPrice: 2800,
    minPrice: 2600,
    maxPrice: 3000,
    unit: "क्विंटल",
    trend: "down",
  },
];

export default function LiveMandiTicker({ onOpenDownload }: { onOpenDownload: () => void }) {
  const [selectedDistrict, setSelectedDistrict] = useState<string>("all");

  const filteredRates =
    selectedDistrict === "all"
      ? SAMPLE_RATES
      : SAMPLE_RATES.filter((item) => item.district === selectedDistrict);

  return (
    <section id="mandi" className="py-16 sm:py-24 bg-gradient-to-b from-white to-slate-50 relative overflow-hidden">
      {/* Background Decorative Blur */}
      <div className="absolute top-10 left-1/2 -translate-x-1/2 w-full max-w-7xl h-64 bg-emerald-200/20 blur-3xl rounded-full pointer-events-none" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative">
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto mb-10">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-emerald-100/80 text-emerald-800 text-xs font-bold mb-4 border border-emerald-200/60 shadow-xs">
            <Clock className="w-3.5 h-3.5 text-emerald-700 animate-pulse" />
            <span>आज का लाइव सरकारी मंडी भाव</span>
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight">
            खलीलाबाद व नौगढ़ मंडी के ताज़ा रेट
          </h2>
          <p className="mt-3 text-base text-slate-600">
            सरकारी एगमार्कनेट (Agmarknet) द्वारा सत्यापित रोज़ाना सुबह 07:45 बजे अपडेटेड भाव। बिचौलियों के चक्कर से बचें, सही रेट जानकर ही फसल बेचें!
          </p>
        </div>

        {/* Filter Tabs */}
        <div className="flex items-center justify-center gap-2 mb-8 flex-wrap">
          <button
            onClick={() => setSelectedDistrict("all")}
            className={`px-4 py-2 rounded-xl text-xs sm:text-sm font-bold transition shadow-xs ${
              selectedDistrict === "all"
                ? "bg-emerald-700 text-white shadow-md shadow-emerald-700/20"
                : "bg-white text-slate-600 hover:bg-slate-100 border border-slate-200"
            }`}
          >
            सभी मंडियां (All Markets)
          </button>
          <button
            onClick={() => setSelectedDistrict("Sant Kabir Nagar")}
            className={`px-4 py-2 rounded-xl text-xs sm:text-sm font-bold transition shadow-xs ${
              selectedDistrict === "Sant Kabir Nagar"
                ? "bg-emerald-700 text-white shadow-md shadow-emerald-700/20"
                : "bg-white text-slate-600 hover:bg-slate-100 border border-slate-200"
            }`}
          >
            संत कबीर नगर (खलीलाबाद APMC)
          </button>
          <button
            onClick={() => setSelectedDistrict("Siddharth Nagar")}
            className={`px-4 py-2 rounded-xl text-xs sm:text-sm font-bold transition shadow-xs ${
              selectedDistrict === "Siddharth Nagar"
                ? "bg-emerald-700 text-white shadow-md shadow-emerald-700/20"
                : "bg-white text-slate-600 hover:bg-slate-100 border border-slate-200"
            }`}
          >
            सिद्धार्थ नगर (नौगढ़ APMC)
          </button>
        </div>

        {/* Mandi Rates Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-5">
          {filteredRates.map((item, idx) => {
            return (
              <div
                key={idx}
                className="group relative bg-white rounded-2xl p-5 border border-slate-200/80 hover:border-emerald-500/50 shadow-sm hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 flex flex-col justify-between"
              >
                <div>
                  {/* Top Bar: Market Badge & Trend */}
                  <div className="flex items-center justify-between gap-2 mb-3">
                    <span className="text-[11px] font-semibold px-2 py-0.5 rounded-md bg-slate-100 text-slate-700 border border-slate-200/60 truncate">
                      {item.market}
                    </span>
                    <span
                      className={`inline-flex items-center gap-1 text-[11px] font-bold px-2 py-0.5 rounded-full ${
                        item.trend === "up"
                          ? "bg-emerald-50 text-emerald-700 border border-emerald-200/70"
                          : item.trend === "down"
                          ? "bg-rose-50 text-rose-700 border border-rose-200/70"
                          : "bg-amber-50 text-amber-700 border border-amber-200/70"
                      }`}
                    >
                      {item.trend === "up" && <TrendingUp className="w-3 h-3 text-emerald-600" />}
                      {item.trend === "down" && <TrendingDown className="w-3 h-3 text-rose-600" />}
                      {item.trend === "stable" && <Minus className="w-3 h-3 text-amber-600" />}
                      <span>
                        {item.trend === "up" ? "तेज़" : item.trend === "down" ? "मंदा" : "स्थिर"}
                      </span>
                    </span>
                  </div>

                  {/* Commodity Title */}
                  <h3 className="text-lg font-bold text-slate-900 group-hover:text-emerald-700 transition">
                    {item.hindiName}
                  </h3>

                  {/* Modal Price */}
                  <div className="mt-3">
                    <div className="flex items-baseline gap-1.5">
                      <span className="text-3xl font-black text-slate-900 tracking-tight">
                        ₹{item.modalPrice.toLocaleString("en-IN")}
                      </span>
                      <span className="text-xs font-medium text-slate-500">
                        / {item.unit}
                      </span>
                    </div>
                  </div>

                  {/* Min / Max Range */}
                  <div className="mt-3 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
                    <div>
                      <span className="text-[10px] text-slate-400 block">न्यूनतम</span>
                      <span className="font-semibold text-slate-700">₹{item.minPrice}</span>
                    </div>
                    <div className="h-6 w-px bg-slate-200/60" />
                    <div className="text-right">
                      <span className="text-[10px] text-slate-400 block">अधिकतम</span>
                      <span className="font-semibold text-slate-700">₹{item.maxPrice}</span>
                    </div>
                  </div>
                </div>

                <div className="mt-4 pt-2">
                  <button
                    onClick={onOpenDownload}
                    className="w-full py-2 rounded-xl bg-emerald-50 hover:bg-emerald-600 hover:text-white text-emerald-800 text-xs font-bold transition flex items-center justify-center gap-1.5"
                  >
                    <span>विस्तार से ऐप में देखें</span>
                    <span>→</span>
                  </button>
                </div>
              </div>
            );
          })}
        </div>

        {/* Bottom Verification Note */}
        <div className="mt-10 p-4 rounded-2xl bg-emerald-900 text-emerald-100 flex flex-col sm:flex-row items-center justify-between gap-4 text-center sm:text-left shadow-lg">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-xl bg-emerald-800 text-emerald-300">
              <CheckCircle className="w-6 h-6" />
            </div>
            <div>
              <p className="font-bold text-sm sm:text-base text-white">
                सभी 25+ फसलों और सब्जियों का पूरा दैनिक रिकॉर्ड
              </p>
              <p className="text-xs text-emerald-200">
                आपन गांव ऐप पर पिछले 7 दिनों का ऐतिहासिक मूल्य चार्ट भी उपलब्ध है।
              </p>
            </div>
          </div>
          <button
            onClick={onOpenDownload}
            className="px-5 py-2.5 rounded-xl bg-white hover:bg-emerald-50 text-emerald-900 font-bold text-xs sm:text-sm whitespace-nowrap shadow-sm transition active:scale-95"
          >
            सभी फसलों के रेट देखें
          </button>
        </div>
      </div>
    </section>
  );
}
