(ns ta.indicator.xf.indicator
  (:require [net.cgrand.xforms :as x]
            [net.cgrand.xforms.rfs :as rfs]))

; stolen from: https://github.com/pangloss/transducers/blob/master/src/xn/transducers.cljc
(defn multiplex
  "Allow a single chain of transducers to branch data out to be processed by multiple transducers, then merged back into a single one.
   Data pipeline looks something like this:
   (comp xform1
         (multiplex xform2 xform3 xform4)
         xform5)
              ,--<xform2>--.
   <xform1>--<---<xform3>--->--<xform5>
              `--<xform4>--'
   "
  [& xforms]
  (if (seq xforms)
    (fn [rf]
      (let [rfs (into [] (map #(% rf)) xforms)]
        (fn
          ([] (doseq [f rfs] (f)))
          ([result]
           (reduce (fn [result f] (f result)) result rfs))
          ([result input]
           (reduce (fn [result f] (f result input)) result rfs)))))
    (map identity)))

(defn sma-xf [n]
  (comp (x/window n rfs/avg #(rfs/avg %1 %2 -1))))

(defn sma [n xs] (into [] (sma-xf n) xs))

(defn ema-xf
  ([n] (ema-xf n (/ 2 (inc n))))
  ([_ #_n alpha] ;todo: this seems stupid as n does not get used
   (comp (x/reductions
          (fn [prev-ema x]
            (if prev-ema
              (-> (- x prev-ema)
                  (* alpha)
                  (+ prev-ema))
              x))
          nil)
         (drop 1))))

(defn ema
  ([n xs]
   (into [] (ema-xf n) xs))
  ([n alpha xs]
   (into [] (ema-xf n alpha) xs)))

(def NaN Double/NaN) ; ##NaN)  ;; ##NaN crashes cljfmt

;;must be passed [high low close]
(defn stochastic-xf [n]
  (comp (x/window n
                  (fn
                    ([] {:lo (sorted-map-by <)
                         :hi (sorted-map-by >)
                         ;;:cl ##NaN
                         })
                    ([acc]
                     (let [{:keys [hi lo cl]} acc
                           hi                 (-> hi first key)
                           lo                 (-> lo first key)
                           diff               (if (= hi lo)
                                                NaN
                                                (- hi lo))]
                       (-> (- cl lo)
                           (/ diff)
                           (* 100))))
                    ([acc x]
                     (let [[high low close] x]
                       (-> acc
                           (assoc :cl close)
                           (update :lo update low (fnil inc 0))
                           (update :hi update high (fnil inc 0))))))

                  (fn [acc out]
                    (let [[high low _ #_close] out
                          f                (fn [acc old]
                                             (if (= (get acc old) 1)
                                               (dissoc acc old)
                                               (update acc old dec)))]
                      (-> acc
                          (update :lo f low)
                          (update :hi f high)))))))

(defn stochastic [n xs]
  (into [] (stochastic-xf n) xs))

(defn tr [high low prevcl]
  (max (- high low)
       (Math/abs (- high prevcl))
       (Math/abs (- low prevcl))))

(defn atr [n highs lows closes]
  (->> (map tr (rest highs) (rest lows) closes)
       (cons (- (first highs) (first lows)))
       (ema n (/ 1 n))))

(defn gains [xs]
  (->> (map (fn [curr last]
              (if (> curr last)
                (- curr last)
                0))
            (rest xs)
            xs)
       (cons 0)))

(defn losses [xs]
  (->> (map (fn [curr last]
              (if (> curr last)
                0
                (- last curr)))
            (rest xs)
            xs)
       (cons 0)))

(defn rsi [n xs]
  (let [g (ema n (/ 1 n) (gains xs))
        l (ema n (/ 1 n) (losses xs))]
    (map (fn [g l]
           (cond
             (and (zero? l) (zero? g)) 0
             (zero? l)                 100
             :else                     (- 100 (/ 100 (inc (/ g l))))))
         g l)))

;; awb99

(defn prct-change-
  "calculates percentage change of prior/current"
  [prior current]
  (if (or (nil? prior) (= 0 prior)) nil
      (* (/ (- current prior) prior) 100.0))) ; absolute change

(defn ago
  "returns a timeseries of values n ago.
   first n items are nil."
  [n ts]
  (concat
   (repeat n nil)
   (drop-last n ts)))

(defn change-n
  "calculates changes in timeseries (relative to n ago)"
  [n ts]
  (into []
        (map #(prct-change- %1 %2)
             (ago n ts); prior
             ts        ; current                                    
             )))

(comment

  (sma 3 [1 2 3 3])

  (defn many [x]
    (into {} (map (fn [[k v]]
                    [k (v x)]) {:a inc :b dec})))

  (many 7)

  (def concat-and-reverse (comp (partial apply str) reverse str))
  (concat-and-reverse "hello" "clojuredocs" "!")

  (def xf (comp (filter odd?) (take 10)))
  (sequence xf (range 1 50))
  ;; => (1 3 5 7 9)

  (def countif (comp count filter))
  (countif even? [2 3 1 5 4])

  (range 16)

  (defn p [& a]
    (println "+" a)
    (apply + a)
  ;(+ a b)
    )

  (defn a [b c]
    (println "a " b c)
    b)

  ;; sum of last 3 items
  (sequence (x/window 4 p a) (range 16))

  (sequence (x/window 3 + -) (range 16))

  (defn chg [a]
    (println "a: " a)
  ;  (x/reduce +)
    ;(first a)
    a
    '(4 5))

  (sequence (x/partition 4 chg) (range 16))

  (sequence
   (let [_n   (atom nil)
         _n-1 (atom nil)]
     (map (fn [x]
            (let [n   @_n
                  n-1 @_n-1]
              (reset! _n-1 n)
              (reset! _n x)
              [(when n (/ x n))
               (when n-1 (/ x n-1))]))))
   (range 1 16))

  (ago 1 (range 10))
  (change-n 1 (range 1 11))

  (drop 2 (range

           10)))




