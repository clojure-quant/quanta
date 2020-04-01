#_(ns ta.series.compress-test
  (:require
   [clojure.test :refer :all]
   [ta.data.csv :refer [load-csv-bars]]
   [clj-time.core :as t]
   [ta.series.compress :refer [compress group-month]]))

(comment

  (def spx-bars  (load-csv-bars "ta/spx.csv"))


  (->> spx-bars
       (take 1)
       ;(compress group-month)
       ;(take 2)
       #_(doall))


  ;comment
  )

