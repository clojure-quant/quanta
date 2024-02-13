(ns ta.env.algo.ds
  (:require
   [tablecloth.api :as tc]))

(defn last-result-row [ds-algo]
  (tc/select-rows ds-algo [(dec (tc/row-count ds-algo))]))

(defn last-ds-row [results]
  (let [last-rows (->> results
                       (map :result)
                       (map last-result-row))]
    (if (> (count last-rows) 0)
      (apply tc/concat last-rows)
      :error/no-data)))

(defn get-current-position
  "if input is nil, it will return :flat
   if input is a tml-dataset it has to contain a :position column and
   returns the last :position column value.
   Has a variation that can be used in a chain."
  ([ds-position]
   (if ds-position 
     (tc/get-entry ds-position :position (dec (tc/row-count ds-position)))
     :flat))
  ([_env _opts ds-position]
   (get-current-position ds-position)))

(defn get-current-positions
  ([ds-positions]
   (map get-current-position ds-positions))
  ([_env _opts ds-position]
   (get-current-position ds-position)))

(defn all-positions-agree
  ([positions]
   (if (apply = positions)
       (first positions)
       :flat))
  ([_env _opts positions]
   (all-positions-agree positions)))


(comment
  (def ds-position-a
    (tc/dataset
     {:date [1 2 3]
      :position [:flat :long :short]}))

  (def ds-position-b
    (tc/dataset
     {:date [1 2 3]
      :position [:flat :long :long]}))

  (tc/get-entry ds-position-a :position 2)

  ds-position-a
  (get-current-position ds-position-a)
  ;; => :short

  (get-current-positions [ds-position-a ds-position-b])
   ;; => (:short :long)

  (->> [ds-position-a ds-position-b]
       (get-current-positions)
       (all-positions-agree))
  ;; => :flat

  (->> [ds-position-a ds-position-a]
       (get-current-positions)
       (all-positions-agree))
  ;; => :short

  (->> [ds-position-b ds-position-b]
       (get-current-positions)
       (all-positions-agree))
  ;; => :long

  (->> [ds-position-b ds-position-b ds-position-b]
       (get-current-positions)
       (all-positions-agree))
    ;; => :long
  
   (->> [ds-position-b ds-position-b ds-position-a]
       (get-current-positions)
       (all-positions-agree))
   ;; => :flat


  (get-current-positions [ds-position-a nil])


 ; 
  )


