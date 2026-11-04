"use client";

import React, { useState, useEffect, useMemo } from "react";
import {
  collection,
  query,
  onSnapshot,
  doc,
  updateDoc,
  deleteDoc,
  setDoc,
  getDoc,
  getDocs,
  serverTimestamp
} from "firebase/firestore";
import { db } from "@/lib/firebase";
import {
  Search,
  CheckCircle2,
  XCircle,
  Clock,
  Edit3,
  Trash2,
  Phone,
  MapPin,
  User,
  X,
  Save,
  Globe2,
  Building2,
  LayoutGrid,
  ChevronRight,
  Filter,
  Plus,
  Loader2
} from "lucide-react";

export interface SubmissionItem {
  id: string;
  contactName: string;
  businessName: string;
  category: string;
  phoneNumber: string;
  address: string;
  description: string;
  villageId?: string;
  villageName?: string;
  status: "pending" | "approved" | "rejected";
  createdAt?: any;
  // Bilingual Fields
  nameEn?: string;
  nameHi?: string;
  detailsEn?: string;
  detailsHi?: string;
  contactNameEn?: string;
  contactNameHi?: string;
  villageNameEn?: string;
  villageNameHi?: string;
  addressEn?: string;
  addressHi?: string;
  publishedDocPath?: string;
  publishedCollection?: string;
  chargesEn?: string;
  chargesHi?: string;
  experienceEn?: string;
  experienceHi?: string;
  startingPriceEn?: string;
  startingPriceHi?: string;
  extraInfoEn?: string;
  extraInfoHi?: string;
  vehicleTypeEn?: string;
  vehicleTypeHi?: string;
  availabilityEn?: string;
  availabilityHi?: string;
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

// NextAdmin Light Badge Palette
const CATEGORY_COLORS: Record<string, { bg: string; text: string; border: string }> = {
  education: { bg: "bg-blue-50", text: "text-blue-700", border: "border-blue-200" },
  health: { bg: "bg-emerald-50", text: "text-emerald-700", border: "border-emerald-200" },
  mandi: { bg: "bg-amber-50", text: "text-amber-700", border: "border-amber-200" },
  transport: { bg: "bg-purple-50", text: "text-purple-700", border: "border-purple-200" },
  labour: { bg: "bg-cyan-50", text: "text-cyan-700", border: "border-cyan-200" },
  family: { bg: "bg-pink-50", text: "text-pink-700", border: "border-pink-200" },
  construction: { bg: "bg-orange-50", text: "text-orange-700", border: "border-orange-200" },
  business: { bg: "bg-indigo-50", text: "text-indigo-700", border: "border-indigo-200" },
};

export interface CategoryDefinition {
  id: string; // The exact categoryId in Firestore DTO
  displayName: string;
  group: "labour" | "transport" | "family" | "construction" | "health" | "education" | "legal" | "mandi" | "business";
  collection: string; // The Firestore subcollection under villages/{villageId}/
  nameLabelEn: string;
  nameLabelHi: string;
  namePlaceholderEn: string;
  namePlaceholderHi: string;
  showOwnerName?: boolean;
  addressLabelEn?: string;
  addressLabelHi?: string;
  extra1?: {
    key: string;
    labelEn: string;
    labelHi: string;
    placeholderEn: string;
    placeholderHi: string;
  };
  extra2?: {
    key: string;
    labelEn: string;
    labelHi: string;
    placeholderEn: string;
    placeholderHi: string;
  };
  extra3?: {
    key: string;
    labelEn: string;
    labelHi: string;
    placeholderEn: string;
    placeholderHi: string;
  };
}

export const CATEGORY_DEFINITIONS: CategoryDefinition[] = [
  // 1. Labour & Skilled Artisans
  {
    id: "painter",
    displayName: "पेंटर (Painter)",
    group: "labour",
    collection: "labour",
    nameLabelEn: "Painter Name (English) *",
    nameLabelHi: "कारीगर / पेंटर का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Ramesh Painter / Verma Painting",
    namePlaceholderHi: "उदा. रमेश पेंटर / वर्मा पेंटिंग वर्क्स",
    showOwnerName: false,
    addressLabelEn: "Working Area / Address (English)",
    addressLabelHi: "कार्य क्षेत्र व पता (Hindi)",
    extra1: {
      key: "skills",
      labelEn: "Painting Skills & Work (English)",
      labelHi: "काम का प्रकार व कौशल (Hindi)",
      placeholderEn: "e.g. Wall putty, plastic paint, primer, enamel, polish",
      placeholderHi: "उदा. दीवार पुट्टी, प्लास्टिक पेंट, प्राइमर, डिस्टेंपर, पॉलिश"
    },
    extra2: {
      key: "charges",
      labelEn: "Daily Charges / Wage (English)",
      labelHi: "दैनिक मजदूरी / चार्ज (Hindi)",
      placeholderEn: "e.g. ₹500 / day",
      placeholderHi: "उदा. ₹500 / प्रतिदिन"
    },
    extra3: {
      key: "experience",
      labelEn: "Experience (English)",
      labelHi: "कार्य अनुभव (Hindi)",
      placeholderEn: "e.g. 5 Years",
      placeholderHi: "उदा. 5 साल का अनुभव"
    }
  },
  {
    id: "rajmistri",
    displayName: "राजमिस्त्री (Rajmistri / Mason)",
    group: "labour",
    collection: "labour",
    nameLabelEn: "Mason Name (English) *",
    nameLabelHi: "राजमिस्त्री का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Suresh Mistri",
    namePlaceholderHi: "उदा. सुरेश मिस्त्री",
    showOwnerName: false,
    extra1: {
      key: "skills",
      labelEn: "Skills / Speciality (English)",
      labelHi: "काम व कौशल (Hindi)",
      placeholderEn: "e.g. Brickwork, plastering, tiles, floor casting",
      placeholderHi: "उदा. ईंट जोड़ाई, प्लास्टर, टाइल्स, फर्श ढलाई"
    },
    extra2: {
      key: "charges",
      labelEn: "Daily Wage (English)",
      labelHi: "दैनिक मजदूरी (Hindi)",
      placeholderEn: "e.g. ₹700 / day",
      placeholderHi: "उदा. ₹700 / दिन"
    },
    extra3: {
      key: "experience",
      labelEn: "Experience (English)",
      labelHi: "अनुभव (Hindi)",
      placeholderEn: "e.g. 10 Years",
      placeholderHi: "उदा. 10 वर्ष"
    }
  },
  {
    id: "electrician",
    displayName: "बिजली मिस्त्री (Electrician)",
    group: "labour",
    collection: "labour",
    nameLabelEn: "Electrician Name (English) *",
    nameLabelHi: "बिजली मिस्त्री का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Manoj Electrician",
    namePlaceholderHi: "उदा. मनोज बिजली मिस्त्री",
    showOwnerName: false,
    extra1: {
      key: "skills",
      labelEn: "Electrical Skills (English)",
      labelHi: "काम का विवरण (Hindi)",
      placeholderEn: "e.g. House wiring, inverter, motor fitting, repair",
      placeholderHi: "उदा. मकान वायरिंग, इन्वर्टर, मोटर फिटिंग, रिपेयर"
    },
    extra2: {
      key: "charges",
      labelEn: "Visiting / Daily Charges (English)",
      labelHi: "चार्ज / दैनिक मजदूरी (Hindi)",
      placeholderEn: "e.g. ₹350 / visit or ₹600/day",
      placeholderHi: "उदा. ₹350 / विजिट या ₹600/दिन"
    },
    extra3: {
      key: "experience",
      labelEn: "Experience (English)",
      labelHi: "अनुभव (Hindi)",
      placeholderEn: "e.g. 6 Years",
      placeholderHi: "उदा. 6 वर्ष"
    }
  },
  {
    id: "plumber",
    displayName: "प्लंबर (Plumber)",
    group: "labour",
    collection: "labour",
    nameLabelEn: "Plumber Name (English) *",
    nameLabelHi: "प्लंबर का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Rajesh Plumber",
    namePlaceholderHi: "उदा. राजेश प्लंबर",
    showOwnerName: false,
    extra1: {
      key: "skills",
      labelEn: "Plumbing Skills (English)",
      labelHi: "काम का विवरण (Hindi)",
      placeholderEn: "e.g. Water tank, pipe fitting, tap repair, bathroom",
      placeholderHi: "उदा. पानी टंकी, पाइप फिटिंग, नल मरम्मत, समरसेबल"
    },
    extra2: {
      key: "charges",
      labelEn: "Charges (English)",
      labelHi: "चार्ज / मजदूरी (Hindi)",
      placeholderEn: "e.g. ₹300 / visit",
      placeholderHi: "उदा. ₹300 / विजिट"
    },
    extra3: {
      key: "experience",
      labelEn: "Experience (English)",
      labelHi: "अनुभव (Hindi)",
      placeholderEn: "e.g. 4 Years",
      placeholderHi: "उदा. 4 वर्ष"
    }
  },
  {
    id: "carpenter",
    displayName: "बढ़ई (Carpenter)",
    group: "labour",
    collection: "labour",
    nameLabelEn: "Carpenter Name (English) *",
    nameLabelHi: "बढ़ई का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Ramu Carpenter",
    namePlaceholderHi: "उदा. रामू बढ़ई",
    showOwnerName: false,
    extra1: {
      key: "skills",
      labelEn: "Woodwork Skills (English)",
      labelHi: "काम का विवरण (Hindi)",
      placeholderEn: "e.g. Doors, windows, modular kitchen, furniture",
      placeholderHi: "उदा. दरवाजे, खिड़की, बेड, अलमारी, मॉड्यूलर किचन"
    },
    extra2: {
      key: "charges",
      labelEn: "Daily Wage (English)",
      labelHi: "दैनिक मजदूरी (Hindi)",
      placeholderEn: "e.g. ₹600 / day",
      placeholderHi: "उदा. ₹600 / दिन"
    },
    extra3: {
      key: "experience",
      labelEn: "Experience (English)",
      labelHi: "अनुभव (Hindi)",
      placeholderEn: "e.g. 8 Years",
      placeholderHi: "उदा. 8 वर्ष"
    }
  },
  {
    id: "tailor",
    displayName: "दर्जी (Tailor)",
    group: "labour",
    collection: "labour",
    nameLabelEn: "Tailor Name (English) *",
    nameLabelHi: "दर्जी का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Royal Tailors / Shyamu",
    namePlaceholderHi: "उदा. रॉयल टेलर्स / श्यामू दर्जी",
    showOwnerName: false,
    extra1: {
      key: "skills",
      labelEn: "Stitching Work (English)",
      labelHi: "सिलाई कार्य (Hindi)",
      placeholderEn: "e.g. Men's suit, pant-shirt, women's suit, blouse",
      placeholderHi: "उदा. पैंट-शर्ट, सूट, कुर्ता-पायजामा, लेडीज ब्लाउज"
    },
    extra2: {
      key: "charges",
      labelEn: "Stitching Rate (English)",
      labelHi: "सिलाई दर (Hindi)",
      placeholderEn: "e.g. ₹350 / pair",
      placeholderHi: "उदा. ₹350 / जोड़ी"
    },
    extra3: {
      key: "experience",
      labelEn: "Experience (English)",
      labelHi: "अनुभव (Hindi)",
      placeholderEn: "e.g. 7 Years",
      placeholderHi: "उदा. 7 वर्ष"
    }
  },
  {
    id: "labour",
    displayName: "मजदूर / लेबर (Labour)",
    group: "labour",
    collection: "labour",
    nameLabelEn: "Worker Name (English) *",
    nameLabelHi: "मजदूर / कामगार का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Pappu Kumar",
    namePlaceholderHi: "उदा. पप्पू कुमार",
    showOwnerName: false,
    extra1: {
      key: "skills",
      labelEn: "Work Type (English)",
      labelHi: "काम का प्रकार (Hindi)",
      placeholderEn: "e.g. Construction helper, farm labour, loading",
      placeholderHi: "उदा. मकान निर्माण लेबर, खेत मजदूरी, लोडिंग"
    },
    extra2: {
      key: "charges",
      labelEn: "Daily Wage (English)",
      labelHi: "दैनिक मजदूरी (Hindi)",
      placeholderEn: "e.g. ₹400 / day",
      placeholderHi: "उदा. ₹400 / दिन"
    },
    extra3: {
      key: "experience",
      labelEn: "Experience (English)",
      labelHi: "अनुभव (Hindi)",
      placeholderEn: "e.g. 3 Years",
      placeholderHi: "उदा. 3 वर्ष"
    }
  },

  // 2. Transport & Vehicles
  {
    id: "car",
    displayName: "कार व टैक्सी किराया (Car / Taxi)",
    group: "transport",
    collection: "transport",
    nameLabelEn: "Driver / Owner Name (English) *",
    nameLabelHi: "चालक / मालिक का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Naimullah Ansari",
    namePlaceholderHi: "उदा. नईमुल्लाह अंसारी",
    showOwnerName: false,
    extra1: {
      key: "vehicleType",
      labelEn: "Vehicle Model / Name (English)",
      labelHi: "गाड़ी का नाम व मॉडल (Hindi)",
      placeholderEn: "e.g. Ertiga VXI / Bolero AC",
      placeholderHi: "उदा. अर्टिगा कार (AC) / बोलेरो"
    },
    extra2: {
      key: "charges",
      labelEn: "Rental Rate (English)",
      labelHi: "किराया दर (Hindi)",
      placeholderEn: "e.g. ₹12 / KM or ₹2,200/day",
      placeholderHi: "उदा. ₹12 / किमी या ₹2,200/दिन"
    }
  },
  {
    id: "tractor",
    displayName: "ट्रैक्टर व ट्रॉली किराया (Tractor Rental)",
    group: "transport",
    collection: "transport",
    nameLabelEn: "Owner / Driver Name (English) *",
    nameLabelHi: "मालिक / चालक का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Radheshyam Yadav",
    namePlaceholderHi: "उदा. राधे श्याम यादव",
    showOwnerName: false,
    extra1: {
      key: "vehicleType",
      labelEn: "Tractor Model & Implements (English)",
      labelHi: "ट्रैक्टर मॉडल व उपकरण (Hindi)",
      placeholderEn: "e.g. Mahindra 575 DI with Trolley & Rotavator",
      placeholderHi: "उदा. महिंद्रा 575 DI (ट्रॉली व रोटावेटर सहित)"
    },
    extra2: {
      key: "charges",
      labelEn: "Hourly / Trolley Rate (English)",
      labelHi: "किराया दर (Hindi)",
      placeholderEn: "e.g. ₹800 / hour or ₹1,200/trolley",
      placeholderHi: "उदा. ₹800 / घंटा या ₹1,200/ट्रॉली"
    }
  },
  {
    id: "pickup",
    displayName: "पिकअप वाहन (Pickup Truck)",
    group: "transport",
    collection: "transport",
    nameLabelEn: "Driver / Owner Name (English) *",
    nameLabelHi: "चालक / मालिक का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Amit Kumar",
    namePlaceholderHi: "उदा. अमित कुमार",
    showOwnerName: false,
    extra1: {
      key: "vehicleType",
      labelEn: "Vehicle Type (English)",
      labelHi: "गाड़ी का प्रकार (Hindi)",
      placeholderEn: "e.g. Bolero Maxi Truck Plus",
      placeholderHi: "उदा. बोलेरो मैक्सी ट्रक"
    },
    extra2: {
      key: "charges",
      labelEn: "Rate per Trip / KM (English)",
      labelHi: "किराया दर (Hindi)",
      placeholderEn: "e.g. ₹15 / KM",
      placeholderHi: "उदा. ₹15 / किमी"
    }
  },
  {
    id: "loader",
    displayName: "लोडर वाहन (Loader Vehicle)",
    group: "transport",
    collection: "transport",
    nameLabelEn: "Owner / Driver Name (English) *",
    nameLabelHi: "मालिक / चालक का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Sonu Transporters",
    namePlaceholderHi: "उदा. सोनू ट्रांसपोर्ट",
    showOwnerName: false,
    extra1: {
      key: "vehicleType",
      labelEn: "Vehicle Model (English)",
      labelHi: "वाहन का मॉडल (Hindi)",
      placeholderEn: "e.g. Tata Ace (Chhota Hathi)",
      placeholderHi: "उदा. टाटा एस (छोटा हाथी)"
    },
    extra2: {
      key: "charges",
      labelEn: "Trip Rate (English)",
      labelHi: "किराया दर (Hindi)",
      placeholderEn: "e.g. ₹500 minimum trip",
      placeholderHi: "उदा. ₹500 न्यूनतम ट्रिप"
    }
  },
  {
    id: "jcb",
    displayName: "जेसीबी मशीन (JCB Machine)",
    group: "transport",
    collection: "transport",
    nameLabelEn: "Owner / Operator Name (English) *",
    nameLabelHi: "मालिक / ऑपरेटर का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Balaji Earthmovers",
    namePlaceholderHi: "उदा. बालाजी अर्थमूवर्स",
    showOwnerName: false,
    extra1: {
      key: "vehicleType",
      labelEn: "Machine Model (English)",
      labelHi: "मशीन मॉडल (Hindi)",
      placeholderEn: "e.g. JCB 3DX Super 4WD",
      placeholderHi: "उदा. जेसीबी 3DX सुपर 4WD"
    },
    extra2: {
      key: "charges",
      labelEn: "Hourly Rate (English)",
      labelHi: "घंटे की दर (Hindi)",
      placeholderEn: "e.g. ₹1,200 / hour",
      placeholderHi: "उदा. ₹1,200 / घंटा"
    }
  },

  // 3. Family & Wedding Functions
  {
    id: "tent",
    displayName: "टेंट हाउस व सजावट (Tent House)",
    group: "family",
    collection: "family_functions",
    nameLabelEn: "Tent House Business Name (English) *",
    nameLabelHi: "टेंट हाउस का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Royal Tent House & Decorators",
    namePlaceholderHi: "उदा. रॉयल टेंट हाउस व डेकोरेशन",
    showOwnerName: true,
    extra1: {
      key: "services",
      labelEn: "Available Equipment & Decor (English)",
      labelHi: "उपलब्ध सामग्री व सेवाएं (Hindi)",
      placeholderEn: "e.g. Waterproof pandal, sofa, stage, lighting, coolers",
      placeholderHi: "उदा. वाटरप्रूफ पंडाल, सोफा, स्टेज, लाइट, कूलर, पंखे"
    },
    extra2: {
      key: "startingPrice",
      labelEn: "Starting Package Price (English)",
      labelHi: "प्रारंभिक पैकेज दर (Hindi)",
      placeholderEn: "e.g. ₹15,000",
      placeholderHi: "उदा. ₹15,000 से शुरू"
    }
  },
  {
    id: "catering",
    displayName: "हलवाई व कैटरिंग (Halwai & Catering)",
    group: "family",
    collection: "family_functions",
    nameLabelEn: "Catering / Halwai Name (English) *",
    nameLabelHi: "हलवाई / कैटरिंग का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Sharma Sweets & Caterers",
    namePlaceholderHi: "उदा. शर्मा स्वीट्स एवं कैटरर्स",
    showOwnerName: true,
    extra1: {
      key: "services",
      labelEn: "Food Items & Specialities (English)",
      labelHi: "खाद्य व्यंजन व विशेषताएं (Hindi)",
      placeholderEn: "e.g. Shahi paneer, rasgulla, chaat counter, buffet",
      placeholderHi: "उदा. शाही पनीर, रसगुल्ला, चाट काउंटर, बुफे व्यवस्था"
    },
    extra2: {
      key: "startingPrice",
      labelEn: "Per Plate / Daily Rate (English)",
      labelHi: "प्रति प्लेट / दैनिक दर (Hindi)",
      placeholderEn: "e.g. ₹250 / plate",
      placeholderHi: "उदा. ₹250 / प्लेट"
    }
  },
  {
    id: "photo",
    displayName: "फोटो व वीडियोग्राफी (Photography)",
    group: "family",
    collection: "family_functions",
    nameLabelEn: "Studio / Photographer Name (English) *",
    nameLabelHi: "स्टूडियो / फोटोग्राफर का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Dream Photo Studio",
    namePlaceholderHi: "उदा. ड्रीम फोटो स्टूडियो",
    showOwnerName: true,
    extra1: {
      key: "services",
      labelEn: "Photography Services (English)",
      labelHi: "सेवाएं (Hindi)",
      placeholderEn: "e.g. Drone shoot, 4K video, pre-wedding, candid album",
      placeholderHi: "उदा. ड्रोन शूट, 4K वीडियो, प्री-वेडिंग, प्रीमियम एल्बम"
    },
    extra2: {
      key: "startingPrice",
      labelEn: "Starting Price (English)",
      labelHi: "प्रारंभिक दर (Hindi)",
      placeholderEn: "e.g. ₹10,000",
      placeholderHi: "उदा. ₹10,000 से"
    }
  },
  {
    id: "dj",
    displayName: "डीजे व बैंड बाजा (DJ Sound & Band)",
    group: "family",
    collection: "family_functions",
    nameLabelEn: "DJ / Sound Name (English) *",
    nameLabelHi: "डीजे / साउंड का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Rock Star DJ & Sound",
    namePlaceholderHi: "उदा. रॉक स्टार डीजे साउंड",
    showOwnerName: true,
    extra1: {
      key: "services",
      labelEn: "Sound Setup Features (English)",
      labelHi: "सिस्टम विवरण व लाइट (Hindi)",
      placeholderEn: "e.g. High bass speakers, smoke machine, laser lights",
      placeholderHi: "उदा. हाई बेस साउंड, स्मोक मशीन, लेजर लाइट, रोड शो"
    },
    extra2: {
      key: "startingPrice",
      labelEn: "Booking Rate (English)",
      labelHi: "बुकिंग दर (Hindi)",
      placeholderEn: "e.g. ₹6,000 / night",
      placeholderHi: "उदा. ₹6,000 / रात"
    }
  },
  {
    id: "marriage_halls",
    displayName: "मैरिज हॉल व वाटिका (Marriage Hall)",
    group: "family",
    collection: "family_functions",
    nameLabelEn: "Lawn / Hall Name (English) *",
    nameLabelHi: "मैरिज हॉल / वाटिका का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Milan Marriage Lawn",
    namePlaceholderHi: "उदा. मिलन मैरिज लॉन एवं वाटिका",
    showOwnerName: true,
    extra1: {
      key: "services",
      labelEn: "Facilities (English)",
      labelHi: "उपलब्ध सुविधाएं (Hindi)",
      placeholderEn: "e.g. 10 AC rooms, huge parking, generator, lawn",
      placeholderHi: "उदा. 10 एसी कमरे, विशाल पार्किंग, जनरेटर, हरा-भरा लॉन"
    },
    extra2: {
      key: "startingPrice",
      labelEn: "Booking Price (English)",
      labelHi: "बुकिंग शुल्क (Hindi)",
      placeholderEn: "e.g. ₹30,000 / day",
      placeholderHi: "उदा. ₹30,000 / दिन"
    }
  },

  // 4. Construction & Materials
  {
    id: "bricks",
    displayName: "ईंट भट्ठा सप्लायर (Bricks Supplier)",
    group: "construction",
    collection: "construction",
    nameLabelEn: "Brick Kiln / Supplier Name (English) *",
    nameLabelHi: "भट्ठे / सप्लायर का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Kisan Bricks Udyog",
    namePlaceholderHi: "उदा. किसान ईंट उद्योग",
    showOwnerName: true,
    extra1: {
      key: "products",
      labelEn: "Brick Varieties (English)",
      labelHi: "ईंटों का प्रकार (Hindi)",
      placeholderEn: "e.g. First class red bricks, fly-ash bricks, ballast",
      placeholderHi: "उदा. अव्वल लाल ईंट, दोयम ईंट, रोड़ा, फ्लाई ऐश"
    },
    extra2: {
      key: "charges",
      labelEn: "Rate per 1000 Bricks (English)",
      labelHi: "भाव प्रति 1000 ईंट (Hindi)",
      placeholderEn: "e.g. ₹7,500 / 1000 bricks",
      placeholderHi: "उदा. ₹7,500 / हज़ार"
    }
  },
  {
    id: "material_shops",
    displayName: "सीमेंट, बालू व सरिया (Building Material)",
    group: "construction",
    collection: "construction",
    nameLabelEn: "Shop Name (English) *",
    nameLabelHi: "दुकान / प्रतिष्ठान का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Gupta Cement & Building Materials",
    namePlaceholderHi: "उदा. गुप्ता सीमेंट एवं बिल्डिंग मैटेरियल",
    showOwnerName: true,
    extra1: {
      key: "products",
      labelEn: "Available Brands & Products (English)",
      labelHi: "उपलब्ध ब्रांड्स व सामान (Hindi)",
      placeholderEn: "e.g. Ultratech cement, Kamdhenu TMT bars, red sand",
      placeholderHi: "उदा. अल्ट्राटेक सीमेंट, कामधेनु सरिया, मौरंग, बालू"
    }
  },
  {
    id: "hardware_shops",
    displayName: "हार्डवेयर स्टोर (Hardware Shop)",
    group: "construction",
    collection: "construction",
    nameLabelEn: "Hardware Shop Name (English) *",
    nameLabelHi: "हार्डवेयर स्टोर का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Verma Hardware & Electricals",
    namePlaceholderHi: "उदा. वर्मा हार्डवेयर एंड पेंट्स",
    showOwnerName: true,
    extra1: {
      key: "products",
      labelEn: "Key Items (English)",
      labelHi: "उपलब्ध सामग्री (Hindi)",
      placeholderEn: "e.g. PVC pipes, paints, locks, sanitary fittings, tools",
      placeholderHi: "उदा. पीवीसी पाइप, एशियन पेंट, ताले, सेनेटरी फिटिंग्स"
    }
  },
  {
    id: "linter_machine",
    displayName: "लिन्टर मशीन (Linter Machine)",
    group: "construction",
    collection: "construction",
    nameLabelEn: "Contractor / Machine Name (English) *",
    nameLabelHi: "लिन्टर ठेकेदार / मशीन का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Shivam Linter & Mixer",
    namePlaceholderHi: "उदा. शिवम लिन्टर एवं मिक्सर मशीन",
    showOwnerName: true,
    extra1: {
      key: "services",
      labelEn: "Machine & Lift Details (English)",
      labelHi: "मशीन विवरण (Hindi)",
      placeholderEn: "e.g. High-speed lift mixer with vibrator",
      placeholderHi: "उदा. हाई स्पीड लिफ्ट मिक्सर एवं वाइब्रेटर"
    },
    extra2: {
      key: "charges",
      labelEn: "Rate per Sq Ft (English)",
      labelHi: "ढलाई दर (Hindi)",
      placeholderEn: "e.g. ₹35 / sq ft",
      placeholderHi: "उदा. ₹35 / वर्ग फुट"
    }
  },
  {
    id: "shuttering",
    displayName: "सेंटरिंग व शटरिंग (Shuttering)",
    group: "construction",
    collection: "construction",
    nameLabelEn: "Contractor Name (English) *",
    nameLabelHi: "शटरिंग ठेकेदार का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Yadav Shuttering Store",
    namePlaceholderHi: "उदा. यादव शटरिंग स्टोर",
    showOwnerName: true,
    extra1: {
      key: "services",
      labelEn: "Materials (English)",
      labelHi: "शटरिंग सामग्री (Hindi)",
      placeholderEn: "e.g. Iron plates, channels, iron props, bamboo poles",
      placeholderHi: "उदा. लोहे की प्लेटें, चैनल, लोहे की बल्लियां, बांस"
    },
    extra2: {
      key: "charges",
      labelEn: "Rate per Sq Ft (English)",
      labelHi: "किराया दर (Hindi)",
      placeholderEn: "e.g. ₹25 / sq ft",
      placeholderHi: "उदा. ₹25 / वर्ग फुट"
    }
  },

  // 5. Health & Hospital
  {
    id: "doctors",
    displayName: "डॉक्टर व क्लिनिक (Doctor / Clinic)",
    group: "health",
    collection: "health",
    nameLabelEn: "Doctor / Clinic Name (English) *",
    nameLabelHi: "डॉक्टर / क्लिनिक का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Dr. A. K. Singh (Singh Clinic)",
    namePlaceholderHi: "उदा. डॉ. ए. के. सिंह (सिंह क्लिनिक)",
    showOwnerName: false,
    extra1: {
      key: "specialisation",
      labelEn: "Speciality / Degree (English)",
      labelHi: "डिग्री व विशेषज्ञता (Hindi)",
      placeholderEn: "e.g. MBBS, MD - General Physician",
      placeholderHi: "उदा. MBBS, सामान्य रोग विशेषज्ञ"
    },
    extra2: {
      key: "availability",
      labelEn: "Clinic Timings (English)",
      labelHi: "क्लिनिक समय (Hindi)",
      placeholderEn: "e.g. 9:00 AM - 1:00 PM, 5:00 PM - 8:00 PM",
      placeholderHi: "उदा. सुबह 9 से 1, शाम 5 से 8 बजे"
    },
    extra3: {
      key: "facilities",
      labelEn: "Facilities (English)",
      labelHi: "उपलब्ध सुविधाएं (Hindi)",
      placeholderEn: "e.g. Nebulizer, ECG, Blood test, Medicine",
      placeholderHi: "उदा. नेबुलाइजर, ईसीजी, खून जांच, दवाएं"
    }
  },
  {
    id: "pharmacy",
    displayName: "मेडिकल स्टोर (Pharmacy / Medical)",
    group: "health",
    collection: "health",
    nameLabelEn: "Medical Store Name (English) *",
    nameLabelHi: "मेडिकल स्टोर का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Sanjeevani Medical Store",
    namePlaceholderHi: "उदा. संजीवनी मेडिकल स्टोर",
    showOwnerName: true,
    extra1: {
      key: "availability",
      labelEn: "Opening Hours (English)",
      labelHi: "खुलने का समय (Hindi)",
      placeholderEn: "e.g. 8:00 AM - 10:00 PM (Daily)",
      placeholderHi: "उदा. सुबह 8 से रात 10 बजे तक (प्रतिदिन)"
    },
    extra2: {
      key: "facilities",
      labelEn: "Services (English)",
      labelHi: "सुविधाएं (Hindi)",
      placeholderEn: "e.g. All English & Ayurvedic medicines, home delivery",
      placeholderHi: "उदा. सभी अंग्रेजी व आयुर्वेदिक दवाएं, होम डिलीवरी"
    }
  },
  {
    id: "hospitals",
    displayName: "अस्पताल (Hospital)",
    group: "health",
    collection: "health",
    nameLabelEn: "Hospital / Nursing Home Name (English) *",
    nameLabelHi: "अस्पताल का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Janta Hospital & Trauma Center",
    namePlaceholderHi: "उदा. जनता हॉस्पिटल एवं ट्रॉमा सेंटर",
    showOwnerName: true,
    extra1: {
      key: "facilities",
      labelEn: "Available Departments (English)",
      labelHi: "सुविधाएं व विभाग (Hindi)",
      placeholderEn: "e.g. 24x7 Emergency, ICU, Operation theater, Pathology",
      placeholderHi: "उदा. 24 घंटे इमरजेंसी, आईसीयू, ऑपरेशन थियेटर, पैथोलॉजी"
    },
    extra2: {
      key: "availability",
      labelEn: "Hours (English)",
      labelHi: "समय (Hindi)",
      placeholderEn: "e.g. 24 Hours Open",
      placeholderHi: "उदा. 24 घंटे खुला"
    }
  },
  {
    id: "ambulance",
    displayName: "एम्बुलेंस सेवा (Ambulance)",
    group: "health",
    collection: "health",
    nameLabelEn: "Ambulance Driver / Service Name (English) *",
    nameLabelHi: "एम्बुलेंस चालक / सेवा का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Gaon Emergency Ambulance",
    namePlaceholderHi: "उदा. गाँव आपातकालीन एम्बुलेंस",
    showOwnerName: false,
    extra1: {
      key: "facilities",
      labelEn: "Life Support on Board (English)",
      labelHi: "उपलब्ध सुविधाएं (Hindi)",
      placeholderEn: "e.g. Oxygen cylinder, stretcher, first aid kit",
      placeholderHi: "उदा. ऑक्सीजन सिलेंडर, स्ट्रेचर, प्राथमिक उपचार"
    },
    extra2: {
      key: "availability",
      labelEn: "Availability (English)",
      labelHi: "उपलब्धता (Hindi)",
      placeholderEn: "e.g. 24x7 Emergency Service",
      placeholderHi: "उदा. 24 घंटे उपलब्ध"
    }
  },

  // 6. Education
  {
    id: "private_schools",
    displayName: "स्कूल व कॉलेज (School)",
    group: "education",
    collection: "education",
    nameLabelEn: "School Name (English) *",
    nameLabelHi: "विद्यालय / स्कूल का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Saraswati Shishu Mandir",
    namePlaceholderHi: "उदा. सरस्वती शिशु मंदिर",
    showOwnerName: true,
    extra1: {
      key: "details",
      labelEn: "Classes & Board (English)",
      labelHi: "कक्षाएं व बोर्ड (Hindi)",
      placeholderEn: "e.g. Nursery to 10th (UP / CBSE Board)",
      placeholderHi: "उदा. नर्सरी से 10वीं तक (यूपी/सीबीएसई बोर्ड)"
    },
    extra2: {
      key: "extraInfo",
      labelEn: "Facilities (English)",
      labelHi: "सुविधाएं (Hindi)",
      placeholderEn: "e.g. School bus, computer lab, library, sports",
      placeholderHi: "उदा. बस सुविधा, कंप्यूटर लैब, खेल मैदान"
    }
  },
  {
    id: "coaching_centers",
    displayName: "कोचिंग सेंटर (Coaching Center)",
    group: "education",
    collection: "education",
    nameLabelEn: "Coaching Name (English) *",
    nameLabelHi: "कोचिंग संस्थान का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Target Coaching Classes",
    namePlaceholderHi: "उदा. टारगेट कोचिंग क्लासेज",
    showOwnerName: true,
    extra1: {
      key: "details",
      labelEn: "Courses & Subjects (English)",
      labelHi: "कक्षाएं व विषय (Hindi)",
      placeholderEn: "e.g. 9th-12th Math & Science, Police/SSC exam prep",
      placeholderHi: "उदा. कक्षा 9 से 12 गणित व विज्ञान, पुलिस भर्ती तैयारी"
    },
    extra2: {
      key: "extraInfo",
      labelEn: "Batch Timings (English)",
      labelHi: "बैच का समय (Hindi)",
      placeholderEn: "e.g. Morning 6-9 AM, Evening 4-7 PM",
      placeholderHi: "उदा. सुबह 6-9, शाम 4-7 बजे"
    }
  },
  {
    id: "home_tutors",
    displayName: "होम ट्यूटर (Home Tutor)",
    group: "education",
    collection: "education",
    nameLabelEn: "Tutor Name (English) *",
    nameLabelHi: "शिक्षक का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Amit Kumar (Tutor)",
    namePlaceholderHi: "उदा. अमित कुमार (ट्यूटर)",
    showOwnerName: false,
    extra1: {
      key: "details",
      labelEn: "Subjects & Classes (English)",
      labelHi: "पढ़ाने के विषय (Hindi)",
      placeholderEn: "e.g. Math & English for class 6th to 10th",
      placeholderHi: "उदा. गणित व अंग्रेजी कक्षा 6 से 10"
    },
    extra2: {
      key: "extraInfo",
      labelEn: "Qualifications (English)",
      labelHi: "योग्यता (Hindi)",
      placeholderEn: "e.g. M.Sc (Mathematics), B.Ed",
      placeholderHi: "उदा. एम.एससी. (गणित), बी.एड."
    }
  },
  {
    id: "skill_centers",
    displayName: "कंप्यूटर व स्किल सेंटर (Skill Center)",
    group: "education",
    collection: "education",
    nameLabelEn: "Center Name (English) *",
    nameLabelHi: "संस्थान का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Digital Computer Academy",
    namePlaceholderHi: "उदा. डिजिटल कंप्यूटर एकेडमी",
    showOwnerName: true,
    extra1: {
      key: "details",
      labelEn: "Courses (English)",
      labelHi: "उपलब्ध कोर्स (Hindi)",
      placeholderEn: "e.g. CCC, Tally, DCA, Hindi/English Typing",
      placeholderHi: "उदा. CCC, टैली, डीसीए, हिंदी व अंग्रेजी टाइपिंग"
    }
  },

  // 7. Legal & Panchayat
  {
    id: "court_lawyers",
    displayName: "वकील (Court Lawyer)",
    group: "legal",
    collection: "legal_panchayat",
    nameLabelEn: "Advocate Name (English) *",
    nameLabelHi: "अधिवक्ता / वकील का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Adv. R. K. Mishra",
    namePlaceholderHi: "उदा. एडवोकेट आर. के. मिश्रा",
    showOwnerName: false,
    extra1: {
      key: "details",
      labelEn: "Court & Practice Areas (English)",
      labelHi: "न्यायालय व केस प्रकार (Hindi)",
      placeholderEn: "e.g. Civil & Revenue Court, Land registry, Mutation",
      placeholderHi: "उदा. दीवानी व तहसील, जमीन बैनामा, दाखिल खारिज, विवाद"
    },
    extra2: {
      key: "extraInfo",
      labelEn: "Chamber Location (English)",
      labelHi: "चैम्बर का स्थान (Hindi)",
      placeholderEn: "e.g. Tehsil Compound, Chamber #14",
      placeholderHi: "उदा. तहसील परिसर, चैम्बर नं. 14"
    }
  },
  {
    id: "panchayat_officials",
    displayName: "पंचायत प्रतिनिधि (Panchayat Official)",
    group: "legal",
    collection: "legal_panchayat",
    nameLabelEn: "Official Name (English) *",
    nameLabelHi: "प्रतिनिधि / अधिकारी का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Gram Pradhan / Lekhpal",
    namePlaceholderHi: "उदा. ग्राम प्रधान / लेखपाल",
    showOwnerName: false,
    extra1: {
      key: "details",
      labelEn: "Designation / Role (English)",
      labelHi: "पद / पदनाम (Hindi)",
      placeholderEn: "e.g. Gram Pradhan / Panchayat Sachiv",
      placeholderHi: "उदा. ग्राम प्रधान / पंचायत सचिव / लेखपाल"
    }
  },
  {
    id: "senior_citizens",
    displayName: "वरिष्ठ सम्मानित नागरिक (Senior Citizen)",
    group: "legal",
    collection: "legal_panchayat",
    nameLabelEn: "Senior Citizen Name (English) *",
    nameLabelHi: "सम्मानित नागरिक का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Masterji / Ex-Pradhan",
    namePlaceholderHi: "उदा. पंडित जी / पूर्व प्रधान",
    showOwnerName: false,
    extra1: {
      key: "details",
      labelEn: "Role / Community Position (English)",
      labelHi: "पहचान व अनुभव (Hindi)",
      placeholderEn: "e.g. Retired Headmaster, Village Advisor",
      placeholderHi: "उदा. सेवानिवृत्त प्रधानाध्यापक, ग्राम सलाहकार"
    }
  },

  // 8. Mandi & Crops
  {
    id: "mandi_buyer",
    displayName: "फसल व गल्ला खरीदार (Mandi Crop Buyer)",
    group: "mandi",
    collection: "mandi",
    nameLabelEn: "Trader / Buyer Name (English) *",
    nameLabelHi: "व्यापारी / खरीदार का नाम (Hindi) *",
    namePlaceholderEn: "e.g. Kishan Trading Co.",
    namePlaceholderHi: "उदा. किसान ट्रेडिंग कंपनी / लाला जी",
    showOwnerName: false,
    extra1: {
      key: "cropName",
      labelEn: "Crop / Grain Name (English)",
      labelHi: "फसल / अनाज का नाम (Hindi)",
      placeholderEn: "e.g. Wheat, Mustard, Paddy, Potato",
      placeholderHi: "उदा. गेहूं, सरसों, धान, मक्का, आलू"
    },
    extra2: {
      key: "price",
      labelEn: "Current Buying Price (English)",
      labelHi: "वर्तमान खरीद दर (Hindi)",
      placeholderEn: "e.g. 2450",
      placeholderHi: "उदा. 2450"
    },
    extra3: {
      key: "unit",
      labelEn: "Unit (English)",
      labelHi: "इकाई (Hindi)",
      placeholderEn: "e.g. Quintal / Bag",
      placeholderHi: "उदा. क्विंटल"
    }
  }
];

interface SubmissionsManagerProps {
  initialFilterStatus?: string;
  initialCategoryFilter?: string;
}

export default function SubmissionsManager({
  initialFilterStatus = "all",
  initialCategoryFilter = "all"
}: SubmissionsManagerProps) {
  const [submissions, setSubmissions] = useState<SubmissionItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [filterStatus, setFilterStatus] = useState<string>(initialFilterStatus);
  const [categoryFilter, setCategoryFilter] = useState<string>(initialCategoryFilter);
  const [selectedVillage, setSelectedVillage] = useState<string>("all");
  const [viewMode, setViewMode] = useState<"grid" | "village_grouped">("village_grouped");
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [editingItem, setEditingItem] = useState<SubmissionItem | null>(null);
  const [actionLoading, setActionLoading] = useState<string | null>(null);

  // Modern Toast Notification State
  const [toast, setToast] = useState<{
    message: string;
    subMessage?: string;
    type?: "success" | "error" | "info";
  } | null>(null);

  const showToast = (message: string, subMessage?: string, type: "success" | "error" | "info" = "success") => {
    setToast({ message, subMessage, type });
    setTimeout(() => {
      setToast((prev) => (prev?.message === message ? null : prev));
    }, 4500);
  };

  // New Service Add Modal State
  const [isAddServiceModalOpen, setIsAddServiceModalOpen] = useState(false);
  const [selectedCatId, setSelectedCatId] = useState<string>("painter");
  const [newServiceNameEn, setNewServiceNameEn] = useState("");
  const [newServiceNameHi, setNewServiceNameHi] = useState("");
  const [newContactNameEn, setNewContactNameEn] = useState("");
  const [newContactNameHi, setNewContactNameHi] = useState("");
  const [newPhone, setNewPhone] = useState("");
  const [newVillageNameEn, setNewVillageNameEn] = useState("");
  const [newVillageNameHi, setNewVillageNameHi] = useState("");
  const [newAddressEn, setNewAddressEn] = useState("");
  const [newAddressHi, setNewAddressHi] = useState("");
  const [newDetailsEn, setNewDetailsEn] = useState("");
  const [newDetailsHi, setNewDetailsHi] = useState("");

  // Dynamic extra fields for specific DTO mappings
  const [newExtra1En, setNewExtra1En] = useState("");
  const [newExtra1Hi, setNewExtra1Hi] = useState("");
  const [newExtra2En, setNewExtra2Hi] = useState("");
  const [newExtra2HiState, setNewExtra2HiState] = useState("");
  const [newExtra3En, setNewExtra3En] = useState("");
  const [newExtra3Hi, setNewExtra3Hi] = useState("");

  const [newPublishLive, setNewPublishLive] = useState(true);

  // Active Category Definition
  const currentCatDef = useMemo(() => {
    return CATEGORY_DEFINITIONS.find((c) => c.id === selectedCatId) || CATEGORY_DEFINITIONS[0];
  }, [selectedCatId]);

  useEffect(() => {
    setFilterStatus(initialFilterStatus);
  }, [initialFilterStatus]);

  useEffect(() => {
    setCategoryFilter(initialCategoryFilter);
  }, [initialCategoryFilter]);

  useEffect(() => {
    const q = query(collection(db, "business_registrations"));
    const unsubscribe = onSnapshot(q, (snapshot) => {
      const items: SubmissionItem[] = [];
      snapshot.forEach((docSnap) => {
        const data = docSnap.data();
        const bName = parseStringOrMap(data.businessName) || docSnap.id;
        const cName = parseStringOrMap(data.contactName);
        const cat = parseStringOrMap(data.category) || "business";
        const phone = parseStringOrMap(data.phoneNumber);
        const addr = parseStringOrMap(data.address);
        const desc = parseStringOrMap(data.description);
        const vName = parseStringOrMap(data.villageName) || parseStringOrMap(data.address);

        items.push({
          id: docSnap.id,
          contactName: cName,
          businessName: bName,
          category: cat,
          phoneNumber: phone,
          address: addr,
          description: desc,
          villageId: data.villageId || "",
          villageName: vName || "General",
          status: data.status || "pending",
          createdAt: data.createdAt,
          nameEn: parseStringOrMap(data.nameEn) || bName,
          nameHi: parseStringOrMap(data.nameHi) || bName,
          detailsEn: parseStringOrMap(data.detailsEn) || desc,
          detailsHi: parseStringOrMap(data.detailsHi) || desc,
          contactNameEn: parseStringOrMap(data.contactNameEn) || cName,
          contactNameHi: parseStringOrMap(data.contactNameHi) || cName,
          villageNameEn: parseStringOrMap(data.villageNameEn) || vName,
          villageNameHi: parseStringOrMap(data.villageNameHi) || vName,
          addressEn: parseStringOrMap(data.addressEn) || addr,
          addressHi: parseStringOrMap(data.addressHi) || addr,
          publishedDocPath: data.publishedDocPath || undefined,
          publishedCollection: data.publishedCollection || undefined,
          chargesEn: parseStringOrMap(data.chargesEn) || (typeof data.charges === "object" ? parseStringOrMap(data.charges?.en) : parseStringOrMap(data.charges)) || "",
          chargesHi: parseStringOrMap(data.chargesHi) || (typeof data.charges === "object" ? parseStringOrMap(data.charges?.hi) : parseStringOrMap(data.charges)) || "",
          experienceEn: parseStringOrMap(data.experienceEn) || (typeof data.experience === "object" ? parseStringOrMap(data.experience?.en) : parseStringOrMap(data.experience)) || "",
          experienceHi: parseStringOrMap(data.experienceHi) || (typeof data.experience === "object" ? parseStringOrMap(data.experience?.hi) : parseStringOrMap(data.experience)) || "",
          startingPriceEn: parseStringOrMap(data.startingPriceEn) || (typeof data.startingPrice === "object" ? parseStringOrMap(data.startingPrice?.en) : parseStringOrMap(data.startingPrice)) || "",
          startingPriceHi: parseStringOrMap(data.startingPriceHi) || (typeof data.startingPrice === "object" ? parseStringOrMap(data.startingPrice?.hi) : parseStringOrMap(data.startingPrice)) || "",
          extraInfoEn: parseStringOrMap(data.extraInfoEn) || (typeof data.extraInfo === "object" ? parseStringOrMap(data.extraInfo?.en) : parseStringOrMap(data.extraInfo)) || "",
          extraInfoHi: parseStringOrMap(data.extraInfoHi) || (typeof data.extraInfo === "object" ? parseStringOrMap(data.extraInfo?.hi) : parseStringOrMap(data.extraInfo)) || "",
          vehicleTypeEn: parseStringOrMap(data.vehicleTypeEn) || (typeof data.vehicleType === "object" ? parseStringOrMap(data.vehicleType?.en) : parseStringOrMap(data.vehicleType)) || "",
          vehicleTypeHi: parseStringOrMap(data.vehicleTypeHi) || (typeof data.vehicleType === "object" ? parseStringOrMap(data.vehicleType?.hi) : parseStringOrMap(data.vehicleType)) || "",
          availabilityEn: parseStringOrMap(data.availabilityEn) || (typeof data.availability === "object" ? parseStringOrMap(data.availability?.en) : parseStringOrMap(data.availability)) || "",
          availabilityHi: parseStringOrMap(data.availabilityHi) || (typeof data.availability === "object" ? parseStringOrMap(data.availability?.hi) : parseStringOrMap(data.availability)) || ""
        });
      });

      items.sort((a, b) => {
        const tA = a.createdAt?.seconds || 0;
        const tB = b.createdAt?.seconds || 0;
        return tB - tA;
      });
      setSubmissions(items);
      setLoading(false);
    }, (error) => {
      console.error("Error listening to submissions:", error);
      setLoading(false);
    });

    return () => unsubscribe();
  }, []);

  // Unique Village List
  const villageList = useMemo(() => {
    const map = new Map<string, number>();
    submissions.forEach((item) => {
      const vName = item.villageName?.trim() || item.address.trim() || "Unspecified Village";
      map.set(vName, (map.get(vName) || 0) + 1);
    });
    return Array.from(map.entries()).sort((a, b) => a[0].localeCompare(b[0]));
  }, [submissions]);

  // Stats
  const stats = useMemo(() => {
    const total = submissions.length;
    const pending = submissions.filter((s) => s.status === "pending").length;
    const approved = submissions.filter((s) => s.status === "approved").length;
    const rejected = submissions.filter((s) => s.status === "rejected").length;
    return { total, pending, approved, rejected };
  }, [submissions]);

  // Filtered Items
  const filteredSubmissions = useMemo(() => {
    return submissions.filter((item) => {
      if (filterStatus !== "all" && item.status !== filterStatus) return false;
      if (categoryFilter !== "all") {
        if (!item.category.toLowerCase().includes(categoryFilter.toLowerCase())) return false;
      }
      if (selectedVillage !== "all") {
        const itemVillage = item.villageName?.trim() || item.address.trim() || "Unspecified Village";
        if (itemVillage !== selectedVillage) return false;
      }
      if (searchQuery.trim()) {
        const q = searchQuery.toLowerCase();
        return (
          item.businessName.toLowerCase().includes(q) ||
          item.contactName.toLowerCase().includes(q) ||
          item.phoneNumber.includes(q) ||
          item.address.toLowerCase().includes(q) ||
          item.category.toLowerCase().includes(q) ||
          (item.villageName && item.villageName.toLowerCase().includes(q))
        );
      }
      return true;
    });
  }, [submissions, filterStatus, categoryFilter, selectedVillage, searchQuery]);

  // Village Grouped
  const villageGroupedData = useMemo(() => {
    const groups: Record<string, SubmissionItem[]> = {};
    filteredSubmissions.forEach((item) => {
      const key = item.villageName?.trim() || item.address.trim() || "Unspecified Village";
      if (!groups[key]) groups[key] = [];
      groups[key].push(item);
    });
    return Object.entries(groups).sort((a, b) => b[1].length - a[1].length);
  }, [filteredSubmissions]);

  // Open Edit Modal with full fallback initialization
  const openEditModal = (item: SubmissionItem) => {
    setEditingItem({
      ...item,
      nameEn: item.nameEn || item.businessName || "",
      nameHi: item.nameHi || item.businessName || "",
      contactNameEn: item.contactNameEn || item.contactName || "",
      contactNameHi: item.contactNameHi || item.contactName || "",
      detailsEn: item.detailsEn || item.description || "",
      detailsHi: item.detailsHi || item.description || "",
      addressEn: item.addressEn || item.address || "",
      addressHi: item.addressHi || item.address || "",
      villageNameEn: item.villageNameEn || item.villageName || "",
      villageNameHi: item.villageNameHi || item.villageName || "",
      phoneNumber: item.phoneNumber || "",
      category: item.category || "",
      chargesEn: item.chargesEn || "",
      chargesHi: item.chargesHi || "",
      experienceEn: item.experienceEn || "",
      experienceHi: item.experienceHi || "",
      startingPriceEn: item.startingPriceEn || "",
      startingPriceHi: item.startingPriceHi || "",
      extraInfoEn: item.extraInfoEn || "",
      extraInfoHi: item.extraInfoHi || "",
      vehicleTypeEn: item.vehicleTypeEn || "",
      vehicleTypeHi: item.vehicleTypeHi || "",
      availabilityEn: item.availabilityEn || "",
      availabilityHi: item.availabilityHi || ""
    });
  };

  // Helper to find the existing village ID in Firestore without creating ghost village docs!
  const getExistingVillageId = async (vIdCandidate?: string, vNameCandidate?: string): Promise<string> => {
    try {
      const vSnap = await getDocs(collection(db, "villages"));
      if (vSnap.empty) return vIdCandidate || "v_mahuwa_shukla";

      const cleanCandidate = (vNameCandidate || "").toLowerCase().replace(/[^a-z0-9]/g, "").replace(/a+/g, "a");

      // 1. Direct ID match
      if (vIdCandidate) {
        for (const vDoc of vSnap.docs) {
          if (vDoc.id === vIdCandidate) return vDoc.id;
        }
      }

      // 2. Match by normalized villageName
      for (const vDoc of vSnap.docs) {
        const vData = vDoc.data();
        const vName = vData.villageName;
        const en = (typeof vName === "object" ? vName?.en : vName) || "";
        const hi = (typeof vName === "object" ? vName?.hi : vName) || "";

        const cleanEn = en.toLowerCase().replace(/[^a-z0-9]/g, "").replace(/a+/g, "a");
        if (cleanCandidate && cleanEn && (cleanEn === cleanCandidate || cleanEn.includes(cleanCandidate) || cleanCandidate.includes(cleanEn))) {
          return vDoc.id;
        }
        if (vNameCandidate && (hi.includes(vNameCandidate) || vNameCandidate.includes(hi))) {
          return vDoc.id;
        }
      }

      // 3. Match by partial ID
      for (const vDoc of vSnap.docs) {
        const cleanId = vDoc.id.toLowerCase().replace(/[^a-z0-9]/g, "").replace(/a+/g, "a");
        if (cleanCandidate && (cleanId.includes(cleanCandidate) || cleanCandidate.includes(cleanId))) {
          return vDoc.id;
        }
      }

      return vIdCandidate || vSnap.docs[0].id;
    } catch (e) {
      console.warn("Could not query villages:", e);
      return vIdCandidate || "v_mahuwa_shukla";
    }
  };

  // Determine target collection matching mobile app category
  const getTargetCollection = (categoryNameOrId: string): { collection: string; categoryId: string } => {
    const catLower = (categoryNameOrId || "").toLowerCase();
    const matchedDef = CATEGORY_DEFINITIONS.find(
      (c) => c.displayName.toLowerCase().includes(catLower) || c.id.toLowerCase() === catLower
    );
    if (matchedDef) {
      return { collection: matchedDef.collection, categoryId: matchedDef.id };
    }
    if (catLower.includes("transport") || catLower.includes("vehicle") || catLower.includes("car") || catLower.includes("taxi") || catLower.includes("टैक्सी") || catLower.includes("किराया")) {
      return { collection: "transport", categoryId: "car" };
    } else if (catLower.includes("tractor") || catLower.includes("ट्रैक्टर")) {
      return { collection: "transport", categoryId: "tractor" };
    } else if (catLower.includes("education") || catLower.includes("school") || catLower.includes("coaching") || catLower.includes("शिक्षा")) {
      return { collection: "education", categoryId: "private_schools" };
    } else if (catLower.includes("health") || catLower.includes("doctor") || catLower.includes("hospital") || catLower.includes("pharmacy") || catLower.includes("दवा")) {
      return { collection: "health", categoryId: "doctors" };
    } else if (catLower.includes("mandi") || catLower.includes("crop") || catLower.includes("buyer") || catLower.includes("फसल")) {
      return { collection: "mandi", categoryId: "mandi_buyer" };
    } else if (catLower.includes("family") || catLower.includes("event") || catLower.includes("tent") || catLower.includes("catering") || catLower.includes("शादी")) {
      return { collection: "family_functions", categoryId: "tent" };
    } else if (catLower.includes("construction") || catLower.includes("brick") || catLower.includes("hardware") || catLower.includes("ईंट")) {
      return { collection: "construction", categoryId: "bricks" };
    } else if (catLower.includes("lawyer") || catLower.includes("panchayat") || catLower.includes("वकील")) {
      return { collection: "legal_panchayat", categoryId: "court_lawyers" };
    }
    return { collection: "labour", categoryId: "painter" };
  };

  // Approve Action - Publishes directly into mobile app Firestore path
  const handleApprove = async (item: SubmissionItem) => {
    try {
      setActionLoading(item.id);

      const vNameEn = item.villageNameEn || item.villageName || item.address || "General";
      const vNameHi = item.villageNameHi || item.villageName || item.address || "General";

      const { collection: targetColl, categoryId: catId } = getTargetCollection(item.category);

      const bNameEn = item.nameEn || item.businessName || "";
      const bNameHi = item.nameHi || item.businessName || "";
      const cNameEn = item.contactNameEn || item.contactName || bNameEn;
      const cNameHi = item.contactNameHi || item.contactName || bNameHi;
      const dEn = item.detailsEn || item.description || "";
      const dHi = item.detailsHi || item.description || "";
      const aEn = item.addressEn || item.address || "";
      const aHi = item.addressHi || item.address || "";

      const approvePayload: Record<string, any> = {
        id: item.id,
        categoryId: catId,
        contact: item.phoneNumber || "",
        status: "active",
        approvedAt: serverTimestamp()
      };

      if (targetColl === "transport") {
        const driverNameEn = cNameEn.trim();
        const driverNameHi = cNameHi.trim();
        const vehicleNameEn = bNameEn.trim();
        const vehicleNameHi = bNameHi.trim();

        // 1. Title next to Person icon: Driver/Owner Name only (NO car type)
        const fullTitleEn = driverNameEn || vehicleNameEn;
        const fullTitleHi = driverNameHi || vehicleNameHi;

        approvePayload.name = { en: fullTitleEn, hi: fullTitleHi };

        // 2. Location with Landmark & Village: "Maurya kirna store, Mahuawa Shukla"
        let locEn = aEn.trim();
        if (vNameEn && !locEn.toLowerCase().includes(vNameEn.toLowerCase())) {
          locEn = locEn ? `${locEn}, ${vNameEn}` : vNameEn;
        }
        let locHi = aHi.trim();
        if (vNameHi && !locHi.includes(vNameHi)) {
          locHi = locHi ? `${locHi}, ${vNameHi}` : vNameHi;
        }
        approvePayload.location = { en: locEn, hi: locHi };

        // 3. Vehicle section: Car name + Details concatenated
        let vehDescEn = vehicleNameEn;
        if (dEn.trim()) {
          vehDescEn = vehicleNameEn ? `${vehicleNameEn} - ${dEn.trim()}` : dEn.trim();
        }
        let vehDescHi = vehicleNameHi;
        if (dHi.trim()) {
          vehDescHi = vehicleNameHi ? `${vehicleNameHi} - ${dHi.trim()}` : dHi.trim();
        }
        approvePayload.vehicleType = { en: vehDescEn, hi: vehDescHi };
        approvePayload.driverName = { en: driverNameEn, hi: driverNameHi };
        approvePayload.image = "";
      } else if (targetColl === "labour") {
        const isGenericLabourName = (s: string) => {
          const l = s.trim().toLowerCase();
          return l === "painter" || l === "rajmistri" || l === "plumber" || l === "electrician" ||
                 l === "carpenter" || l === "labour" || l === "mistri" || l === "welder" ||
                 l === "पेंटर" || l === "राजमिस्त्री" || l === "बढ़ई" || l === "प्लंबर" || l === "इलेक्ट्रीशियन" || l === "मजदूर";
        };
        const capitalize = (str: string) => str ? str.charAt(0).toUpperCase() + str.slice(1) : str;

        let workerNameEn = cNameEn.trim();
        if (!workerNameEn || isGenericLabourName(workerNameEn)) {
          workerNameEn = bNameEn.trim();
        } else if (bNameEn.trim() && !isGenericLabourName(bNameEn) && !workerNameEn.toLowerCase().includes(bNameEn.toLowerCase())) {
          workerNameEn = `${workerNameEn} (${bNameEn.trim()})`;
        }
        if (!workerNameEn) workerNameEn = "Worker";
        workerNameEn = capitalize(workerNameEn);

        let workerNameHi = cNameHi.trim();
        if (!workerNameHi || isGenericLabourName(workerNameHi)) {
          workerNameHi = bNameHi.trim();
        } else if (bNameHi.trim() && !isGenericLabourName(bNameHi) && !workerNameHi.includes(bNameHi)) {
          workerNameHi = `${workerNameHi} (${bNameHi.trim()})`;
        }
        if (!workerNameHi) workerNameHi = workerNameEn;

        approvePayload.name = { en: workerNameEn, hi: workerNameHi };
        approvePayload.location = { en: aEn, hi: aHi };
        approvePayload.skills = { en: dEn, hi: dHi };
        const chEn = item.chargesEn?.trim() || "Call to discuss";
        const chHi = item.chargesHi?.trim() || "कॉल पर बातचीत करें";
        approvePayload.charges = { en: chEn, hi: chHi };
        approvePayload.experience = { en: item.experienceEn?.trim() || "Experienced", hi: item.experienceHi?.trim() || "अनुभवी" };
        approvePayload.image = "";
      } else if (targetColl === "family_functions") {
        const isGenericFamilyName = (s: string) => {
          const l = s.trim().toLowerCase();
          return l === "tent" || l === "dj" || l === "catering" || l === "halwai" || l === "decoration" ||
                 l === "टेंट" || l === "डीजे" || l === "कैटरिंग" || l === "हलवाई";
        };
        const famNameEn = isGenericFamilyName(bNameEn) ? (cNameEn || bNameEn) : bNameEn;
        const famNameHi = isGenericFamilyName(bNameHi) ? (cNameHi || bNameHi) : bNameHi;

        approvePayload.name = { en: famNameEn, hi: famNameHi };
        approvePayload.services = { en: item.extraInfoEn?.trim() || dEn || "Tent & Event Services", hi: item.extraInfoHi?.trim() || dHi || "टेंट व इवेंट सेवाएं" };
        approvePayload.startingPrice = { en: item.startingPriceEn?.trim() || "Phone par baat karke tay hoga", hi: item.startingPriceHi?.trim() || "फोन पर बातचीत करके तय होगा" };
        approvePayload.address = { en: aEn, hi: aHi };
        approvePayload.image = "";
      } else if (targetColl === "construction") {
        approvePayload.shopName = { en: bNameEn, hi: bNameHi };
        approvePayload.address = { en: aEn, hi: aHi };
        approvePayload.image = "";
        const cPriceEn = item.chargesEn?.trim() || "Area ke hisab se (call par baat kar lein)";
        const cPriceHi = item.chargesHi?.trim() || "क्षेत्रफल के हिसाब से (कॉल पर बात कर लें)";
        approvePayload.products = [{
          name: { en: item.extraInfoEn?.trim() || dEn || "Construction Material", hi: item.extraInfoHi?.trim() || dHi || "निर्माण सामग्री" },
          price: cPriceEn,
          unit: { en: cPriceEn, hi: cPriceHi }
        }];
      } else if (targetColl === "health") {
        approvePayload.name = { en: bNameEn, hi: bNameHi };
        approvePayload.address = { en: aEn, hi: aHi };
        approvePayload.facilities = { en: dEn, hi: dHi };
        approvePayload.availability = { en: item.availabilityEn?.trim() || "09:00 AM - 08:00 PM", hi: item.availabilityHi?.trim() || "सुबह 09:00 - शाम 08:00" };
        approvePayload.specialisation = { en: item.extraInfoEn?.trim() || dEn || "Healthcare Clinic", hi: item.extraInfoHi?.trim() || dHi || "क्लिनिक व स्वास्थ्य सेवा" };
        approvePayload.services = { en: item.extraInfoEn?.trim() || dEn || "Healthcare Clinic", hi: item.extraInfoHi?.trim() || dHi || "क्लिनिक व स्वास्थ्य सेवा" };
        approvePayload.image = "";
      } else if (targetColl === "education" || targetColl === "legal_panchayat") {
        approvePayload.name = { en: bNameEn, hi: bNameHi };
        approvePayload.address = { en: aEn, hi: aHi };
        approvePayload.details = { en: dEn, hi: dHi };
        approvePayload.extraInfo = { en: item.extraInfoEn?.trim() || cNameEn, hi: item.extraInfoHi?.trim() || cNameHi };
        approvePayload.image = "";
      } else if (targetColl === "mandi") {
        approvePayload.buyerName = { en: cNameEn, hi: cNameHi };
        approvePayload.cropName = { en: bNameEn, hi: bNameHi };
        approvePayload.price = 2450;
        approvePayload.unit = { en: "Quintal", hi: "क्विंटल" };
        approvePayload.address = { en: aEn, hi: aHi };
      }

      // Save into the matched real village in Firestore without polluting villages collection
      const targetVillageId = await getExistingVillageId(item.villageId, vNameEn);
      const targetDocRef = doc(db, "villages", targetVillageId, targetColl, item.id);
      await setDoc(targetDocRef, { ...approvePayload, villageId: targetVillageId }, { merge: true });

      // Mark approved in business_registrations
      await updateDoc(doc(db, "business_registrations", item.id), {
        status: "approved",
        approvedAt: serverTimestamp(),
        publishedCollection: targetColl,
        publishedDocPath: `villages/${targetVillageId}/${targetColl}/${item.id}`
      });

      showToast("सर्विस सफलतापूर्वक अप्रूव हो गई!", "मोबाइल ऐप में लाइव प्रकाशित कर दी गई है।", "success");

    } catch (err) {
      console.error("Failed to approve submission:", err);
      showToast("अप्रूव करने में त्रुटि आई!", "कृपया कंसोल चेक करें।", "error");
    } finally {
      setActionLoading(null);
    }
  };

  // Reject Action - Marks as rejected in admin and hides from mobile app
  const handleReject = async (item: SubmissionItem) => {
    try {
      setActionLoading(item.id);

      // 1. Remove from live mobile app paths so it immediately hides from the app
      const vNameEn = item.villageNameEn || item.villageName || item.address || "General";
      const targetVillageId = await getExistingVillageId(item.villageId, vNameEn);
      const { collection: targetColl } = getTargetCollection(item.category);

      try {
        await deleteDoc(doc(db, "villages", targetVillageId, targetColl, item.id));
      } catch (err) {
        console.warn("Could not delete from target village collection:", err);
      }

      if (item.publishedDocPath) {
        try {
          await deleteDoc(doc(db, item.publishedDocPath));
        } catch (err) {
          console.warn("Could not delete from publishedDocPath:", err);
        }
      }

      // 2. Mark as rejected in business_registrations (keeps admin record, hides from app)
      await updateDoc(doc(db, "business_registrations", item.id), {
        status: "rejected",
        rejectedAt: serverTimestamp(),
        publishedDocPath: null
      });

      showToast("सर्विस रिजेक्ट कर दी गई!", "यह ऐप से तुरंत हट गई है और एडमिन रिजेक्टेड लिस्ट में सुरक्षित है।", "error");
    } catch (err) {
      console.error("Failed to reject submission:", err);
      showToast("रिजेक्ट करने में त्रुटि आई!", undefined, "error");
    } finally {
      setActionLoading(null);
    }
  };

  // Delete Action - Permanently deletes from BOTH mobile app AND business_registrations database
  const handleDelete = async (item: SubmissionItem) => {
    if (!confirm("क्या आप वाकई इस सर्विस को हमेशा के लिए डिलीट करना चाहते हैं? यह डेटाबेस और ऐप दोनों से हमेशा के लिए हट जाएगी।")) return;
    try {
      setActionLoading(item.id);

      // 1. Permanently remove from live mobile app paths
      const vNameEn = item.villageNameEn || item.villageName || item.address || "General";
      const targetVillageId = await getExistingVillageId(item.villageId, vNameEn);
      const { collection: targetColl } = getTargetCollection(item.category);

      try {
        await deleteDoc(doc(db, "villages", targetVillageId, targetColl, item.id));
      } catch (err) {
        console.warn("Could not delete from target village collection:", err);
      }

      if (item.publishedDocPath) {
        try {
          await deleteDoc(doc(db, item.publishedDocPath));
        } catch (err) {
          console.warn("Could not delete from publishedDocPath:", err);
        }
      }

      // 2. Permanently delete from business_registrations database
      await deleteDoc(doc(db, "business_registrations", item.id));

      showToast("सर्विस पूरी तरह डिलीट कर दी गई!", "यह डेटाबेस और मोबाइल ऐप दोनों से हमेशा के लिए हटा दी गई है।", "info");
    } catch (err) {
      console.error("Failed to delete submission:", err);
      showToast("डिलीट करने में त्रुटि आई!", undefined, "error");
    } finally {
      setActionLoading(null);
    }
  };

  // Save Edits - Directly updates BOTH business_registrations AND live mobile app Firestore paths!
  const handleSaveEdit = async () => {
    if (!editingItem) return;
    try {
      setActionLoading(editingItem.id);

      const bNameEn = editingItem.nameEn || editingItem.businessName || "";
      const bNameHi = editingItem.nameHi || editingItem.businessName || "";
      const cNameEn = editingItem.contactNameEn || editingItem.contactName || bNameEn;
      const cNameHi = editingItem.contactNameHi || editingItem.contactName || bNameHi;
      const dEn = editingItem.detailsEn || editingItem.description || "";
      const dHi = editingItem.detailsHi || editingItem.description || "";
      const aEn = editingItem.addressEn || editingItem.address || "";
      const aHi = editingItem.addressHi || editingItem.address || "";
      const vNameEn = editingItem.villageNameEn || editingItem.villageName || editingItem.address || "General";
      const vNameHi = editingItem.villageNameHi || editingItem.villageName || editingItem.address || "General";

      // 1. Update business_registrations
      const subRef = doc(db, "business_registrations", editingItem.id);
      await updateDoc(subRef, {
        businessName: bNameEn,
        contactName: cNameEn,
        category: editingItem.category || "business",
        phoneNumber: editingItem.phoneNumber || "",
        address: aEn,
        description: dEn,
        nameEn: bNameEn,
        nameHi: bNameHi,
        detailsEn: dEn,
        detailsHi: dHi,
        contactNameEn: cNameEn,
        contactNameHi: cNameHi,
        villageNameEn: vNameEn,
        villageNameHi: vNameHi,
        addressEn: aEn,
        addressHi: aHi,
        villageName: vNameEn,
        chargesEn: editingItem.chargesEn || "",
        chargesHi: editingItem.chargesHi || "",
        experienceEn: editingItem.experienceEn || "",
        experienceHi: editingItem.experienceHi || "",
        startingPriceEn: editingItem.startingPriceEn || "",
        startingPriceHi: editingItem.startingPriceHi || "",
        extraInfoEn: editingItem.extraInfoEn || "",
        extraInfoHi: editingItem.extraInfoHi || "",
        vehicleTypeEn: editingItem.vehicleTypeEn || "",
        vehicleTypeHi: editingItem.vehicleTypeHi || "",
        availabilityEn: editingItem.availabilityEn || "",
        availabilityHi: editingItem.availabilityHi || "",
        updatedAt: serverTimestamp()
      });

      // 2. If approved, ALSO update the live document in villages/{villageId}/{targetColl}/{id}!
      const { collection: targetColl, categoryId: catId } = getTargetCollection(editingItem.category);

      const livePayload: Record<string, any> = {
        id: editingItem.id,
        categoryId: catId,
        contact: editingItem.phoneNumber || "",
        status: "active",
        updatedAt: serverTimestamp()
      };

      if (targetColl === "transport") {
        const driverNameEn = cNameEn.trim();
        const driverNameHi = cNameHi.trim();
        const vehicleNameEn = bNameEn.trim();
        const vehicleNameHi = bNameHi.trim();

        // 1. Title next to Person icon: Driver/Owner Name only (NO car type)
        const fullTitleEn = driverNameEn || vehicleNameEn;
        const fullTitleHi = driverNameHi || vehicleNameHi;

        livePayload.name = { en: fullTitleEn, hi: fullTitleHi };

        // 2. Location with Landmark & Village: "Maurya kirna store, Mahuawa Shukla"
        let locEn = aEn.trim();
        if (vNameEn && !locEn.toLowerCase().includes(vNameEn.toLowerCase())) {
          locEn = locEn ? `${locEn}, ${vNameEn}` : vNameEn;
        }
        let locHi = aHi.trim();
        if (vNameHi && !locHi.includes(vNameHi)) {
          locHi = locHi ? `${locHi}, ${vNameHi}` : vNameHi;
        }
        livePayload.location = { en: locEn, hi: locHi };

        // 3. Vehicle section: Car name + Details concatenated
        let vehDescEn = vehicleNameEn;
        if (dEn.trim()) {
          vehDescEn = vehicleNameEn ? `${vehicleNameEn} - ${dEn.trim()}` : dEn.trim();
        }
        let vehDescHi = vehicleNameHi;
        if (dHi.trim()) {
          vehDescHi = vehicleNameHi ? `${vehicleNameHi} - ${dHi.trim()}` : dHi.trim();
        }
        livePayload.vehicleType = { en: vehDescEn, hi: vehDescHi };
        livePayload.driverName = { en: driverNameEn, hi: driverNameHi };
        livePayload.image = "";
      } else if (targetColl === "labour") {
        const isGenericLabourName = (s: string) => {
          const l = s.trim().toLowerCase();
          return l === "painter" || l === "rajmistri" || l === "plumber" || l === "electrician" ||
                 l === "carpenter" || l === "labour" || l === "mistri" || l === "welder" ||
                 l === "पेंटर" || l === "राजमिस्त्री" || l === "बढ़ई" || l === "प्लंबर" || l === "इलेक्ट्रीशियन" || l === "मजदूर";
        };
        const capitalize = (str: string) => str ? str.charAt(0).toUpperCase() + str.slice(1) : str;

        let workerNameEn = cNameEn.trim();
        if (!workerNameEn || isGenericLabourName(workerNameEn)) {
          workerNameEn = bNameEn.trim();
        } else if (bNameEn.trim() && !isGenericLabourName(bNameEn) && !workerNameEn.toLowerCase().includes(bNameEn.toLowerCase())) {
          workerNameEn = `${workerNameEn} (${bNameEn.trim()})`;
        }
        if (!workerNameEn) workerNameEn = "Worker";
        workerNameEn = capitalize(workerNameEn);

        let workerNameHi = cNameHi.trim();
        if (!workerNameHi || isGenericLabourName(workerNameHi)) {
          workerNameHi = bNameHi.trim();
        } else if (bNameHi.trim() && !isGenericLabourName(bNameHi) && !workerNameHi.includes(bNameHi)) {
          workerNameHi = `${workerNameHi} (${bNameHi.trim()})`;
        }
        if (!workerNameHi) workerNameHi = workerNameEn;

        livePayload.name = { en: workerNameEn, hi: workerNameHi };
        livePayload.location = { en: aEn, hi: aHi };
        livePayload.skills = { en: dEn, hi: dHi };
        const chEn = editingItem.chargesEn?.trim() || "Call to discuss";
        const chHi = editingItem.chargesHi?.trim() || "कॉल पर बातचीत करें";
        livePayload.charges = { en: chEn, hi: chHi };
        livePayload.experience = { en: editingItem.experienceEn?.trim() || "Experienced", hi: editingItem.experienceHi?.trim() || "अनुभवी" };
        livePayload.image = "";
      } else if (targetColl === "family_functions") {
        const isGenericFamilyName = (s: string) => {
          const l = s.trim().toLowerCase();
          return l === "tent" || l === "dj" || l === "catering" || l === "halwai" || l === "decoration" ||
                 l === "टेंट" || l === "डीजे" || l === "कैटरिंग" || l === "हलवाई";
        };
        const famNameEn = isGenericFamilyName(bNameEn) ? (cNameEn || bNameEn) : bNameEn;
        const famNameHi = isGenericFamilyName(bNameHi) ? (cNameHi || bNameHi) : bNameHi;

        livePayload.name = { en: famNameEn, hi: famNameHi };
        livePayload.services = { en: editingItem.extraInfoEn?.trim() || dEn || "Tent & Event Services", hi: editingItem.extraInfoHi?.trim() || dHi || "टेंट व इवेंट सेवाएं" };
        livePayload.startingPrice = { en: editingItem.startingPriceEn?.trim() || "Phone par baat karke tay hoga", hi: editingItem.startingPriceHi?.trim() || "फोन पर बातचीत करके तय होगा" };
        livePayload.address = { en: aEn, hi: aHi };
        livePayload.image = "";
      } else if (targetColl === "construction") {
        livePayload.shopName = { en: bNameEn, hi: bNameHi };
        livePayload.address = { en: aEn, hi: aHi };
        livePayload.image = "";
        const cPriceEn = editingItem.chargesEn?.trim() || "Area ke hisab se (call par baat kar lein)";
        const cPriceHi = editingItem.chargesHi?.trim() || "क्षेत्रफल के हिसाब से (कॉल पर बात कर लें)";
        livePayload.products = [{
          name: { en: editingItem.extraInfoEn?.trim() || dEn || "Construction Material", hi: editingItem.extraInfoHi?.trim() || dHi || "निर्माण सामग्री" },
          price: cPriceEn,
          unit: { en: cPriceEn, hi: cPriceHi }
        }];
      } else if (targetColl === "health") {
        livePayload.name = { en: bNameEn, hi: bNameHi };
        livePayload.address = { en: aEn, hi: aHi };
        livePayload.facilities = { en: dEn, hi: dHi };
        livePayload.availability = { en: editingItem.availabilityEn?.trim() || "09:00 AM - 08:00 PM", hi: editingItem.availabilityHi?.trim() || "सुबह 09:00 - शाम 08:00" };
        livePayload.specialisation = { en: editingItem.extraInfoEn?.trim() || dEn || "Healthcare Clinic", hi: editingItem.extraInfoHi?.trim() || dHi || "क्लिनिक व स्वास्थ्य सेवा" };
        livePayload.services = { en: editingItem.extraInfoEn?.trim() || dEn || "Healthcare Clinic", hi: editingItem.extraInfoHi?.trim() || dHi || "क्लिनिक व स्वास्थ्य सेवा" };
        livePayload.image = "";
      } else if (targetColl === "education" || targetColl === "legal_panchayat") {
        livePayload.name = { en: bNameEn, hi: bNameHi };
        livePayload.address = { en: aEn, hi: aHi };
        livePayload.details = { en: dEn, hi: dHi };
        livePayload.extraInfo = { en: editingItem.extraInfoEn?.trim() || cNameEn, hi: editingItem.extraInfoHi?.trim() || cNameHi };
        livePayload.image = "";
      } else if (targetColl === "mandi") {
        livePayload.buyerName = { en: cNameEn, hi: cNameHi };
        livePayload.cropName = { en: bNameEn, hi: bNameHi };
        livePayload.price = 2450;
        livePayload.unit = { en: "Quintal", hi: "क्विंटल" };
        livePayload.address = { en: aEn, hi: aHi };
      }

      // If approved, update live mobile app path for the real village
      if (editingItem.status === "approved") {
        const targetVillageId = await getExistingVillageId(editingItem.villageId, vNameEn);
        const liveDocRef = doc(db, "villages", targetVillageId, targetColl, editingItem.id);
        await setDoc(liveDocRef, { ...livePayload, villageId: targetVillageId }, { merge: true });
      }

      setEditingItem(null);
      showToast(
        "बदलाव सफलतापूर्वक सेव हो गए!",
        editingItem.status === "approved"
          ? "मोबाइल ऐप में लाइव डेटा तुरंत अपडेट कर दिया गया है।"
          : "रिकॉर्ड सफलतापूर्वक अपडेट हो गया है।",
        "success"
      );
    } catch (err) {
      console.error("Failed to save edits:", err);
      showToast("सेव करने में त्रुटि आई!", "कृपया दोबारा प्रयास करें।", "error");
    } finally {
      setActionLoading(null);
    }
  };

  // Add Service Directly by Admin (Category-Aware Firestore DTO Mapping)
  const handleAddServiceSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newServiceNameEn.trim() && !newServiceNameHi.trim()) {
      alert("Please enter a name (English or Hindi).");
      return;
    }
    if (!newPhone.trim()) {
      alert("Please enter a contact phone number.");
      return;
    }

    try {
      setActionLoading("adding_service");
      const serviceId = `srv_${Date.now()}`;
      const vNameEn = newVillageNameEn.trim() || (selectedVillage !== "all" ? selectedVillage : "General");
      const vNameHi = newVillageNameHi.trim() || newVillageNameEn.trim() || (selectedVillage !== "all" ? selectedVillage : "सामान्य");
      const vId = await getExistingVillageId(undefined, vNameEn);

      const bNameEn = newServiceNameEn.trim() || newServiceNameHi.trim();
      const bNameHi = newServiceNameHi.trim() || newServiceNameEn.trim();
      const cNameEn = newContactNameEn.trim() || newContactNameHi.trim() || bNameEn;
      const cNameHi = newContactNameHi.trim() || newContactNameEn.trim() || bNameHi;
      const dEn = newDetailsEn.trim() || newDetailsHi.trim();
      const dHi = newDetailsHi.trim() || newDetailsEn.trim();
      const aEn = newAddressEn.trim() || newAddressHi.trim() || vNameEn;
      const aHi = newAddressHi.trim() || newAddressEn.trim() || vNameHi;

      const catDef = currentCatDef;
      const targetColl = catDef.collection;
      const catId = catDef.id;

      // 2. Build DTO payload based on database schema
      const targetPayload: Record<string, any> = {
        id: serviceId,
        categoryId: catId,
        contact: newPhone.trim(),
        villageId: vId,
        approvedAt: serverTimestamp(),
        createdAt: serverTimestamp(),
        status: "active"
      };

      if (catDef.group === "labour") {
        const isGenericLabourName = (s: string) => {
          const l = s.trim().toLowerCase();
          return l === "painter" || l === "rajmistri" || l === "plumber" || l === "electrician" ||
                 l === "carpenter" || l === "labour" || l === "mistri" || l === "welder" ||
                 l === "पेंटर" || l === "राजमिस्त्री" || l === "बढ़ई" || l === "प्लंबर" || l === "इलेक्ट्रीशियन" || l === "मजदूर";
        };
        const capitalize = (str: string) => str ? str.charAt(0).toUpperCase() + str.slice(1) : str;

        let workerNameEn = cNameEn.trim();
        if (!workerNameEn || isGenericLabourName(workerNameEn)) {
          workerNameEn = bNameEn.trim();
        } else if (bNameEn.trim() && !isGenericLabourName(bNameEn) && !workerNameEn.toLowerCase().includes(bNameEn.toLowerCase())) {
          workerNameEn = `${workerNameEn} (${bNameEn.trim()})`;
        }
        if (!workerNameEn) workerNameEn = "Worker";
        workerNameEn = capitalize(workerNameEn);

        let workerNameHi = cNameHi.trim();
        if (!workerNameHi || isGenericLabourName(workerNameHi)) {
          workerNameHi = bNameHi.trim();
        } else if (bNameHi.trim() && !isGenericLabourName(bNameHi) && !workerNameHi.includes(bNameHi)) {
          workerNameHi = `${workerNameHi} (${bNameHi.trim()})`;
        }
        if (!workerNameHi) workerNameHi = workerNameEn;

        targetPayload.name = { en: workerNameEn, hi: workerNameHi };
        targetPayload.location = { en: aEn, hi: aHi };
        targetPayload.skills = { en: newExtra1En.trim() || dEn || "Wall painting, putty, enamel", hi: newExtra1Hi.trim() || dHi || "दीवार पुट्टी, पेंट, पॉलिश" };
        const chEn = newExtra2En.trim() || "Call to discuss";
        const chHi = newExtra2HiState.trim() || "कॉल पर बातचीत करें";
        targetPayload.charges = { en: chEn, hi: chHi };
        targetPayload.experience = { en: newExtra3En.trim() || "5 Years", hi: newExtra3Hi.trim() || "5 साल" };
        targetPayload.image = "";
      } else if (catDef.group === "transport") {
        const driverNameEn = cNameEn.trim();
        const driverNameHi = cNameHi.trim();
        const vehicleNameEn = bNameEn.trim();
        const vehicleNameHi = bNameHi.trim();

        // 1. Title next to Person icon: Driver/Owner Name only (NO car type)
        const fullTitleEn = driverNameEn || vehicleNameEn;
        const fullTitleHi = driverNameHi || vehicleNameHi;

        targetPayload.name = { en: fullTitleEn, hi: fullTitleHi };

        let locEn = aEn.trim();
        if (vNameEn && !locEn.toLowerCase().includes(vNameEn.toLowerCase())) {
          locEn = locEn ? `${locEn}, ${vNameEn}` : vNameEn;
        }
        let locHi = aHi.trim();
        if (vNameHi && !locHi.includes(vNameHi)) {
          locHi = locHi ? `${locHi}, ${vNameHi}` : vNameHi;
        }
        targetPayload.location = { en: locEn, hi: locHi };

        let vehDescEn = vehicleNameEn;
        const extraDescEn = newExtra1En.trim() || dEn.trim();
        if (extraDescEn) {
          vehDescEn = vehicleNameEn ? `${vehicleNameEn} - ${extraDescEn}` : extraDescEn;
        }
        let vehDescHi = vehicleNameHi;
        const extraDescHi = newExtra1Hi.trim() || dHi.trim();
        if (extraDescHi) {
          vehDescHi = vehicleNameHi ? `${vehicleNameHi} - ${extraDescHi}` : extraDescHi;
        }
        targetPayload.vehicleType = { en: vehDescEn, hi: vehDescHi };
        targetPayload.driverName = { en: driverNameEn, hi: driverNameHi };
        targetPayload.charges = { en: newExtra2En.trim(), hi: newExtra2HiState.trim() };
        targetPayload.image = "";
      } else if (catDef.group === "family") {
        targetPayload.name = { en: bNameEn, hi: bNameHi };
        targetPayload.services = { en: newExtra1En.trim() || dEn, hi: newExtra1Hi.trim() || dHi };
        targetPayload.startingPrice = { en: newExtra2En.trim() || "₹10,000", hi: newExtra2HiState.trim() || "₹10,000 से" };
        targetPayload.address = { en: aEn, hi: aHi };
        targetPayload.image = "";
      } else if (catDef.group === "construction") {
        targetPayload.shopName = { en: bNameEn, hi: bNameHi };
        targetPayload.address = { en: aEn, hi: aHi };
        targetPayload.image = "";
        targetPayload.products = newExtra1En ? [{ name: { en: newExtra1En, hi: newExtra1Hi }, price: newExtra2En || "", unit: { en: "unit", hi: "इकाई" } }] : [];
      } else if (catDef.group === "health") {
        targetPayload.name = { en: bNameEn, hi: bNameHi };
        targetPayload.address = { en: aEn, hi: aHi };
        targetPayload.specialisation = { en: newExtra1En.trim(), hi: newExtra1Hi.trim() };
        targetPayload.availability = { en: newExtra2En.trim() || "9:00 AM - 8:00 PM", hi: newExtra2HiState.trim() || "सुबह 9 से रात 8 बजे" };
        targetPayload.facilities = { en: newExtra3En.trim() || dEn, hi: newExtra3Hi.trim() || dHi };
        targetPayload.image = "";
      } else if (catDef.group === "education" || catDef.group === "legal") {
        targetPayload.name = { en: bNameEn, hi: bNameHi };
        targetPayload.address = { en: aEn, hi: aHi };
        targetPayload.details = { en: newExtra1En.trim() || dEn, hi: newExtra1Hi.trim() || dHi };
        targetPayload.extraInfo = { en: newExtra2En.trim() || cNameEn, hi: newExtra2HiState.trim() || cNameHi };
        targetPayload.image = "";
      } else if (catDef.group === "mandi") {
        targetPayload.buyerName = { en: bNameEn, hi: bNameHi };
        targetPayload.cropName = { en: newExtra1En.trim() || "Wheat", hi: newExtra1Hi.trim() || "गेहूं" };
        targetPayload.price = parseFloat(newExtra2En) || 2400;
        targetPayload.unit = { en: newExtra3En.trim() || "Quintal", hi: newExtra3Hi.trim() || "क्विंटल" };
        targetPayload.address = { en: aEn, hi: aHi };
        targetPayload.date = Date.now();
      }

      // 3. If publishing live, save directly into villages/{vId}/{targetColl}/{serviceId}
      if (newPublishLive) {
        const targetDocRef = doc(db, "villages", vId, targetColl, serviceId);
        await setDoc(targetDocRef, targetPayload, { merge: true });
      }

      // 4. Also register in business_registrations for admin record
      await setDoc(doc(db, "business_registrations", serviceId), {
        businessName: bNameEn,
        contactName: cNameEn,
        category: catDef.displayName,
        categoryId: catDef.id,
        phoneNumber: newPhone.trim(),
        address: aEn,
        description: dEn || newExtra1En,
        nameEn: bNameEn,
        nameHi: bNameHi,
        detailsEn: dEn,
        detailsHi: dHi,
        contactNameEn: cNameEn,
        contactNameHi: cNameHi,
        villageNameEn: vNameEn,
        villageNameHi: vNameHi,
        addressEn: aEn,
        addressHi: aHi,
        villageId: vId,
        villageName: vNameEn,
        status: newPublishLive ? "approved" : "pending",
        createdAt: serverTimestamp(),
        approvedAt: newPublishLive ? serverTimestamp() : null,
        publishedCollection: newPublishLive ? targetColl : null,
        publishedDocPath: newPublishLive ? `villages/${vId}/${targetColl}/${serviceId}` : null
      });

      // Reset Form
      setNewServiceNameEn("");
      setNewServiceNameHi("");
      setNewContactNameEn("");
      setNewContactNameHi("");
      setNewPhone("");
      setNewAddressEn("");
      setNewAddressHi("");
      setNewDetailsEn("");
      setNewDetailsHi("");
      setNewExtra1En("");
      setNewExtra1Hi("");
      setNewExtra2Hi("");
      setNewExtra2HiState("");
      setNewExtra3En("");
      setNewExtra3Hi("");
      setNewVillageNameEn("");
      setNewVillageNameHi("");
      setIsAddServiceModalOpen(false);
      showToast(
        "नई सर्विस सफलतापूर्वक जुड़ गई!",
        newPublishLive ? "मोबाइल ऐप में लाइव प्रकाशित कर दी गई है।" : "पेंडिंग लिस्ट में जोड़ दी गई है।",
        "success"
      );
    } catch (err) {
      console.error("Error adding service:", err);
      showToast("सर्विस जोड़ने में त्रुटि आई!", "कृपया दोबारा प्रयास करें।", "error");
    } finally {
      setActionLoading(null);
    }
  };

  return (
    <div className="space-y-6 relative">
      {/* ==================== TOAST NOTIFICATION POPUP ==================== */}
      {toast && (
        <div className="fixed top-6 right-6 z-[9999] flex items-center gap-3 px-4 py-3.5 bg-white border border-slate-200/90 rounded-2xl shadow-[0_10px_30px_-5px_rgba(0,0,0,0.12)] animate-in fade-in slide-in-from-top-4 duration-200">
          <div
            className={`w-9 h-9 rounded-xl flex items-center justify-center shrink-0 ${
              toast.type === "error"
                ? "bg-rose-50 text-rose-600 border border-rose-100"
                : toast.type === "info"
                ? "bg-indigo-50 text-indigo-600 border border-indigo-100"
                : "bg-emerald-50 text-emerald-600 border border-emerald-100"
            }`}
          >
            {toast.type === "error" ? (
              <XCircle className="w-5 h-5" />
            ) : toast.type === "info" ? (
              <Clock className="w-5 h-5" />
            ) : (
              <CheckCircle2 className="w-5 h-5" />
            )}
          </div>
          <div className="pr-2">
            <h4 className="text-xs font-bold text-slate-900 leading-snug">{toast.message}</h4>
            {toast.subMessage && (
              <p className="text-[11px] text-slate-500 font-medium mt-0.5 leading-snug">{toast.subMessage}</p>
            )}
          </div>
          <button
            onClick={() => setToast(null)}
            className="p-1 text-slate-400 hover:text-slate-600 rounded-lg hover:bg-slate-100 transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* ==================== CLEAN NEXTADMIN FILTER TOOLBAR ==================== */}
      <div className="bg-white p-5 rounded-2xl border border-slate-100 shadow-[0_4px_20px_-2px_rgba(0,0,0,0.03)] space-y-4">
        {/* Status Tabs */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-center gap-2 overflow-x-auto pb-1 sm:pb-0">
            {[
              { id: "all", label: "All Status", count: stats.total },
              { id: "pending", label: "Pending", count: stats.pending },
              { id: "approved", label: "Approved", count: stats.approved },
              { id: "rejected", label: "Rejected", count: stats.rejected }
            ].map((tab) => (
              <button
                key={tab.id}
                onClick={() => setFilterStatus(tab.id)}
                className={`px-4 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition-all flex items-center gap-2 ${
                  filterStatus === tab.id
                    ? "bg-indigo-600 text-white shadow-md shadow-indigo-600/20"
                    : "bg-slate-100 text-slate-600 hover:bg-slate-200/70"
                }`}
              >
                <span>{tab.label}</span>
                <span
                  className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                    filterStatus === tab.id
                      ? "bg-white text-indigo-600"
                      : "bg-white text-slate-600 border border-slate-200"
                  }`}
                >
                  {tab.count}
                </span>
              </button>
            ))}
          </div>

          {/* View Mode Toggle & Add Service */}
          <div className="flex items-center gap-2 self-start sm:self-auto">
            <div className="flex items-center gap-1 bg-slate-100 p-1 rounded-xl">
              <button
                onClick={() => setViewMode("village_grouped")}
                className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 ${
                  viewMode === "village_grouped"
                    ? "bg-white text-slate-900 shadow-sm"
                    : "text-slate-500 hover:text-slate-900"
                }`}
              >
                <Building2 className="w-3.5 h-3.5" />
                <span>Village Grouped</span>
              </button>

              <button
                onClick={() => setViewMode("grid")}
                className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 ${
                  viewMode === "grid"
                    ? "bg-white text-slate-900 shadow-sm"
                    : "text-slate-500 hover:text-slate-900"
                }`}
              >
                <LayoutGrid className="w-3.5 h-3.5" />
                <span>Flat Grid</span>
              </button>
            </div>

            {/* Admin Add Service Button */}
            <button
              onClick={() => setIsAddServiceModalOpen(true)}
              className="px-3.5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl text-xs transition-all flex items-center gap-1.5 shadow-md shadow-indigo-600/20 shrink-0 active:scale-95"
            >
              <Plus className="w-4 h-4" />
              <span>Add Service</span>
            </button>
          </div>
        </div>

        {/* Search & Village Filter */}
        <div className="flex flex-col sm:flex-row items-center gap-3 pt-3 border-t border-slate-100">
          <div className="relative w-full sm:w-64 shrink-0">
            <MapPin className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-indigo-600 pointer-events-none" />
            <select
              value={selectedVillage}
              onChange={(e) => setSelectedVillage(e.target.value)}
              className="w-full pl-10 pr-8 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 focus:outline-none focus:border-indigo-500 focus:bg-white cursor-pointer appearance-none transition-all font-medium"
            >
              <option value="all">📍 All Villages ({submissions.length})</option>
              {villageList.map(([vName, count]) => (
                <option key={vName} value={vName}>
                  🏡 {vName} ({count})
                </option>
              ))}
            </select>
            <ChevronRight className="w-4 h-4 absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none rotate-90" />
          </div>

          <div className="relative w-full flex-1">
            <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search by business name, owner name, phone, category, address..."
              className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 placeholder-slate-400 focus:outline-none focus:border-indigo-500 focus:bg-white transition-all font-medium"
            />
          </div>
        </div>
      </div>

      {/* ==================== SUBMISSIONS CARDS / GRID ==================== */}
      {loading ? (
        <div className="py-20 text-center text-slate-400 flex flex-col items-center justify-center gap-3">
          <div className="w-8 h-8 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
          <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">Loading submissions...</p>
        </div>
      ) : filteredSubmissions.length === 0 ? (
        <div className="p-16 text-center bg-white rounded-2xl border border-slate-100 shadow-sm">
          <Clock className="w-12 h-12 text-slate-300 mx-auto mb-3" />
          <h3 className="text-base font-bold text-slate-800">No submissions found</h3>
          <p className="text-xs text-slate-400 mt-1">Try selecting a different filter or search query.</p>
        </div>
      ) : viewMode === "village_grouped" ? (
        <div className="space-y-6">
          {villageGroupedData.map(([villageName, items]) => (
            <div key={villageName} className="bg-white border border-slate-100 rounded-2xl p-6 shadow-sm">
              {/* Village Header */}
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 pb-4 mb-5">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center font-bold">
                    <Building2 className="w-5 h-5" />
                  </div>
                  <div>
                    <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
                      <span>{villageName}</span>
                    </h2>
                    <p className="text-xs text-slate-400 font-medium">
                      {items.length} registration{items.length > 1 ? "s" : ""} in this village
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2 text-xs">
                  <span className="px-3 py-1 bg-amber-50 text-amber-700 border border-amber-200 rounded-full font-bold">
                    {items.filter(i => i.status === "pending").length} Pending
                  </span>
                  <span className="px-3 py-1 bg-emerald-50 text-emerald-700 border border-emerald-200 rounded-full font-bold">
                    {items.filter(i => i.status === "approved").length} Approved
                  </span>
                </div>
              </div>

              {/* Grid of cards */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
                {items.map((item) => (
                  <SubmissionCard
                    key={item.id}
                    item={item}
                    actionLoading={actionLoading}
                    onApprove={handleApprove}
                    onReject={handleReject}
                    onDelete={handleDelete}
                    onEdit={openEditModal}
                  />
                ))}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredSubmissions.map((item) => (
            <SubmissionCard
              key={item.id}
              item={item}
              actionLoading={actionLoading}
              onApprove={handleApprove}
              onReject={handleReject}
              onDelete={handleDelete}
              onEdit={openEditModal}
            />
          ))}
        </div>
      )}

      {/* ==================== BILINGUAL EDIT MODAL ==================== */}
      {editingItem && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-sm flex items-center justify-center p-4 overflow-y-auto">
          <div className="bg-white border border-slate-100 rounded-3xl p-6 sm:p-8 max-w-2xl w-full my-8 shadow-2xl space-y-5">
            <div className="flex items-center justify-between border-b border-slate-100 pb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center font-bold">
                  <Globe2 className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-base font-bold text-slate-900">Edit Service (Bilingual Hindi & English)</h3>
                  <p className="text-xs text-slate-400">Update information in both Hindi & English before publishing</p>
                </div>
              </div>
              <button
                onClick={() => setEditingItem(null)}
                className="p-2 text-slate-400 hover:text-slate-700 rounded-xl hover:bg-slate-100 transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-4 max-h-[65vh] overflow-y-auto pr-2">
              {/* Category */}
              <div>
                <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                  Category
                </label>
                <input
                  type="text"
                  value={editingItem.category ?? ""}
                  onChange={(e) => setEditingItem({ ...editingItem, category: e.target.value })}
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                />
              </div>

              {/* 1. Service Name English & Hindi */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-indigo-600 uppercase tracking-wider mb-1">
                    Service Name (English)
                  </label>
                  <input
                    type="text"
                    value={editingItem.nameEn ?? ""}
                    onChange={(e) => setEditingItem({ ...editingItem, nameEn: e.target.value })}
                    placeholder="e.g. Ertiga Car"
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-indigo-600 uppercase tracking-wider mb-1">
                    सेवा का नाम (Hindi)
                  </label>
                  <input
                    type="text"
                    value={editingItem.nameHi ?? ""}
                    onChange={(e) => setEditingItem({ ...editingItem, nameHi: e.target.value })}
                    placeholder="उदा. अर्टिगा कार"
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
              </div>

              {/* 2. Owner Name English & Hindi */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    Owner Name (English)
                  </label>
                  <input
                    type="text"
                    value={editingItem.contactNameEn ?? ""}
                    onChange={(e) => setEditingItem({ ...editingItem, contactNameEn: e.target.value })}
                    placeholder="e.g. Naimullah Ansari"
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    मालिक का नाम (Hindi)
                  </label>
                  <input
                    type="text"
                    value={editingItem.contactNameHi ?? ""}
                    onChange={(e) => setEditingItem({ ...editingItem, contactNameHi: e.target.value })}
                    placeholder="उदा. नईमुल्लाह अंसारी"
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
              </div>

              {/* Phone Number */}
              <div>
                <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                  Phone Number
                </label>
                <input
                  type="text"
                  value={editingItem.phoneNumber ?? ""}
                  onChange={(e) => setEditingItem({ ...editingItem, phoneNumber: e.target.value })}
                  placeholder="e.g. 9956922370"
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium font-mono focus:bg-white focus:border-indigo-500 focus:outline-none"
                />
              </div>

              {/* 3. Details / Description English & Hindi */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-indigo-600 uppercase tracking-wider mb-1">
                    Details / Description (English)
                  </label>
                  <textarea
                    rows={3}
                    value={editingItem.detailsEn ?? ""}
                    onChange={(e) => setEditingItem({ ...editingItem, detailsEn: e.target.value })}
                    placeholder="Describe services in English..."
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-indigo-600 uppercase tracking-wider mb-1">
                    विवरण / सुविधाएं (Hindi)
                  </label>
                  <textarea
                    rows={3}
                    value={editingItem.detailsHi ?? ""}
                    onChange={(e) => setEditingItem({ ...editingItem, detailsHi: e.target.value })}
                    placeholder="सेवाओं का विवरण हिंदी में लिखें..."
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
              </div>

              {/* 4. Address English & Hindi */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    Address / Landmark (English)
                  </label>
                  <input
                    type="text"
                    value={editingItem.addressEn ?? ""}
                    onChange={(e) => setEditingItem({ ...editingItem, addressEn: e.target.value })}
                    placeholder="e.g. Near Main Market"
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    पता / लैंडमार्क (Hindi)
                  </label>
                  <input
                    type="text"
                    value={editingItem.addressHi ?? ""}
                    onChange={(e) => setEditingItem({ ...editingItem, addressHi: e.target.value })}
                    placeholder="उदा. मुख्य बाज़ार के पास"
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
              </div>

              {/* 5. Village Name English & Hindi */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    Village Name (English)
                  </label>
                  <input
                    type="text"
                    value={editingItem.villageNameEn ?? ""}
                    onChange={(e) => setEditingItem({ ...editingItem, villageNameEn: e.target.value })}
                    placeholder="e.g. Mahuawa"
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    गांव का नाम (Hindi)
                  </label>
                  <input
                    type="text"
                    value={editingItem.villageNameHi ?? ""}
                    onChange={(e) => setEditingItem({ ...editingItem, villageNameHi: e.target.value })}
                    placeholder="उदा. महुआवा"
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
              </div>

              {/* 6. Dynamic Category Specific Fields (Charges, Experience, Starting Price, Timings, etc.) */}
              {(() => {
                const catDef = CATEGORY_DEFINITIONS.find(
                  (c) => c.displayName.toLowerCase().includes((editingItem.category || "").toLowerCase()) || c.id.toLowerCase() === (editingItem.category || "").toLowerCase()
                );
                const group = catDef?.group || "labour";

                return (
                  <>
                    {group === "labour" && (
                      <>
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                          <div>
                            <label className="block text-xs font-bold text-emerald-600 uppercase tracking-wider mb-1">
                              Daily Charges / Rate (English)
                            </label>
                            <input
                              type="text"
                              value={editingItem.chargesEn ?? ""}
                              onChange={(e) => setEditingItem({ ...editingItem, chargesEn: e.target.value })}
                              placeholder="e.g. ₹500 / day or Call to discuss"
                              className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-emerald-500 focus:outline-none"
                            />
                          </div>
                          <div>
                            <label className="block text-xs font-bold text-emerald-600 uppercase tracking-wider mb-1">
                              दैनिक मजदूरी / चार्ज (Hindi)
                            </label>
                            <input
                              type="text"
                              value={editingItem.chargesHi ?? ""}
                              onChange={(e) => setEditingItem({ ...editingItem, chargesHi: e.target.value })}
                              placeholder="उदा. ₹500 / दिन या कॉल पर बातचीत करें"
                              className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-emerald-500 focus:outline-none"
                            />
                          </div>
                        </div>

                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                          <div>
                            <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                              Work Experience (English)
                            </label>
                            <input
                              type="text"
                              value={editingItem.experienceEn ?? ""}
                              onChange={(e) => setEditingItem({ ...editingItem, experienceEn: e.target.value })}
                              placeholder="e.g. 5 Years / Experienced"
                              className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                            />
                          </div>
                          <div>
                            <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                              कार्य अनुभव (Hindi)
                            </label>
                            <input
                              type="text"
                              value={editingItem.experienceHi ?? ""}
                              onChange={(e) => setEditingItem({ ...editingItem, experienceHi: e.target.value })}
                              placeholder="उदा. 5 साल का अनुभव"
                              className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                            />
                          </div>
                        </div>
                      </>
                    )}

                    {group === "transport" && (
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div>
                          <label className="block text-xs font-bold text-indigo-600 uppercase tracking-wider mb-1">
                            Vehicle Model & Service (English)
                          </label>
                          <input
                            type="text"
                            value={editingItem.vehicleTypeEn ?? ""}
                            onChange={(e) => setEditingItem({ ...editingItem, vehicleTypeEn: e.target.value })}
                            placeholder="e.g. Bolero / Car Rental (Satta Booking)"
                            className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                          />
                        </div>
                        <div>
                          <label className="block text-xs font-bold text-indigo-600 uppercase tracking-wider mb-1">
                            गाड़ी मॉडल व बुकिंग विवरण (Hindi)
                          </label>
                          <input
                            type="text"
                            value={editingItem.vehicleTypeHi ?? ""}
                            onChange={(e) => setEditingItem({ ...editingItem, vehicleTypeHi: e.target.value })}
                            placeholder="उदा. बोलेरो / कार सट्टा बुकिंग"
                            className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                          />
                        </div>
                      </div>
                    )}

                    {group === "family" && (
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div>
                          <label className="block text-xs font-bold text-emerald-600 uppercase tracking-wider mb-1">
                            Starting Price / Budget (English)
                          </label>
                          <input
                            type="text"
                            value={editingItem.startingPriceEn ?? ""}
                            onChange={(e) => setEditingItem({ ...editingItem, startingPriceEn: e.target.value })}
                            placeholder="e.g. Phone par baat karke tay hoga"
                            className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-emerald-500 focus:outline-none"
                          />
                        </div>
                        <div>
                          <label className="block text-xs font-bold text-emerald-600 uppercase tracking-wider mb-1">
                            शुरुआती बजट / कीमत (Hindi)
                          </label>
                          <input
                            type="text"
                            value={editingItem.startingPriceHi ?? ""}
                            onChange={(e) => setEditingItem({ ...editingItem, startingPriceHi: e.target.value })}
                            placeholder="उदा. फोन पर बातचीत करके तय होगा"
                            className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-emerald-500 focus:outline-none"
                          />
                        </div>
                      </div>
                    )}

                    {group === "health" && (
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div>
                          <label className="block text-xs font-bold text-indigo-600 uppercase tracking-wider mb-1">
                            Availability / Timings (English)
                          </label>
                          <input
                            type="text"
                            value={editingItem.availabilityEn ?? ""}
                            onChange={(e) => setEditingItem({ ...editingItem, availabilityEn: e.target.value })}
                            placeholder="e.g. 09:00 AM - 08:00 PM"
                            className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                          />
                        </div>
                        <div>
                          <label className="block text-xs font-bold text-indigo-600 uppercase tracking-wider mb-1">
                            समय / टाइमिंग (Hindi)
                          </label>
                          <input
                            type="text"
                            value={editingItem.availabilityHi ?? ""}
                            onChange={(e) => setEditingItem({ ...editingItem, availabilityHi: e.target.value })}
                            placeholder="उदा. सुबह 09:00 - शाम 08:00"
                            className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                          />
                        </div>
                      </div>
                    )}

                    {(group === "education" || group === "legal" || group === "construction") && (
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div>
                          <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                            Extra Info / Facilities / Rates (English)
                          </label>
                          <input
                            type="text"
                            value={editingItem.extraInfoEn ?? ""}
                            onChange={(e) => setEditingItem({ ...editingItem, extraInfoEn: e.target.value })}
                            placeholder="e.g. Transport Available / 10+ Years Experience"
                            className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                          />
                        </div>
                        <div>
                          <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                            अतिरिक्त विवरण / सुविधाएं / समय (Hindi)
                          </label>
                          <input
                            type="text"
                            value={editingItem.extraInfoHi ?? ""}
                            onChange={(e) => setEditingItem({ ...editingItem, extraInfoHi: e.target.value })}
                            placeholder="उदा. वाहन व कंप्यूटर लैब सुविधा / 10+ वर्ष अनुभव"
                            className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                          />
                        </div>
                      </div>
                    )}
                  </>
                );
              })()}
            </div>

            <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setEditingItem(null)}
                disabled={actionLoading === editingItem.id}
                className="px-4 py-2 text-xs font-bold text-slate-600 bg-slate-100 hover:bg-slate-200 disabled:opacity-50 rounded-xl transition-colors cursor-pointer"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleSaveEdit}
                disabled={actionLoading === editingItem.id}
                className="px-5 py-2.5 text-xs font-bold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-70 disabled:cursor-not-allowed rounded-xl shadow-md shadow-indigo-600/20 flex items-center gap-2 transition-all cursor-pointer"
              >
                {actionLoading === editingItem.id ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin text-white" />
                    <span>अपडेट हो रहा है... (Saving...)</span>
                  </>
                ) : (
                  <>
                    <Save className="w-4 h-4" />
                    <span>Save Changes (बदलाव सेव करें)</span>
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ==================== ADD SERVICE MODAL (ADMIN CREATE DIRECTLY) ==================== */}
      {isAddServiceModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-sm flex items-center justify-center p-4 overflow-y-auto">
          <div className="bg-white border border-slate-100 rounded-3xl p-6 sm:p-8 max-w-2xl w-full my-8 shadow-2xl space-y-5">
            <div className="flex items-center justify-between border-b border-slate-100 pb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center font-bold">
                  <Plus className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-base font-bold text-slate-900">Add New Village Service</h3>
                  <p className="text-xs text-slate-400">Directly register and publish service into the mobile app</p>
                </div>
              </div>
              <button
                onClick={() => setIsAddServiceModalOpen(false)}
                className="p-2 text-slate-400 hover:text-slate-700 rounded-xl hover:bg-slate-100 transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleAddServiceSubmit} className="space-y-4 max-h-[65vh] overflow-y-auto pr-2">
              {/* Category Dropdown */}
              <div>
                <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                  Select Category (श्रेणी चुनें) *
                </label>
                <select
                  value={selectedCatId}
                  onChange={(e) => setSelectedCatId(e.target.value)}
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none cursor-pointer"
                  required
                >
                  {CATEGORY_DEFINITIONS.map((cat) => (
                    <option key={cat.id} value={cat.id}>
                      {cat.displayName}
                    </option>
                  ))}
                </select>
              </div>

              {/* 1. Dynamic Primary Name English & Hindi */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-indigo-600 uppercase tracking-wider mb-1">
                    {currentCatDef.nameLabelEn}
                  </label>
                  <input
                    type="text"
                    value={newServiceNameEn}
                    onChange={(e) => setNewServiceNameEn(e.target.value)}
                    placeholder={currentCatDef.namePlaceholderEn}
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                    required
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-indigo-600 uppercase tracking-wider mb-1">
                    {currentCatDef.nameLabelHi}
                  </label>
                  <input
                    type="text"
                    value={newServiceNameHi}
                    onChange={(e) => setNewServiceNameHi(e.target.value)}
                    placeholder={currentCatDef.namePlaceholderHi}
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
              </div>

              {/* 2. Owner Contact Name (Only shown if showOwnerName is not false) */}
              {currentCatDef.showOwnerName !== false && (
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                      Owner / Contact Person (English)
                    </label>
                    <input
                      type="text"
                      value={newContactNameEn}
                      onChange={(e) => setNewContactNameEn(e.target.value)}
                      placeholder="e.g. Ram Kumar Verma"
                      className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                      मालिक / संपर्क व्यक्ति (Hindi)
                    </label>
                    <input
                      type="text"
                      value={newContactNameHi}
                      onChange={(e) => setNewContactNameHi(e.target.value)}
                      placeholder="उदा. राम कुमार वर्मा"
                      className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                    />
                  </div>
                </div>
              )}

              {/* 3. Phone Number & Village Name */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    Phone Number (10 Digits) *
                  </label>
                  <input
                    type="tel"
                    value={newPhone}
                    onChange={(e) => setNewPhone(e.target.value)}
                    placeholder="e.g. 9876543210"
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium font-mono focus:bg-white focus:border-indigo-500 focus:outline-none"
                    required
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    Village Name (English) *
                  </label>
                  <input
                    type="text"
                    value={newVillageNameEn}
                    onChange={(e) => setNewVillageNameEn(e.target.value)}
                    placeholder="e.g. Allahabad / Mahuawa"
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                    required
                  />
                </div>
              </div>

              {/* 4. Village Name Hindi & Address */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    गाँव का नाम (Hindi)
                  </label>
                  <input
                    type="text"
                    value={newVillageNameHi}
                    onChange={(e) => setNewVillageNameHi(e.target.value)}
                    placeholder="उदा. महुआवा / इलाहाबाद"
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    {currentCatDef.addressLabelEn || "Working Area / Address (English)"}
                  </label>
                  <input
                    type="text"
                    value={newAddressEn}
                    onChange={(e) => setNewAddressEn(e.target.value)}
                    placeholder="e.g. Main Market, Near Shiv Temple"
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
              </div>

              {/* 5. Address Hindi */}
              <div>
                <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                  {currentCatDef.addressLabelHi || "कार्य क्षेत्र व पता (Hindi)"}
                </label>
                <input
                  type="text"
                  value={newAddressHi}
                  onChange={(e) => setNewAddressHi(e.target.value)}
                  placeholder="उदा. मुख्य बाज़ार, शिव मंदिर के पास"
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                />
              </div>

              {/* 6. Dynamic DTO Extra Field 1 (e.g. Skills / Speciality / Vehicle Model / Services) */}
              {currentCatDef.extra1 && (
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 p-3 bg-indigo-50/40 rounded-xl border border-indigo-100">
                  <div>
                    <label className="block text-xs font-bold text-indigo-700 uppercase tracking-wider mb-1">
                      {currentCatDef.extra1.labelEn}
                    </label>
                    <input
                      type="text"
                      value={newExtra1En}
                      onChange={(e) => setNewExtra1En(e.target.value)}
                      placeholder={currentCatDef.extra1.placeholderEn}
                      className="w-full px-4 py-2 bg-white border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:border-indigo-500 focus:outline-none"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-bold text-indigo-700 uppercase tracking-wider mb-1">
                      {currentCatDef.extra1.labelHi}
                    </label>
                    <input
                      type="text"
                      value={newExtra1Hi}
                      onChange={(e) => setNewExtra1Hi(e.target.value)}
                      placeholder={currentCatDef.extra1.placeholderHi}
                      className="w-full px-4 py-2 bg-white border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:border-indigo-500 focus:outline-none"
                    />
                  </div>
                </div>
              )}

              {/* 7. Dynamic DTO Extra Field 2 (e.g. Daily Wage / Charges / Starting Price / Timings) */}
              {currentCatDef.extra2 && (
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 p-3 bg-indigo-50/40 rounded-xl border border-indigo-100">
                  <div>
                    <label className="block text-xs font-bold text-indigo-700 uppercase tracking-wider mb-1">
                      {currentCatDef.extra2.labelEn}
                    </label>
                    <input
                      type="text"
                      value={newExtra2En}
                      onChange={(e) => setNewExtra2Hi(e.target.value)}
                      placeholder={currentCatDef.extra2.placeholderEn}
                      className="w-full px-4 py-2 bg-white border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:border-indigo-500 focus:outline-none"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-bold text-indigo-700 uppercase tracking-wider mb-1">
                      {currentCatDef.extra2.labelHi}
                    </label>
                    <input
                      type="text"
                      value={newExtra2HiState}
                      onChange={(e) => setNewExtra2HiState(e.target.value)}
                      placeholder={currentCatDef.extra2.placeholderHi}
                      className="w-full px-4 py-2 bg-white border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:border-indigo-500 focus:outline-none"
                    />
                  </div>
                </div>
              )}

              {/* 8. Dynamic DTO Extra Field 3 (e.g. Experience / Facilities / Unit) */}
              {currentCatDef.extra3 && (
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 p-3 bg-indigo-50/40 rounded-xl border border-indigo-100">
                  <div>
                    <label className="block text-xs font-bold text-indigo-700 uppercase tracking-wider mb-1">
                      {currentCatDef.extra3.labelEn}
                    </label>
                    <input
                      type="text"
                      value={newExtra3En}
                      onChange={(e) => setNewExtra3En(e.target.value)}
                      placeholder={currentCatDef.extra3.placeholderEn}
                      className="w-full px-4 py-2 bg-white border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:border-indigo-500 focus:outline-none"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-bold text-indigo-700 uppercase tracking-wider mb-1">
                      {currentCatDef.extra3.labelHi}
                    </label>
                    <input
                      type="text"
                      value={newExtra3Hi}
                      onChange={(e) => setNewExtra3Hi(e.target.value)}
                      placeholder={currentCatDef.extra3.placeholderHi}
                      className="w-full px-4 py-2 bg-white border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:border-indigo-500 focus:outline-none"
                    />
                  </div>
                </div>
              )}

              {/* 9. Additional Details / Description English & Hindi */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    Other Details / Notes (English)
                  </label>
                  <textarea
                    rows={2}
                    value={newDetailsEn}
                    onChange={(e) => setNewDetailsEn(e.target.value)}
                    placeholder="Any extra info..."
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                    अन्य विवरण / टिप्पणी (Hindi)
                  </label>
                  <textarea
                    rows={2}
                    value={newDetailsHi}
                    onChange={(e) => setNewDetailsHi(e.target.value)}
                    placeholder="कोई अन्य जानकारी..."
                    className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-800 font-medium focus:bg-white focus:border-indigo-500 focus:outline-none"
                  />
                </div>
              </div>

              {/* Instant Publish Checkbox */}
              <div className="flex items-center justify-between p-3.5 bg-indigo-50/60 rounded-xl border border-indigo-100">
                <div>
                  <span className="text-xs font-bold text-indigo-950 block">Publish Live Immediately in App?</span>
                  <span className="text-[11px] text-slate-500">Auto-approves and makes the service immediately visible to village users</span>
                </div>
                <input
                  type="checkbox"
                  checked={newPublishLive}
                  onChange={(e) => setNewPublishLive(e.target.checked)}
                  className="w-4 h-4 accent-indigo-600 rounded cursor-pointer"
                />
              </div>

              {/* Form Buttons */}
              <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsAddServiceModalOpen(false)}
                  className="px-4 py-2 text-xs font-bold text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={actionLoading === "adding_service"}
                  className="px-5 py-2.5 text-xs font-bold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-70 disabled:cursor-not-allowed rounded-xl shadow-md shadow-indigo-600/20 flex items-center gap-2 cursor-pointer transition-all"
                >
                  {actionLoading === "adding_service" ? (
                    <>
                      <Loader2 className="w-4 h-4 animate-spin text-white" />
                      <span>जोड़ा जा रहा है... (Adding...)</span>
                    </>
                  ) : (
                    <>
                      <Plus className="w-4 h-4" />
                      <span>Add & Save Service (सर्विस जोड़ें)</span>
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

// Single Submission Card
function SubmissionCard({
  item,
  actionLoading,
  onApprove,
  onReject,
  onDelete,
  onEdit
}: {
  item: SubmissionItem;
  actionLoading: string | null;
  onApprove: (item: SubmissionItem) => void;
  onReject: (item: SubmissionItem) => void;
  onDelete: (item: SubmissionItem) => void;
  onEdit: (item: SubmissionItem) => void;
}) {
  const catKey = item.category.toLowerCase();
  const catStyle = CATEGORY_COLORS[catKey] || {
    bg: "bg-slate-100",
    text: "text-slate-700",
    border: "border-slate-200"
  };

  return (
    <div className="bg-white border border-slate-100 rounded-2xl p-5 flex flex-col justify-between hover:shadow-md transition-all">
      <div>
        {/* Top Badges */}
        <div className="flex items-center justify-between gap-2 mb-3">
          <span
            className={`inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[11px] font-bold ${
              item.status === "pending"
                ? "bg-amber-50 text-amber-700 border border-amber-200"
                : item.status === "approved"
                ? "bg-emerald-50 text-emerald-700 border border-emerald-200"
                : "bg-rose-50 text-rose-700 border border-rose-200"
            }`}
          >
            {item.status === "pending" && <Clock className="w-3 h-3 text-amber-600" />}
            {item.status === "approved" && <CheckCircle2 className="w-3 h-3 text-emerald-600" />}
            {item.status === "rejected" && <XCircle className="w-3 h-3 text-rose-600" />}
            <span className="capitalize">{item.status}</span>
          </span>

          <span className={`px-2.5 py-0.5 text-[10px] font-bold rounded-full border uppercase tracking-wider ${catStyle.bg} ${catStyle.text} ${catStyle.border}`}>
            {item.category}
          </span>
        </div>

        {/* Business Name */}
        <h3 className="text-base font-bold text-slate-900 mb-1 line-clamp-1">
          {item.businessName}
        </h3>

        {/* Village Tag */}
        {item.villageName && (
          <div className="inline-flex items-center gap-1 px-2 py-0.5 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-600 font-medium mb-2.5">
            <Building2 className="w-3 h-3 text-indigo-600" />
            <span>{item.villageName}</span>
          </div>
        )}

        {/* Details */}
        <div className="space-y-1 text-xs text-slate-500 mb-3">
          <div className="flex items-center gap-2">
            <User className="w-3.5 h-3.5 text-slate-400 shrink-0" />
            <span className="font-medium text-slate-700">{item.contactName}</span>
          </div>
          <div className="flex items-center gap-2">
            <Phone className="w-3.5 h-3.5 text-indigo-600 shrink-0" />
            <a href={`tel:${item.phoneNumber}`} className="text-indigo-600 font-bold font-mono hover:underline">
              {item.phoneNumber}
            </a>
          </div>
          <div className="flex items-start gap-2">
            <MapPin className="w-3.5 h-3.5 text-slate-400 shrink-0 mt-0.5" />
            <span className="line-clamp-2 text-slate-500">{item.address}</span>
          </div>
        </div>
      </div>

      {/* Card Actions Footer */}
      <div className="pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
        <div className="flex items-center gap-1">
          <button
            onClick={() => onEdit(item)}
            className="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors"
            title="Edit Details"
          >
            <Edit3 className="w-3.5 h-3.5" />
          </button>
          <button
            onClick={() => onDelete(item)}
            className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
            title="Delete"
          >
            <Trash2 className="w-3.5 h-3.5" />
          </button>
        </div>

        <div className="flex items-center gap-2">
          {item.status !== "rejected" && (
            <button
              onClick={() => onReject(item)}
              disabled={actionLoading === item.id}
              className="px-2.5 py-1 text-xs font-bold text-rose-600 hover:bg-rose-50 rounded-lg border border-rose-200 transition-colors"
            >
              Reject
            </button>
          )}

          {item.status !== "approved" && (
            <button
              onClick={() => onApprove(item)}
              disabled={actionLoading === item.id}
              className="px-3.5 py-1 text-xs font-bold text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg shadow-sm shadow-indigo-600/20 transition-all flex items-center gap-1"
            >
              <CheckCircle2 className="w-3.5 h-3.5" />
              <span>Approve</span>
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
