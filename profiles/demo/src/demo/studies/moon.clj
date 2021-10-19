(ns demo.studies.moon
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]
   [ta.warehouse :refer [load-symbol]]
   [ta.helper.returns :refer [log-return]]
   [ta.series.moon :refer [inst->moon-phase-kw]]
   [ta.backtest.roundtrip-backtest :refer [run-backtest]]
   [ta.backtest.print :as p]
   [ta.backtest.nav :as nav]
   [ta.algo.buy-hold :refer [buy-hold-signal]]))

(defn win? [logret]
  (> logret 0))

(defn study-moon [w symbol normalize?]
  (let [ds-bars (load-symbol w "D" symbol)
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
  (study-moon :stocks "SPY" false)
  (study-moon :stocks "SPY" true)

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

(-> (study-moon :stocks "SPY" false) moon-mean)
(-> (study-moon :stocks "SPY" true) moon-mean)

(-> (study-moon :stocks "EURUSD" true) moon-mean)

(defn add-moon-indicator [ds-bars _]
  (tc/add-column
   ds-bars
   :phase  (dtype/emap inst->moon-phase-kw :object (:date ds-bars))))

(defn calc-moon-signal [phase]
  (if phase
    (case phase
      :i1 :flat
      :full :buy
      :hold)
    :hold))

(defn moon-signal [ds-bars options]
  (let [ds-study (add-moon-indicator ds-bars options)
        signal (into [] (map calc-moon-signal (:phase ds-study)))]
    (tc/add-columns ds-study {:signal signal})))

(def options-d
  {:w :stocks
   :symbol "SPY"
   :frequency "D"})

(def r-d
  (run-backtest moon-signal options-d))

(p/print-roundtrips r-d)
(p/print-overview-stats r-d)
(p/print-roundtrip-stats r-d)
(nav/nav-metrics r-d)
(p/print-nav r-d)
;; {:cum-pl 0.5967662874298225, :max-dd 0.27377890476728983}
;; end nav: 395
(Math/pow 10 -0.274)

(-> (run-backtest buy-hold-signal options-d)
    nav/nav-metrics)
(-> (run-backtest buy-hold-signal options-d)
    p/print-nav)
;; {:cum-pl 0.5170686780676208, :max-dd 0.3612479414113057}
;; end nav: 328
(Math/pow 10 -0.361)

; QQQ - NASDAQ
(-> (run-backtest buy-hold-signal (assoc options-d :symbol "QQQ"))
    nav/nav-metrics
    ;p/print-nav
    )
; buyhold: EndNav: 284. dd: 1.05
(-> (run-backtest moon-signal (assoc options-d :symbol "QQQ"))
    nav/nav-metrics
    ;p/print-nav
    )
; moon: EndNav: 537 dd: 0.42

;; GOLD SHARES
(-> (run-backtest moon-signal (assoc options-d :symbol "GLD"))
    nav/nav-metrics
    ;p/print-nav
    )
(-> (run-backtest buy-hold-signal (assoc options-d :symbol "GLD"))
    nav/nav-metrics
    ;p/print-nav
    )
; gold shares do not have the same moon influence.

;; GOLD HAS EXTREME MOON SAISONALITY
(-> (run-backtest moon-signal (assoc options-d :symbol "IAU"))
    nav/nav-metrics
    ;p/print-nav
    )
; {:cum-pl 0.4838585145483276, :max-dd 0.2321969274554916} EndNAV: 305

(-> (run-backtest buy-hold-signal (assoc options-d :symbol "IAU"))
    nav/nav-metrics
    ;p/print-nav
    )
; {:cum-pl -0.10437480353320505, :max-dd 1.0831565191879098} EndNAV: 78s
; bad data. split not correct.

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




