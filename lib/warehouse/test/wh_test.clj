(ns wh-test
  (:require
   [clojure.test :refer :all]
   [ta.random :refer [random-ts]]
   [ta.warehouse :as wh]))

(wh/init-tswh {:series "/tmp/"})

(def ts (random-ts 2000))

(deftest test-wh
  (let [symbol "_test_"
        _ (wh/save-ts ts symbol)
        ts2 (wh/load-ts symbol)]
    (is (= (count ts) (count ts2)))
    (is (= ts ts2))
    
    ))