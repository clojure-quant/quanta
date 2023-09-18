(ns astro.moon
  (:require
   [tick.core :as tick]))

(def moon-period 29.530588853)
(def phase-length (/ moon-period 8))
; current moon phase is computed relative to the new moon of 2000-01-06
(def new-moon-start (java.time.LocalDate/of 2000 1 6))

(defn end-of-phase [phase]
  (- (* phase phase-length) (/ phase-length 2)))

(defn moon-phase [now]
  (let [days-since (.until new-moon-start now java.time.temporal.ChronoUnit/DAYS)
        since-last-new (mod days-since moon-period)]
    (condp > since-last-new
      ; since Java strings are stored with UTF-16,
      ; we have to represent emoji as integers, and
      ; later convert them into strings.
      (end-of-phase 1) 127761 ; new
      (end-of-phase 2) 127762 ; i1
      (end-of-phase 3) 127763 ; i2
      (end-of-phase 4) 127764 ; i3
      (end-of-phase 5) 127765 ; full
      (end-of-phase 6) 127766 ; d1
      (end-of-phase 7) 127767 ; d2 
      (end-of-phase 8) 127768 ; d3
      127761)))

(defn moon-phase-from-instant [inst]
  (-> inst tick/date moon-phase))

(defn emoji-int->string [emoji]
  (String. (int-array [emoji]) 0 1))

(defn moon-phase->kw [phase]
  (case phase
    127761 :new
    127762 :i1
    127763 :i2
    127764 :i3
    127765 :full
    127766 :d1
    127767 :d2
    127768 :d3))

(def inst->moon-phase-kw (comp moon-phase->kw moon-phase-from-instant))

(defn phase->text [phase]
  (case phase
    :new  "🌑" ;New Moon	U+1F311
    :i1   "🌒" ;Waxing Crescent Moon	U+1F312
    :i2   "🌓" ;First Quarter Moon	U+1F313
    :i3   "🌔" ;Waxing Gibbous Moon	U+1F314
    :full "🌕" ;Full Moon	U+1F315
    :d1   "🌖" ;Waning Gibbous Moon	U+1F316
    :d2   "🌗" ;Last Quarter Moon	U+1F317
    :d3   "🌘" ;Waning Crescent Moon	U+1F318
    "?"))

(comment
  ; test if moon phase from localdate is the same as using an instant
  (-> (java.time.LocalDate/now) moon-phase)
  (->  (tick/now) moon-phase-from-instant)

  (map emoji-int->string (range 127761 127769))
  (map moon-phase->kw (range 127761 127769))

  (-> tick/now moon-phase-from-instant emoji-int->string)

  (inst->moon-phase-kw (tick/now))

  ; PHASE => UNICODE SYMOBL
  (phase->text :full)
  (phase->text :new)

  (map phase->text [:new :i1 :i2 :i3 :full :d1 :d2 :d3])

;
  )

