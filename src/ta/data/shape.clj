(ns ta.data.shape)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;data shapes;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn row-maps->column-map [row-maps]
  (let [header (-> row-maps first keys)]
    (reduce (fn [acc row]
              (reduce-kv
               (fn [acc column-name value]
                 (update acc column-name conj value))
               acc
               row))
            (apply hash-map (interleave header (repeat [])))
            row-maps)))

(defn column-map->rows [cm]
  (reduce-kv
   (fn [rs _ #_column values]
     (map (fn [row value] (conj row value)) rs values))
   (repeat (count (val (first cm))) [])
   cm))

(defn column-map->row-maps [cm]
  (let [header (keys cm)
        rows   (column-map->rows cm)]
    (mapv (partial zipmap header) rows)))