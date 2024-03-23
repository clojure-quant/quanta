(ns ta.indicator.returns
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]))

(defn diff
  "returns a vector of the difference between subsequent values.
   first value is 0, indicating no difference."
  [integrated-values]
  (let [n (count integrated-values)]
    (dtype/clone
     (dtype/make-reader
      :float32
      n
      (if (= idx 0)
        0
        (- (integrated-values idx)
           (integrated-values (dec idx))))))))

(defn diff-n
  "returns a vector of the difference between subsequent values.
   first value is 0, indicating no difference."
  [n integrated-values]
  (let [l (count integrated-values)]
    (dtype/clone
     (dtype/make-reader
      :float32
      l
      (if (< idx n)
        0
        (- (integrated-values idx)
           (integrated-values (- idx n))))))))

(comment
  (->> [1 8 0 -9 1 4]
       (reductions +)
       vec
       diff)

  (->> [1 8 0 -9 1 4]
       (reductions +)
       vec
       (diff-n 2)
       vec)
   ;; => [1 9 9  0  1 5]
   ;; => [0 0 8 -9 -8 5]

;; #array-buffer<float32> [6]
  ;; [0.000, 8.000, 0.000, -9.000, 1.000, 4.000]
  )
(defn log-return [price-vec]
  (let [log-price (dfn/log10 price-vec)]
    (diff log-price)))

(defn forward-shift-col [col offset]
  (dtype/make-reader :float64 (count col) (if (>= idx offset)
                                            (col (- idx offset))
                                            0)))

(comment

  (require '[tech.v3.dataset :as tds])
  (def d (tds/->dataset {:a [1.0 2.0 3.0 4.0 5.0]
                         :b [1.0 2.0 3.0 4.0 5.0]
                         :c [1.0 2.0 3.0 4.0 100.0]}))

  (-> d
      :a
      (forward-shift-col 1))

;  
  )



