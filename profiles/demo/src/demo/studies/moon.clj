(ns demo.studies.moon)

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
      (end-of-phase 1) 127761
      (end-of-phase 2) 127762
      (end-of-phase 3) 127763
      (end-of-phase 4) 127764
      (end-of-phase 5) 127765
      (end-of-phase 6) 127766
      (end-of-phase 7) 127767
      (end-of-phase 8) 127768
      127761)))

(defn emoji-int->string [emoji]
  (String. (int-array [emoji]) 0 1))

(-> (java.time.LocalDate/now)
    moon-phase
    emoji-int->string
    println)