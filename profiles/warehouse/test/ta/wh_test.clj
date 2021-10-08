(ns ta.wh-test
  (:require
   [clojure.test :refer :all]
   [ta.random :refer [random-ts]]
   [ta.warehouse :as wh]
   [ta.config :refer [w]]))

(deftest test-symbollist
  (let [symbol "fidelity-select"
        l (wh/load-list w symbol)]
    (is (= (count l) 41))))


(defn series-generate-save-reload 
  [size name]
    (let [ts-original (random-ts size) 
          symbol (str "_test_" name "_")
          _ (wh/save-ts w ts-original symbol)
          ts-reloaded (wh/load-ts w symbol)]
       (is (= (count ts-original) (count ts-reloaded)))
   (is (= ts-original ts-reloaded))  
  ))



(deftest test-wh
  (series-generate-save-reload 2000 "small")
  (series-generate-save-reload 20000 "big")) ; tradingview limit
  



