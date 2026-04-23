/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#030213',
          foreground: '#ffffff',
        },
        secondary: {
          DEFAULT: '#f3f4f6',
          foreground: '#030213',
        },
        destructive: {
          DEFAULT: '#d4183d',
          foreground: '#ffffff',
        },
        muted: {
          DEFAULT: '#ececf0',
          foreground: '#717182',
        },
        accent: {
          DEFAULT: '#e9ebef',
          foreground: '#030213',
        },
        background: '#ffffff',
        foreground: '#1f2937',
        card: {
          DEFAULT: '#ffffff',
          foreground: '#1f2937',
        },
        border: 'rgba(0, 0, 0, 0.1)',
        input: {
          DEFAULT: 'transparent',
          background: '#f3f3f5',
        },
        ring: '#6b7280',
      },
      borderRadius: {
        lg: '0.625rem',
        md: 'calc(0.625rem - 2px)',
        sm: 'calc(0.625rem - 4px)',
      },
    },
  },
  plugins: [],
}
