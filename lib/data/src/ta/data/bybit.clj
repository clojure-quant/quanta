(ns ta.data.bybit
  (:require
   [taoensso.timbre :refer [info]]
   [clj-http.client :as http]
   [cheshire.core :as cheshire] ; JSON Encoding
   [cljc.java-time.instant :as inst]
   [cljc.java-time.local-date-time :as ldt]
   [cljc.java-time.zone-offset :refer [utc]]
   [ta.data.helper :refer [str->float remove-last-bar-if-timestamp-equals]]))

(defn epoch-millisecond->datetime [epoch-ms]
  (-> epoch-ms
      inst/of-epoch-milli
      (ldt/of-instant utc)))

(defn ->epoch-millisecond [dt]
  (-> dt
      (ldt/to-instant utc)
      inst/to-epoch-milli))

(comment
  (epoch-millisecond->datetime 1669852800000)
  (Long/parseLong "1693180800000")

   (require '[tick.core :as tick]) 
  (-> (tick/date-time "2018-11-01T00:00:00")
      ->epoch-millisecond
      epoch-millisecond->datetime
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
  {:date (-> open-time Long/parseLong epoch-millisecond->datetime) ; we work with local-datetime
   :open (str->float open)
   :high (str->float high)
   :low (str->float low)
   :close (str->float close)
   :volume (str->float volume)
   :turnover (str->float turnover)
   }))

(defn- parse-history [result]
  (->> result
       (:result)
       (:list)
       (map convert-bar)
       ))

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


(defn get-history-page
  [query-params]
   (get-history-request 
    (update query-params :start ->epoch-millisecond)))

(defn get-history
  "gets history since timestamp.
   joins multiple pages
   works nicely to request new bars to add to existing series."
  [query-params]
  (let [page-no-limit 1000
        page-no (atom 0)
        page-size 1000 ; for testing this might be reduced
        total (atom '())
        last-page-date (atom nil)]
    (loop [page-start (:start query-params)]
      (let [query-params (assoc query-params 
                                :start page-start
                                :limit page-size)
            page-series (get-history-page query-params)
            current-page-size  (count page-series)
            page-last-date (-> page-series first :date)
            page-series (remove-last-bar-if-timestamp-equals page-series @last-page-date)]
        (reset! last-page-date page-last-date)
        (swap! page-no inc)
        (swap! total (fn [t p]
                       (concat p t)) page-series)
        (when (and (= page-size current-page-size)
                   (< @page-no page-no-limit))
          (recur page-last-date))))
    @total))



(comment 
  
  (require '[tick.core :as tick]) 
  (def start-date-daily (tick/date-time "2018-11-01T00:00:00"))

   (-> (get-history-request
          {:symbol "BTCUSD"
           :start 1669852800000
           :interval "D"
           :limit 200})
       (count))

  (-> (get-history-request
         {:symbol "BTCUSD"
          :start (-> "2018-11-01T00:00:00" tick/date-time ->epoch-millisecond)
          :interval "D"
          :limit 200})
      count)
  

   (-> (get-history-page
        {:symbol "BTCUSD"
         :start (tick/date-time "2018-11-01T00:00:00") ;start-date-daily
         :interval "D"
         :limit 200
         :page 1
         })
       first
       ;count
       )

  (epoch-millisecond->datetime 1687046400000)

  (require '[clojure.pprint :refer [print-table]])

   (-> 
    (get-history {:symbol "BTCUSD"
                  :start (tick/date-time "2018-11-01T00:00:00") 
                  :interval "D"
                  :limit 200
                  })
    print-table)
   
  ;
  )