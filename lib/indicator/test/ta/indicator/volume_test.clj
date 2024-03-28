(ns ta.indicator.volume-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator.volume :as vind]))

;; TESTS

(deftest test-tr
  (is (all-fuzzy=
       0.1
       (ta4j/bar ds :volume/VWAP 2)
        (vind/vwap 2 ds))))


(comment
  (:close ds)

   (ta4j/bar ds :volume/VWAP 2)
   (vind/vwap 2 ds)


;
)