(ns notebook.studies.full-market-downtrend
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as fun]
   [ta.db.bars.duckdb :as duck]
   [ta.data.api-ds.kibot-ftp :as kibot-ds]
   [notebook.studies.full-market :refer [load-ds-day]]
   ))

(defn turnover [ds]
   (fun/* (:close ds) (:volume ds)))

(defn get-symbols-with-volume [day min-volume]
  (let [ds (load-ds-day :stock :daily-unadjusted day)
        t (turnover ds)
        ds-turnover (tc/add-column ds :turnover t)
        t-min (fun/>= t min-volume)]
    (tc/select-rows ds-turnover t-min)))

(defn down-days-percent [ds]
  ;(println "calc: down-days-percent ..")
  (let [close (:close ds)
        close-1 (fun/shift close 1)
        diff (fun/- close close-1)
        neg? (fun/<= diff 0.0)
        ds-neg (tc/select-rows ds neg?)
        pos? (fun/>= diff 0.0)
        ds-pos (tc/select-rows ds pos?)
        count-neg (tc/row-count ds-neg)
        count-all (tc/row-count ds)
        count-pos (tc/row-count ds-pos)]
    {:bars count-all
     :bars-pos count-pos
     :bars-neg count-neg
     :neg-prct (int (/ (* count-neg 100) count-all))}
    ))


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

 (def db (duck/duckdb-start))

(defn load-history [opts]
   ;(tc/dataset {:close [100 101 102 101 102 103 104 105 106 108]})
    ;(println "loading: " symbol)
  (->  (duck/get-bars db opts {})
      (tc/rename-columns {"open" :open
                          "high" :high
                          "low" :low 
                          "close" :close
                          "volume" :volume
                          "symbol" :symbol})))
 

(defn calc-downtrend [symbol]
  (let [ds (load-history symbol)
        down-prct (down-days-percent ds)]
     (assoc down-prct :symbol symbol)
      ))

(defn screener-downdays [date]
  (let [ds-symbols (get-symbols-with-volume date 1000000)
        symbols (:symbol ds-symbols)]
    (->> (map calc-downtrend symbols)
         (sort-by :neg-prct)
         (reverse))))


(comment
  

    (kibot-ds/existing-rar-days :stock :daily-unadjusted)

    (def ds (load-ds-day :stock :daily-unadjusted "20230703"))
    (def ds (load-ds-day :stock :daily-unadjusted "20230607"))
   
   

    (duck/append-bars db [:us :d] ds)
    (duck/get-bars db "MSFT")
    (duck/delete-bars db)
  
   (-> (load-history "MSFT")
       (down-days-percent))
  
  

    (doall (map (fn [day]
                  ;(println "loading ds: " day)
                 (let [ds (load-ds-day :stock :daily-unadjusted day)]
                   (duck/append-bars db ds)))
                (kibot-ds/existing-ds-files :stock :daily-unadjusted)))
    


   (require '[clojure.pprint :refer [print-table]])
   (->> (screener-downdays "20230703")
        (print-table))
    
   (defn print-to-file [data]
     (let [table (with-out-str (print-table data))]
       (spit "../../output/downtrend.txt" table)))
    
    (print-to-file [{:a 1 :b 2} {:a 3 :b 5}])


    (->> (screener-downdays "20230703")
         ;(print-table)
         (print-to-file)
         )

  ;
  )

