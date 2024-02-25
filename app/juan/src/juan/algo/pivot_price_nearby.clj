(ns juan.algo.pivot-price-nearby
  (:require
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tc]))

(defn pivots-sorted [pivot-ds]
  (tc/order-by pivot-ds :price))

(defn add-difference-pivot [pivot-ds price]
  (let [diff (fun/- (:price pivot-ds) price)]
    (tc/add-column pivot-ds :diff diff)))

(defn neareby-pivots [max-diff pivot-diff-ds]
  (tc/select-rows pivot-diff-ds (fn [row] (<= (abs (:diff row)) max-diff))))

(defn short-pivots [pivot-diff-ds]
  (tc/select-rows pivot-diff-ds (fn [row] (>= (:diff row) 0.0))))

(defn long-pivots [pivot-diff-ds]
  (tc/select-rows pivot-diff-ds (fn [row] (<= (:diff row) 0.0))))


(defn nearest-long-pivot [pivot-diff-ds]
  (let [pivots (long-pivots pivot-diff-ds)
        pivots-sorted (tc/order-by pivots :price :desc)]
    (when (> (tc/row-count pivots) 0)
      (tc/select-rows pivots-sorted [0]))))

(defn nearest-short-pivot [pivot-diff-ds]
  (let [pivots (short-pivots pivot-diff-ds)
        pivots-sorted (tc/order-by pivots :price :asc)]
    (when (> (tc/row-count pivots) 0)
      (tc/select-rows pivots-sorted [0]))))




(defn nearby-pivots [{:keys [max-diff]} pivot-ds current-price]
  (let [pivot-diff-ds (add-difference-pivot pivot-ds current-price)
        pivot-nearby-ds (neareby-pivots max-diff pivot-diff-ds)
        long-pivot (nearest-long-pivot pivot-nearby-ds)
        short-pivot (nearest-short-pivot pivot-nearby-ds)]
    {:pivot-long (when long-pivot 
                   (-> long-pivot :name first))
     :pivot-short (when short-pivot
                   (-> short-pivot :name first))}))


(comment

  (require '[tablecloth.api :as tc])
  (def pivot-ds (tc/dataset [{:name :p0-low :price 0.98544973}
                             {:name :p0-high :price 1.98544973}
                             {:name :p1-low  :price 0.96355819}
                             {:name  :p1-high :price 1.9635581910}
                             {:name :pweek-high :price 1.98544973}
                             {:name :pweek-low  :price 0.78332691}]))
  pivot-ds

  (pivots-sorted pivot-ds)

  (def pivot-diff-ds (add-difference-pivot pivot-ds 1.00))
  pivot-diff-ds
  ;; => _unnamed [6 3]:
  ;;    
  ;;    |       :name |     :price |       :diff |
  ;;    |-------------|-----------:|------------:|
  ;;    |     :p0-low | 0.98544973 | -0.01455027 |
  ;;    |    :p0-high | 1.98544973 |  0.98544973 |
  ;;    |     :p1-low | 0.96355819 | -0.03644181 |
  ;;    |    :p1-high | 1.96355819 |  0.96355819 |
  ;;    | :pweek-high | 1.98544973 |  0.98544973 |
  ;;    |  :pweek-low | 0.78332691 | -0.21667309 |

  (->> pivot-diff-ds
       (neareby-pivots 0.2)
       (long-pivots))
  ;; => _unnamed [2 3]:
  ;;    
  ;;    |   :name |     :price |       :diff |
  ;;    |---------|-----------:|------------:|
  ;;    | :p0-low | 0.98544973 | -0.01455027 |
  ;;    | :p1-low | 0.96355819 | -0.03644181 |

  (->> pivot-diff-ds
       (neareby-pivots 0.2)
       (nearest-long-pivot))
  ;; => _unnamed [1 3]:
  ;;    
  ;;    |   :name |     :price |       :diff |
  ;;    |---------|-----------:|------------:|
  ;;    | :p0-low | 0.98544973 | -0.01455027 |

  (->> pivot-diff-ds
       (neareby-pivots 0.2)
       (short-pivots))
  ;; note that short pivots have all differences above 0.2
 ;; => _unnamed [0 3]:
 ;;    
 ;;    | :name | :price | :diff |
 ;;    |-------|-------:|------:|

  (->> pivot-diff-ds
       (neareby-pivots 0.2)
       (nearest-short-pivot))
  ;; => nil

  (nearby-pivots {:max-diff 0.2} pivot-ds 1.0)
  ;; => {:pivot-long :p0-low, :pivot-short nil}




 ; 
  )


