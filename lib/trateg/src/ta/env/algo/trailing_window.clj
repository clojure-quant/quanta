(ns ta.env.algo.trailing-window
  (:require
   [tick.core :as t]
   [ta.calendar.core :refer [trailing-window]]
   [ta.env.core :as env]))

(defn create-trailing-bar-loader [{:keys [asset calendar trailing-n] :as _spec}]
  ; fail once, when required parameters are missing
  (assert trailing-n)
  (assert asset)
  (assert calendar)
  (fn [env spec time]
    (when time
      (let [[c i] calendar
            time-seq (trailing-window c i trailing-n time)
            dend  (first time-seq)
            dstart (last time-seq)
            dend-instant (t/instant dend)
            dstart-instant (t/instant dstart)
            ds-bars (env/get-bars env
                                  spec
                                  {:start dstart-instant
                                   :end dend-instant})]
        ds-bars))))


