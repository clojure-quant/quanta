(ns ta.tradingview.goldly.indicator.clj-col
  (:require
   [ta.tradingview.goldly.indicator.clj :refer [clj-meta clj-study-runner]]))

(def clj-meta-col
  (merge
   (assoc-in clj-meta
             ["defaults" "styles" "plot_0" "plottype"] 5) ; 5- columns
   {:id "cljcol@tv-basicstudies-1"
    :name "CLJCOL"
    :description "CLJCOL" ; this is used in the api
    :shortDescription "CLJCOL DESC"
    "is_price_study" false}))

(defn study-clj-col [PineJS]
  (clj->js
   {:name "CLJCOL"
    :metainfo clj-meta-col
    :constructor (clj-study-runner PineJS)}))
