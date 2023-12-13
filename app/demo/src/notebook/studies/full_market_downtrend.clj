(ns notebook.studies.full-market-downtrend
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as fun]
   [ta.warehouse :as wh]
   [notebook.studies.full-market :refer [load-ds-day]]
   [tick.core :as t]))

(defn turnover [ds]
   (fun/* (:close ds) (:volume ds)))

(defn get-symbols-with-volume [day min-volume]
  (let [ds (load-ds-day :stock :daily-unadjusted day)
        t (turnover ds)
        ds-turnover (tc/add-column ds :turnover t)
        t-min (fun/>= t min-volume)]
    (tc/select-rows ds-turnover t-min)))

(defn down-days-percent [ds]
  (let [close (:close ds)
        close-1 (fun/shift close 1)
        diff (fun/- close close-1)
        neg? (fun/<= diff 0.0)
        ds-neg (tc/select-rows ds neg?)
        count-neg (tc/row-count ds-neg)
        count-all (tc/row-count ds)]
     (/ (* count-neg 100) count-all)))


(comment 
  (get-symbols-with-volume "20230703" 0.0)       ; 7141
  (get-symbols-with-volume "20230703" 1000000.0) ; 3102
  (get-symbols-with-volume "20230703" 5000000.0) ; 1844

  (def ds
    (tc/dataset
      {:close [100 101 102 101 102 103 104 105 106 108]}))
  (down-days-percent ds)  
 
;  
  )

(defn load-history [symbol interval days]
   (tc/dataset
   {:close [100 101 102 101 102 103 104 105 106 108]}))

(defn calc-downtrend [symbol]
  (let [ds (load-history symbol :daily 90)
        down-prct (down-days-percent ds)]
     {:symbol symbol
      :down-prct down-prct}))

(defn screener-downdays [date ]
  (let [ds-symbols (get-symbols-with-volume date 1000000)
        symbols (:symbol ds-symbols)]
    (map calc-downtrend symbols)))


(comment
   (require '[clojure.pprint :refer [print-table]])
   (->> (screener-downdays "20230703")
        (print-table))  

  ;
  )


(def link 
"afa5a2acala1mzadaca8a2ala6tzzka3a1afagaua3a8a2a7agmzzpzka8tzajakalapmzmsmfmrm6zkaca1a2a7a6anafapmzzjzka8a2afa6a2agafa2a7mzzjzbzjzbzjz3zpzpzkagaca6a7a5a2mzzjzkafa2a2afa5adaja7a1a2mzzjzka6a7a9a3apafa6a8a7a8a8acala1mzzpzka3a8a7a6mzadala7a6a2apa7ada1a7a6mta9ajafacapzua5alajzkaiafa8a8aeala6agmzz1z2z1ajz1avada9ada4abaeac7v7d"  
  )
