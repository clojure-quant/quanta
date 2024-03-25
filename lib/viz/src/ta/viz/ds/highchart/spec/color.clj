(ns ta.viz.ds.highchart.spec.color)

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
   "blue-95" "#172554"})

(defn set-color [c]
  (if-let [hex (get colors c)]
    hex
    c))