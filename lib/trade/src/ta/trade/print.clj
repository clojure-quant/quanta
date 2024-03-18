(ns ta.trade.print
  (:require
   [tablecloth.api :as tc]
   [ta.helper.print :refer [print-all]]))

;; ROUNDTRIPS

(def cols-rt
  [:rt-no
   :trade
   :pl-log :win
   :date-open :date-close :bars
   :price-open :price-close
   ;:index-open :index-close
   ])

(defn- roundtrips-view [ds-rt]
  (tc/select-columns ds-rt cols-rt))

(defn print-roundtrips [roundtrip-ds]
  (-> roundtrip-ds
      (roundtrips-view)
      (print-all)))

