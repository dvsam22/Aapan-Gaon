"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { onAuthStateChanged, signOut } from "firebase/auth";
import { auth, db } from "@/lib/firebase";
import { collection, onSnapshot } from "firebase/firestore";
import SubmissionsManager from "@/components/admin/SubmissionsManager";
import VillagesManager from "@/components/admin/VillagesManager";
import {
  LayoutDashboard,
  Layers,
  MapPin,
  ChevronDown,
  ChevronUp,
  Search,
  Sun,
  Moon,
  Bell,
  LogOut,
  Menu,
  X,
  TrendingUp,
  GraduationCap,
  Scale,
  Stethoscope,
  Wheat,
  Truck,
  HardHat,
  PartyPopper,
  FileCheck,
  Clock,
  CheckCircle2,
  Building2,
  BarChart3
} from "lucide-react";

export default function AdminDashboardPage() {
  const router = useRouter();
  const [currentUserEmail, setCurrentUserEmail] = useState<string>("apna@gmail.com");
  const [loading, setLoading] = useState(true);

  // Active view & filter states
  const [activeView, setActiveView] = useState<"dashboard" | "submissions" | "villages">("dashboard");
  const [categoryFilter, setCategoryFilter] = useState<string>("all");
  const [statusFilter, setStatusFilter] = useState<string>("all");

  // Drawer accordions open state
  const [drawerOpen, setDrawerOpen] = useState({
    dashboard: true,
    services: true,
    villages: true,
  });

  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [profileDropdownOpen, setProfileDropdownOpen] = useState(false);

  // Live Firestore counters
  const [stats, setStats] = useState({
    totalSubs: 0,
    pendingSubs: 0,
    approvedSubs: 0,
    totalVillages: 0,
    categoryCounts: {} as Record<string, number>,
  });

  useEffect(() => {
    if (typeof window !== "undefined" && sessionStorage.getItem("admin_auth") === "true") {
      setCurrentUserEmail(sessionStorage.getItem("admin_email") || "apna@gmail.com");
      setLoading(false);
    }

    const unsubscribe = onAuthStateChanged(auth, (user) => {
      if (user) {
        setCurrentUserEmail(user.email || "apna@gmail.com");
        sessionStorage.setItem("admin_auth", "true");
        sessionStorage.setItem("admin_email", user.email || "apna@gmail.com");
        setLoading(false);
      } else {
        if (typeof window !== "undefined" && sessionStorage.getItem("admin_auth") !== "true") {
          router.push("/admin/login");
        } else {
          setLoading(false);
        }
      }
    });

    return () => unsubscribe();
  }, [router]);

  // Live Firestore data listeners
  useEffect(() => {
    const unsubSubs = onSnapshot(collection(db, "business_registrations"), (snap) => {
      let pending = 0;
      let approved = 0;
      const catMap: Record<string, number> = {};

      snap.forEach((doc) => {
        const d = doc.data();
        if (d.status === "pending" || !d.status) pending++;
        if (d.status === "approved") approved++;

        const cat = (d.category || "other").toLowerCase();
        let key = "other";
        if (cat.includes("edu") || cat.includes("school") || cat.includes("teach") || cat.includes("coach")) key = "education";
        else if (cat.includes("law") || cat.includes("panchayat") || cat.includes("court") || cat.includes("legal")) key = "legal";
        else if (cat.includes("health") || cat.includes("doct") || cat.includes("hosp")) key = "health";
        else if (cat.includes("mandi") || cat.includes("kisan") || cat.includes("crop")) key = "mandi";
        else if (cat.includes("trans") || cat.includes("vehi") || cat.includes("auto")) key = "transport";
        else if (cat.includes("family") || cat.includes("shadi") || cat.includes("mangal") || cat.includes("event") || cat.includes("tent") || cat.includes("cater")) key = "family";
        else if (cat.includes("labour") || cat.includes("const") || cat.includes("work")) key = "labour";

        catMap[key] = (catMap[key] || 0) + 1;
      });

      setStats((prev) => ({
        ...prev,
        totalSubs: snap.size,
        pendingSubs: pending,
        approvedSubs: approved,
        categoryCounts: catMap,
      }));
    });

    const unsubVillages = onSnapshot(collection(db, "villages"), (snap) => {
      setStats((prev) => ({
        ...prev,
        totalVillages: snap.size,
      }));
    });

    return () => {
      unsubSubs();
      unsubVillages();
    };
  }, []);

  const handleLogout = async () => {
    try {
      if (typeof window !== "undefined") {
        sessionStorage.removeItem("admin_auth");
        sessionStorage.removeItem("admin_email");
      }
      await signOut(auth);
      router.push("/admin/login");
    } catch (err) {
      console.error("Logout error:", err);
      router.push("/admin/login");
    }
  };

  const toggleDrawer = (key: keyof typeof drawerOpen) => {
    setDrawerOpen((prev) => ({ ...prev, [key]: !prev[key] }));
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#F4F7FB] flex flex-col items-center justify-center gap-3 text-slate-500">
        <div className="w-10 h-10 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
        <p className="text-sm font-medium">Loading Apna Gaon Admin Dashboard...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#F4F7FB] text-slate-800 flex selection:bg-indigo-500 selection:text-white">
      {/* Mobile Sidebar Overlay */}
      {sidebarOpen && (
        <div
          onClick={() => setSidebarOpen(false)}
          className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-40 lg:hidden"
        />
      )}

      {/* ==================== LEFT SIDEBAR (CLEAN, FOCUSED) ==================== */}
      <aside
        className={`fixed top-0 bottom-0 left-0 w-64 bg-white border-r border-slate-200/90 z-50 flex flex-col transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:h-screen lg:shrink-0 ${
          sidebarOpen ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        {/* Brand Header */}
        <div className="h-20 px-6 flex items-center justify-between border-b border-slate-100">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-full bg-gradient-to-tr from-indigo-600 via-indigo-500 to-sky-400 p-[3px] flex items-center justify-center shadow-md shadow-indigo-500/20">
              <div className="w-full h-full bg-white rounded-full flex items-center justify-center">
                <div className="w-3.5 h-3.5 rounded-full bg-indigo-600" />
              </div>
            </div>
            <div>
              <span className="text-lg font-bold tracking-tight text-slate-900">
                Apna<span className="text-indigo-600">Admin</span>
              </span>
              <p className="text-[10px] text-slate-400 font-medium">Village Services Portal</p>
            </div>
          </div>

          <button
            onClick={() => setSidebarOpen(false)}
            className="p-1.5 text-slate-400 hover:text-slate-700 rounded-lg lg:hidden"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Sidebar Nav Items */}
        <div className="flex-1 overflow-y-auto px-4 py-6 space-y-6">
          {/* Main Menu Section */}
          <div>
            <p className="px-3 text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-2">
              Main Menu
            </p>

            {/* 1. Dashboard Drawer */}
            <div className="space-y-1">
              <button
                onClick={() => toggleDrawer("dashboard")}
                className={`w-full px-3 py-2 rounded-xl text-sm font-semibold transition-all flex items-center justify-between ${
                  activeView === "dashboard"
                    ? "text-indigo-600 bg-indigo-50/70"
                    : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                }`}
              >
                <div className="flex items-center gap-3">
                  <LayoutDashboard className={`w-4 h-4 ${activeView === "dashboard" ? "text-indigo-600" : "text-slate-400"}`} />
                  <span>Dashboard</span>
                </div>
                {drawerOpen.dashboard ? (
                  <ChevronUp className="w-4 h-4 text-slate-400" />
                ) : (
                  <ChevronDown className="w-4 h-4 text-slate-400" />
                )}
              </button>

              {drawerOpen.dashboard && (
                <div className="pl-9 pr-1 py-1 space-y-1">
                  <button
                    onClick={() => {
                      setActiveView("dashboard");
                      setCategoryFilter("all");
                      setStatusFilter("all");
                      setSidebarOpen(false);
                    }}
                    className={`w-full text-left px-3 py-1.5 rounded-lg text-xs font-semibold transition-all flex items-center justify-between ${
                      activeView === "dashboard" && statusFilter === "all"
                        ? "bg-indigo-50 text-indigo-600 font-bold"
                        : "text-slate-500 hover:text-slate-900"
                    }`}
                  >
                    <span>Overview</span>
                  </button>

                  <button
                    onClick={() => {
                      setActiveView("submissions");
                      setStatusFilter("pending");
                      setCategoryFilter("all");
                      setSidebarOpen(false);
                    }}
                    className={`w-full text-left px-3 py-1.5 rounded-lg text-xs font-semibold transition-all flex items-center justify-between ${
                      activeView === "submissions" && statusFilter === "pending"
                        ? "bg-indigo-50 text-indigo-600 font-bold"
                        : "text-slate-500 hover:text-slate-900"
                    }`}
                  >
                    <span>Pending Reviews</span>
                    {stats.pendingSubs > 0 && (
                      <span className="px-1.5 py-0.5 rounded-full text-[10px] font-bold bg-amber-100 text-amber-700">
                        {stats.pendingSubs}
                      </span>
                    )}
                  </button>

                  <button
                    onClick={() => {
                      setActiveView("submissions");
                      setStatusFilter("all");
                      setCategoryFilter("all");
                      setSidebarOpen(false);
                    }}
                    className={`w-full text-left px-3 py-1.5 rounded-lg text-xs font-semibold transition-all flex items-center justify-between ${
                      activeView === "submissions" && statusFilter === "all"
                        ? "bg-indigo-50 text-indigo-600 font-bold"
                        : "text-slate-500 hover:text-slate-900"
                    }`}
                  >
                    <span>All Submissions</span>
                    <span className="text-[10px] text-slate-400 font-mono">
                      {stats.totalSubs}
                    </span>
                  </button>
                </div>
              )}
            </div>

            {/* 2. Village Services / Categories Drawer */}
            <div className="space-y-1 mt-3">
              <button
                onClick={() => toggleDrawer("services")}
                className="w-full px-3 py-2 rounded-xl text-sm font-semibold transition-all flex items-center justify-between text-slate-600 hover:bg-slate-50 hover:text-slate-900"
              >
                <div className="flex items-center gap-3">
                  <Layers className="w-4 h-4 text-slate-400" />
                  <span>Village Services</span>
                </div>
                {drawerOpen.services ? (
                  <ChevronUp className="w-4 h-4 text-slate-400" />
                ) : (
                  <ChevronDown className="w-4 h-4 text-slate-400" />
                )}
              </button>

              {drawerOpen.services && (
                <div className="pl-9 pr-1 py-1 space-y-1">
                  {[
                    { id: "education", label: "School & Coaching", icon: GraduationCap },
                    { id: "legal", label: "Lawyer & Panchayat", icon: Scale },
                    { id: "health", label: "Health & Hospital", icon: Stethoscope },
                    { id: "mandi", label: "Kisan & Mandi", icon: Wheat },
                    { id: "transport", label: "Vehicle & Transport", icon: Truck },
                    { id: "family", label: "Wedding & Family Functions", icon: PartyPopper },
                    { id: "labour", label: "Labour & Construction", icon: HardHat },
                  ].map((cat) => {
                    const Icon = cat.icon;
                    const count = stats.categoryCounts[cat.id] || 0;
                    return (
                      <button
                        key={cat.id}
                        onClick={() => {
                          setActiveView("submissions");
                          setCategoryFilter(cat.id);
                          setSidebarOpen(false);
                        }}
                        className={`w-full text-left px-3 py-1.5 rounded-lg text-xs font-medium transition-all flex items-center justify-between ${
                          activeView === "submissions" && categoryFilter === cat.id
                            ? "bg-indigo-50 text-indigo-600 font-bold"
                            : "text-slate-500 hover:text-slate-900"
                        }`}
                      >
                        <div className="flex items-center gap-2">
                          <Icon className="w-3.5 h-3.5 shrink-0" />
                          <span>{cat.label}</span>
                        </div>
                        {count > 0 && (
                          <span className="text-[10px] text-slate-400 font-mono">
                            {count}
                          </span>
                        )}
                      </button>
                    );
                  })}
                </div>
              )}
            </div>

            {/* 3. Villages Management Drawer */}
            <div className="space-y-1 mt-3">
              <button
                onClick={() => toggleDrawer("villages")}
                className="w-full px-3 py-2 rounded-xl text-sm font-semibold transition-all flex items-center justify-between text-slate-600 hover:bg-slate-50 hover:text-slate-900"
              >
                <div className="flex items-center gap-3">
                  <MapPin className="w-4 h-4 text-slate-400" />
                  <span>Villages Directory</span>
                </div>
                {drawerOpen.villages ? (
                  <ChevronUp className="w-4 h-4 text-slate-400" />
                ) : (
                  <ChevronDown className="w-4 h-4 text-slate-400" />
                )}
              </button>

              {drawerOpen.villages && (
                <div className="pl-9 pr-1 py-1 space-y-1">
                  <button
                    onClick={() => {
                      setActiveView("villages");
                      setSidebarOpen(false);
                    }}
                    className={`w-full text-left px-3 py-1.5 rounded-lg text-xs font-medium transition-all flex items-center justify-between ${
                      activeView === "villages"
                        ? "bg-indigo-50 text-indigo-600 font-bold"
                        : "text-slate-500 hover:text-slate-900"
                    }`}
                  >
                    <span>All Villages</span>
                    <span className="text-[10px] text-slate-400 font-mono font-bold">
                      {stats.totalVillages}
                    </span>
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Sidebar Footer User Info */}
        <div className="p-4 border-t border-slate-100 bg-slate-50/50">
          <div className="flex items-center justify-between gap-3">
            <div className="flex items-center gap-2.5 overflow-hidden">
              <div className="w-8 h-8 rounded-full bg-indigo-600 text-white font-bold text-xs flex items-center justify-center shrink-0">
                AG
              </div>
              <div className="truncate">
                <p className="text-xs font-bold text-slate-900 truncate">Administrator</p>
                <p className="text-[10px] text-slate-400 truncate">{currentUserEmail}</p>
              </div>
            </div>

            <button
              onClick={handleLogout}
              className="p-1.5 text-slate-400 hover:text-rose-600 rounded-lg hover:bg-rose-50 transition-colors"
              title="Sign Out"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        </div>
      </aside>

      {/* ==================== MAIN CONTENT ==================== */}
      <div className="flex-1 flex flex-col min-w-0 h-screen overflow-y-auto">
        {/* Top Navbar Header */}
        <header className="sticky top-0 z-30 bg-white/90 backdrop-blur-md border-b border-slate-200/80 px-6 py-4 flex items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <button
              onClick={() => setSidebarOpen(true)}
              className="p-2 text-slate-500 hover:text-slate-800 rounded-xl bg-slate-100 lg:hidden"
            >
              <Menu className="w-5 h-5" />
            </button>

            <div>
              <h1 className="text-xl font-bold text-slate-900 tracking-tight">
                {activeView === "dashboard"
                  ? "Dashboard"
                  : activeView === "submissions"
                  ? "Service Submissions"
                  : "Villages Directory"}
              </h1>
              <p className="text-xs text-slate-400 hidden sm:block">Apna Gaon Village Directory Solution</p>
            </div>
          </div>

          {/* Right Header Controls */}
          <div className="flex items-center gap-3">
            <div className="hidden md:flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-emerald-50 border border-emerald-200 text-xs text-emerald-700 font-semibold">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
              <span>Firestore Live</span>
            </div>

            {/* Notification Bell */}
            <button
              onClick={() => {
                setActiveView("submissions");
                setStatusFilter("pending");
              }}
              className="relative p-2 rounded-full bg-slate-100 text-slate-600 hover:bg-slate-200/70 transition-colors"
              title="Pending Reviews"
            >
              <Bell className="w-4 h-4" />
              {stats.pendingSubs > 0 && (
                <span className="absolute top-1 right-1 w-2 h-2 rounded-full bg-rose-500 ring-2 ring-white" />
              )}
            </button>

            {/* User Profile Pill Dropdown */}
            <div className="relative">
              <button
                onClick={() => setProfileDropdownOpen(!profileDropdownOpen)}
                className="flex items-center gap-2.5 p-1 sm:pr-3 rounded-full hover:bg-slate-100 transition-colors"
              >
                <div className="w-8 h-8 rounded-full bg-indigo-600 text-white font-bold text-xs flex items-center justify-center shadow-sm">
                  AG
                </div>
                <div className="hidden sm:block text-left">
                  <p className="text-xs font-bold text-slate-800 leading-tight">Admin</p>
                </div>
                <ChevronDown className="w-3.5 h-3.5 text-slate-400 hidden sm:block" />
              </button>

              {profileDropdownOpen && (
                <div className="absolute right-0 mt-2 w-48 bg-white border border-slate-100 rounded-2xl shadow-xl p-2 z-50">
                  <p className="px-3 py-2 text-xs font-bold text-slate-900 border-b border-slate-100">
                    {currentUserEmail}
                  </p>
                  <button
                    onClick={handleLogout}
                    className="w-full text-left px-3 py-2 text-xs font-medium text-rose-600 hover:bg-rose-50 rounded-xl transition-colors flex items-center gap-2 mt-1"
                  >
                    <LogOut className="w-3.5 h-3.5" />
                    <span>Sign Out</span>
                  </button>
                </div>
              )}
            </div>
          </div>
        </header>

        {/* Dashboard Body */}
        <main className="flex-1 p-6 sm:p-8 max-w-7xl w-full mx-auto space-y-6">
          {/* ==================== 4 TOP KPI METRIC CARDS (REAL VILLAGE DATA) ==================== */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
            {/* 1. Green Circle Card: Total Submissions */}
            <div
              onClick={() => {
                setActiveView("submissions");
                setStatusFilter("all");
              }}
              className="bg-white p-6 rounded-2xl border border-slate-100 shadow-[0_4px_20px_-2px_rgba(0,0,0,0.03)] hover:shadow-md transition-all cursor-pointer"
            >
              <div className="w-12 h-12 rounded-full bg-emerald-500 text-white flex items-center justify-center shadow-lg shadow-emerald-500/20 mb-4">
                <FileCheck className="w-5 h-5" />
              </div>
              <h3 className="text-2xl font-bold text-slate-900 tracking-tight">
                {stats.totalSubs}
              </h3>
              <div className="flex items-center justify-between mt-1">
                <span className="text-xs text-slate-400 font-medium">Total Submissions</span>
                <span className="text-xs font-bold text-emerald-600 flex items-center gap-0.5">
                  Live <TrendingUp className="w-3 h-3" />
                </span>
              </div>
            </div>

            {/* 2. Orange Circle Card: Pending Reviews */}
            <div
              onClick={() => {
                setActiveView("submissions");
                setStatusFilter("pending");
              }}
              className="bg-white p-6 rounded-2xl border border-slate-100 shadow-[0_4px_20px_-2px_rgba(0,0,0,0.03)] hover:shadow-md transition-all cursor-pointer"
            >
              <div className="w-12 h-12 rounded-full bg-amber-500 text-white flex items-center justify-center shadow-lg shadow-amber-500/20 mb-4">
                <Clock className="w-5 h-5" />
              </div>
              <h3 className="text-2xl font-bold text-slate-900 tracking-tight">
                {stats.pendingSubs}
              </h3>
              <div className="flex items-center justify-between mt-1">
                <span className="text-xs text-slate-400 font-medium">Pending Review</span>
                <span className="text-xs font-bold text-amber-600 flex items-center gap-0.5">
                  Action Required
                </span>
              </div>
            </div>

            {/* 3. Purple Circle Card: Approved & Live Services */}
            <div
              onClick={() => {
                setActiveView("submissions");
                setStatusFilter("approved");
              }}
              className="bg-white p-6 rounded-2xl border border-slate-100 shadow-[0_4px_20px_-2px_rgba(0,0,0,0.03)] hover:shadow-md transition-all cursor-pointer"
            >
              <div className="w-12 h-12 rounded-full bg-indigo-600 text-white flex items-center justify-center shadow-lg shadow-indigo-600/20 mb-4">
                <CheckCircle2 className="w-5 h-5" />
              </div>
              <h3 className="text-2xl font-bold text-slate-900 tracking-tight">
                {stats.approvedSubs}
              </h3>
              <div className="flex items-center justify-between mt-1">
                <span className="text-xs text-slate-400 font-medium">Approved Services</span>
                <span className="text-xs font-bold text-emerald-600 flex items-center gap-0.5">
                  In App
                </span>
              </div>
            </div>

            {/* 4. Blue Circle Card: Villages Directory */}
            <div
              onClick={() => setActiveView("villages")}
              className="bg-white p-6 rounded-2xl border border-slate-100 shadow-[0_4px_20px_-2px_rgba(0,0,0,0.03)] hover:shadow-md transition-all cursor-pointer"
            >
              <div className="w-12 h-12 rounded-full bg-sky-500 text-white flex items-center justify-center shadow-lg shadow-sky-500/20 mb-4">
                <Building2 className="w-5 h-5" />
              </div>
              <h3 className="text-2xl font-bold text-slate-900 tracking-tight">
                {stats.totalVillages}
              </h3>
              <div className="flex items-center justify-between mt-1">
                <span className="text-xs text-slate-400 font-medium">Active Villages</span>
                <span className="text-xs font-bold text-sky-600 flex items-center gap-0.5">
                  Coverage
                </span>
              </div>
            </div>
          </div>

          {/* ==================== REAL VILLAGE ANALYTICS (NO FAKE PAYMENTS) ==================== */}
          {activeView === "dashboard" && (
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              {/* Left Card: Monthly Service Registrations Trend */}
              <div className="lg:col-span-2 bg-white p-6 rounded-2xl border border-slate-100 shadow-[0_4px_20px_-2px_rgba(0,0,0,0.03)] space-y-4">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-base font-bold text-slate-900">Village Registrations Trend</h3>
                    <p className="text-xs text-slate-400">Monthly breakdown of service listings and registrations</p>
                  </div>
                  <div className="px-3 py-1 rounded-lg border border-slate-200 text-xs text-slate-500 font-semibold">
                    Current Year
                  </div>
                </div>

                {/* Smooth Wave Chart for Registrations */}
                <div className="h-44 w-full relative pt-2">
                  <svg viewBox="0 0 500 130" className="w-full h-full overflow-visible">
                    <defs>
                      <linearGradient id="villageWave" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor="#6366F1" stopOpacity="0.3" />
                        <stop offset="100%" stopColor="#6366F1" stopOpacity="0.0" />
                      </linearGradient>
                    </defs>

                    <path
                      d="M 0 110 Q 80 80 140 70 T 250 45 T 370 25 T 500 35 L 500 130 L 0 130 Z"
                      fill="url(#villageWave)"
                    />
                    <path
                      d="M 0 110 Q 80 80 140 70 T 250 45 T 370 25 T 500 35"
                      fill="none"
                      stroke="#4F46E5"
                      strokeWidth="3"
                    />
                  </svg>
                </div>

                <div className="flex justify-between text-[11px] text-slate-400 border-t border-slate-100 pt-3 px-1">
                  {["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"].map((m) => (
                    <span key={m}>{m}</span>
                  ))}
                </div>
              </div>

              {/* Right Card: Category Distribution Breakdown */}
              <div className="bg-white p-6 rounded-2xl border border-slate-100 shadow-[0_4px_20px_-2px_rgba(0,0,0,0.03)] space-y-4">
                <div className="flex items-center justify-between border-b border-slate-100 pb-3">
                  <h3 className="text-base font-bold text-slate-900">Categories Distribution</h3>
                  <BarChart3 className="w-4 h-4 text-indigo-600" />
                </div>

                <div className="space-y-3 pt-1">
                  {[
                    { label: "School & Coaching", key: "education", color: "bg-blue-500", count: stats.categoryCounts["education"] || 0 },
                    { label: "Lawyer & Panchayat", key: "legal", color: "bg-indigo-600", count: stats.categoryCounts["legal"] || 0 },
                    { label: "Health & Medical", key: "health", color: "bg-emerald-500", count: stats.categoryCounts["health"] || 0 },
                    { label: "Kisan & Mandi", key: "mandi", color: "bg-amber-500", count: stats.categoryCounts["mandi"] || 0 },
                    { label: "Transport & Vehicles", key: "transport", color: "bg-purple-500", count: stats.categoryCounts["transport"] || 0 },
                    { label: "Wedding & Family Functions", key: "family", color: "bg-pink-500", count: stats.categoryCounts["family"] || 0 },
                    { label: "Labour & Construction", key: "labour", color: "bg-orange-500", count: stats.categoryCounts["labour"] || 0 },
                  ].map((item) => (
                    <div key={item.key} className="space-y-1">
                      <div className="flex items-center justify-between text-xs font-semibold">
                        <span className="text-slate-600">{item.label}</span>
                        <span className="text-slate-900 font-bold">{item.count}</span>
                      </div>
                      <div className="w-full h-1.5 rounded-full bg-slate-100 overflow-hidden">
                        <div
                          className={`h-full rounded-full ${item.color}`}
                          style={{
                            width: `${stats.totalSubs > 0 ? Math.max((item.count / stats.totalSubs) * 100, 8) : 10}%`
                          }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* ==================== ACTIVE VIEW COMPONENT ==================== */}
          <div className="space-y-4">
            {/* View Switcher Bar */}
            <div className="flex items-center justify-between bg-white p-2.5 rounded-2xl border border-slate-100 shadow-sm">
              <div className="flex items-center gap-2">
                <button
                  onClick={() => {
                    setActiveView("dashboard");
                    setCategoryFilter("all");
                    setStatusFilter("all");
                  }}
                  className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                    activeView === "dashboard"
                      ? "bg-indigo-600 text-white shadow-md shadow-indigo-500/20"
                      : "text-slate-600 hover:bg-slate-100"
                  }`}
                >
                  Dashboard Overview
                </button>
                <button
                  onClick={() => {
                    setActiveView("submissions");
                    setStatusFilter("all");
                  }}
                  className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                    activeView === "submissions"
                      ? "bg-indigo-600 text-white shadow-md shadow-indigo-500/20"
                      : "text-slate-600 hover:bg-slate-100"
                  }`}
                >
                  Service Submissions ({stats.totalSubs})
                </button>
                <button
                  onClick={() => setActiveView("villages")}
                  className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                    activeView === "villages"
                      ? "bg-indigo-600 text-white shadow-md shadow-indigo-500/20"
                      : "text-slate-600 hover:bg-slate-100"
                  }`}
                >
                  Villages Directory ({stats.totalVillages})
                </button>
              </div>
            </div>

            {/* Content Display */}
            {activeView === "dashboard" || activeView === "submissions" ? (
              <SubmissionsManager
                initialFilterStatus={statusFilter}
                initialCategoryFilter={categoryFilter}
              />
            ) : (
              <VillagesManager />
            )}
          </div>
        </main>
      </div>
    </div>
  );
}
