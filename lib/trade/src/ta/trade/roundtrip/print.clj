(ns ta.trade.roundtrip.print
  (:require
   [tablecloth.api :as tc]
   [ta.helper.print :refer [print-all]]))

;; ROUNDTRIPS

(def cols-rt
  [:rt-no
   :trade
   :pl-log :win
   :entry-date :exit-date :bars
   :entry-price :exit-price
   ;:index-open :index-close
   ])

(defn- roundtrips-view [ds-rt]
  (tc/select-columns ds-rt cols-rt))

(defn print-roundtrips [roundtrip-ds]
  (-> roundtrip-ds
      (roundtrips-view)
      (print-all)))

