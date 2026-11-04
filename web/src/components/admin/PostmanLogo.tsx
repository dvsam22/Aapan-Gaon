import React from "react";

interface PostmanLogoProps {
  className?: string;
  size?: number;
}

export default function PostmanLogo({ className = "w-8 h-8", size = 32 }: PostmanLogoProps) {
  return (
    <div
      style={{ width: size, height: size }}
      className={`relative rounded-2xl bg-gradient-to-tr from-[#FF6C37] via-[#FF7A47] to-[#FFA07A] p-[2px] shadow-lg shadow-[#FF6C37]/30 flex items-center justify-center shrink-0 ${className}`}
    >
      <div className="w-full h-full bg-[#111317] rounded-[14px] flex items-center justify-center overflow-hidden relative group">
        {/* Glowing backdrop inside */}
        <div className="absolute inset-0 bg-gradient-to-br from-[#FF6C37]/20 to-transparent pointer-events-none" />
        
        {/* Postman-style Astronaut in Flight SVG */}
        <svg
          viewBox="0 0 48 48"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          className="w-3/4 h-3/4 text-[#FF6C37] group-hover:scale-110 transition-transform duration-300"
        >
          {/* Helmet */}
          <circle cx="34" cy="14" r="7" fill="#FF6C37" />
          <circle cx="34" cy="14" r="5" fill="#111317" />
          <path
            d="M32 12C33 11 35 11 36 12C37 13 37 15 36 16"
            stroke="#FFA07A"
            strokeWidth="1.5"
            strokeLinecap="round"
          />
          {/* Visor highlight */}
          <path
            d="M31 15L35 15"
            stroke="#FF6C37"
            strokeWidth="1.5"
            strokeLinecap="round"
          />
          {/* Flying Body / Torso */}
          <path
            d="M30 19L21 28L15 25L23 17L30 19Z"
            fill="#FF6C37"
          />
          {/* Back Jetpack */}
          <rect
            x="20"
            y="14"
            width="5"
            height="10"
            rx="2.5"
            transform="rotate(45 20 14)"
            fill="#FFA07A"
          />
          {/* Jet Thruster Flame */}
          <path
            d="M17 19L11 17L15 23L17 19Z"
            fill="#FFF"
            opacity="0.9"
          />
          {/* Legs in flight */}
          <path
            d="M21 28L12 37L9 36L16 27"
            stroke="#FF6C37"
            strokeWidth="3.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
          <path
            d="M18 30L10 39L7 38"
            stroke="#FF8C5D"
            strokeWidth="2.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
          {/* Star sparkle */}
          <circle cx="10" cy="10" r="1.5" fill="#FFA07A" />
          <circle cx="40" cy="36" r="1" fill="#FF8C5D" />
        </svg>
      </div>
    </div>
  );
}
