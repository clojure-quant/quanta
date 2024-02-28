(ns ta.viz.ds.highchart.data
  (:require
   [tick.core :as t]
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.helper.date :as dt]
   [ta.series.signal :refer [select-signal-is select-signal-has select-signal-contains]]
   [ta.viz.ds.highchart.chart-spec :refer [series-input]]))

(defn add-epoch
  "add epoch column to ds"
  [ds]
  (tds/column-map ds :epoch #(* 1000 (dt/->epoch-second %)) [:date]))

(defn series
  "extracts one column from ds 
   in format needed by highchart"
  [bar-study-epoch-ds col]
  (let [r (tds/mapseq-reader bar-study-epoch-ds)]
    (mapv (juxt :epoch col) r)))

(defn series-ohlc
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


(defn flag [col row]
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

(defn convert-series [bar-study-epoch-ds [col type]]
  (cond 
    (or (= type :ohlc) (= type :candlestick) )
    (series-ohlc bar-study-epoch-ds)

    (= type :flags)
    (series-flags bar-study-epoch-ds col)

    :else
    (series bar-study-epoch-ds col)))


(defn convert-data [bar-study-ds chart-spec]
  (let [bar-study-epoch-ds (add-epoch bar-study-ds)
        cols (series-input chart-spec)]
    (map #(convert-series bar-study-epoch-ds %) cols)))

(comment

  (def ds
    (tc/dataset
     [{:date (t/date-time) :open 1 :high 2 :low 3 :close 11 :volume 5}
      {:date (t/date-time) :open 2 :high 2 :low 3 :close 12 :volume 5}
      {:date (t/date-time) :open 3 :high 2 :low 3 :close 13 :volume 5}
      {:date (t/date-time) :open 4 :high 2 :low 3 :close 14 :volume 5}]))

  (convert-data ds
                [{:bar "x";:trade "flags"
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
;
  )