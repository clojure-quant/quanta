(ns ta.indicator.band-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator.band :as band]))

;; band helper

(defn band-kw [col]
  (keyword (str "band-" (name col))))

(defn get-series-ds [ds col]
  (let [col (if (= col :middle) :mid col) ; ta4j calls it :middle, we call it :mid
        kw (band-kw col)]
    (get ds kw)))

(defn bands-fuzzy= [ds ta4j]
  (let [cols [:middle :lower :upper]]
    ;(and
     (all-fuzzy= (get-series-ds ds :middle) (:middle ta4j))
     ;(all-fuzzy= (get-series-ds ds :lower) (:lower ta4j))
     ;(all-fuzzy= (get-series-ds ds :upper) (:upper ta4j))
    ; )
  ))

;; tests

(deftest test-keltner
  (is 
   (bands-fuzzy=
     (band/add-keltner {:n 20 :k 2.0 :pre "band"} ds)
     (ta4j/facade-bar ds :keltner/KeltnerChannel [:middle :lower :upper] 20 20 2))))


#_(deftest test-bollinger
  (is (all-fuzzy= 0.1
        (ta4j/bar ds :ATR 4)
        (->> (band/add-bollinger {:n 4 :m 2.0} ds) (into []))
        ;(-> (ind/atr-mma {:n 4} ds) (round))
       )))




(comment 
 
   (bands-fuzzy=
     (band/add-keltner {:n 20 :k 2.0 :pre "band"} ds)
     (ta4j/facade-bar ds :keltner/KeltnerChannel [:middle :lower :upper] 20 20 2))

  (bands-fuzzy= 
     (band/add-bollinger {:n 20 :k 2.0 :pre "band"} ds)
     (ta4j/facade-bar ds :bollinger/BollingerBand [:middle :lower :upper] 20 2))

 
  

  (-> (band/add-bollinger {:n 4 :m 2.0 :pre "band"} ds)
      ;(get-series-ds :lower)
      ;(get-series-ds :mid)
      (get-series-ds :middle)
   )
   

  
;  
  )
               

