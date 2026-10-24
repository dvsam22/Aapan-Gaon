"use client";

import React, { useState } from "react";
import Navbar from "@/components/Navbar";
import Hero from "@/components/Hero";
import LiveMandiTicker from "@/components/LiveMandiTicker";
import FeaturesGrid from "@/components/FeaturesGrid";
import InteractiveShowcase from "@/components/InteractiveShowcase";
import ComparisonSection from "@/components/ComparisonSection";
import Testimonials from "@/components/Testimonials";
import FaqSection from "@/components/FaqSection";
import DownloadCTA from "@/components/DownloadCTA";
import Footer from "@/components/Footer";
import DownloadModal from "@/components/DownloadModal";

export default function HomePage() {
  const [downloadModalOpen, setDownloadModalOpen] = useState(false);

  const handleOpenDownload = () => {
    setDownloadModalOpen(true);
  };

  const handleCloseDownload = () => {
    setDownloadModalOpen(false);
  };

  return (
    <div className="flex flex-col min-h-screen">
      {/* Navigation */}
      <Navbar onOpenDownload={handleOpenDownload} />

      {/* Main Content Area */}
      <main className="flex-grow">
        {/* Hero Section */}
        <Hero onOpenDownload={handleOpenDownload} />

        {/* Live Mandi Bhav Ticker & Sample Rates */}
        <LiveMandiTicker onOpenDownload={handleOpenDownload} />

        {/* Key Features Grid */}
        <FeaturesGrid onOpenDownload={handleOpenDownload} />

        {/* Interactive App Screen Switcher */}
        <InteractiveShowcase onOpenDownload={handleOpenDownload} />

        {/* Why Aapan Gaon / Before vs After Comparison */}
        <ComparisonSection onOpenDownload={handleOpenDownload} />

        {/* Villager & Farmer Testimonials */}
        <Testimonials />

        {/* SEO-Optimized FAQ Section */}
        <FaqSection />

        {/* High-Impact Download CTA */}
        <DownloadCTA onOpenDownload={handleOpenDownload} />
      </main>

      {/* Footer */}
      <Footer onOpenDownload={handleOpenDownload} />

      {/* Interactive Download / QR Code Modal */}
      <DownloadModal isOpen={downloadModalOpen} onClose={handleCloseDownload} />
    </div>
  );
}
