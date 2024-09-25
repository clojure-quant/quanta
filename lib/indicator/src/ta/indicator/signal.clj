(ns ta.indicator.signal
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tablecloth.api :as tc]))

(defn buyhold-signal-bar-length [n]
  (concat [:buy]
          (repeat (- n 2) :hold)
          [:flat]))

(defn buy-hold-algo [_env _opts bar-ds]
  (tc/add-columns bar-ds {:signal (-> bar-ds tc/row-count buyhold-signal-bar-length)}))

(defn cross-up [price indicator]
  (let [n (count price)]
    ; dtype/clone is essential. otherwise on large datasets, the mapping will not
    ; be done in sequence, which means that the stateful mapping function will fail.
    (dtype/clone
     (dtype/make-reader
      :boolean n
      (if
       (= idx 0)
        false
        (and (> (price idx)
                (indicator idx))
             (<= (price (dec idx))
                 (indicator (dec idx)))))))))

(defn cross-down [price indicator]
  (let [n (count price)]
    ; dtype/clone is essential. otherwise on large datasets, the mapping will not
    ; be done in sequence, which means that the stateful mapping function will fail.
    (dtype/clone
     (dtype/make-reader
      :boolean n
      (if
       (= idx 0)
        false
        (and (< (price idx)
                (indicator idx))
             (>= (price (dec idx))
                 (indicator (dec idx)))))))))

(defn buy-above [p o]
  (if (and p o)
    (cond
      (> p o) :buy
      (< p o) :flat
      :else :hold)
    :hold))

(defn changed-signal-or [signal or-value]
  (let [n (count signal)]
    (dtype/make-reader
     :keyword n
     (if (= 0 idx)
       (signal idx)
       (if (= (signal idx) (signal (dec idx)))
         or-value
         (signal idx))))))

(defn price-when
  "returns the price, when the signal is true.
   otherwise returns NaN."
  [price signal]
  (let [n (count price)]
    (dtype/clone
     (dtype/make-reader
      :float64 n
      (if (signal idx)
        (price idx)
        Double/NaN)))))

(defn prior-int [price n-ago]
  (let [l (count price)]
    (dtype/make-reader
     :int32 l
     (if (>= idx n-ago)
       (price (- idx n-ago))
       0))))

(defn upward-change
  "returns the diff if cur value is >= prev value else 0
   awb99:
     1. this can be solved with price-when and condition: (dfn/>= )
        (-> (diff price) (dtype/min 0.0)))
   "
  [price]
  (let [n (count price)]
    (dtype/make-reader
     :float64 n
     (if (= idx 0)
       0
       (if (>= (price idx) (price (dec idx)))
         (- (price idx) (price (dec idx)))
         0)))))

(defn downward-change
  "returns the diff if cur value is <= prev value else 0"
  [price]
  (let [n (count price)]
    (dtype/make-reader
     :float64 n
     (if (= idx 0)
       0
       (if (<= (price idx) (price (dec idx)))
         (- (price (dec idx)) (price idx))
         0)))))

(defn barcount-while
  "while signal is true, returns # bars since
   signal changed to true. otherwise nil."
  [signal]
  (let [prior (volatile! 0)
        n (count signal)]
    ; dtype/clone is essential. otherwise on large datasets, the mapping will not
    ; be done in sequence, which means that the stateful mapping function will fail.
    (dtype/clone
     (dtype/make-reader
      :int64 n
      (cond

        (= idx 0) ; no prior for idx 0
        0

        (not (signal idx)) ; 0 when-not signal
        0

       ; here it is guaranteed that signal is true.
        (signal (dec idx)) ; count if prior signal also true.
        (vswap! prior inc)

        :else ; prior=false signal=true => reset to 0.
        (vreset! prior 0))))))

(comment

  (buyhold-signal-bar-length 5)

  (cross-up [1 2 3 5 6 7 8 9]
            [4 4 4 4 4 4 4 4])

  (cross-up [1 2 Double/NaN 5 6 7 8 9]
            [4 4 4 4 4 4 4 4])

  (def px-d [9 8 8 6 5 3 2 1])

  px-d
  (prior-int px-d 1)

  (dfn/eq [1 2 3] [1 2 4])

  (let [c [(float 1) 2 2 (float 2) 3]
        p (prior-int c 1)]
    (dfn/eq c p))

  (let [c [1 2 2 2 3]
        p (prior-int c 1)]
    (dfn/eq c p))

  (dfn/eq px-d (prior-int px-d 1))

  (def ind [4 4 4 4 4 4 4 4])
  (cross-down px-d ind)
  (->> (cross-down px-d ind)
       (price-when px-d))

  (def ds (tc/dataset {:signal
                       [true  true  true true false
                        false true true false false]
                       :price
                       [1.0 2.0 3.0 4.0 5.0
                        6.0 7.0 8.0 9.0 10.0]}))

  ds

  (barcount-while (:signal ds))

  (barcount-while [true  true  true true false
                   false true true false false])

  (price-when (:price ds) (:signal ds))

  ;
  )
