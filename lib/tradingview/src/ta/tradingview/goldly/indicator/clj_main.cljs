(ns ta.tradingview.goldly.indicator.clj-main
  (:require
   [ta.tradingview.goldly.indicator.clj :refer [clj-meta clj-study-runner]]))


(def clj-meta-main
  (merge clj-meta
         {:id "cljmain@tv-basicstudies-1"
          :name "CLJMAIN"
          :description "CLJMAIN" ; this is used in the api
          :shortDescription "CLJMAIN DESC"
          "is_price_study" true}))


(defn study-clj-main [PineJS]
  (clj->js
   {:name "CLJMAIN"
    :metainfo clj-meta-main
    :constructor (clj-study-runner PineJS)}))