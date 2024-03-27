(ns ta.indicator.util.fuzzy)

(def diff-tolerance 0.0000000001)

(defn fuzzy=
  ([x y] (fuzzy= diff-tolerance x y))
  ([tolerance x y]
   (let [diff (Math/abs (- x y))]
     (< diff tolerance))))

(defn all-fuzzy=
  ([a b]
   (all-fuzzy= diff-tolerance a b))
  ([tolerance a b]
   (and (= (count a) (count b))
        (every? true? (map (fn [a b] (fuzzy= tolerance a b)) a b)))))

(comment
  (all-fuzzy= [1.0 1.0 1.0] [1.0 1.0 1.00000000005])
  (all-fuzzy= [1.0 1.0 1.0] [1.0 1.0 1.0000000005])
  (all-fuzzy= 0.1 [1.0 1.0 1.0] [1.0 1.0 1.0000000005])
;  
  )