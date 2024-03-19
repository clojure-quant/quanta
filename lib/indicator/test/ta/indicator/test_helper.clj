(ns ta.indicator.test-helper)

(def diff-tolerance 0.0000000001)

(defn fuzzy=
  ([x y] (fuzzy= diff-tolerance x y))
  ([tolerance x y]
   (let [diff (Math/abs (- x y))]
     (< diff tolerance))))

(defn all-fuzzy= [a b]
  (and (= (count a) (count b))
       (every? true? (map (fn [a b] (fuzzy= a b)) a b))))
