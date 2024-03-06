(ns notebook.strategy.sector-rotation.portfolio
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   ))


(defn take-max [ds max-pos sort-by where]
  (let [c (tc/row-count ds)
        nr-available (min max-pos c)
        middle-idx-start (-> (- c nr-available)
                             (/ 2)
                             (long)) ; range has to be made of longs, otherwise tc cannot filter
        r (case where
            :bottom [0 nr-available]
            :top    [(- c nr-available) c]
            :middle [middle-idx-start (long (+ middle-idx-start nr-available))])
        rg (apply range r)]
  ;(println "range: " r "vals: " rg " type: " (type (first r)) (type (last r)) )
    (-> ds
        (tc/order-by sort-by)
        (tc/select-rows rg))))

(defn max-positions [ds-rts max-pos sort-by where]
  (as-> ds-rts x
    (tc/group-by x [:year-month])
    (:data x)
    (map #(take-max % max-pos sort-by where) x)
    (apply tc/concat x)))


(comment 
  ; make sure we take the highest :sma-r
  (-> (tc/dataset {:year-month [202001 202001 202001 202001 202001 202001 202001]
                   :sma-r [3 4 6 2 3 4 5]})
      (max-positions 2 [:sma-r] :middle)
        ;(max-positions 2 [:sma-r] :bottom)
        ;(max-positions 2 [:sma-r] :top)
    )  
  
  
  
 ; 
  )

