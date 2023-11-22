(ns juan.fx
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tablecloth.api :as tc]
   [ta.data.import :refer [import-series]]
   [ta.warehouse :refer [load-symbol]]
   [juan.algo :refer [calc-daily]]
   
   ))

(def settings 
  {:atr-n 20})


(def instruments
  [{:fx "USDJPY" :future "JY"}
   {:fx "USDSEK" :future "SEK"} ; err
   {:fx "USDNOK" :future "NOK"} ; err
   {:fx "EURUSD" :future "EU"}
   {:fx "GBPUSD" :future "BP"}
   {:fx "USDCAD" :future "CD"} ; err
   ])

(defn get-daily-symbol [symbol]
  (import-series :kibot {:symbol symbol
                         :frequency "D"
                         :warehouse :juan}
                 :full
                 {}))

(defn get-daily []
  (doall (map get-daily-symbol (map :fx instruments))))

(get-daily)

(defn calc []
  (doall (map 
          #(calc-daily % (:atr-n settings)) 
          (map :fx instruments))))
  

(comment 
  (require '[clojure.pprint :refer [print-table]])

  (-> (calc) print-table)
  
  
  ;
  )


(calc)
  



(history {:type "forex",
          :symbol "EURUSD",
              ;:startdate "2023-09-01",
          :period 2
          :interval "daily"
          :timezone "UTC"})



(import-series :kibot {:symbol "USDJPY"
                       :frequency "D"
                       :warehouse :juan}
               :full
               {})
