(ns ta.trade.exit-test
  (:require
   [clojure.test :refer :all]
   [ta.trade.backtest.exit :refer [exit]]))

(def position {:side :long
               :entry-idx 0
               :entry-price 100.0})

(def row {:idx 9
          :open 101.0
          :high 103.0
          :low 100.0
          :close 102.0})

(deftest exit-test-time
  (testing "exit-time"
    (is (=  (exit [:time 5] position row)
            {:side :long
             :entry-idx 0
             :entry-price 100.0
             :exit-price 102.0
             :exit-rule :time
             :exit-idx 9, :exit-date nil}))
    (is (=  (exit [:time 9] position row)
            nil))))

(deftest exit-test-profit-percent
  (testing "exit-profit-percent"
    (is (=  (exit [:profit-percent 2.0] position row)
            {:side :long
             :entry-idx 0
             :entry-price 100.0
             :exit-price 102.0
             :exit-rule :profit-percent
             :exit-idx 9 :exit-date nil}))
    (is (=  (exit [:profit-percent 100.0] position row)
            nil))))

(comment
  (exit [:time 9] position row)
  (exit [:profit-percent 2.0] position row)

 ; 
  )




