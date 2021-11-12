(ns storage-test
  (:require
   [clojure.test :refer :all]
   [ta.tradingview.db-ts :refer [save-chart delete-chart load-chart chart-list now-epoch chart-unbox save-chart-boxed load-chart-boxed]]
   [reval.persist.edn] ; side effects
   [reval.persist.json] ; side effects
    [reval.persist.protocol :refer [save loadr]]
   ))


(def demochart  {:symbol "QQQ"
                 :resolution "D"
                 :name "WILLY"
                 :content nil})

(deftest chart-meta-save-test
  (let [chart-id 131
        _ (save-chart 10 10 chart-id demochart)
        chart (load-chart  10 10 chart-id)]
    (is (=  demochart (dissoc chart :id :timestamp)))))

(deftest chart-box-test
  (let [chart-boxed (loadr :edn "test/data/boxed_chart_77_77_1636530570.edn")
        chart-unboxed (chart-unbox chart-boxed)
        ; reload:
         _ (save-chart-boxed 10 10 131 chart-boxed)
        chart2 (load-chart 10 10 131)
       
        ]
    (is (= chart-unboxed (dissoc chart2 :timestamp)))))




#_(deftest storage2-test
  (let [chart2 ( -> (loadr :json "test/data/chart.json")
                   :data)
        chart-id 777 
        _ (save-chart 10 10 chart-id chart2)
        chart (load-chart  10 10 chart-id)
        ;_ (delete-chart (:tradingview @state) 10 10 chart-id)
        ]
    (is (=  (:name chart) (:name chart)))
    ;(is (=  {} (dissoc chart :id :timestamp)))
    
    ))






(def demo-template {:name "demo-mania"
                    :content "mega"})

#_(deftest template-test
    (let [template-id (.save-template (:tradingview @state) 10 10 demo-template)
          _ (println "template id: " template-id)
          data nil
       ; data (.load-template (:tradingview @state) 10 10 template-id) ;"5d87c9db3e4d5711b9cd0cc7")
       ; _ (println "template data: " data)
          ]
      (is (=  (:name data) "moving average 200 with bollinger"))))

#_(deftest search-test
    (let [result (.search (:tradingview @state) "CAC" "Index" "" 2)
          _ (println "search result: " result)]
      (is (=  2 (count result)))))
