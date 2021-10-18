(ns demo.studies.moon
  (:require
   [tick.alpha.api :as t])
  )

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
  (-> inst t/date moon-phase))


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

(comment
  ; test if moon phase from localdate is the same as using an instant
   (-> (java.time.LocalDate/now) moon-phase)
   (->  (t/now) moon-phase-from-instant)
  
   (map emoji-int->string (range 127761 127769))
   (map moon-phase->kw (range 127761 127769))

   (-> t/now moon-phase-from-instant emoji-int->string)
;  
  )





