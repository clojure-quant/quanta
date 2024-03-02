(ns ta.import.provider.bybit.raw
  (:require
   [taoensso.timbre :refer [info]]
   [tick.core :as t]
   [cljc.java-time.instant :as inst]
   [de.otto.nom.core :as nom]
   [cheshire.core :as cheshire] ; JSON Encoding
   [ta.import.helper :refer [str->float http-get]]))

;; # Bybit api
;; The query api does NOT need credentials. The trading api does.
; https://bybit-exchange.github.io/docs/v5/announcement
;; https://www.bybit.com/
;; https://bybit-exchange.github.io/docs/spot/#t-introduction
;; https://bybit-exchange.github.io/bybit-official-api-docs/en/index.html#operation/query_symbol
;; Intervals:  1 3 5 15 30 60 120 240 360 720 "D" "M" "W" "Y"
;; limit:      less than or equal 200

(defn epoch-millisecond->instant [epoch-ms]
  (-> epoch-ms
      inst/of-epoch-milli))

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

(defn http-get-json [url query-params]
  (nom/let-nom> [res (http-get url query-params)
                 {:keys [status headers body]} res]
                (info "status:" status "headers: " headers)
                (cheshire/parse-string body true)))


(defn get-history-request
  "makes rest-call to binance to return bar-seq or nom-anomaly
   on error. 
   query-params keys:
   symbol: BTC, ....
   interval: #{ 1 3 5 15 30 60 120 240 360 720 \"D\" \"M\" \"W\" \"Y\"}  
   start: epoch-millisecond
   start: epoch-millisecond
   limit: between 1 and 200 (maximum)"
  [query-params]
  (info "get-history: " query-params)
  (nom/let-nom>
   [res (http-get "https://api.bybit.com/v5/market/kline" query-params)
    {:keys [status headers body]} res
    result (cheshire/parse-string body true)]
   (info "status: " status "headers: " headers)
   (if (= (:retCode result) 0)
     (let [bar-seq (parse-history result)]
       (if (> (count bar-seq) 0)
         bar-seq
         (nom/fail ::bybit-get-history {:message "bar-seq has count 0"
                                        :query-params query-params})))
     (nom/fail ::bybit-get-history {:message "returnCode is not 0"
                                    :ret-code (:retCode result)
                                    :query-params query-params}))))

(comment

  (require '[tick.core :as t])
  (def start-date-daily (t/instant "2018-11-01T00:00:00Z"))

  (epoch-millisecond->instant 1669852800000)

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

{"X-Cache" "Miss from cloudfront",
 "Server" "Openresty",
 "Via" "1.1 b920186f8b4bb4541e72f9e499a32dd0.cloudfront.net (CloudFront)",
 "Content-Type" "application/json",
 "Content-Length" "275",
 "Connection" "close",
 "X-Amz-Cf-Pop" "MIA3-C5",
 "Timenow" "1709397001926",
 "Ret_code" "0",
 "x-cld-src" "Loc-A",
 "Date" "Sat, 02 Mar 2024 16:30:01 GMT",
 "Traceid" "2b76140e45e0b2211bd94bf1b63c2a45",
 "X-Amz-Cf-Id" "MBgy-7sAwuLvoC4bcuIeYOSYg4z6tUC3SNkkmlQRr8GncTg5fatMow=="}