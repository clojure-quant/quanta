(ns juan.sentiment
  (:require
   [clojure.string :as str]
   [clj-http.client :as http]
   [hickory.core :as hc]
   [hickory.select :as s]))

;; download 

(def html-path "../../output/juan.html")

(defn download-sentiment []
  (let [result (-> (http/get "https://www.myfxbook.com/community/outlook"
                             {:accept :html})
                   (:body))]
    (spit html-path result)
    result))


;; parse

(defn select-tables [htree]
  (-> (s/select (s/child
                 (s/tag :table))
                htree)))

(defn select-table-rows [htree]
  (-> (s/select (s/child
                 (s/tag :tbody)
                 (s/tag :tr))
                htree)))

; <tbody>
; <tr>
; <td rowspan= "2" >EURNOK</td>
; <td>Short</td>
; <td>100%</td>
; <td>0.83 lots</td>
; <td>26</td>
; </tr>
; <tr>
; <td>Long</td>
; <td>0%</td>
; <td>0.00 lots</td>
; <td>0</td>
; </tr>
; </tbody>

(defn col-vec-symbol->map [col-vec]
  ; ["EURUSD" "Short" "85%" "33588.79 lots" "82953"]
  (let [[symbol side prct qty nr] col-vec]
    {:symbol symbol
     :side side
     :prct prct
     :qty qty
     :nr nr}))

(defn col-vec->map [symbol col-vec]
  ; ["Short" "85%" "33588.79 lots" "82953"]
  ; note that the vector has no symbol
  (let [[side prct qty nr] col-vec]
    {:symbol symbol
     :side side
     :prct prct
     :qty qty
     :nr nr}))

(defn tds->vec [tds]
  (->> (map :content tds)
       (map first)
       (into [])))

(defn select-td [htree]
  (s/select  (s/child
              (s/tag :td)) htree))

(defn parse-prct [sprct]
  (-> sprct
      (str/replace #"%" "")
      parse-long))

(defn parse-qty [sqty]
  (-> sqty
      (str/replace #"lots" "")
      ;parse-long
      ))



(defn extract-table [table]
  (let [rows (select-table-rows table)]
    (when (= 2 (count rows))
      (let [vec-1 (-> rows first select-td tds->vec)
            vec-2 (-> rows last select-td tds->vec)]
        (when (and (= 5 (count vec-1))
                   (= 4 (count vec-2)))
          (let [data-1 (col-vec-symbol->map vec-1)
                symbol (:symbol data-1)
                data-2 (col-vec->map symbol vec-2)
                [long-data short-data] (if (= (:side data-1) "Long")
                                         [data-1 data-2]
                                         [data-2 data-1])]
            {:symbol symbol
             :long-prct (parse-prct (:prct long-data))
             :short-prct (parse-prct (:prct short-data))
             :long-qty (parse-qty (:qty long-data))
             :short-qty (parse-qty (:qty short-data))
             :long-nr (:nr long-data)
             :short-nr (:nr short-data)}))))))

(defn calc-sentiment [data]
  (->> data ; (slurp html-path)
       hc/parse
       hc/as-hickory
       select-tables
       (map extract-table)
       (remove nil?)))

(defn sentiment-dict [data]
  (let [s (calc-sentiment data)]
    (into {} (map (juxt :symbol identity) s))))


(comment

  (-> (http/get "https://www.myfxbook.com/community/outlook"
                {:accept :html})
      (:body))
    
  (download-sentiment)

  (-> (download-sentiment)
      (calc-sentiment))
  
  (require '[clojure.pprint :refer [print-table]])
  (->> (download-sentiment)
       (calc-sentiment) 
       (sort-by :symbol)
       print-table)

  (-> (download-sentiment)
      (sentiment-dict))
  


;  
  )





 
