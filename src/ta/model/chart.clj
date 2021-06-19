(ns ta.model.chart
  (:require
   [clojure.walk :refer [prewalk]]
   ;[cheshire.core :as json]
   ;[cheshire.generate :as json-gen]
  ; [trateg.core :refer :all]
   ;[ta.model.stats :refer [win? cash-flow]]
   ))

;;highcharts uses epoch millis for times
;(json-gen/add-encoder java.time.ZonedDateTime
;                      (fn [zdt gen] (.writeNumber gen (-> zdt .toInstant .toEpochMilli str))))


(defn zoned-time-to-epoch-milli [zdt]
  (-> zdt .toInstant .toEpochMilli))

(defn replace-ZonedDateTime
  "replaces type ZonedDateTime to epoch-with-milliseconds"
  [spec]
  (prewalk
   (fn [x]
     (if (= java.time.ZonedDateTime (type x))
       (zoned-time-to-epoch-milli x)
       x))
   spec))

(defn view-highchart [specs]
  (let [;spec (->> specs json/encode)
        spec-safe (replace-ZonedDateTime specs)
        ;_ (println "safe spec: " spec-safe)
        ]
    ^:R [:highchart spec-safe]))

(defn trade-chart [{:keys [trades bars stops tps]} indicator-key]
  (view-highchart
   {:rangeSelector {:selected 1}
    :chart         {:height 600}
    :navigator     {:enabled true}
    :tooltip {:split true :shared true}
    :xAxis         {:crosshair {:snap true}}
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
               :dataGrouping {:enabled false}}]}))


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