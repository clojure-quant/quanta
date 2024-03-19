(ns ta.indicator.momentum-test
  (:require [clojure.test :refer :all]
            [ta.indicator.test-helper :refer [all-fuzzy=]]
            [tick.core :as tick]
            [tablecloth.api :as tc]
            [ta.indicator :refer [macd rsi]]
            [ta.indicator.signal :refer [upward-change downward-change]]
            [ta.indicator.ta4j.ta4j :refer [ind ind-values ds->ta4j-close]]))


;; init

(def ds
  (tc/dataset [{:date (tick/instant "2019-11-01T00:00:00.000Z") :open 100 :high 120 :low 90 :close 100 :volume 10023}
               {:date (tick/instant "2019-11-02T00:00:00.000Z") :open 100 :high 120 :low 90 :close 101 :volume 10050}
               {:date (tick/instant "2019-11-03T00:00:00.000Z") :open 101 :high 140 :low 90 :close 130 :volume 11000}
               {:date (tick/instant "2019-11-04T00:00:00.000Z") :open 130 :high 140 :low 100 :close 135 :volume 12000}
               {:date (tick/instant "2019-11-05T00:00:00.000Z") :open 135 :high 140 :low 110 :close 138 :volume 33000}
               {:date (tick/instant "2019-11-06T00:00:00.000Z") :open 138 :high 160 :low 120 :close 150 :volume 55000}
               {:date (tick/instant "2019-11-07T00:00:00.000Z") :open 119 :high 160 :low 100 :close 158 :volume 26000}
               {:date (tick/instant "2019-11-08T00:00:00.000Z") :open 158 :high 160 :low 120 :close 130 :volume 34000}
               {:date (tick/instant "2019-11-09T00:00:00.000Z") :open 130 :high 130 :low 90 :close 120 :volume 13000}
               {:date (tick/instant "2019-11-10T00:00:00.000Z") :open 120 :high 140 :low 90 :close 130 :volume 14000}
               {:date (tick/instant "2019-11-11T00:00:00.000Z") :open 130 :high 150 :low 90 :close 125 :volume 15000}
               {:date (tick/instant "2019-11-12T00:00:00.000Z") :open 125 :high 130 :low 90 :close 120 :volume 12000}
               {:date (tick/instant "2019-11-13T00:00:00.000Z") :open 120 :high 120 :low 90 :close 110 :volume 11000}
               {:date (tick/instant "2019-11-14T00:00:00.000Z") :open 101 :high 110 :low 90 :close 100 :volume  9000}
               {:date (tick/instant "2019-11-15T00:00:00.000Z") :open 100 :high 120 :low 90 :close 110 :volume 11000}]))


(defn macd-ta4j [n m ds]
  (let [close (ds->ta4j-close ds)]
    (ind :MACD close n m)))

(defn rsi-ta4j [n ds]
  (let [close (ds->ta4j-close ds)]
    (ind :RSI close n)))


;; TESTS

(deftest macd-test
  (let [macd-ds (map double (macd 12 26 :close ds))
        macd-ta4j (-> (macd-ta4j 12 26 ds)
                      (ind-values))]
    (is (all-fuzzy= macd-ds macd-ta4j))))

(deftest rsi-test
  (let [rsi-ds (map double (rsi 2 :close ds))
        rsi-ta4j (-> (rsi-ta4j 2 ds)
                      (ind-values))]
    (is (all-fuzzy= rsi-ds rsi-ta4j))))


(comment
  (-> (macd-ta4j 12 26 ds)
      (ind-values))

  (-> (rsi-ta4j 2 ds)
      (ind-values))

  (map double (macd :close ds))

  (upward-change (:close ds))
  (downward-change (:close ds))

  (rsi 2 :close ds)
  )
