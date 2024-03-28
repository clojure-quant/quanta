(ns ta.indicator.band-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [nthrest-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator.band :as band]))

;; band helper

(defn bands-fuzzy= [n ds ta4j]
  (and
   (nthrest-fuzzy= n (:band-mid ds) (:middle ta4j))
   (nthrest-fuzzy= n (:band-lower ds) (:lower ta4j))
   (nthrest-fuzzy= n (:band-upper ds) (:upper ta4j))))

;; tests

(deftest test-keltner
  (is
   (bands-fuzzy=
    3
    (band/add-keltner {:n 20 :k 2.0 :pre "band"} ds)
    (ta4j/facade-bar ds :keltner/KeltnerChannel [:middle :lower :upper] 20 20 2))))

(deftest test-bollinger
  (is
   (bands-fuzzy=
    3
    (band/add-bollinger {:n 3 :k 2.0 :pre "band"} ds)
    (ta4j/facade-bar ds :bollinger/BollingerBand [:middle :lower :upper] 3 2))))

(comment

  (bands-fuzzy=
   3
   (band/add-keltner {:n 20 :k 2.0 :pre "band"} ds)
   (ta4j/facade-bar ds :keltner/KeltnerChannel [:middle :lower :upper] 20 20 2))

  (->  (band/add-keltner {:n 20 :k 2.0 :pre "band"} ds)
       :band-lower)

  (->  (ta4j/facade-bar ds :keltner/KeltnerChannel [:middle :lower :upper] 20 20 2)
       :lower)

  (bands-fuzzy= 3
                (band/add-bollinger {:n 3 :k 2.0 :pre "band"} ds)
                (ta4j/facade-bar ds :bollinger/BollingerBand [:middle :lower :upper] 3 2))

  (-> (band/add-bollinger {:n 3 :k 2.0 :pre "band"} ds)
      :band-mid)

  (->  (ta4j/facade-bar ds :bollinger/BollingerBand [:middle :lower :upper] 3 2)
       :middle)

;  
  )
               

