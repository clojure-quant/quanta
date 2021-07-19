(ns demo.datasource.bybit
  (:require
   [clojure.pprint :refer [print-table]]
   ;[medley.core :as m]
   [tick.alpha.api :as t] ; tick uses cljc.java-time
   [cljc.java-time.instant :as ti]
   [ta.data.bybit :as bybit]))

;; # Bybit api
;; The query api does NOT need credentials. The trading api does.
;; https://www.bybit.com/
;; https://bybit-exchange.github.io/docs/spot/#t-introduction
;; https://bybit-exchange.github.io/bybit-official-api-docs/en/index.html#operation/query_symbol
;; Intervals:  1 3 5 15 30 60 120 240 360 720 "D" "M" "W" "Y"
;; limit:      less than or equal 200

(defn days-ago [n]
  (-> (t/now)
      (t/- (t/new-duration n :days))
        ;(t/date)
      ))

(days-ago 7)

; some tests to generate dates
;(tc/to-long (-> 2 t/hours t/ago))
;(-> 2 t/hours t/ago)

;; # get data - very raw format

(def since-epoch-sec
  (-> 7
      days-ago
      ti/get-epoch-second))

(-> (bybit/history-page "D" since-epoch-sec 200 "ETHUSD")
    (print-table))

;; # after here it does not work 
;; need to refactor all work to eech-sec
;; and use the best datatype for requests

;; # get data
(-> (bybit/history-page "15" (-> 2 t/hours t/ago) 5 "ETHUSD")
    (print-table))

(-> (bybit/history-recent "BTCUSD" 10)
    (print-table))

(-> (bybit/history-recent-extended "BTCUSD" 500)
    (print-table))

; figure out how costly requests are 
(bybit/requests-needed 950)

