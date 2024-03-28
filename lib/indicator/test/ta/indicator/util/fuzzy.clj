(ns ta.indicator.util.fuzzy)

; from https://github.com/jjttjj/trateg/blob/master/src/trateg/core.clj

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

(defn nthrest-fuzzy=
  "fuzzy= compare of two seqs.
   ignores the first n items in the seq."
  ([n a b]
   (nthrest-fuzzy= diff-tolerance n a b))
  ([tolerance n a b]
   (let [a (nthrest a n)
         b (nthrest b n)]
   (and (= (count a) (count b))
        (every? true? (map (fn [a b] (fuzzy= tolerance a b)) a b))))))


(comment
  (all-fuzzy= [1.0 1.0 1.0] [1.0 1.0 1.00000000005])
  (all-fuzzy= [1.0 1.0 1.0] [1.0 1.0 1.0000000005])
  (all-fuzzy= 0.1 [1.0 1.0 1.0] [1.0 1.0 1.0000000005])

  (all-fuzzy= 1 [99.0 1.0 1.0] [1.0 1.0 1.0000000000001])
  (nthrest-fuzzy= 1 [99.0 1.0 1.0] [1.0 1.0 1.0000000000001])
  (nthrest-fuzzy= 1 [99.0 44.0 1.0] [1.0 1.0 1.0000000000001])
  (nthrest-fuzzy= 2 [99.0 44.0 1.0] [1.0 1.0 1.0000000000001])

;  
  )