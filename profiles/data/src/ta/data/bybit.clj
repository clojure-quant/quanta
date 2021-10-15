(ns ta.data.bybit
  (:require
   [taoensso.timbre :refer [info]]
   [clj-http.client :as http]
   [cheshire.core :as cheshire] ; JSON Encoding
   [cljc.java-time.instant :as ti]
   [tick.alpha.api :as t] ; tick uses cljc.java-time
   [ta.data.date :as d]
   [ta.data.helper :as h]))

;; # Bybit api
;; The query api does NOT need credentials. The trading api does.
;; https://www.bybit.com/
;; https://bybit-exchange.github.io/docs/spot/#t-introduction
;; https://bybit-exchange.github.io/bybit-official-api-docs/en/index.html#operation/query_symbol
;; Intervals:  1 3 5 15 30 60 120 240 360 720 "D" "M" "W" "Y"
;; limit:      less than or equal 200

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
  [interval from-ti limit symbol]
  (info "bybit get page " symbol interval from-ti)
  (get-history-page-from-epoch-second interval (ti/get-epoch-second from-ti) limit symbol))

(defn get-history
  "gets history since timestamp.
   joins multiple pages
   works nicely to request new bars to add to existing series."
  [interval from-ti symbol]
  (let [page-no-limit 1000
        page-no (atom 0)
        page-size 200 ; for testing this might be reduced
        total (atom '())
        last-page-date (atom nil)]
    (loop [page-from-ti from-ti]
      (let [page-series (get-history-page interval page-from-ti page-size symbol)
            current-page-size  (count page-series)
            page-last-date (-> page-series last :date)
            page-series (h/remove-first-bar-if-timestamp-equals page-series @last-page-date)]
        (reset! last-page-date page-last-date)
        (swap! page-no inc)
        (swap! total concat page-series)
        (when (and (= page-size current-page-size)
                   (< @page-no page-no-limit))
          (recur page-last-date))))
    @total))

(comment

  (get-history-page "D" (d/days-ago 10) 3 "ETHUSD")

  (require '[clojure.pprint])
  (-> (get-history-page "D" (d/days-ago 10) 3 "ETHUSD")
      (clojure.pprint/print-table))

  ; does not remove
  (h/remove-first-bar-if-timestamp-equals
   [{:date (ti/now)}]
   nil)

  ; removes
  (h/remove-first-bar-if-timestamp-equals
   [{:date (t/instant "1999-12-31T00:00:00Z")}]
   (t/instant "1999-12-31T00:00:00Z"))

  (-> (get-history "D" (d/days-ago 20) "ETHUSD")
      (clojure.pprint/print-table))

;
  )
