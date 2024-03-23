(ns ta.math.series)

(defn gauss-summation
  "1+2+3+...+n = n*(n+1)/2"
  [n]
  (/ (* n (+ n 1)) 2))
