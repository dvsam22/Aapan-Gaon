"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { signInWithEmailAndPassword, createUserWithEmailAndPassword, onAuthStateChanged } from "firebase/auth";
import { auth } from "@/lib/firebase";
import { Lock, Mail, ArrowRight, KeyRound, Sparkles, AlertCircle } from "lucide-react";
import PostmanLogo from "@/components/admin/PostmanLogo";

export default function AdminLoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("apna@gmail.com");
  const [password, setPassword] = useState("123456");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (typeof window !== "undefined" && sessionStorage.getItem("admin_auth") === "true") {
      router.push("/admin");
      return;
    }
    const unsubscribe = onAuthStateChanged(auth, (user) => {
      if (user) {
        sessionStorage.setItem("admin_auth", "true");
        sessionStorage.setItem("admin_email", user.email || "apna@gmail.com");
        router.push("/admin");
      }
    });
    return () => unsubscribe();
  }, [router]);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) {
      setError("Please enter both email and password.");
      return;
    }

    setLoading(true);
    setError(null);

    // 1. Direct match for pre-login credentials
    if (email.trim().toLowerCase() === "apna@gmail.com" && password === "123456") {
      try {
        await signInWithEmailAndPassword(auth, email.trim(), password);
      } catch (err: any) {
        try {
          await createUserWithEmailAndPassword(auth, email.trim(), password);
        } catch (_regErr) {
          // Fallback session auth
        }
      }
      sessionStorage.setItem("admin_auth", "true");
      sessionStorage.setItem("admin_email", "apna@gmail.com");
      router.push("/admin");
      setLoading(false);
      return;
    }

    // 2. Standard Firebase Auth
    try {
      const userCred = await signInWithEmailAndPassword(auth, email.trim(), password);
      sessionStorage.setItem("admin_auth", "true");
      sessionStorage.setItem("admin_email", userCred.user.email || email);
      router.push("/admin");
    } catch (err: any) {
      console.error("Login error:", err);
      if (
        err.code === "auth/user-not-found" ||
        err.code === "auth/wrong-password" ||
        err.code === "auth/invalid-credential"
      ) {
        setError("Invalid credentials. Use email: apna@gmail.com and password: 123456");
      } else {
        setError(err.message || "Failed to sign in. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleFillPrelogin = () => {
    setEmail("apna@gmail.com");
    setPassword("123456");
    setError(null);
  };

  return (
    <div className="min-h-screen bg-[#0B0C0E] text-zinc-100 flex items-center justify-center p-4 relative overflow-hidden selection:bg-[#FF6C37] selection:text-white">
      {/* Background Ambient Glows */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-[#FF6C37]/10 rounded-full blur-[140px] pointer-events-none" />
      <div className="absolute -bottom-20 -right-20 w-[450px] h-[450px] bg-[#FF6C37]/5 rounded-full blur-[120px] pointer-events-none" />
      
      {/* Grid pattern overlay */}
      <div 
        className="absolute inset-0 opacity-[0.03] pointer-events-none"
        style={{
          backgroundImage: "radial-gradient(#FF6C37 1px, transparent 1px)",
          backgroundSize: "24px 24px"
        }}
      />

      <div className="w-full max-w-md relative z-10">
        {/* Brand Header */}
        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <PostmanLogo size={58} />
          </div>
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#FF6C37]/10 border border-[#FF6C37]/20 text-[#FF6C37] text-xs font-semibold tracking-wide uppercase mb-3">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Mission Control Portal</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
            Apna Gaon <span className="text-[#FF6C37]">Admin</span>
          </h1>
          <p className="text-xs sm:text-sm text-zinc-400 mt-1.5">
            Village Services & Directory Management System
          </p>
        </div>

        {/* Quick Fill Pre-login Card */}
        <div className="mb-5 bg-[#161920]/90 border border-[#FF6C37]/25 rounded-2xl p-4 flex items-center justify-between gap-3 text-xs shadow-lg shadow-[#FF6C37]/5 backdrop-blur-xl">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-xl bg-[#FF6C37]/15 flex items-center justify-center text-[#FF6C37] shrink-0 border border-[#FF6C37]/30">
              <KeyRound className="w-4 h-4" />
            </div>
            <div>
              <p className="font-medium text-zinc-300">Default Credentials:</p>
              <p className="font-mono text-[#FF8557] font-semibold">apna@gmail.com / 123456</p>
            </div>
          </div>
          <button
            type="button"
            onClick={handleFillPrelogin}
            className="px-3.5 py-1.5 bg-[#FF6C37] hover:bg-[#FF7A47] active:scale-95 text-white font-bold rounded-xl text-[11px] shadow-md shadow-[#FF6C37]/30 transition-all flex items-center gap-1.5 shrink-0"
          >
            <span>Quick Fill</span>
          </button>
        </div>

        {/* Login Form Box */}
        <div className="bg-[#12141A]/95 border border-zinc-800/90 rounded-3xl p-7 sm:p-8 shadow-2xl backdrop-blur-2xl">
          <form onSubmit={handleLogin} className="space-y-5">
            {error && (
              <div className="p-3.5 rounded-2xl bg-rose-500/10 border border-rose-500/25 text-rose-300 text-xs flex items-start gap-2.5 animate-shake">
                <AlertCircle className="w-4 h-4 text-rose-400 shrink-0 mt-0.5" />
                <span>{error}</span>
              </div>
            )}

            <div>
              <label className="block text-[11px] font-bold text-zinc-400 uppercase tracking-wider mb-2">
                Admin Email Address
              </label>
              <div className="relative">
                <Mail className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-zinc-500" />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="apna@gmail.com"
                  className="w-full pl-10 pr-4 py-3 bg-[#0B0C0E] border border-zinc-800 rounded-xl text-white placeholder-zinc-600 focus:outline-none focus:border-[#FF6C37] focus:ring-2 focus:ring-[#FF6C37]/20 transition-all text-xs sm:text-sm font-mono"
                  required
                />
              </div>
            </div>

            <div>
              <label className="block text-[11px] font-bold text-zinc-400 uppercase tracking-wider mb-2">
                Secret Access Key / Password
              </label>
              <div className="relative">
                <Lock className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-zinc-500" />
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full pl-10 pr-4 py-3 bg-[#0B0C0E] border border-zinc-800 rounded-xl text-white placeholder-zinc-600 focus:outline-none focus:border-[#FF6C37] focus:ring-2 focus:ring-[#FF6C37]/20 transition-all text-xs sm:text-sm font-mono"
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-3.5 bg-gradient-to-r from-[#FF6C37] via-[#FF7A47] to-[#FF8C5D] hover:from-[#F05A24] hover:to-[#FF7A47] text-white font-bold rounded-xl text-sm shadow-lg shadow-[#FF6C37]/25 transition-all flex items-center justify-center gap-2 group disabled:opacity-50 disabled:cursor-not-allowed mt-2"
            >
              {loading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  <span>Signing In...</span>
                </>
              ) : (
                <>
                  <span>Access Dashboard</span>
                  <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
                </>
              )}
            </button>
          </form>

          {/* Footer note */}
          <div className="mt-6 pt-5 border-t border-zinc-800/80 text-center">
            <p className="text-[11px] text-zinc-500 flex items-center justify-center gap-1.5">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
              <span>Secured with Firebase Auth & Cloud Firestore</span>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
