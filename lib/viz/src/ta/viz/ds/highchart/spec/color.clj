(ns ta.viz.ds.highchart.spec.color)

; https://tailwindcss.com/docs/customizing-colors
; https://github.com/tailwindlabs/tailwindcss/blob/next/packages/tailwindcss/src/utils/is-color.ts
; https://github.com/tailwindlabs/tailwindcss/blob/99c4afd9cd0e9414981b31321e83c5e2d5b0544b/packages/tailwindcss/theme.css#L196

(def colors
  {"blue-1" "#dbeafe"
   "blue-2" "#bfdbfe"
   "blue-3" "#93c5fd"
   "blue-4" "#60a5fa"
   "blue-5" "#3b82f6"
   "blue-6" "#2563eb"
   "blue-7" "#1d4ed8"
   "blue-8" "#1e40af"
   "blue-9" "#1e3a8a"
   "blue-95" "#172554"

   "slate-1" "#f1f5f9"
   "slate-2" "#e2e8f0"
   "slate-3" "#cbd5e1"
   "slate-4" "#94a3b8"
   "slate-5" "#64748b"
   "slate-6" "#475569"
   "slate-7" "#334155"
   "slate-8" "#1e293b"
   "slate-9" "#0f172a"
   "slate-95" "#020617"
   
   "gray-1" "#f3f4f6"
   "gray-2" "#e5e7eb"
   "gray-3" "#d1d5db"
   "gray-4" "#9ca3af"
   "gray-5" "#6b7280"
   "gray-6" "#4b5563"
   "gray-7" "#374151"
   "gray-8" "#1f2937"
   "gray-9" "#111827"
   "gray-95" "#030712"
   
   "zinc-1" "#f4f4f5"
   "zinc-2" "#e4e4e7"
   "zinc-3" "#d4d4d8"
   "zinc-4" "#a1a1aa"
   "zinc-5" "#71717a"
   "zinc-6" "#52525b"
   "zinc-7" "#3f3f46"
   "zinc-8" "#27272a"
   "zinc-9" "#18181b"
   "zinc-95" "#09090b"

   "neutral-1" "#f5f5f5"
   "neutral-2" "#e5e5e5"
   "neutral-3" "#d4d4d4"
   "neutral-4" "#a3a3a3"
   "neutral-5" "#737373"
   "neutral-6" "#525252"
   "neutral-7" "#404040"
   "neutral-8" "#262626"
   "neutral-9" "#171717"
   "neutral-95" "#0a0a0a"

   "stone-1" "#f5f5f4"
   "stone-2" "#e7e5e4"
   "stone-3" "#d6d3d1"
   "stone-4" "#a8a29e"
   "stone-5" "#78716c"
   "stone-6" "#57534e"
   "stone-7" "#44403c"
   "stone-8" "#292524"
   "stone-9" "#1c1917"
   "stone-95" "#0c0a09"

   "red-1" "#fee2e2"
   "red-2" "#fecaca"
   "red-3" "#fca5a5"
   "red-4" "#f87171"
   "red-5" "#ef4444"
   "red-6" "#dc2626"
   "red-7" "#b91c1c"
   "red-8" "#991b1b"
   "red-9" "#7f1d1d"
   "red-95" "#450a0a"

   "orange-1" "#ffedd5"
   "orange-2" "#fed7aa"
   "orange-3" "#fdba74"
   "orange-4" "#fb923c"
   "orange-5" "#f97316"
   "orange-6" "#ea580c"
   "orange-7" "#c2410c"
   "orange-8" "#9a3412"
   "orange-9" "#7c2d12"
   "orange-95" "#431407"

   "amber-1" "#fef3c7"
   "amber-2" "#fde68a"
   "amber-3" "#fcd34d"
   "amber-4" "#fbbf24"
   "amber-5" "#f59e0b"
   "amber-6" "#d97706"
   "amber-7" "#b45309"
   "amber-8" "#92400e"
   "amber-9" "#78350f"
   "amber-95" "#451a03"

   "yellow-1" "#fef9c3"
   "yellow-2" "#fef08a"
   "yellow-3" "#fde047"
   "yellow-4" "#facc15"
   "yellow-5" "#eab308"
   "yellow-6" "#ca8a04"
   "yellow-7" "#a16207"
   "yellow-8" "#854d0e"
   "yellow-9" "#713f12"
   "yellow-95" "#422006"

   "lime-1" "#ecfccb"
   "lime-2" "#d9f99d"
   "lime-3" "#bef264"
   "lime-4" "#a3e635"
   "lime-5" "#84cc16"
   "lime-6" "#65a30d"
   "lime-7" "#4d7c0f"
   "lime-8" "#3f6212"
   "lime-9" "#365314"
   "lime-95" "#1a2e05"

   "green-1" "#dcfce7"
   "green-2" "#bbf7d0"
   "green-3" "#86efac"
   "green-4" "#4ade80"
   "green-5" "#22c55e"
   "green-6" "#16a34a"
   "green-7" "#15803d"
   "green-8" "#166534"
   "green-9" "#14532d"
   "green-95" "#052e16"

   "emerald-1" "#d1fae5"
   "emerald-2" "#a7f3d0"
   "emerald-3" "#6ee7b7"
   "emerald-4" "#34d399"
   "emerald-5" "#10b981"
   "emerald-6" "#059669"
   "emerald-7" "#047857"
   "emerald-8" "#065f46"
   "emerald-9" "#064e3b"
   "emerald-95" "#022c22"

   "teal-1" "#ccfbf1"
   "teal-2" "#99f6e4"
   "teal-3" "#5eead4"
   "teal-4" "#2dd4bf"
   "teal-5" "#14b8a6"
   "teal-6" "#0d9488"
   "teal-7" "#0f766e"
   "teal-8" "#115e59"
   "teal-9" "#134e4a"
   "teal-95" "#042f2e"

   "cyan-1" "#cffafe"
   "cyan-2" "#a5f3fc"
   "cyan-3" "#67e8f9"
   "cyan-4" "#22d3ee"
   "cyan-5" "#06b6d4"
   "cyan-6" "#0891b2"
   "cyan-7" "#0e7490"
   "cyan-8" "#155e75"
   "cyan-9" "#164e63"
   "cyan-95" "#083344"

   "sky-1" "#e0f2fe"
   "sky-2" "#bae6fd"
   "sky-3" "#7dd3fc"
   "sky-4" "#38bdf8"
   "sky-5" "#0ea5e9"
   "sky-6" "#0284c7"
   "sky-7" "#0369a1"
   "sky-8" "#075985"
   "sky-9" "#0c4a6e"
   "sky-95" "#082f49"

   "indigo-1" "#e0e7ff"
   "indigo-2" "#c7d2fe"
   "indigo-3" "#a5b4fc"
   "indigo-4" "#818cf8"
   "indigo-5" "#6366f1"
   "indigo-6" "#4f46e5"
   "indigo-7" "#4338ca"
   "indigo-8" "#3730a3"
   "indigo-9" "#312e81"
   "indigo-95" "#1e1b4b"

   "violet-1" "#ede9fe"
   "violet-2" "#ddd6fe"
   "violet-3" "#c4b5fd"
   "violet-4" "#a78bfa"
   "violet-5" "#8b5cf6"
   "violet-6" "#7c3aed"
   "violet-7" "#6d28d9"
   "violet-8" "#5b21b6"
   "violet-9" "#4c1d95"
   "violet-95" "#2e1065"

   "purple-1" "#f3e8ff"
   "purple-2" "#e9d5ff"
   "purple-3" "#d8b4fe"
   "purple-4" "#c084fc"
   "purple-5" "#a855f7"
   "purple-6" "#9333ea"
   "purple-7" "#7e22ce"
   "purple-8" "#6b21a8"
   "purple-9" "#581c87"
   "purple-95" "#3b0764"

   "fuchsia-1" "#fae8ff"
   "fuchsia-2" "#f5d0fe"
   "fuchsia-3" "#f0abfc"
   "fuchsia-4" "#e879f9"
   "fuchsia-5" "#d946ef"
   "fuchsia-6" "#c026d3"
   "fuchsia-7" "#a21caf"
   "fuchsia-8" "#86198f"
   "fuchsia-9" "#701a75"
   "fuchsia-95" "#4a044e"

   "pink-1" "#fce7f3"
   "pink-2" "#fbcfe8"
   "pink-3" "#f9a8d4"
   "pink-4" "#f472b6"
   "pink-5" "#ec4899"
   "pink-6" "#db2777"
   "pink-7" "#be185d"
   "pink-8" "#9d174d"
   "pink-9" "#831843"
   "pink-95" "#500724"

   "rose-1" "#ffe4e6"
   "rose-2" "#fecdd3"
   "rose-3" "#fda4af"
   "rose-4" "#fb7185"
   "rose-5" "#f43f5e"
   "rose-6" "#e11d48"
   "rose-7" "#be123c"
   "rose-8" "#9f1239"
   "rose-9" "#881337"
   "rose-95" "#4c0519"
   })

(defn set-color [c]
  (if-let [hex (get colors c)]
    hex
    c))