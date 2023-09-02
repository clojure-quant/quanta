(ns demo.data-import.demo-bybit
  (:require
   [clojure.pprint]
   [ta.data.bybit :refer [get-history-page]]))

(defn demo-bybit []
  ;(get-history-page "D" (ta.helper.date/days-ago 10) 3 "ETHUSD")
  (-> (get-history-page 
       {:symbol "ETHUSD"
        :frequency "D"
        :start (ta.helper.date/days-ago 10)
        :limit 3
        })
      (clojure.pprint/print-table))
  #_(-> (get-history "D" (ta.helper.date/days-ago 20) "ETHUSD")
        (clojure.pprint/print-table))

;
  )

(demo-bybit)