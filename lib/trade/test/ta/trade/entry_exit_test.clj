(ns ta.trade.entry-exit-test
  (:require
   [clojure.test :refer :all]
   [tech.v3.dataset :as tds]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.trade.data :as data]
   [ta.trade.backtest.from-entry :refer [entry-signal->roundtrips]]))

(def rts-result
  (entry-signal->roundtrips
   {:asset "BTC"
    :entry  [:fixed-amount 100000]
    :exit [:time 5
           :loss-percent 4.0
           :profit-percent 5.0]}
   data/bar-entry-ds))

(def rts-result-time
  (entry-signal->roundtrips
   {:asset "BTC"
    :entry  [:fixed-amount 100000]
    :exit [:time 2
           :loss-percent 4.0
           :profit-percent 5.0]}
   data/time-bar-entry-ds))

(defn sanitize-row [row]
  (dissoc row :entry-date :exit-date))

(defn ds->map [ds]
  (->> (tds/mapseq-reader ds)
       (map sanitize-row)
       (into [])))

(deftest trade-entry-exit
  (testing "trade-entry-exit"
    (is (= (:exit rts-result)  [:none :none :close]))
    (is (= (ds->map (:roundtrips rts-result))
           [{:entry-idx 1
             :entry-price 2.0
             :exit-idx 2
             :id 1
             :side :short
             :qty 50000.0
             :exit-price 2.08
             :exit-rule :loss-percent
             :asset "BTC"}]))
    (is (= (ds->map (:roundtrips rts-result-time))
           [{:entry-idx 1,
             :entry-price 2.999
             :exit-idx 4
             :id 1
             :side :short
             :qty 33344.448149383126
             :exit-price 3.01
             :exit-rule :time
             :asset "BTC"}]))))

(comment

  (-> rts-result-time
      :roundtrips
      ds->map)

; 
  )


  