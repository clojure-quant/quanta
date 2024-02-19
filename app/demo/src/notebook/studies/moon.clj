(ns notebook.studies.moon
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]
   [ta.helper.returns :refer [log-return]]
   [ta.trade.roundtrip-backtest :refer [signal-ds->roundtrips]]
   [ta.trade.metrics.roundtrip-stats :refer [roundtrip-metrics]]
   [ta.trade.print :as p]
   [ta.trade.metrics.nav :as nav]
   [notebook.algo.moon :refer [moon-indicator]]
   [ta.algo.buy-hold :refer [buy-hold-signal]]
   [ta.env.javelin.core :refer [backtest-single-bar-strategy]]))

;; 1. calculate algo, observe moon-phase.

(def algo-spec {:calendar [:us :d]
                :algo  'notebook.algo.moon/moon-indicator
                :asset "SPY"
                :import :kibot
                :trailing-n 1000
                :normalize? false})

(def moon-ds
  (backtest-single-bar-strategy :bardb-dynamic algo-spec))

moon-ds

;; 2. analyzes returns depending on moon-phase

(defn win? [logret]
  (> logret 0))

(defn study-moon [env {:keys [normalize?] :as spec} ds-bars]
  (let [ds-moon (moon-indicator env spec ds-bars)
        ds-study (tc/add-columns ds-moon {:logret (log-return (:close ds-bars))})
        avg-move (fun/mean (:logret ds-study))
        ds-study (if normalize?
                   (do (println "avg move: " avg-move)
                       (tc/add-column ds-study :logret (fun/- (:logret ds-study) avg-move)))
                   ds-study)
        ds-study (tc/add-columns ds-study
                                 {:win (dtype/emap win? :bool (:logret ds-study))
                                  :move (fun/abs (:logret ds-study))})
        ds-study (tc/select-rows ds-study (range 1 (tc/row-count ds-study)))]
    ds-study))


(defn distribution [ds-moon]
  (let [ds-grouped (-> ds-moon
                       (tc/group-by [:moon-phase])
                       (tc/aggregate {:count (fn [ds]
                                              (->> ds tc/row-count))
                                      :mean (fn [ds]
                                              (->> ds
                                                   :logret
                                                   fun/mean))}))]
    ds-grouped))

(def algo-spec {:calendar [:us :d]
                :algo 'notebook.studies.moon/study-moon
                :asset "SPY"
                :import :kibot
                :trailing-n 1000
                :normalize? false})

(def result-ds
  (backtest-single-bar-strategy :bardb-dynamic algo-spec))


(distribution result-ds)
;;    | :moon-phase | :count |       :mean |
;;    |-------------|-------:|------------:|
;;    |        :new |    121 |  0.00036331 |
;;    |         :i1 |    118 | -0.00067738 |
;;    |         :i2 |    129 |  0.00045863 |
;;    |         :i3 |    115 |  0.00005925 |
;;    |       :full |    121 |  0.00060311 |
;;    |         :d1 |    116 |  0.00000548 |
;;    |         :d2 |    122 |  0.00024143 |
;;    |         :d3 |    122 |  0.00098021 |


;; distribution for nasdaq
(def result-qqq-ds 
  (backtest-single-bar-strategy :bardb-dynamic (assoc algo-spec :asset "QQQ")))
(distribution result-qqq-ds)
;;    | :moon-phase | :count |       :mean |
;;    |-------------|-------:|------------:|
;;    |        :new |    121 |  0.00042063 |
;;    |         :i1 |    118 | -0.00116947 |
;;    |         :i2 |    129 |  0.00083900 |
;;    |         :i3 |    115 |  0.00007238 |
;;    |       :full |    121 |  0.00082979 |
;;    |         :d1 |    115 |  0.00015753 |
;;    |         :d2 |    122 | -0.00002570 |
;;    |         :d3 |    122 |  0.00135081 |


;; distribution for gold
(def result-gld-ds 
  (backtest-single-bar-strategy :bardb-dynamic (assoc algo-spec :asset "GLD")))
(distribution result-gld-ds)
;;    | :moon-phase | :count |       :mean |
;;    |-------------|-------:|------------:|
;;    |        :new |    121 |  0.00012363 |
;;    |         :i1 |    118 | -0.00003122 |
;;    |         :i2 |    129 |  0.00034141 |
;;    |         :i3 |    115 | -0.00006347 |
;;    |       :full |    121 |  0.00014722 |
;;    |         :d1 |    116 |  0.00020369 |
;;    |         :d2 |    122 | -0.00016964 |
;;    |         :d3 |    122 | -0.00005881 |


result-gld-ds

;; make the view more compact.
(tc/select-columns result-ds [:date :moon-phase :logret])

;; get roundtrips.
(-> result-gld-ds
    (signal-ds->roundtrips))

(-> result-gld-ds
    (signal-ds->roundtrips)
    roundtrip-metrics)
;;    | :avg-win-log | :avg-bars-win | :win-nr-prct |        :pf |   :avg-log | :pl-log-cum | :avg-loss-log | :trades | :avg-bars-loss |       :p |
;;    |-------------:|--------------:|-------------:|-----------:|-----------:|------------:|--------------:|--------:|---------------:|----------|
;;    |   0.01097137 |   12.82608696 |  24.21052632 | 1.04275617 | 0.00010891 |  0.01034677 |   -0.00336104 |      95 |     9.29166667 | _unnamed |


;; TODO: make it work starting from here; really just simple nav metrics.

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




