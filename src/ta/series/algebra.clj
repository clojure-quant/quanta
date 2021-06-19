(ns ta.series.algebra)

(defn- add [a b]
  (if (or (nil? a) (nil? b))
    nil
    (+ a b)))

(defn series-add
  [a b]
  (vec (map add a b)))

(defn subtract [a b]
  (if (or (nil? a) (nil? b))
    nil
    (- a b)))

(defn series-subtract
  [a b]
  (vec (map subtract a b)))

(defn div [a b]
  (if (or (nil? a) (nil? b))
    nil
    (if (> b 0) (/ a b) 0)))

(defn series-divide
  [a b]
  (vec (map div a b)))

(defn abs
  [n]
  (if (nil? n)
    nil
    (max n (- n))))

(defn series-abs
  [s]
  (vec (map abs s)))

(defn mult-c [n c]
  (if (or (nil? n) (nil? c))
    nil
    (* n c)))

(defn series-mult-c
  [series c]
  (vec (map #(mult-c % c) series)))

(defn diff [coll]
  (map - coll (rest coll)))

(defn cross-up? [p c]
  (if (or (nil? p) (nil? c))
    false
    (and (< p 0) (> c 0))))

(defn cross-down? [p c]
  (if (or (nil? p) (nil? c))
    false
    (and (> p 0) (< c 0))))

(defn cross [cross? series]
  (vec (conj (map cross? series (rest series)) false)))

(def cross-up (partial cross cross-up?))
(def cross-down (partial cross cross-down?))

(defn series-inrange
  [r-min r-max s]
  (vec (map #(if (nil? %)
               false
               (and (> % r-min) (< % r-max)))
            s)))

(defn series>c
  [c s]
  (vec (map #(> % c) s)))

(defn series<c
  [c s]
  (vec (map #(< % c) s)))

(defn series>0
  [s]
  (vec (map #(> % 0) s)))

(defn series<0
  [s]
  (vec (map #(< % 0) s)))

(defn series-and
  [a b]
  (vec (map #(and %1 %2) a b)))

(defn series?
  [s show?]
  (vec (map #(if %2 %1 nil) s show?)))

(defn roll-apply
  "
  A generic function for applying a function to rolling window of a collection.
  Arguments:
  f -- function to be applied
  n -- size of rolling window
  coll -- collection of data
  "
  [f n coll]
  (map f (partition n 1 coll)))

(defn series-roll
  "like roll-apply but result-series has same size as input-timeseries
   the first (n-1) elements will be nil"
  [f n s]
  (let [empty (vec (repeat (- n 1) nil))]
    (into empty (roll-apply f n s))))

(defn start-at
  "override series until start-at time"
  [s idx]
  (let [idx- (- idx 1)
        [_ #_bad good] (split-at idx- s)
        empty (vec (repeat idx- nil))]
    (into empty good)))

(comment

  (series-subtract [100 110 90] [90 95 95])

  (diff [100 110 120 110 90 150])

  (cross-down [100 -110 120 110 90 150])
  (cross-up [100 -110 120 110 90 150])
  (series>0 [100 -110 120 110 -90 -150])
  (series<0 [100 -110 120 110 90 150])

  (let [s [100 -110 120 110 -90 -150]]
    (series-and (cross-down s) (series<0 s)))

  (series? [10 11 12 13 9]
           [false true false true false])

  ; ROLLING SUM TEST
  (defn sum [series]
    (reduce + series))
  (sum [0 1 2 3 4 5 6 7 8 9])
  (roll-apply sum 3 [0 1 2 3 4 5 6 7 8 9])
  (series-roll sum 3 [0 1 2 3 4 5 6 7 8 9])

  (start-at [1 2 3 4 5 6 7 8 9] 4)

  ; comment
  )
