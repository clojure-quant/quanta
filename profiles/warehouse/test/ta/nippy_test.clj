(ns ta.nippy-test
  (:require
   [tech.v3.dataset :as ds]
   [ta.warehouse :as wh]
   [tech.v3.io :as io]
   [ta.config] ; side effects
   ))
(->> [{:a 1 :b 2} {:a 2 :c 3}]
     ds/->dataset
     (io/put-nippy! "/tmp/test.nippy"))

(let [ds (ds/->dataset "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/stocks.csv")]
  ds
  (wh/save-ts :test-wh ds "bongo")
  (wh/load-ts :test-wh "bongo"))

(comment

  #_(def ds-2010 (time (ds/->dataset
                        "nippy-demo/2010.tsv.gz"
                        {:parser-fn {"date" [:packed-local-date "yyyy-MM-dd"]}}))))