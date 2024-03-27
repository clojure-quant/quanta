(ns ta.indicator.atr-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator :as ind]))

(deftest test-tr
  (is (all-fuzzy=
       (ta4j/bar ds :helpers/TR)
       (ind/tr ds))))

(deftest test-atr
  (is (all-fuzzy= 0.1
        (ta4j/bar ds :ATR 4)
        (->> (ind/atr-mma {:n 4} ds) (into []))
       )))


(comment 
  (ind/tr ds)

  (->> (ind/atr {:n 4} ds)
      (into []))

  (ind/sma {:n 4} [30.0 30.0 40.0 40.0])
    
  (->> (ind/atr-mma {:n 4} ds)
       (into [])
   )
  
   (ta4j/bar ds :ATR 4)
  
;  
  )
               

