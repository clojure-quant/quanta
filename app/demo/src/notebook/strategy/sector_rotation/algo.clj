(ns notebook.strategy.sector-rotation.algo
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [ta.helper.date-ds :refer [month-begin? month-end? add-year-and-month-date-as-local-date]]
   [ta.indicator :refer [sma]]))

(defn long-during-month-when-above-ma [mb? me? close sma-v]
  (if me?
    :flat  ; flat on month-end
    (if mb?
      (if (> close sma-v)
        :buy  ; long when above ma
        :flat) ; flat when below ma
      :hold ;  hold when not month-begin or month-end
      )))

(defn trade-sma-monthly
  [env {:keys [sma-length] 
        :or {sma-length 30}} bar-ds]
  (let [{:keys [date close]} bar-ds
        sma-v (sma sma-length close)
        sma-r (dfn// close sma-v)
        m-b (month-begin? date)
        m-e (month-end? date)]
    (-> bar-ds
        add-year-and-month-date-as-local-date
        (tc/add-columns {:sma-v sma-v
                         :sma-r sma-r
                         :m-b m-b
                         ;:signal1 (dtype/emap buy-above :object close sma-v)
                         :signal (dtype/emap long-during-month-when-above-ma :object m-b m-e close sma-v)}))))

(def col-study [:date :close :sma-v  :signal
                   ; :signal2
                   ;:sma-r
                ])