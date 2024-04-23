(ns ta.indicator.returns
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]))

(defn- nil-or-nan? [n]
  (or (nil? n) (NaN? n)))

(defn diff
  "returns a vector of the difference between subsequent values.
   returns NaN if diff cannot be calculated."
  [integrated-values]
  (let [n (count integrated-values)]
    (dtype/clone
     (dtype/make-reader
      :float32
      n
      (if (= idx 0)
        Double/NaN
        (let [c (integrated-values idx)
              p (integrated-values (dec idx))
              invalid (or (nil-or-nan? c) (nil-or-nan? p))]
          (if invalid
            Double/NaN
            (- c p))))))))

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

(defn diff-2col
  "returns a vector of the difference between 2 columns (shifted)
   formula: x = col1 (current) - col2 (prev)
   first value is passed as argument"
  [col1 col2 v]
  (let [len (count col1)]
    (dtype/clone
     (dtype/make-reader
      :float64 len
      (if (= idx 0)
        v
        (- (col1 idx) (col2 (dec idx))))))))

(defn return-stddev [price]
  (let [d (diff price)]
    (dfn/standard-deviation d)))

(defn log-return [price-vec]
  (let [log-price (dfn/log10 price-vec)]
    (diff log-price)))

(defn forward-shift-col [col offset]
  (dtype/make-reader :float64 (count col) (if (>= idx offset)
                                            (col (- idx offset))
                                            0)))

(comment

  (->> [1 8 0 -9 1 4]
       (reductions +)
       vec
       diff)

  (->> [10 18 20 Double/NaN 24 30]
       diff)

  (def data [10 18 20 Double/NaN 24 30])

  (require '[tablecloth.api :as tc])
  (-> (tc/dataset {:data data
                   :chg (diff data)})
      ;(:chg)
      (tc/select-rows (fn [row]
                        (not (nil-or-nan? (:chg row))))))

  (->> [1 8 0 -9 1 4]
       (reductions +)
       vec
       (diff-n 2)
       vec)
   ;; => [1 9 9  0  1 5]
   ;; => [0 0 8 -9 -8 5]
  (require '[tablecloth.api :as tc])

  (def ds (tc/dataset {:close [1.0 1.1 1.2 1.3 1.4 2.0 3.0]}))
  ds

  (-> (:close ds)
      (diff)
      (dfn/standard-deviation))

  (return-stddev (:close ds))

  (def d (tds/->dataset {:a [1.0 2.0 3.0 4.0 5.0]
                         :b [1.0 2.0 3.0 4.0 5.0]
                         :c [1.0 2.0 3.0 4.0 100.0]}))

  (-> d
      :a
      (forward-shift-col 1))

;  
  )



