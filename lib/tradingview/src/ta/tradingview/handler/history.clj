(ns ta.tradingview.handler.history
  (:require
   [clojure.walk]
   [ring.util.response :as res]
   [ta.tradingview.db.bars :refer [load-series]]))

(defn history-handler [{:keys [query-params] :as req}]
  (let [{:keys [symbol resolution from to]} (clojure.walk/keywordize-keys query-params)
        from (Long/parseLong from) ;(Integer/parseInt from)
        to (Long/parseLong to) ; (Integer/parseInt to)
        series (load-series symbol resolution from to)]
    (res/response series)))

(comment

  ; test load known symbol
  (history-handler {:query-params {:symbol "ETHUSD"
                                   :from "1617235200"
                                   :to "1619827200"
                                   :resolution "D"}})

  ; test for unknown symbol
  (history-handler {:query-params {:symbol "XXXX"
                                   :from "1617235200"
                                   :to "1619827200"
                                   :resolution "D"}})

  (history-handler {:query-params {:symbol "Unknown-"
                                   :from "1617235200"
                                   :to "1619827200"
                                   :resolution "D"}})

  (history-handler {:query-params   {"symbol" "BTCUSD"
                                     "resolution" "D"
                                     "from" "1299075015"
                                     "to" "1303308614"}})
;
  )