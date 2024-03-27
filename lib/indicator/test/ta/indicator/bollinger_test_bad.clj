(ns ta.indicator.bollinger-test-bad
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator.band :as band])
    (:import [org.ta4j.core BaseStrategy #_BaseTimeSeries$SeriesBuilder
                  ;TimeSeriesManager
            ])s)


; ta4j calculates bollinger via facade; 
; we dont yet have wrappers for facade.

(deftest test-bollinger
  (is (all-fuzzy= 0.1
        (ta4j/bar ds :ATR 4)
        (->> (band/add-bollinger {:n 4 :m 2.0} ds) (into []))
        ;(-> (ind/atr-mma {:n 4} ds) (round))
       )))


(comment 
  (-> (band/add-bollinger {:n 4 :m 2.0} ds)
      :bollinger-lower)

  (->> (ind/atr {:n 4} ds)
      (into []))

  (ind/sma {:n 4} [30.0 30.0 40.0 40.0])
    
  (->> (ind/atr-mma {:n 4} ds)
       (into [])
   )
  
   (ta4j/bar ds :ATR 4)
  
;  
  )
               

