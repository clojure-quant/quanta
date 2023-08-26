(ns ta.tradingview.goldly.indicator.clj-char
  (:require
     [ta.tradingview.goldly.indicator.clj :refer [clj-meta clj-study-runner ]])
  )



(def clj-meta-char
  (merge
   clj-meta
   {:id "cljchar@tv-basicstudies-1"
    :name "CLJCHAR"
    :description "CLJCHAR" ; this is used in the api
    :shortDescription "CLJCHAR DESC"
    "is_price_study" true
    "plots" [{"id" "plot_0"
              "type" "chars"}]
    "defaults" {"styles" {"plot_0" {"linestyle" 0
                                    "visible" true
                                    "char" "*"
                                    "title" "bongo"
                                    "location" "AboveBar" ; AboveBar BelowBar Top Bottom Right Left Absolute AbsoluteUp AbsoluteDown
                                    "linewidth" 1 ; Make the line thinner
                                    "plottype" 2 ; Plot type is Line
                                    "trackPrice" false ; Show price line (horizontal line with last price)
                                    "color" "#880000" ; Set the plotted line color to dark red
                                    }}}}))


(defn study-clj-char [PineJS]
  (clj->js
   {:name "CLJCHAR"
    :metainfo clj-meta-char
    :constructor (clj-study-runner PineJS)}))