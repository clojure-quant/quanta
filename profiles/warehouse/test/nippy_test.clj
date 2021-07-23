(ns demo.warehouse.nippy-test
  (:require
   [tech.v3.dataset :as ds]
   [ta.warehouse :as w]))


(->> [{:a 1 :b 2} {:a 2 :c 3}]
     ds/->dataset
     (io/put-nippy! "test.nippy"))



(let [ds (ds/->dataset "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/stocks.csv")]
    ds
    (w/save-ts ds "bongo")
    (w/load-ts "bongo"))

(comment

  

  #_(def ds-2010 (time (ds/->dataset
                        "nippy-demo/2010.tsv.gz"
                        {:parser-fn {"date" [:packed-local-date "yyyy-MM-dd"]}}))))