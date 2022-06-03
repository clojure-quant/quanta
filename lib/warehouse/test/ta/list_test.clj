(ns ta.list-test
  (:require
   [clojure.test :refer :all]
   [ta.warehouse.symbollist :refer [load-list load-list-full]]
   [ta.config]))

(deftest test-symbollist
  (let [symbol "fidelity-select"
        l (load-list symbol)]
    ;(println "list: " (pr-str l))
    (is (= (count l) 41))))

(deftest list-test
  (let [l-test (load-list-full "test")
        l-test-r (load-list-full "test-recursive")]
    (println "list full: " (pr-str l-test-r))
    (is (= (count l-test-r) (inc (count l-test))))))
