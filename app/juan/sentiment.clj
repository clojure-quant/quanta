(ns juan.sentiment
  (:require
   [hickory.core :as hc]
   [hickory.select :as s]))



; table class= "table table-bordered table-vertical-middle text-center margin-top-5" >



(def h
  (slurp "../juan/Forex Sentiment _ Myfxbook.html"))


(def site-htree
  (->  h hc/parse hc/as-hickory))

(defn select-table-tr [htree]
  (-> (s/select (s/child ;(s/class "subCalender") ; sic
                        ;(s/tag :div)
                        ;(s/id :raceDates)
                        ;s/first-child
                 (s/tag :table)
                 (s/tag :tbody)
                 (s/tag :tr))
                htree)))



(defn select-child-of [htree tag]
  (s/select  (s/child
              (s/tag tag)) htree))

(defn content [htree]
  (map :content htree))

(defn child-element-content [htree tag]
  (->  (select-child-of htree tag)
       first
       :content))

(defn extract-x [htree]
  {:1 (-> htree first (child-element-content :a))
   :2 (-> htree second (child-element-content :span))
   :3 (-> htree (nth 2) (child-element-content :a))
   :4 (-> htree (nth 3) (child-element-content :span))})

(defn extract-data [htree]
  (map :content htree))


(defn col-vec->map [col-vec]
  ; ["EURUSD" "Short" "85%" "33588.79 lots" "82953"]
  (let [[symbol side prct qty nr] col-vec] 
    {:symbol symbol
     :side side
     :prct prct
     :qty qty
     :nr nr}))


(defn extract-column-data [htree]
  (when (= 5 (count htree))
    (let [col-vec (->> (map :content htree)
                       (map first)
                       (into []))
          long-short (get col-vec 1)]
      (case long-short
        "Short" (col-vec->map col-vec)
        "Long" (col-vec->map col-vec)
        nil))))

(defn select-td [htree]
  (s/select  (s/child
              (s/tag :td)) htree))

(defn extract-valid-table [htree]
  (-> htree
      (select-td)
      (extract-column-data)))

(defn extract-valid-tables [htree]
  (->> (select-table-tr htree)
       (map extract-valid-table)
       (remove nil?)
       ))

(comment
  (require '[clojure.pprint :refer [print-table]]) 


  (-> (extract-valid-tables site-htree) print-table)

  
(-> (select-table-tr site-htree)
    ;first ;last
    ;second
    (nth 5)
    (select-td) ;(map :content)
    ;(content)
    ;first
    ;second
    ;span-content
    ;(child-element-content :a)
    ;(child-element-content :span)
    ;(extract-x)
    ;(extract-data)
    ;(count)
    (extract-column-data))

;  
)








 
