(ns ta.indicator.ma-test
  (:require [clojure.test :refer :all]
            [ta.indicator.test-helper :refer [all-fuzzy=]]
            [tick.core :as tick]
            [tablecloth.api :as tc]
            [ta.indicator :refer [sma wma ema mma]]
            [ta.indicator.ta4j.ta4j :refer [ind ind-values ds->ta4j-close]]
            [tech.v3.datatype.statistics :as stats]
            ))

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


(defn sma-ta4j [len ds]
  (let [close (ds->ta4j-close ds)]
    (ind :SMA close len)))

(defn wma-ta4j [len ds]
  (let [close (ds->ta4j-close ds)]
    (ind :WMA close len)))

(defn ema-ta4j [len ds]
  (let [close (ds->ta4j-close ds)]
    (ind :EMA close len)))

(defn mma-ta4j [len ds]
  (let [close (ds->ta4j-close ds)]
    (ind :MMA close len)))


;; TESTS

(deftest sma-test
  (let [sma-ds (vec (sma {:n 2} (:close ds)))
        sma-ta4j (-> (sma-ta4j 2 ds)
                     (ind-values)
                     (vec))]
    (is (all-fuzzy= sma-ds sma-ta4j))))

(deftest wma-test
  (let [wma-ds (vec (wma 2 (:close ds)))
        wma-ta4j (-> (wma-ta4j 2 ds)
                     (ind-values)
                     (vec))]
    (is (all-fuzzy= wma-ds wma-ta4j))))

(deftest ema-test
  (let [ema-ds (->> (ema 2 (:close ds))
                    (map double)
                    (vec))
        ema-ta4j (-> (ema-ta4j 2 ds)
                     (ind-values)
                     (vec))]
    (is (all-fuzzy= ema-ds ema-ta4j))))

(deftest mma-test
  (let [mma-ds (->> (mma 2 (:close ds))
                    (map double)
                    (vec))
        mma-ta4j (-> (mma-ta4j 2 ds)
                     (ind-values)
                     (vec))]
    (is (all-fuzzy= mma-ds mma-ta4j))))


(comment
  (vec (ind-values (sma-ta4j 2 ds)))
  (vec (ind-values (wma-ta4j 2 ds)))
  (vec (ind-values (ema-ta4j 2 ds)))
  (vec (ind-values (mma-ta4j 2 ds)))

  (vec (sma {:n 2} (:close ds)))
  (vec (wma 2 (:close ds)))


  (all-fuzzy= [1.0 1.0 1.0] [1.0 1.0 1.00000000005])
  (all-fuzzy= [1.0 1.0 1.0] [1.0 1.0 1.0000000005])

  (stats/mean [1 2 3 4 5 6 7 8])

  (map double (ema 2 (:close ds)))
  (map double (mma 2 (:close ds)))
  )