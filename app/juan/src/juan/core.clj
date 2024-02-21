(ns juan.core
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as fun]
   [ta.warehouse :refer [load-symbol]]
   [ta.indicator :refer [atr]]
   [juan.sentiment :refer [sentiment-dict]]
   [juan.data :refer [settings instruments]]))


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
                   (last-days atr-n))
        size (tc/row-count series)        
       ; _ (info "size: " size)
        s0 (tc/last series)
        s1 (tc/select-rows series [(- size 2)])
        series-week (last-days series 5)

        ]
    {:symbol symbol
     :atr (atr series)
     :close (-> s0 :close last)
     :close-dt (-> s0 :date last)
     :close1 (-> s1 :close last)
     :close1-dt (-> s1 :date last)
     :pivots {:p0-high (-> s0 :high last)
               :p0-low (-> s0 :low last)
               :p1-high (-> s1 :high last)
               :p1-low (-> s1 :low last)
               :pweek-high (apply fun/max (:high series-week))
               :pweek-low (apply fun/min (:low series-week))
               }
     }))

(defn calc-sentiment [get-sentiment symbol]
  (if-let [s (get-sentiment symbol)]
    (let [long-prct (:long-prct s)
          sentiment-treshold (:sentiment-treshold settings)
          short-sentiment (when long-prct (>= long-prct sentiment-treshold))
          short-prct (- 100 long-prct)
          long-sentiment (when long-prct (>= short-prct sentiment-treshold))]
     {:sentiment-long-prct long-prct
      :sentiment-signal (cond 
                          short-sentiment :short
                          long-sentiment :long
                          long-prct false
                          :else nil
                          )})
    {:sentiment-long-prct "XXX"
     :sentiment-signal nil}))


(defn calc-core [get-sentiment]
  (let [calc-one (fn [symbol]
                   (info "calculating: " symbol)
                   (let [data-series (calc-daily symbol (:atr-n settings))
                         data-sentiment (calc-sentiment get-sentiment symbol)]
                     (merge data-series data-sentiment)))]
    (doall (map
            calc-one
            (map :fx instruments)))))


(comment
  (calc-daily "USDJPY" 20)
  (calc-daily "EURUSD" 20)






 ; 
  )


 