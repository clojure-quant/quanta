(ns techml.math.vector
  (:require
    [tech.v3.datatype :as dtype]
    [tech.v3.datatype.functional :as fun]))


(defn diff [integrated-values]
  (let [n (count integrated-values)]
    (dtype/clone
     (dtype/make-reader
      :float64
      n
      (if (= idx 0)
        0
        (- (integrated-values idx)
           (integrated-values (dec idx))))))))

(defn log-return [price-vec]
  (let [log-price (fun/log10 price-vec)]
    (diff log-price)))

(defn forward-shift [col offset]
  (dtype/make-reader :float64 (count col) (if (>= idx offset)
                                            (col (- idx offset))
                                            0)))
