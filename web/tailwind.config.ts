import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        postman: {
          50: "#fff5f0",
          100: "#ffe8dd",
          200: "#ffd1bc",
          300: "#ffb290",
          400: "#ff8c5d",
          500: "#ff6c37", // Official Postman Orange
          600: "#f05118",
          700: "#c7390b",
          800: "#9e2f0d",
          900: "#802b10",
          DEFAULT: "#ff6c37",
        },
        dark: {
          950: "#0b0c0e", // Deepest background
          900: "#111317", // Sidebar & cards
          850: "#161920", // Card hover / elevated
          800: "#1e222b", // Borders & separators
          750: "#272c38",
          700: "#323948",
        },
      },
      boxShadow: {
        "postman-glow": "0 0 25px -5px rgba(255, 108, 55, 0.35)",
        "postman-sm": "0 0 15px -3px rgba(255, 108, 55, 0.25)",
      },
    },
  },
  plugins: [],
};

export default config;
