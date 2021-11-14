(ns ta.signal-test
  (:require
   [clojure.test :refer :all]
   [ta.backtest.signal :refer [signal->position signal->trade]]))

(def s [:none
        :buy :buy :buy
        :flat nil nil
        :buy :none :none
        :sell :none])

(deftest signal-conversion-test
  (is (=  (signal->position s)
          [:flat
           :long :long :long
           :flat :flat :flat
           :long :long :long
           :short :short]))
  (is (=  (signal->trade s)
          [:flat
           :buy nil nil
           :flat nil nil
           :buy nil nil
           :sell nil])))