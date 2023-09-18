(ns notebook.gann.window
  (:require
   [clojure.pprint]
   [ta.helper.date :refer [parse-date now-datetime]]
   [ta.gann.window :as window]
   [goldly.scratchpad :refer [show! show-as clear!]]))

(window/get-gann-boxes {:s "BTCUSD"})

(window/get-gann-boxes {:s "BAD666"})

(window/get-gann-data {:s "GLD"})

(window/get-gann-data {:s "GLD" :dt-start "2020-01-01"})

(window/get-gann-data {:s "BAD"})

(-> (window/get-gann-boxes {:s "GLD"
                            :wh :stocks
                            :dt-start  "1990-01-01"
                            :dt-end "2022-03-31"})
    (clojure.pprint/print-table))