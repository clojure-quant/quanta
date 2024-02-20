(ns ta.math.bin)

(defn bin-full [n-bins xs]
  (let [min-x    (apply min xs)
        max-x    (apply max xs)
        range-x  (- max-x min-x)
        bin-fn   (fn [x]
                   (-> x
                       (- min-x)
                       (/ range-x)
                       (* n-bins)
                       (int)
                       (min (dec n-bins))))]
    {:n-bins n-bins
     :min-x min-x 
     :max-x max-x 
     :range-x range-x 
     :bin-fn bin-fn
     :result (map bin-fn xs)}))

(defn bin-lower-bound [{:keys [n-bins min-x max-x range-x]} b]
  (-> b
      (* range-x)
      (/ n-bins)
      (+ min-x)))

(defn bin-upper-bound [{:keys [n-bins min-x max-x range-x]} b]
   (-> b
       inc
       (* range-x)
       (/ n-bins)
       (+ min-x)))

(defn bin-middle [{:keys [n-bins min-x max-x range-x]} b]
  (-> b
      (+ 0.5)
      (* range-x)
      (/ n-bins)
      (+ min-x)))

(defn bin-result [r]
  (:result r))


(defn bin [n-bins xs]
  (-> (bin-full n-bins xs) :result))

(comment 
  ; For example, we can bin range 0-14 into 5 bins like so:
  (bin 5 (range 15))
  ;; (0 0 0 1 1 1 2 2 2 3 3 3 4 4 4)  
  
  ; we can use bin for tml-datasets:

  (require '[tablecloth.api :as tc])
  (def ds (tc/dataset {:close (range 15)}))
  
  (:close ds)

  (bin 5 (:close ds))
  ;; => (0 0 0 1 1 1 2 2 2 3 3 3 4 4 4)
  ;; same result.

  (bin-full 5 (range 15))

  ; 5 bins 10-110
  ; 0 10-30
  ; 1 30-50
  ; 2 50-70
  ; 3 70-90
  ; 4 90-110

  (bin-lower-bound {:n-bins 5 :min-x 10 :max-x 110 :range-x 100} 4)
  ; 90
  (bin-upper-bound {:n-bins 5 :min-x 10 :max-x 110 :range-x 100} 4)
  ; 110
  (bin-middle {:n-bins 5 :min-x 10 :max-x 110 :range-x 100} 4) 
  ; 100


  ;
  )


