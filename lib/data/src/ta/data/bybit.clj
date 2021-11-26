(ns ta.data.bybit
  (:require
   [taoensso.timbre :refer [info]]
   [clj-http.client :as http]
   [cheshire.core :as cheshire] ; JSON Encoding
   [ta.helper.date :refer [epoch-second->datetime ->epoch-second]]
   [ta.data.helper :refer [str->float remove-first-bar-if-timestamp-equals]]))

;; # Bybit api
;; The query api does NOT need credentials. The trading api does.
;; https://www.bybit.com/
;; https://bybit-exchange.github.io/docs/spot/#t-introduction
;; https://bybit-exchange.github.io/bybit-official-api-docs/en/index.html#operation/query_symbol
;; Intervals:  1 3 5 15 30 60 120 240 360 720 "D" "M" "W" "Y"
;; limit:      less than or equal 200

(defn- convert-bar [bar]
  {:date (epoch-second->datetime (:open_time bar)) ; we work with local-datetime
   :open (str->float (:open bar))
   :high (str->float (:high bar))
   :low (str->float (:low bar))
   :close (str->float (:close bar))
   :volume (str->float (:volume bar))})

(defn- parse-history [result]
  (->> result
       (:result)
       (map convert-bar)))

(defn get-history-page-from-epoch-second
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

(defn get-history-page
  [interval from limit symbol]
  (info "bybit get page " symbol interval from)
  (get-history-page-from-epoch-second interval (->epoch-second from) limit symbol))

(defn get-history
  "gets history since timestamp.
   joins multiple pages
   works nicely to request new bars to add to existing series."
  [interval from symbol]
  (let [page-no-limit 1000
        page-no (atom 0)
        page-size 200 ; for testing this might be reduced
        total (atom '())
        last-page-date (atom nil)]
    (loop [page-from from]
      (let [page-series (get-history-page interval page-from page-size symbol)
            current-page-size  (count page-series)
            page-last-date (-> page-series last :date)
            page-series (remove-first-bar-if-timestamp-equals page-series @last-page-date)]
        (reset! last-page-date page-last-date)
        (swap! page-no inc)
        (swap! total concat page-series)
        (when (and (= page-size current-page-size)
                   (< @page-no page-no-limit))
          (recur page-last-date))))
    @total))

(comment

  (get-history-page "D" (ta.helper.date/days-ago 10) 3 "ETHUSD")

  (require '[clojure.pprint])
  (-> (get-history-page "D" (ta.helper.date/days-ago 10) 3 "ETHUSD")
      (clojure.pprint/print-table))

  (-> (get-history "D" (ta.helper.date/days-ago 20) "ETHUSD")
      (clojure.pprint/print-table))

;
  )
