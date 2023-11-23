(ns juan.pivot
  (:require 
   [juan.data :refer [instruments]]))

(defn pivots-sorted [pivots]
  (->> pivots
       (map (fn [[name price]]
              [price name]))
       (sort-by first)))

(defn add-difference-pivot [pivot-vec side current-price]
  (let [diff (if (= side :long)
               (- current-price (first pivot-vec))
               (- (first pivot-vec) current-price))]
  (conj pivot-vec diff)))

(defn add-difference-pivots [pivots-vec-seq side current-price]
  (map #(add-difference-pivot % side current-price) pivots-vec-seq))

(defn reversal-pivots-for-price [pivots side current-price]
  (let [pivots (pivots-sorted pivots)
        filter-fn (if (= side :long)
                    (fn [[price _]]
                      (<= price current-price))
                    (fn [[price _]]
                      (>= price current-price)))
        pivots (filter filter-fn pivots)]
      (add-difference-pivots pivots side current-price)))

(defn nearby-pivot [pivots]
  (->> (sort-by last pivots)
       first))

(defn pip-multiplyer [symbol]
  (->> (filter #(= symbol (:fx %)) instruments)
       first
       :pip))

(defn pivot-trigger [pivots symbol side current-price]
  (let [p (-> (reversal-pivots-for-price pivots side current-price)
              (nearby-pivot))
        mult (pip-multiplyer symbol)
        ]
      ;; p is either nil or this format: [185.312 :p1-low 0.6879999999999882]
      (if p
        (let [[price name diff] p]
          {:price price
           :name name
           :diff diff
           :pip-diff (/ diff mult)})
        {:name :no-pivots})))

(comment 

  (def pivots 
   {:p0-high 186.788
    :p0-low 184.625 
    :p1-high 187.151
    :p1-low 185.312
    :pweek-high 188.285
    :pweek-low 184.625})

  (pivots-sorted pivots)

  (add-difference-pivot [185.788 :p0-high] :long 190.00)
  (add-difference-pivot [185.788 :p0-high] :short 184.00)

  (-> pivots pivots-sorted (add-difference-pivots :long 190.00))
  (-> pivots pivots-sorted (add-difference-pivots :short 190.00))
  

  (reversal-pivots-for-price pivots :long 183.0)
  (reversal-pivots-for-price pivots :short 183.0)


  (-> (reversal-pivots-for-price pivots :short 186.0)
       (nearby-pivot))
  
  (-> (reversal-pivots-for-price pivots :long 186.0)
      (nearby-pivot))
  
  (pip-multiplyer "EURUSD")
  (pip-multiplyer "USDJPY")
  
  (pivot-trigger pivots "USDJPY" :long 186.0)
  ;; => {:price 185.312, :name :p1-low, :diff 0.6879999999999882, :pip-diff 6.879999999999882}

  (pivot-trigger pivots "USDJPY" :short 200.0)
  ;; => {:name :no-pivots}

  (pivot-trigger pivots "USDJPY" :short 186.0)
  ;; => {:price 186.788, :name :p0-high, :diff 0.7880000000000109, :pip-diff 7.880000000000109}


 ; 
  )


