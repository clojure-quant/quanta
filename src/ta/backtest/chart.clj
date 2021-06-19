(ns ta.backtest.chart
  (:require
   [ta.date :refer [->epoch]])
  )

; uses highcharts

(defn inst-chart [df fields-location]
  (let [])
  
  
  )




(defn performance-chart [{:keys [trades bars]}]
  (let [bars       bars
        price-data     (mapv (juxt :date :open :high :low :close :vol) bars)
        ixs            (mapv :date bars)
        cash-flow      (cash-flow bars trades)
        cash-flow-data (map vector ixs cash-flow)
        peaks          (reductions max cash-flow)
        drawdowns      (map (fn [p x] (/ (- p x) p))
                            peaks
                            cash-flow)
        max-drawdowns  (reductions max drawdowns)
        drawdowns-data     (map vector ixs drawdowns)
        max-drawdowns-data (map vector ixs max-drawdowns)]
    (view-highchart
     {:rangeSelector {:enabled false}
      :chart         {:height 600}
      :navigator     {:enabled false}
      :scrollbar     {:enabled false}
      :yAxis         [{:lineWidth 1
                       :title     {:text "Price"}}
                      {:lineWidth 1
                       :title     {:text "Returns"}
                       :opposite  false}]
      :series        [{:type         "line"
                       :name         "price"
                       :id           "priceseries"
                       :data         price-data
                       :dataGrouping {:enabled false}
                       :zIndex       2
                       :yAxis        0
                       :color        "#000000"}
                      {:type         "area"
                       :name         "return"
                       :data         cash-flow-data
                       :yAxis        1
                       :dataGrouping {:enabled false}
                       :zIndex       0
                       :color        "#0000ff"
                       :fillOpacity  0.3}
                      {:type         "area"
                       :name         "drawdown"
                       :data         drawdowns-data
                       :color        "#ff0000"
                       :fillOpacity  0.5
                       :yAxis        1
                       :zIndex       1
                       :dataGrouping {:enabled false}}
                      {:type         "line"
                       :name         "max drawdown"
                       :data         max-drawdowns-data
                       :color        "#800000"
                       :yAxis        1
                       :zIndex       1
                       :dataGrouping {:enabled false}}]})))
