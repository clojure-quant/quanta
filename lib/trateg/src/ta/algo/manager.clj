(ns ta.algo.manager
  (:require
   [tablecloth.api :as tc]
   [ta.helper.ds :refer [ds->map]]
   [ta.helper.date-ds :refer [ds-convert-col-instant->localdatetime ensure-roundtrip-date-localdatetime]]
   ; backtest
   [ta.backtest.roundtrip-backtest :refer [run-backtest]]
   [ta.backtest.roundtrip-stats :refer [roundtrip-performance-metrics]]
   [ta.backtest.nav :refer [nav-metrics nav]]
   [ta.series.signal :refer [select-signal-has]]
   [ta.tradingview.study :refer [get-trades]]
   ; viz
   [ta.viz.study-highchart :refer [study-highchart] :as hc]))

(defonce algos (atom {}))

(defn add-algo [a]
  (swap! algos assoc (:name a) a))

(defn algo-names []
  (keys @algos))

(defn get-algo [name]
  (get @algos name))

(defn col-info [ds]
  (->> ds
       tc/columns
       (map meta)))

(defn has-col? [ds col-kw]
  (->> ds
       tc/columns
       (map meta)
       (filter #(= col-kw (:name %)))
       first))

(defn trade-table
  [ds-study]
  (-> ds-study
      ds-convert-col-instant->localdatetime
       ;(tc/select-rows (range 1000))
      (select-signal-has :trade)))

(defn highchart [ds-study axes-spec]
  (let [axes-spec (if axes-spec
                    axes-spec
                    (if (has-col? ds-study :trade)
                      [{:trade "flags"}
                       {:volume "column"}]
                      [{:volume "column"}]))]
    (println "highchart axes spec:" axes-spec)
    (-> (study-highchart ds-study axes-spec)
        second)))

(defn backtest-stats [backtest]
  (let [ds-rts (-> (:ds-roundtrips backtest)
                   ensure-roundtrip-date-localdatetime)]
    {:rt-metrics (-> (roundtrip-performance-metrics backtest) ds->map first) ; ds
     :nav-metrics (nav-metrics backtest)
     :nav (nav backtest)}))

(def default-study-cols
  [:volume :date :low :open :close :high :symbol :signal :index :trade :trade-no :position])

(defn is-default-col? [c]
  (some #(= % c) default-study-cols))

(defn study-extra-cols [ds-study]
  (let [study-cols (->> ds-study col-info (map :name))]
    (remove is-default-col? study-cols)))

; 

(defn algo-run [name user-options]
  (if-let [{:keys [algo options]} (get-algo name)]
    (let [options (merge options user-options)
          {:keys [ds-study ds-roundtrips] :as backtest} (run-backtest algo options)]
      (merge
       {:name name
        :options options
        :study-extra-cols (study-extra-cols ds-study)}
       backtest
       (if ds-roundtrips
         {:stats (backtest-stats backtest)}
         {})
        ;(if (:axes-spec options)
       {:highchart (highchart ds-study (:axes-spec options))}
         ; {})
       ))
    {:name name
     :error "Algo not found."}))

(defn algo-run-browser [name user-options]
  (let [{:keys [ds-study study-extra-cols ds-roundtrips stats] :as d} (algo-run name user-options)]
    (merge d
           (if ds-study
             {:ds-study (ds->map ds-study)
              :study-extra-cols study-extra-cols
              :tradingview {:marks (get-trades ds-study)}
              }
             {})
           (if ds-roundtrips
             {:ds-roundtrips (ds->map ds-roundtrips)}
             ())
           (if stats
             {:stats {:rt-metrics (:rt-metrics stats)
                      :nav-metrics (:nav-metrics stats)
                      :nav (ds->map (:nav stats))}}
             {}))))

(comment
  (algo-names)

  (def an
    ;"buy-hold"
    ;"sma-trendfollow"
    "moon"
    ;"sma-diff"
    ;"bollinger"
   ; "supertrend"
    )

  (get-algo an)
  (->> ;(algo-run an {:symbol "SPY"})
  (algo-run-browser an {:symbol "TLT"})
  ; :stats
      ;keys
     ;:ds-study ;col-info (map :name)
      ;:ds-roundtrips   ;println
   ;:highchart
   ;first
   ;rest
   ;last
   ;:study-extra-cols
       ;:ds-roundtrips
       ; col-info (map :name)
      ; (get-trades)
   :tradingview-marks
   )
  
  (-> {:x [1 2 3]
       :y ["A" "B" "A"]}
      tc/dataset
      (has-col? :x)
        ;(has-col? :iii)
      )

   ; (require '[ta.viz.study-highchart :refer [ds-epoch series-flags]])
  #_(-> (algo-backtest "buy-hold s&p")
      ;keys
        :backtest
        :ds-study
        hc/ds-epoch
        (hc/series-flags :trade))

; 
  )
