import type { Config } from "tailwindcss";

export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        brand: {
          50: "#f5f8ff",
          100: "#e0e9ff",
          200: "#b7ccff",
          300: "#89acff",
          400: "#5d89ff",
          500: "#3a6bff",
          600: "#254dd1",
          700: "#1a39a3",
          800: "#142a75",
          900: "#101f54"
        }
      }
    }
  },
  plugins: []
} satisfies Config;
