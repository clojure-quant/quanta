(ns juan.algo
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tablecloth.api :as tc]
   [ta.warehouse :refer [load-symbol]]
   [ta.indicator.atr :refer [atr]]))


(defn last-days [ds n]
  (let [c (tc/row-count ds)
        begin (- c n)]
    (tc/select-rows ds (range begin c))))


; pivot: price day h/l
;        price week h/l
;        price current week h/l
;        volume h/l
; sentiment

(defn calc-daily [symbol atr-n]
  (let [series (-> (load-symbol :juan "D" symbol)
                   (last-days atr-n))]
    {:symbol symbol
     :atr (atr series)
     :close (-> series tc/last :close last)
     :close-dt (-> series tc/last :date last)}))



(calc-daily "USDJPY" 20)
(calc-daily "EURUSD" 20)

 

 