(ns ta.storage-test
  (:require
   [clojure.test :refer :all]
   [taoensso.timbre :refer [trace debug info warnf error]]
   [modular.persist.edn] ; side effects
   [modular.persist.json] ; side effects
   [modular.persist.protocol :refer [save loadr]]
   [ta.tradingview.db-ts :refer [save-chart delete-chart load-chart chart-list now-epoch chart-unbox save-chart-boxed load-chart-boxed]]
   [ta.config] ; side effects
   [differ.core :as differ]))

(def demochart  {:symbol "QQQ"
                 :resolution "D"
                 :name "WILLY"
                 :client 10
                 :user 10
                 :content nil})

(deftest chart-meta-save-test
  (let [chart-id 131
        _ (save-chart 10 10 chart-id demochart)
        chart (load-chart  10 10 chart-id)]
    (is (=  demochart (dissoc chart :id :timestamp)))))

(deftest chart-box-test
  (let [chart-boxed (loadr :edn "test/data/boxed_chart_77_77_1636530570.edn")
        chart-unboxed (-> (chart-unbox chart-boxed)
                          (assoc :client 77 :user 77))
        ; reload:
        _ (save-chart-boxed 77 77 1636530570 chart-boxed)
        chart-loaded (load-chart 77 77 1636530570)]
    (info "differences: " (differ/diff chart-unboxed chart-loaded))
    (is (= chart-unboxed (dissoc chart-loaded :timestamp)))))

#_(deftest storage2-test
    (let [chart2 (-> (loadr :json "test/data/chart.json")
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
