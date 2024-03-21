(ns ta.tradingview.handler.asset
  (:require
   [clojure.walk]
   [ring.util.response :as res]
   [ta.tradingview.db.asset :refer [symbol-search symbol-info]]))

;https://demo_feed.tradingview.com/search?query=B&type=stock&exchange=NYSE&limit=10
;[{"symbol":"BLK","full_name":"BLK","description":"BlackRock, Inc.","exchange":"NYSE","type":"stock"},
;  {"symbol":"BA","full_name":"BA","description":"The Boeing Company","exchange":"NYSE","type":"stock"}]

(defn search-handler [{:keys [query-params] :as req}]
  (let [{:keys [query type exchange limit]} (clojure.walk/keywordize-keys query-params)
        limit (Integer/parseInt limit)
        result-tv (symbol-search query type exchange limit)]
    (res/response result-tv)))

(defn symbols-handler [{:keys [query-params] :as req}]
  (let [{:keys [symbol]} (clojure.walk/keywordize-keys query-params)
        si (symbol-info symbol)]
    (res/response si)))

(comment
  ; stocks should have :exchange SG :type Stocks
  ; crypto should have :exchange BB :type Crypto
  (symbol-info "MSFT")
  (symbol-info "ETHUSD")

  (-> (search-handler {:query-params {:query "B"
                                      :type ""
                                      :exchange ""
                                      :limit "10"}})
      :body
      ;count
      )
;
  )