(ns wh-test
  (:require
   [clojure.test :refer :all]
   [ta.random :refer [random-ts]]
   [ta.warehouse :as wh]))

(def w (wh/init {:series "/tmp/"
                 :list "../resources/etf/"}))


(deftest test-symbollist
  (let [symbol "fidelity-select"
        l (wh/load-list w symbol)]
    (is (= (count l) 41))))



(def ts1 (random-ts 2000))
(def ts2 (random-ts 2000))

(deftest test-wh
  (let [symbol1 "_test1_"
        symbol2 "_test2_"
        _ (wh/save-ts w ts1 symbol1)
        _ (wh/save-ts w ts2 symbol2)
        r2 (wh/load-ts w symbol2)
        r1 (wh/load-ts w symbol1)]
    (is (= (count ts1) (count r1)))
    (is (= (count ts2) (count r2)))
    (is (= ts1 r1))
    (is (= ts2 r2))))