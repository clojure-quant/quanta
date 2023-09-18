(ns notebook.gann.chartmaker
  (:require
   [ta.helper.date :refer [parse-date now-datetime]]
   [ta.gann.chartmaker :as cm]
   [goldly.scratchpad :refer [show! show-as clear!]]))

(cm/make-boxes-symbols
 "2005-01-01" "2022-03-31"
 ["BTCUSD" "QQQ"])

(cm/make-boxes-all "2005-01-01" "2022-04-01")

(cm/make-boxes-all-individual "2000-01-01" "2022-04-01")

