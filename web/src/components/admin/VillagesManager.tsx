"use client";

import React, { useState, useEffect } from "react";
import {
  collection,
  query,
  onSnapshot,
  doc,
  updateDoc,
  setDoc,
  deleteDoc,
  serverTimestamp
} from "firebase/firestore";
import { db } from "@/lib/firebase";
import {
  MapPin,
  Search,
  Plus,
  CheckCircle2,
  XCircle,
  ToggleLeft,
  ToggleRight,
  Trash2,
  Building,
  Phone,
  User,
  X,
  Edit3,
  Save
} from "lucide-react";

export interface VillageItem {
  id: string;
  villageName: string;
  district: string;
  state: string;
  pincode: string;
  sarpanchName?: string;
  sarpanchPhone?: string;
  active: boolean;
  isVillageActive?: boolean;
}

function parseStringOrMap(val: any): string {
  if (!val) return "";
  if (typeof val === "string") return val;
  if (typeof val === "number" || typeof val === "boolean") return String(val);
  if (typeof val === "object") {
    return val.en || val.hi || Object.values(val)[0]?.toString() || "";
  }
  return "";
}

export default function VillagesManager() {
  const [villages, setVillages] = useState<VillageItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [filterActive, setFilterActive] = useState<string>("all");
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [editingVillage, setEditingVillage] = useState<VillageItem | null>(null);
  const [actionLoading, setActionLoading] = useState<string | null>(null);

  // New Village Form State
  const [newVillageName, setNewVillageName] = useState("");
  const [newDistrict, setNewDistrict] = useState("");
  const [newState, setNewState] = useState("Uttar Pradesh");
  const [newPincode, setNewPincode] = useState("");
  const [newSarpanchName, setNewSarpanchName] = useState("");
  const [newSarpanchPhone, setNewSarpanchPhone] = useState("");
  const [newActive, setNewActive] = useState(true);

  useEffect(() => {
    const q = query(collection(db, "villages"));
    const unsubscribe = onSnapshot(q, (snapshot) => {
      const items: VillageItem[] = [];
      snapshot.forEach((docSnap) => {
        const d = docSnap.data();
        const vName = parseStringOrMap(d.villageName) || docSnap.id;
        const dist = parseStringOrMap(d.district);
        const st = parseStringOrMap(d.state) || "Uttar Pradesh";
        const pin = parseStringOrMap(d.pincode);
        const sName = parseStringOrMap(d.sarpanchName);
        const sPhone = parseStringOrMap(d.sarpanchPhone);
        const isActive = d.active === true || d.isActive === true;

        items.push({
          id: docSnap.id,
          villageName: vName,
          district: dist,
          state: st,
          pincode: pin,
          sarpanchName: sName,
          sarpanchPhone: sPhone,
          active: isActive,
          isVillageActive: d.isVillageActive
        });
      });

      items.sort((a, b) => a.villageName.localeCompare(b.villageName));
      setVillages(items);
      setLoading(false);
    }, (error) => {
      console.error("Error fetching villages:", error);
      setLoading(false);
    });

    return () => unsubscribe();
  }, []);

  // Filter Logic
  const filteredVillages = villages.filter((v) => {
    if (filterActive === "active" && !v.active) return false;
    if (filterActive === "inactive" && v.active) return false;
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      return (
        v.villageName.toLowerCase().includes(q) ||
        v.district.toLowerCase().includes(q) ||
        v.pincode.includes(q) ||
        v.id.toLowerCase().includes(q)
      );
    }
    return true;
  });

  // Toggle Active Status
  const handleToggleActive = async (village: VillageItem) => {
    try {
      setActionLoading(village.id);
      const newStatus = !village.active;
      const docRef = doc(db, "villages", village.id);
      await updateDoc(docRef, {
        active: newStatus,
        isActive: newStatus,
        updatedAt: serverTimestamp()
      });
    } catch (err) {
      console.error("Failed to toggle village status:", err);
      alert("Error updating village status. Check console.");
    } finally {
      setActionLoading(null);
    }
  };

  // Add Village
  const handleAddVillage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newVillageName.trim()) {
      alert("Please enter a village name.");
      return;
    }

    try {
      setActionLoading("adding");
      const slug = newVillageName
        .trim()
        .toLowerCase()
        .replace(/\s+/g, "_")
        .replace(/[^a-z0-9_]/g, "");
      const vId = `v_${slug}`;

      const docRef = doc(db, "villages", vId);
      await setDoc(docRef, {
        villageName: { en: newVillageName.trim(), hi: newVillageName.trim() },
        district: newDistrict.trim() ? { en: newDistrict.trim(), hi: newDistrict.trim() } : "",
        state: newState.trim(),
        pincode: newPincode.trim(),
        sarpanchName: newSarpanchName.trim(),
        sarpanchPhone: newSarpanchPhone.trim(),
        active: newActive,
        isActive: newActive,
        createdAt: serverTimestamp()
      }, { merge: true });

      // Reset Form
      setNewVillageName("");
      setNewDistrict("");
      setNewPincode("");
      setNewSarpanchName("");
      setNewSarpanchPhone("");
      setIsAddModalOpen(false);
    } catch (err) {
      console.error("Failed to add village:", err);
      alert("Failed to add village. Check console.");
    } finally {
      setActionLoading(null);
    }
  };

  // Edit Village Save Logic
  const handleSaveVillageEdit = async () => {
    if (!editingVillage) return;
    try {
      setActionLoading(editingVillage.id);
      const docRef = doc(db, "villages", editingVillage.id);
      await updateDoc(docRef, {
        villageName: { en: editingVillage.villageName.trim(), hi: editingVillage.villageName.trim() },
        district: editingVillage.district.trim() ? { en: editingVillage.district.trim(), hi: editingVillage.district.trim() } : "",
        state: editingVillage.state.trim(),
        pincode: editingVillage.pincode.trim(),
        sarpanchName: editingVillage.sarpanchName?.trim() || "",
        sarpanchPhone: editingVillage.sarpanchPhone?.trim() || "",
        active: editingVillage.active,
        isActive: editingVillage.active,
        updatedAt: serverTimestamp()
      });
      setEditingVillage(null);
    } catch (err) {
      console.error("Failed to update village:", err);
      alert("Error updating village. Check console.");
    } finally {
      setActionLoading(null);
    }
  };

  // Delete Village
  const handleDeleteVillage = async (id: string, name: string) => {
    if (!confirm(`Are you sure you want to delete village "${name}"?`)) return;
    try {
      setActionLoading(id);
      await deleteDoc(doc(db, "villages", id));
    } catch (err) {
      console.error("Failed to delete village:", err);
    } finally {
      setActionLoading(null);
    }
  };

  return (
    <div className="space-y-6">
      {/* Top Action & Control Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-5 rounded-2xl border border-slate-100 shadow-[0_4px_20px_-2px_rgba(0,0,0,0.03)]">
        {/* Active Filters */}
        <div className="flex items-center gap-2 overflow-x-auto pb-1 sm:pb-0">
          {[
            { id: "all", label: "All Villages", count: villages.length },
            { id: "active", label: "Active in App", count: villages.filter(v => v.active).length },
            { id: "inactive", label: "Inactive / Pending", count: villages.filter(v => !v.active).length }
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setFilterActive(tab.id)}
              className={`px-4 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition-all flex items-center gap-2 ${
                filterActive === tab.id
                  ? "bg-indigo-600 text-white shadow-md shadow-indigo-600/20"
                  : "bg-slate-100 text-slate-600 hover:bg-slate-200/70"
              }`}
            >
              <span>{tab.label}</span>
              <span
                className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                  filterActive === tab.id
                    ? "bg-white text-indigo-600"
                    : "bg-white text-slate-600 border border-slate-200"
                }`}
              >
                {tab.count}
              </span>
            </button>
          ))}
        </div>

        {/* Search & Add Village */}
        <div className="flex items-center gap-3">
          <div className="relative flex-1 sm:w-64">
            <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search villages, district..."
              className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 placeholder-slate-400 focus:outline-none focus:border-indigo-500 focus:bg-white transition-all font-medium"
            />
          </div>

          <button
            onClick={() => setIsAddModalOpen(true)}
            className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl text-xs transition-all flex items-center gap-1.5 shadow-md shadow-indigo-600/20 shrink-0"
          >
            <Plus className="w-4 h-4" />
            <span>Add Village</span>
          </button>
        </div>
      </div>

      {/* Villages Cards Grid */}
      {loading ? (
        <div className="py-20 text-center text-slate-400 flex flex-col items-center justify-center gap-3">
          <div className="w-8 h-8 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
          <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">Loading villages...</p>
        </div>
      ) : filteredVillages.length === 0 ? (
        <div className="p-16 text-center bg-white rounded-2xl border border-slate-100 shadow-sm">
          <MapPin className="w-12 h-12 text-slate-300 mx-auto mb-3" />
          <h3 className="text-base font-bold text-slate-800">No villages found</h3>
          <p className="text-xs text-slate-400 mt-1">There are no villages matching your search criteria.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredVillages.map((village) => (
            <div
              key={village.id}
              className="bg-white border border-slate-100 rounded-2xl p-5 flex flex-col justify-between hover:shadow-md transition-all"
            >
              <div>
                <div className="flex items-center justify-between gap-2 mb-3">
                  <span
                    className={`inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[11px] font-bold ${
                      village.active
                        ? "bg-emerald-50 text-emerald-700 border border-emerald-200"
                        : "bg-amber-50 text-amber-700 border border-amber-200"
                    }`}
                  >
                    {village.active ? (
                      <>
                        <CheckCircle2 className="w-3 h-3 text-emerald-600" />
                        <span>Active in App</span>
                      </>
                    ) : (
                      <>
                        <XCircle className="w-3 h-3 text-amber-600" />
                        <span>Inactive / Hidden</span>
                      </>
                    )}
                  </span>

                  <span className="text-[10px] font-mono text-slate-400 bg-slate-50 px-2 py-0.5 rounded-lg border border-slate-200">
                    {village.id}
                  </span>
                </div>

                <h3 className="text-base font-bold text-slate-900 mb-2 flex items-center gap-2">
                  <MapPin className="w-4 h-4 text-indigo-600 shrink-0" />
                  <span>{village.villageName}</span>
                </h3>

                <div className="space-y-1 text-xs text-slate-500 mb-4 pl-6">
                  {village.district && (
                    <div className="flex items-center gap-2">
                      <Building className="w-3.5 h-3.5 text-slate-400" />
                      <span>District: <strong className="text-slate-700 font-semibold">{village.district}</strong></span>
                    </div>
                  )}

                  {village.pincode && (
                    <div>
                      Pincode: <span className="font-mono text-slate-700 font-semibold">{village.pincode}</span>
                    </div>
                  )}

                  {village.sarpanchName && (
                    <div className="flex items-center gap-2 pt-1">
                      <User className="w-3.5 h-3.5 text-slate-400" />
                      <span>Sarpanch: <strong className="text-slate-700 font-semibold">{village.sarpanchName}</strong></span>
                    </div>
                  )}

                  {village.sarpanchPhone && (
                    <div className="flex items-center gap-2">
                      <Phone className="w-3.5 h-3.5 text-indigo-600" />
                      <span className="font-mono text-indigo-600 font-bold">{village.sarpanchPhone}</span>
                    </div>
                  )}
                </div>
              </div>

              {/* Action Footer */}
              <div className="pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
                <div className="flex items-center gap-1">
                  <button
                    onClick={() => setEditingVillage({ ...village })}
                    className="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors"
                    title="Edit Details"
                  >
                    <Edit3 className="w-3.5 h-3.5" />
                  </button>

                  <button
                    onClick={() => handleDeleteVillage(village.id, village.villageName)}
                    className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                    title="Delete"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>

                <button
                  onClick={() => handleToggleActive(village)}
                  disabled={actionLoading === village.id}
                  className={`px-3 py-1 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
                    village.active
                      ? "bg-slate-100 text-slate-600 hover:bg-slate-200"
                      : "bg-indigo-600 text-white hover:bg-indigo-700 shadow-sm shadow-indigo-600/20"
                  }`}
                >
                  {village.active ? (
                    <>
                      <ToggleRight className="w-4 h-4 text-emerald-600" />
                      <span>Deactivate</span>
                    </>
                  ) : (
                    <>
                      <ToggleLeft className="w-4 h-4 text-white" />
                      <span>Activate for App</span>
                    </>
                  )}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Edit Village Modal */}
      {editingVillage && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-sm flex items-center justify-center p-4 overflow-y-auto">
          <div className="bg-white border border-slate-100 rounded-3xl p-6 sm:p-8 max-w-md w-full my-8 shadow-2xl space-y-5">
            <div className="flex items-center justify-between border-b border-slate-100 pb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center font-bold">
                  <Edit3 className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-base font-bold text-slate-900">Edit Village</h3>
                  <p className="text-xs text-slate-400">Update village information</p>
                </div>
              </div>
              <button
                onClick={() => setEditingVillage(null)}
                className="p-2 text-slate-400 hover:text-slate-700 rounded-xl hover:bg-slate-100"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-3.5">
              <div>
                <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                  Village Name *
                </label>
                <input
                  type="text"
                  value={editingVillage.villageName}
                  onChange={(e) => setEditingVillage({ ...editingVillage, villageName: e.target.value })}
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                  District
                </label>
                <input
                  type="text"
                  value={editingVillage.district}
                  onChange={(e) => setEditingVillage({ ...editingVillage, district: e.target.value })}
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    State
                  </label>
                  <input
                    type="text"
                    value={editingVillage.state}
                    onChange={(e) => setEditingVillage({ ...editingVillage, state: e.target.value })}
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    Pincode
                  </label>
                  <input
                    type="text"
                    value={editingVillage.pincode}
                    onChange={(e) => setEditingVillage({ ...editingVillage, pincode: e.target.value })}
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium font-mono focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="flex items-center justify-between p-3 bg-slate-50 rounded-xl border border-slate-200">
                <span className="text-xs font-bold text-slate-700">Active in Mobile App?</span>
                <input
                  type="checkbox"
                  checked={editingVillage.active}
                  onChange={(e) => setEditingVillage({ ...editingVillage, active: e.target.checked })}
                  className="w-4 h-4 accent-indigo-600 rounded cursor-pointer"
                />
              </div>
            </div>

            <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setEditingVillage(null)}
                className="px-4 py-2 text-xs font-bold text-slate-600 bg-slate-100 rounded-xl"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleSaveVillageEdit}
                disabled={actionLoading === editingVillage.id}
                className="px-5 py-2 text-xs font-bold text-white bg-indigo-600 hover:bg-indigo-700 rounded-xl shadow-md shadow-indigo-600/20 flex items-center gap-1.5"
              >
                <Save className="w-4 h-4" />
                <span>Save Changes</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Add Village Modal */}
      {isAddModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-sm flex items-center justify-center p-4 overflow-y-auto">
          <div className="bg-white border border-slate-100 rounded-3xl p-6 sm:p-8 max-w-md w-full my-8 shadow-2xl space-y-5">
            <div className="flex items-center justify-between border-b border-slate-100 pb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center font-bold">
                  <MapPin className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-base font-bold text-slate-900">Add New Village</h3>
                  <p className="text-xs text-slate-400">Register village for mobile app</p>
                </div>
              </div>
              <button
                onClick={() => setIsAddModalOpen(false)}
                className="p-2 text-slate-400 hover:text-slate-700 rounded-xl hover:bg-slate-100"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleAddVillage} className="space-y-3.5">
              <div>
                <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                  Village Name *
                </label>
                <input
                  type="text"
                  value={newVillageName}
                  onChange={(e) => setNewVillageName(e.target.value)}
                  placeholder="e.g. Allahabad"
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                  District
                </label>
                <input
                  type="text"
                  value={newDistrict}
                  onChange={(e) => setNewDistrict(e.target.value)}
                  placeholder="e.g. Prayagraj"
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    State
                  </label>
                  <input
                    type="text"
                    value={newState}
                    onChange={(e) => setNewState(e.target.value)}
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    Pincode
                  </label>
                  <input
                    type="text"
                    value={newPincode}
                    onChange={(e) => setNewPincode(e.target.value)}
                    placeholder="273303"
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium font-mono focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="flex items-center justify-between p-3 bg-slate-50 rounded-xl border border-slate-200">
                <span className="text-xs font-bold text-slate-700">Active in Mobile App?</span>
                <input
                  type="checkbox"
                  checked={newActive}
                  onChange={(e) => setNewActive(e.target.checked)}
                  className="w-4 h-4 accent-indigo-600 rounded cursor-pointer"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsAddModalOpen(false)}
                  className="px-4 py-2 text-xs font-bold text-slate-600 bg-slate-100 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={actionLoading === "adding"}
                  className="px-5 py-2 text-xs font-bold text-white bg-indigo-600 hover:bg-indigo-700 rounded-xl shadow-md shadow-indigo-600/20"
                >
                  {actionLoading === "adding" ? "Creating..." : "Save Village"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
