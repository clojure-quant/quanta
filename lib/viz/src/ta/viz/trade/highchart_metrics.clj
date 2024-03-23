(ns ta.viz.trade.highchart-metrics)

(defn trade-chart [{:keys [trades bars stops tps]} indicator-key]
  {:rangeSelector {:selected 1}
   :chart         {:height 600}
   :navigator     {:enabled true}
   :tooltip {:split true :shared true}
   :xAxis {:crosshair {:snap true}}
   :yAxis [{:height    "40%"
            :crosshair {:snap false}}
           {:height    "40%"
            :top       "50%"
            :crosshair {:snap false}
            :plotLines [{:value     30
                         :color     "blue"
                         :width     2
                         :dashStyle "shortdash"}
                        {:value     70
                         :color     "red"
                         :width     2
                         :dashStyle "shortdash"}]}]

   :series  [{:type         "candlestick"
              :name         "price"
              :data         (map (juxt :date :open :high :low :close :volume) bars)
              :id           "priceseries"
              :dataGrouping {:enabled false}}
             {:type         "line"
              :name         (name indicator-key)
              :linkedTo     "priceseries"
              :data         (->> bars (map (juxt :date indicator-key)))
              :yAxis        1
              :dataGrouping {:enabled false}}]})

#_(when stops
    {:type         "line"
     :name         "stop"
     :data         stops
     :dataGrouping {:enabled false}
     :yAxis        0
     :color        "black"})

#_(when tps {:type         "line"
             :name         "profit target"
             :data         tps
             :dataGrouping {:enabled false}
             :yAxis        0
             :color        "black"})

#_:plotBands #_(for [{:keys [side] :as trade} trades]
                 {:color
                  (cond
                    (and (= side :long)
                         (win? trade))
                    "rgba(0, 0, 255, 0.50)"
                    (= side :long)

                    "rgba(0, 0, 255, 0.10)"
                    (and (= side :short)
                         (win? trade))
                    "rgba(255, 0, 0, 0.50)"
                    (= side :short)
                    "rgba(255, 0, 0, 0.10)")
                  :from (:entry-time trade)
                  :to   (:exit-time trade)})

(defn performance-chart [{:keys [trades bars]}]
  (let [bars       bars
        price-data     (mapv (juxt :date :open :high :low :close :vol) bars)
        ixs            (mapv :date bars)
        cash-flow      [] ; (cash-flow bars trades)
        cash-flow-data (map vector ixs cash-flow)
        peaks          (reductions max cash-flow)
        drawdowns      (map (fn [p x] (/ (- p x) p))
                            peaks
                            cash-flow)
        max-drawdowns  (reductions max drawdowns)
        drawdowns-data     (map vector ixs drawdowns)
        max-drawdowns-data (map vector ixs max-drawdowns)]
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
                      :dataGrouping {:enabled false}}]}))
