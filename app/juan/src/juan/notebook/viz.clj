(ns juan.notebook.viz
  (:require
    [taoensso.timbre :as log :refer [tracef debug debugf info infof warn error errorf]]
    [ta.viz.ds.highchart :refer [highstock-render-spec]]))

(def table-spec
  {:topic :juan-daily-table
   :class "table-head-fixed padding-sm table-red table-striped table-hover"
   :style {:width "50vw"
           :height "40vh"
           :border "3px solid green"}
   :columns [{:path :date :format 'ta.viz.lib.format/dt-yyyymmdd}
             {:path :atr :header "ATR!" :format 'ta.viz.lib.format/fmt-nodigits}
             {:path :close}
             {:path :ppivotnr}]})

(def chart-spec
  {:topic :juan-daily-chart
   :chart {:box :fl}
   :charts [{:close :candlestick ; :ohlc ; :line 
             ;:color "red"
             ;:atr :flags
             }
            {:atr {:type :line :color "red"}}
            {:volume :column}]})


(def combined-table-spec
  {:topic :juan-combined-table
   :class "table-head-fixed padding-sm table-blue table-striped table-hover table-auto"
   :style {:width "100%" ; "50vw"
           :height "100%" ;"40vh"
           :border "3px solid blue"}
   :columns [{:path :date}
             {:path :daily-close :attrs 'ta.viz.lib.column/gray-column}
             {:path :daily-atr}
             {:path :close}
             {:path :spike :header "spike-signal" :attrs 'ta.viz.lib.column/trading-signal}
             {:path :doji :attrs 'ta.viz.lib.column/trading-signal}
             {:path :spike-doji :attrs 'ta.viz.lib.column/trading-signal}
             {:path :long}
             {:path :short}]})

(def combined-chart-spec
  {:topic :juan-combined-chart
   :chart {:box :fl}
   :charts [{:close :candlestick ; :ohlc 
             :daily-atr-mid :line
             :daily-atr-upper :line
             :daily-atr-lower :line
             ;:doji :flags
             :spike-doji {:type :flags
                          ;:color "blue" 
                          :color "rgba(100,0,50,1)"
                          ;:className "bg-red-900"
                          :fillColor "rgba(100,200,50,1)" ;"green"
                          :dataLabels {:backgroundColor "rgba(100,200,50,1)"
                                       :borderColor "green"}}}
            ;{:daily-atr :line}
            {:doji-v {:type :column :color "red"}
             :spike-v {:type :column :color "blue"}
             :spike-doji-v {:type :column :color "green"}}
            {:volume :column}]})


(defn calc-viz-combined-highchart [signal]
  ;(warn "signal: " signal) ; not good for production - Lots of logging!
  (highstock-render-spec nil combined-chart-spec signal))

