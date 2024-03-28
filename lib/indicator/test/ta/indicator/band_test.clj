(ns ta.indicator.band-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [nthrest-fuzzy=]]
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

(defn bands-fuzzy= [n ds ta4j]
  (let [cols [:middle :lower :upper]]
    ;(and
     (nthrest-fuzzy= n (get-series-ds ds :middle) (:middle ta4j))
     ;(all-fuzzy= (get-series-ds ds :lower) (:lower ta4j))
     ;(all-fuzzy= (get-series-ds ds :upper) (:upper ta4j))
    ; )
  ))

;; tests

(deftest test-keltner
  (is 
   (bands-fuzzy= 3
     (band/add-keltner {:n 20 :k 2.0 :pre "band"} ds)
     (ta4j/facade-bar ds :keltner/KeltnerChannel [:middle :lower :upper] 20 20 2))))


(deftest test-bollinger
  (is 
    (bands-fuzzy= 3
       (band/add-bollinger {:n 3 :k 2.0 :pre "band"} ds)
       (ta4j/facade-bar ds :bollinger/BollingerBand [:middle :lower :upper] 3 2))))

(comment 
 
   (bands-fuzzy= 3
     (band/add-keltner {:n 20 :k 2.0 :pre "band"} ds)
     (ta4j/facade-bar ds :keltner/KeltnerChannel [:middle :lower :upper] 20 20 2))

  (->  (band/add-keltner {:n 20 :k 2.0 :pre "band"} ds)
       :band-lower
   )
  ;; => #tech.v3.dataset.column<float64>[14]
  ;;    :band-lower
  ;;    [39.87, 39.95, 40.78, 42.91, 46.07, 49.07, 50.49, 50.01, 49.50, 47.50, 47.10, 
  ;;     46.83, 46.65, 46.52]

  (->  (ta4j/facade-bar ds :keltner/KeltnerChannel [:middle :lower :upper] 20 20 2)
       :lower
       )
   
  (bands-fuzzy= 3
     (band/add-bollinger {:n 3 :k 2.0 :pre "band"} ds)
     (ta4j/facade-bar ds :bollinger/BollingerBand [:middle :lower :upper] 3 2))

 
   (-> (band/add-bollinger {:n 3 :k 2.0 :pre "band"} ds)
       :band-mid
    )
  

   (->  (ta4j/facade-bar ds :bollinger/BollingerBand [:middle :lower :upper] 3 2)
        :middle
        )
   
;  
  )
               

