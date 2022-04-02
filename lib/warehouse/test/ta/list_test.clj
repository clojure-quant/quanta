(ns ta.list-test
  (:require
   [clojure.test :refer :all]
   [ta.warehouse :as wh]
   [ta.config]))


(deftest test-symbollist
  (let [symbol "fidelity-select"
        l (wh/load-list symbol)]
    ;(println "list: " (pr-str l))
    (is (= (count l) 41))))

(deftest list-test
  (let [l-test (wh/load-list-full "test")
        l-test-r (wh/load-list-full "test-recursive")]
    (println "list full: " (pr-str l-test-r))
    (is (= (count l-test-r) (inc (count l-test))))
  
  ))
 