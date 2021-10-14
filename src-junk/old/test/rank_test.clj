(ns ta.rank-test
  (:require
   [clojure.test :refer :all]
   [ta.model.rank :refer [rank]]))

(def data-
  (atom {:symbols
         {:a {:price [100 105 110]}
          :b {:price [105 110 109]}
          :c {:price [110 104 103]}}}))

(def expected-
  {:symbols {:a {:price [100 105 110], :rank [3 2 1]}
             :b {:price [105 110 109], :rank [2 1 2]}
             :c {:price [110 104 103], :rank [1 3 3]}}})

(deftest test-rank
  (is (= expected- (rank data- 3 :price false

                         :rank))))



