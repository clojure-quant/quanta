(ns ta.import.logger
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [tablecloth.api :as tc]
   [de.otto.nom.core :as nom]))

(defonce log-a (atom []))

(defn add [import asset calendar window series-ds]
  (let [series (if (nom/anomaly? series-ds)
                 nil
                 (tc/row-count series-ds))]
    (swap! log-a conj {:import import
                       :asset asset
                       :calendar calendar
                       ;:window window
                       :start (:start window)
                       :end (:end window)
                       :series series})))

(comment
  (->> @log-a
       (filter #(= :eodhd (:import %))))

;
  )
