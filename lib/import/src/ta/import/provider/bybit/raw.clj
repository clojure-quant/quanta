(ns ta.import.provider.bybit.raw
  (:require
   [taoensso.timbre :refer [info]]
   [clj-http.client :as http]
   [cheshire.core :as cheshire] ; JSON Encoding
   [cljc.java-time.instant :as inst]
   [cljc.java-time.local-date-time :as ldt]
   [cljc.java-time.zone-offset :refer [utc]]
   [ta.import.helper :refer [str->float remove-last-bar-if-timestamp-equals]]
   [tick.core :as t]))

(defn epoch-millisecond->instant [epoch-ms]
  (-> epoch-ms
      inst/of-epoch-milli
      ;(ldt/of-instant utc)
      ))

(def inst-class (class (t/instant)))

(defn ->epoch-millisecond [dt]
  (let [dt-instant (if (= inst-class (class dt))
                     dt
                     (ldt/to-instant dt utc))]
    (inst/to-epoch-milli dt-instant)))

(comment
  inst-class
  (= inst-class (class (t/instant)))
  (= inst-class (class (t/date-time)))
  (epoch-millisecond->instant 1669852800000)
  (Long/parseLong "1693180800000")

  (require '[tick.core :as t])
  (-> (t/date-time "2018-11-01T00:00:00")
      ;(t/instant)
      ->epoch-millisecond
      ;epoch-millisecond->datetime
      )
 ; 
  )

;; # Bybit api
;; The query api does NOT need credentials. The trading api does.
; https://bybit-exchange.github.io/docs/v5/announcement
;; https://www.bybit.com/
;; https://bybit-exchange.github.io/docs/spot/#t-introduction
;; https://bybit-exchange.github.io/bybit-official-api-docs/en/index.html#operation/query_symbol
;; Intervals:  1 3 5 15 30 60 120 240 360 720 "D" "M" "W" "Y"
;; limit:      less than or equal 200

(defn- convert-bar [bar]
  ;; => ["1693180800000" "26075" "26075.5" "25972.5" "25992" "6419373" "246.72884245"]
  (let [[open-time open high low close volume turnover] bar]
    {:date (-> open-time Long/parseLong epoch-millisecond->instant)
     :open (str->float open)
     :high (str->float high)
     :low (str->float low)
     :close (str->float close)
     :volume (str->float volume)
     :turnover (str->float turnover)}))

(defn- parse-history [result]
  (->> result
       (:result)
       (:list)
       (map convert-bar)))

(defn get-history-request
  "gets crypto price history
   symbol: BTC, ....
   interval: #{ 1 3 5 15 30 60 120 240 360 720 \"D\" \"M\" \"W\" \"Y\"}  
   start: epoch-millisecond
   limit: between 1 and 200 (maximum)"
  [query-params]
  (info "get-history: " query-params)
  (let [result (-> (http/get "https://api.bybit.com/v5/market/kline"
                             {:accept :json
                              :query-params query-params})
                   (:body)
                   (cheshire/parse-string true))]
    (if (= (:retCode result) 0)
      (parse-history result)
      (throw (ex-info (:retMsg result) result)))))



(comment

  (require '[tick.core :as t])
  (def start-date-daily (t/instant "2018-11-01T00:00:00Z"))

  (-> (get-history-request
       {:symbol "BTCUSD"
        :start 1669852800000
        :interval "D"
        :limit 3})
      (count))

  (-> (get-history-request
       {:symbol "BTCUSD"
        :start (-> "2024-01-29T00:00:00Z" t/instant t/long (* 1000))
        :end (-> "2024-01-29T00:05:00Z" t/instant t/long (* 1000))
        :interval "1"
        :limit 3})
      count)
  ; first row is the LAST date.
  ; last row is the FIRST date
  ; if result is more than limit, then it will return LAST values first.

   
  (epoch-millisecond->instant 1687046400000)



;
  )