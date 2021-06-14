(ns demo.dataframe)


(require '[clj-time.core :as t])

 (def model [{:a 1 :b 10}
             {:a 2 :b 11}])

(get-ts model [:a])

(set-ts model [:sum] [-6 -9])
(set-ts model [:r :i] [-6 -9])