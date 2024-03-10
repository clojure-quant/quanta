(ns ta.viz.ds.highchart.data
  (:require
   [tick.core :as t]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype :as dtype]
   [tablecloth.api :as tc]
   [ta.indicator.returns :refer [diff]]
   [ta.trade.signal2 :refer [select-signal-is select-signal-has select-signal-contains]]
   [ta.viz.chart-spec :refer [get-series]]))

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

(defn- flag [col row]
  {:x (:epoch row)
   :y (:close row)
   ;:z 1000
   :title (str (col row))
   :text (str col)})

(defn series-flags
  "extracts one column from ds in format needed by highchart
   for signal plot"
  [bar-study-epoch-ds col]
  ;(println "Series flags col:" col)
  (let [;ds-with-signal (select-signal-has bar-study-epoch-ds col)
        ;ds-with-signal (select-signal-is bar-study-epoch-ds col :long)
        ds-with-signal (select-signal-contains bar-study-epoch-ds col #{:long :short})
        r (tds/mapseq-reader ds-with-signal)]
    ;(println "rows with signal: " (tds/row-count ds-with-signal))
    (->> (map #(flag col %) r)
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

(defn- convert-series [bar-study-epoch-ds [col-or-cols type]]
  (cond
    (or (= type :ohlc) (= type :candlestick) (= type :hollowcandlestick))
    (series-ohlc bar-study-epoch-ds)

    (= type :flags)
    (series-flags bar-study-epoch-ds col-or-cols)

    (= type :step)
    (series-step bar-study-epoch-ds col-or-cols)

    (= type :range)
    (let [[col1 col2] col-or-cols]
      (series-col2 bar-study-epoch-ds col1 col2))

    :else
    (series-col bar-study-epoch-ds col-or-cols)))


(defn convert-data [bar-study-ds chart-spec]
  (let [bar-study-epoch-ds (tc/add-column bar-study-ds
                                          :epoch (epoch bar-study-ds))
        series (get-series chart-spec)]
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

  (convert-data ds [{:high {:type :step}}])
  (convert-data ds [{:close {:type :step}}])
  (convert-data ds [{:close :point}])
  (convert-data ds [{[:low :high] {:type :range}}])




;
  )