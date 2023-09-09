(ns demo.algo.joseph
  (:require
   [tablecloth.api :as tc]
   [tick.core :as t]
   [ta.helper.date :refer [parse-date epoch-second->datetime ->epoch-second ]]
   [joseph.trades :refer [load-trades]]
   [ta.algo.manager :refer [add-algo]]
   [ta.tradingview.chart.plot :refer [plot-type linestyle]]
   [ta.tradingview.chart.shape :as shapes2] 
   [ta.tradingview.chart.color :refer [color]]
   ))



(defn trade-filter-symbol [symbol trades]
  (filter #(= (:symbol %) symbol) trades))

(defn inside-window? [window-start window-end date]
  (and (t/>= date window-start)
       (t/<= date window-end)))

(defn trade-in-window [{:keys [entry-date exit-date]} window-start window-end]
  ( or (inside-window? window-start window-end entry-date)
       (inside-window? window-start window-end exit-date)))

(defn trade-filter-window [window-start window-end trades]
   (filter #(trade-in-window % window-start window-end) trades))

(comment 
  
  (->> (load-trades)
       (trade-filter-symbol "PLTR")
       (trade-filter-window (parse-date "2021-01-01") (parse-date "2023-05-30"))
       count)

  (->> (load-trades) (trade-filter-symbol "QQQ") count)
  (->> (load-trades) (trade-filter-symbol "SPY") count)
;
)

#(defn calc-joseph-signal []
    :hold)

(defn joseph-signal [ds-bars options]
    ds-bars
    )


(defn shapes-for-trade [{:keys [direction entry-date exit-date entry-price exit-price] :as trade}]
  [(shapes2/line-vertical entry-date {:text (str direction)
                                      :linecolor (if (= direction :long)
                                                     (color :lightgreen)
                                                     (color :lightsalmon))}) 
   (shapes2/line-vertical exit-date {:text "close"
                                     :linecolor (color :lightslategray)})
   (shapes2/trend-line {:time entry-date :price entry-price}
                       {:time exit-date :price exit-price}
                       {:linecolor (if (= direction :long)
                                     (color :lightgreen)
                                     (color :lightsalmon))
                        :linestyle (linestyle :dashed)
                        :text (str direction)
                        :showLabel true
                        :leftEnd 1
                        :rightEnd 1}
                       )])

(defn roundtrip-shapes [options epoch-start epoch-end]
  (println "roundtripshapes options: " options "epoch-start: " epoch-start "epoch-end: " epoch-end)
  (let [trades (load-trades)
        start (epoch-second->datetime epoch-start)
        end   (epoch-second->datetime epoch-end)
        trades (->> trades
                    (trade-filter-symbol (:symbol options))
                    (trade-filter-window start end))
        trade-shapes (->> (mapcat shapes-for-trade trades)
                          (into []))
        ]
    (println "roundtrip-shapes start: " start " end: " end " trades: " (count trades))
    trade-shapes
  ))

(comment 
   (roundtrip-shapes 
    {:symbol "QQQ"}
    (->epoch-second  (d "2020-01-01"))
    (->epoch-second  (d "2024-01-01")))
  ;
  )
                  

(add-algo
 {:name "joseph"
  :comment "vizualising trades of joseph"
  :algo joseph-signal
  :charts [;{:trades "line"}
           #_{:volume {:type "line"
                     :plottype (plot-type :columns)
                     }}
           ]
  :shapes roundtrip-shapes
  :options {:symbol "QQQ"
            :frequency "D"}})