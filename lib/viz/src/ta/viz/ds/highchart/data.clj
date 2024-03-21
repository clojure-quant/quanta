(ns ta.viz.ds.highchart.data
  (:require
   [tick.core :as t]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype :as dtype]
   [tablecloth.api :as tc]
   [ta.indicator.returns :refer [diff]]
   [ta.trade.signal :refer [select-signal-contains]]
   [ta.viz.chart-spec :refer [chart->series]]))

(defn- instant->epoch-millisecond [dt]
  (-> dt
      (t/long)
      (* 1000)))

(defn- epoch
  "add epoch column to ds"
  [bar-ds]
  (dtype/emap instant->epoch-millisecond :long (:date bar-ds)))

(defn- series-col
  "extracts one column from ds 
   in format needed by highchart"
  [bar-study-epoch-ds col]
  (let [r (tds/mapseq-reader bar-study-epoch-ds)]
    (mapv (juxt :epoch col) r)))

; Data points for range series can be defined either 
; as objects ({ x: 0, low: 1, high: 9 }) or 
; as arrays ([0, 1, 9]). 
; In either case, the x value can be skipped.
; Demonstrating an arearange chart with a low and high value per point. 
; Area range charts are commonly used to visualize a range that changes over time.

(defn- series-col2
  "extracts 2 columns
   in format needed by highchart
   [[1560864600000,49.01,50.07]
    [1560951000000,49.92,49.97]]"
  [bar-study-epoch-ds col1 col2]
  (let [r (tds/mapseq-reader bar-study-epoch-ds)]
    (mapv (juxt :epoch col1 col2) r)))

(defn- series-ohlc
  "extracts ohlc series
   in format needed by highchart
   [[1560864600000,49.01,50.07,48.8,49.61]
    [1560951000000,49.92,49.97,49.33,49.47]]"
  [bar-study-epoch-ds]
  (let [r (tds/mapseq-reader bar-study-epoch-ds)]
    (mapv (juxt :epoch :open :high :low :close :volume) r)))

; Every flag consists of x, title and text. 
; The attribute "x" must be set to the point where the flag should appear. 
; The attribute "title" is the text which is displayed inside the flag on the chart. 
; The attribute "text" contains the text which will appear when the mouse hover above the flag.

(defn- flag [v2style col row]
  {:x (:epoch row)
   ; :y (:close row)
   ;:z 1000
   :title (str (col row))
   :shape (get v2style (col row))
   :text (str "col: " col "val: " (col row))})

(def default-flag-styles
  {;:long "square"
   :long "url(/r/quanta/arrow-up.svg)"
   true "flags"
   ;:short "circle"
   :short "url(/r/quanta/arrow-down.svg)"})

(defn series-flags
  "extracts one column from ds in format needed by highchart
   for signal plot"
  [bar-study-epoch-ds {:keys [v2style column]
                       :or {v2style default-flag-styles}}]
  ;(println "Series flags col:" col)
  (let [signal-set (-> v2style keys set)
        ; (tc/select-rows bar-ds (contains? v2style (:signal bar-ds)))
        ds-with-signal (select-signal-contains bar-study-epoch-ds column signal-set)
        r (tds/mapseq-reader ds-with-signal)]
    ;(println "rows with signal: " (tds/row-count ds-with-signal))
    (->> (map #(flag v2style column %) r)
         (into []))))

;; step
;; steps carry forward the last value; 
;; therefore we can filter out unchanged values;
;; this can be a huge compression!

(defn- select-col-steps [bar-epoch-ds col]
  (let [price (col bar-epoch-ds)
        chg (diff price)
        date (:epoch bar-epoch-ds)]
    (assert price)
    (assert date)
    (-> (tc/dataset {:epoch date
                     col price
                     :chg chg})
        (tc/select-rows #(not (= 0.0 (:chg %))))
        (tc/select-columns [:epoch col]))))

(defn- series-step [bar-epoch-ds col]
  (-> bar-epoch-ds
      (select-col-steps col)
      (series-col col)))

(defn- convert-series [bar-study-epoch-ds {:keys [type column] :as row}]
  (cond
    (or (= type :ohlc) (= type :candlestick) (= type :hollowcandlestick))
    (series-ohlc bar-study-epoch-ds)

    (= type :flags)
    (series-flags bar-study-epoch-ds row)

    (= type :step)
    (series-step bar-study-epoch-ds column)

    (= type :range)
    (let [[col1 col2] column]
      (series-col2 bar-study-epoch-ds col1 col2))

    :else
    (series-col bar-study-epoch-ds column)))

(defn convert-data [bar-study-ds chart-spec]
  (let [bar-study-epoch-ds (tc/add-column bar-study-ds
                                          :epoch (epoch bar-study-ds))
        series (chart->series chart-spec)]
    (map #(convert-series bar-study-epoch-ds %) series)))

(comment

  (def ds
    (tc/dataset
     [{:date (t/instant) :open 1 :high 7 :low 3 :close 11 :volume 5}
      {:date (t/instant) :open 2 :high 7 :low 3 :close 12 :volume 5}
      {:date (t/instant) :open 3 :high 8 :low 4 :close 13 :volume 5}
      {:date (t/instant) :open 4 :high 8 :low 4 :close 14 :volume 5}]))

  (convert-data ds
                [{;:bar "x";:trade "flags"
                  :close {:type "line"
                          :linewidth 2
             ;:color (color :blue-900)
                          }
                  :open {:type :flag
                         :linewidth 4
             ;:color (color :red)
                         }}
                 {:volume {:type "line"
           ;:color (color :gold)
           ;:plottype (plot-type :columns)
                           }}])

  (convert-data ds
                [{;:bar "x";:trade "flags"
                  :close {:type "line"
                          :linewidth 2
             ;:color (color :blue-900)
                          }
                  :open {:type :flag
                         :linewidth 4
             ;:color (color :red)
                         }}
                 {:volume {:type "line"
           ;:color (color :gold)
           ;:plottype (plot-type :columns)
                           }}])

  (get-series [{:high :step}])
  (get-series [{:high {:type :step :color :blue}}])

  (convert-data ds [{:high :step}])

  (convert-data ds [{:high {:type :step}}])
  (convert-data ds [{:close {:type :step}}])
  (convert-data ds [{:close :point}])
  (convert-data ds [{[:low :high] {:type :range}}])

;
  )