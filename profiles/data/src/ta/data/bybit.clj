(ns ta.data.bybit
  (:require
   [clj-http.client :as http]
   [cheshire.core :as cheshire] ; JSON Encoding
   [cljc.java-time.instant :as ti]
   [tick.alpha.api :as t] ; tick uses cljc.java-time
   ))

; https://bybit-exchange.github.io/bybit-official-api-docs/en/index.html#operation/query_symbol

(defn to-epoch-second [date]
  (case (type date)
    java.time.Instant (ti/get-epoch-second date)
    ;java.time.LocalDate 
    nil))

(defn- str->float [str]
  (if (nil? str)
    nil
    (Float/parseFloat str)))

(defn- convert-bar [bar]
  {:date (ti/of-epoch-second (:open_time bar))
   :open (str->float (:open bar))
   :high (str->float (:high bar))
   :low (str->float (:low bar))
   :close (str->float (:close bar))
   :volume (str->float (:volume bar))})

(defn- parse-history [result]
  (->> result
       (:result)
       (map convert-bar)))

(defn history-page
  "gets crypto price history
   symbol: BTC, ....
   interval: #{ 1 3 5 15 30 60 120 240 360 720 \"D\" \"M\" \"W\" \"Y\"}  
   from: epoch-second
   limit: between 1 and 200 (maximum)"
  [interval from-epoch-second limit symbol]
  (-> (http/get "https://api.bybit.com/v2/public/kline/list"
                {:accept :json
                 :query-params {:symbol symbol
                                :interval interval
                                :from  from-epoch-second
                                :limit limit}})
      (:body)
      (cheshire/parse-string true)
      (parse-history)))

(defn requests-needed [bars]
  (let [remaining (atom bars)
        position (atom 0)
        requests (atom [])]
    (while (pos? @remaining)
      (let [current (min 200 @remaining)
            _ (println "cur: " current)]
        (swap! position + current)
        (swap! requests conj {:bars current :position @position})
        (swap! remaining - current)))
    @requests))

(defn history-recent-extended
  "gets recent history from bybit
  in case more than 200 bars (the maximum per request allowed by bybit) are needed,
  then multiple requests are made"
  [symbol bars]
  (let [requests (requests-needed bars)
        now (t/now)
        set-start-time #(assoc % :start-time (t/- now (t/minutes (* 15 (:position %)))))]
    (->> requests
         (map set-start-time)
         (reverse)
         (map #(history-page "15" (:start-time %) (:bars %) symbol))
         (reduce concat []))))

(defn history-recent [symbol bars]
  (let [start (-> (* bars 15) t/minutes t/ago)]
    (history-page "15" start bars symbol)))
