(ns demo.math)

(def sum #(reduce + %))

(def avg #(/ (sum %) (count %)))

(defn square [n] (* n n))

(defn mean [a] (/ (reduce + a) (count a)))

(defn standarddev [a]
  (when (> (count a) 1)
    (Math/sqrt (/
                (reduce + (map square (map - a (repeat (mean a)))))
                (- (count a) 1)))))


(comment
  (mean [1 2 3 4 5])
  (standarddev [1 2 3 4 5])
  (standarddev [])

 ; 
  )