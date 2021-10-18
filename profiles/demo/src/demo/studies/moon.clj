(ns demo.studies.moon
  (:require
   [tick.alpha.api :as t]
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]
   [ta.warehouse :refer [load-symbol]]
   [ta.dataset.returns :refer [log-return]]))

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

(def inst->moon-phase-kw (comp moon-phase->kw moon-phase-from-instant))

(inst->moon-phase-kw (t/now))

(defn win? [logret]
  (> logret 0))

(defn study-moon [normalize?]
  (let [ds-bars (load-symbol :stocks "D" "SPY")
        ds-study (tc/add-columns ds-bars
                                 {:logret (log-return (:close ds-bars))
                                  :phase  (dtype/emap inst->moon-phase-kw :object (:date ds-bars))})
        avg-move (fun/mean (:logret ds-study))
        ds-study (if normalize?
                   (do (println "avg move: " avg-move)
                       (tc/add-column ds-study :logret (fun/- (:logret ds-study) avg-move)))
                   ds-study)
        ds-study (tc/add-columns ds-study
                                 {:win (dtype/emap win? :bool (:logret ds-study))
                                  :move (fun/abs (:logret ds-study))})
        ds-study (tc/select-rows ds-study (range 1 (tc/row-count ds-study)))]
    ds-study
    (tc/select-columns ds-study [:date :phase :win :logret :move])))

(comment
  (study-moon false)
  (study-moon true)

 ; 
  )
(defn moon-mean [ds-study]
  (let [ds-grouped (-> ds-study
                       (tc/group-by [:phase])
                       (tc/aggregate {;:count (fn [ds]
                       ;         (->> ds
                       ;              :move
                       ;              count))
                                      :mean (fn [ds]
                                              (->> ds
                                                   :logret
                                                   fun/mean))}))]
    ds-grouped))

(-> (study-moon false) moon-mean)
(-> (study-moon true) moon-mean)

;; move extremes

(defn moon-max [ds-study]
  (-> ds-study
      (tc/group-by [:win :phase])
      (tc/aggregate {:max (fn [ds]
                            (->> ds
                                 :move
                                 (apply max)))})
      (tc/pivot->wider :win [:max] {:drop-missing? false})))

#_(defn select-big-moves [ds-moon]
    (let [m (-> ds-moon (:move) fun/quartiles (nth 3))]
      (tc/select-rows ds-moon (fn [{:keys [move]}]
                                (> move m)))))




