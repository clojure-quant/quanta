(ns ta.data.bybit
  (:require
   [clj-http.client :as client]
   [cheshire.core] ; JSON Encoding
   [clj-time.core :as t]
   [clj-time.coerce :as c]
   [clj-time.format :as fmt]))

; https://bybit-exchange.github.io/bybit-official-api-docs/en/index.html#operation/query_symbol

; Intervals:  1 3 5 15 30 60 120 240 360 720 "D" "M" "W" "Y"
; limit:      less than or equal 200

(defn to-epoch-no-ms- [date]
  (int (/ (c/to-long date) 1000)))

(defn to-date- [epoch-no-ms]
  (c/from-long (* epoch-no-ms 1000)))

(defn as-float [str]
  (if (nil? str)
    nil
    (Float/parseFloat str)))

(defn convert-bar [bar]
  {:date (to-date- (:open_time bar))
   :open (as-float (:open bar))
   :high (as-float (:high bar))
   :low (as-float (:low bar))
   :close (as-float (:close bar))
   :volume (as-float (:volume bar))})

(defn parse-history [result]
  (->> result
       (:result)
       (map convert-bar)))

(defn history [interval from limit symbol]
  (-> (client/get "https://api.bybit.com/v2/public/kline/list"
                  {:accept :json
                   :query-params {:symbol symbol
                                  :interval interval
                                  :from (to-epoch-no-ms- from)
                                  :limit limit}})
      (:body)
      (cheshire.core/parse-string true)
      (parse-history)))

(defn history-recent [symbol bars]
  (let [start (-> (* bars 15) t/minutes t/ago)]
    (history "15" start bars symbol)))

(defn requests-needed [bars]
  (let [remaining (atom bars)
        position (atom 0)
        requests (atom [])]
    (while (pos? @remaining)
      (let [current (min 200 @remaining)
            _ (println "cur: " current)]
        (do
          (swap! position + current)
          (swap! requests conj {:bars current :position @position})
          (swap! remaining - current))))
    @requests))

(defn history-recent-extended
  "gets recent history from bybit
  in case more than 200 bars (the maximum per request allowed by bybit) are needed,
  then multiple requests are made"
  [symbol bars]
  (let [requests (requests-needed bars)
        now (t/now)
        set-start-time #(assoc % :start-time (t/minus now (t/minutes (* 15 (:position %)))))]
    (->> requests
         (map set-start-time)
         (reverse)
         (map #(history "15" (:start-time %) (:bars %) symbol))
         (reduce concat []))))

(comment
  (c/to-long (-> 2 t/hours t/ago))

  (-> 2 t/hours t/ago)
  (-> (history "15" (-> 2 t/hours t/ago) 5 "ETHUSD")
      (clojure.pprint/print-table))

  (-> (history-recent "BTCUSD" 10)
      (clojure.pprint/print-table))

  (requests-needed 950)
  (clojure.pprint/print-table (history-recent-extended "BTCUSD" 500)))
