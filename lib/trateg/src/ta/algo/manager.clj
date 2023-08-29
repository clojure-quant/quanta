(ns ta.algo.manager
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.helper.date :as dt]
   [ta.helper.ds :refer [ds->map]]
   [ta.helper.date-ds :refer [ds-convert-col-instant->localdatetime ensure-roundtrip-date-localdatetime]]
   ; backtest
   [ta.backtest.roundtrip-backtest :refer [run-backtest]]
   [ta.backtest.roundtrip-stats :refer [roundtrip-performance-metrics]]
   [ta.backtest.nav :refer [nav-metrics nav]]
   [ta.series.signal :refer [select-signal-has]]
   [ta.backtest.trades :refer [get-trades]]
   ; viz
   [ta.viz.study-highchart :refer [study-highchart] :as hc]))

(defonce algos (atom {}))

(defn add-algo [a]
  (swap! algos assoc (:name a) a))

(defn algo-names []
  (keys @algos))

(defn get-algo [name]
  (get @algos name))

(defn algo-info [name]
  (if-let [algo (get-algo name)]
    (let [charts (or (:charts algo) [])
          options (or (:options algo) {})]
      (-> algo
          (dissoc :algo :marks :shapes) ; remove functions
          (assoc :charts charts
                 :options options)))))

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
  (if-let [{:keys [algo options charts]} (get-algo name)]
    (let [options (merge options user-options)
          {:keys [ds-study ds-roundtrips] :as backtest} (run-backtest algo options)]
      (merge
       {:name name
        :options options
        :charts charts
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

(defn epoch
  "add epoch column to ds"
  [ds]
  (dtype/emap dt/->epoch-second :long (:date ds)))

(defn add-epoch-second [ds]
  (tc/add-column
   ds
   :epoch (epoch ds)))

(defn algo-run-browser [algo-name algo-opts]
  (let [{:keys [ds-study study-extra-cols ds-roundtrips stats] :as d} (algo-run algo-name algo-opts)]
    (merge d
           (if ds-study
             {:ds-study (ds->map (add-epoch-second ds-study))
              :study-extra-cols study-extra-cols
              :tradingview {:marks (get-trades ds-study)}}
             {})
           (if ds-roundtrips
             {:ds-roundtrips (ds->map ds-roundtrips)}
             ())
           (if stats
             {:stats {:rt-metrics (:rt-metrics stats)
                      :nav-metrics (:nav-metrics stats)
                      :nav (ds->map (:nav stats))}}
             {}))))


(defn select-in-window [ds epoch-start epoch-end]
  (tc/select-rows
   ds
   (fn [{:keys [epoch]}]
     (and (>= epoch epoch-start) (< epoch epoch-end)))))

(defn algo-run-window [name symbol frequency options epoch-start epoch-end]
  (let [options (assoc options :symbol symbol
                       :w :stocks
                       :frequency frequency)
        {:keys [ds-study] :as d} (algo-run name options)]
    (-> ds-study
        (add-epoch-second)
        (select-in-window epoch-start epoch-end))))

(defn algo-run-window-browser [name symbol frequency options epoch-start epoch-end]
  (let [ds (algo-run-window name symbol frequency options epoch-start epoch-end)]
    (ds->map ds)))

(defn algo-marks [name symbol frequency user-options epoch-start epoch-end]
  (if-let [{:keys [marks options]} (get-algo name)]
    (if marks
      (let [options (merge options user-options)]
        (marks symbol frequency options epoch-start epoch-end))
      (do (println "NO MARKS - " name "does not define a marks fn.")
          []))
    (do  (println "NO MARKS - algo not found: " name)
         [])))

(defn algo-shapes [name symbol frequency user-options epoch-start epoch-end]
  (if-let [{:keys [shapes options]} (get-algo name)]
    (if shapes
      (let [options (merge options user-options)
            data (shapes symbol frequency options epoch-start epoch-end)]
        (println "SHAPE [" (count data) "]" name symbol epoch-start epoch-end)
        data)
      (do (println "NO SHAPES - " name "does not define a shapes fn.")
          []))
    (do  (println "NO SHAPES - algo not found: " name)
         [])))

(comment
  (algo-names)

  (def an
    ;"buy-hold"
    "sma-trendfollow"
    ;"moon"
    ;"sma-diff"
    ;"bollinger"
   ; "supertrend"
    )

  (get-algo an)
  (algo-info an)

  (def epoch-start 1642726800) ; jan 21 2022
  (def epoch-end 1650499200) ; april 21 2022

  (require '[ta.helper.print :refer [print-overview]])
  (->
   (algo-run-window an "SPY" "D" {} epoch-start epoch-end)
    ;(algo-run-window-browser an "SPY" "D" {} epoch-start epoch-end) 
    ;:epoch
   ;print-overview
   println)

  (-> (algo-marks "astro" "SPY" "D" {:show-moon false} epoch-start epoch-end)
      count)

  (-> (algo-marks "astro" "SPY" "D" {:show-moon true} epoch-start epoch-end)
      count)

  (-> (algo-shapes "moon" "SPY" "D" {:show-moon true} epoch-start epoch-end)
      ;count
      )
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
   :tradingview-marks)

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
