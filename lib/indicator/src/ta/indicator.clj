(ns ta.indicator
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.datatype.statistics :as stats]
   [tech.v3.dataset.rolling :as r]
   [ta.indicator.rolling :as roll]
   [tablecloth.api :as tc]
   [ta.indicator.helper :refer [indicator nil-or-nan?]]
   [ta.indicator.signal :refer [upward-change downward-change]]
   [ta.indicator.returns :refer [diff-2col diff-n diff]]
   [ta.helper.ds :refer [has-col]]
   [ta.math.series :refer [gauss-summation]]
   [fastmath.core :as fmath])
  (:import [clojure.lang PersistentQueue]))

(defn prior
  "prior value of a vector. first value returns same value"
  [col]
  (let [ds (tc/dataset {:col col})]
    (:prior (r/rolling ds {:window-size 2
                           :relative-window-position :left}
                       {:prior (r/first :col)}))))

(defn sma
  "Simple moving average"
  [{:keys [n]} v]
  (roll/rolling-window-reduce r/mean n v))

(defn sma2
  "sma indicator, that does not produce any value until
   minimum p bars are present."
  [p]
  (indicator
   [values (volatile! PersistentQueue/EMPTY)
    sum (volatile! 0.0)]
   (fn [x]
     (vswap! sum + x)
     (vswap! values conj x)
     (when (> (count @values) p)
       (vswap! sum - (first @values))
       (vswap! values pop))
     (when (= (count @values) p)
       (/ @sum p)))))

;; https://www.investopedia.com/ask/answers/071414/whats-difference-between-moving-average-and-weighted-moving-average.asp
(defn- wma-f
  "series with asc index order (not reversed like in pine script)"
  [series len norm]
  (let [sum (reduce + (for [i (range len)] (* (nth series i) (+ i 1))))
        ; TODO use vector functions. fix reify
        ;sum (dfn/+
        ;      (dfn/* series
        ;             (range 1 (inc (count series)))))
        ]
    (double (/ sum norm))))

(defn wma
  "Weighted moving average"
  [n col]
  (let [norm (gauss-summation n)]
    (roll/rolling-window-reduce (fn [col-name]
                                  {:column-name col-name
                                   :reducer (fn [w]
                                              (wma-f w n norm))
                                   :datatype :float64})
                                n col)))

(defn- calc-ema-idx
  "EMA-next = (cur-close-price - prev-ema) * alpha + prev-ema"
  [alpha]
  (indicator
   [prev-ema (volatile! nil)]
   (fn [x]
     (let [r (if @prev-ema
               (-> (- x @prev-ema)
                   (* alpha)
                   (+ @prev-ema))
               x)]
       (vreset! prev-ema r)
       r))))

(defn ema
  "Exponential moving average"
  [n col]
  (let [alpha (/ 2.0 (double (inc n)))]
    (into [] (calc-ema-idx alpha) col)))

(defn mma
  "Modified moving average"
  [n col]
  (let [alpha (/ 1.0 (double n))]
    (into [] (calc-ema-idx alpha) col)))

(defn hma
  "Hull Moving Average  http://alanhull.com/hull-moving-average
   A simple application for the HMA, given its superior smoothing, would be to employ the 
   turning points as entry/exit signals. However, it shouldn't be used to generate crossover
   signals as this technique relies on lag."
  [n v]
  (assert (even? n) "n needs to be even")
  (let [n_2 (/ n 2)
        nsqrt (Math/sqrt n)
        wma2 (dfn/* 2.0 (wma n_2 v))
        d (dfn/- wma2 (wma n v))]
    (wma nsqrt d)))

(defn- lma-rfn [n v]
  (let [alpha (dfn/log (range 2 (+ n 2)))
        norm (dfn/sum alpha)
        sum (dfn/sum
             (dfn/* v alpha))]
    (/ sum norm)))

(defn lma
  "Logarithmic moving average"
  [n v]
  (roll/rolling-window-reduce-zero-edge (fn [col-name]
                                          {:column-name col-name
                                           :reducer (fn [w]
                                                      (lma-rfn n w))
                                           :datatype :float64})
                                        n v))

(defn- adjust-src-val
  [x d prev]
  (if (> x (+ prev d))
    (+ x d)
    (if (< x (- prev d))
      (- x d)
      prev)))

(defn- ama-fn
  "a2ma helper"
  [x er prev-a]
  (let [prev-a-nonzero (if (nil-or-nan? prev-a)
                         x
                         prev-a)]
    (+ (* er x)
       (* (- 1 er) prev-a-nonzero))))

(defn arma
  "Autonomous Recursive Moving Average (ARMA)
   https://www.tradingview.com/script/AnmTY0Q3-Autonomous-Recursive-Moving-Average/"
  ([n v]
   (arma n v 3))
  ([n v g]                                               ; TODO: zerolag flag
   (let [tfn (indicator
              [src-d (volatile! [])
               src-ma (volatile! [])
               prev-mad (volatile! [])
               dsum (volatile! 0.0)]
              (fn [i]
                ;; drop last
                (when (>= (count @src-d) n)
                  (vswap! src-d #(vec (rest %))))
                (when (>= (count @src-ma) n)
                  (vswap! src-ma #(vec (rest %))))
                (when (>= (count @prev-mad) n)
                  (vswap! prev-mad #(vec (rest %))))
                ;;
                (let [x (nth v i)
                      prev (if (last @prev-mad)
                             (last @prev-mad)
                             x)
                      xprevn (if (>= i n)
                               (nth v (- i n)))
                      diff (if xprevn
                             (Math/abs (- xprevn prev))
                             0.0)
                      next-sum (vswap! dsum + diff)
                      d (if (> i 0)
                          (/ (* next-sum g) i)
                          0.0)
                      next-src-val (adjust-src-val x d prev)
                      _ (vswap! src-d conj next-src-val)
                      sma0 (if (>= i (dec n))
                             (sma {:n n} @src-d))
                      _ (if sma0
                          (vswap! src-ma conj (last sma0)))
                      sma1 (if (>= i (- (* 2 n) 2))
                             (sma {:n n} @src-ma))
                      next-ma (if (>= i (- (* 2 n) 2))
                                (last sma1))]
                  (vswap! prev-mad conj next-ma)
                  next-ma
                  (if next-ma
                    next-ma
                    Double/NaN))))]
     (into [] tfn (range 0 (count v))))))

(defn a2rma
  "Adaptive Autonomous Recursive Moving Average (A2RMA)
   https://www.tradingview.com/script/4bI1zjc6-Adaptive-Autonomous-Recursive-Moving-Average/"
  ([n v]
   (a2rma n v 3))
  ([n v g]
   (let [diff-n (dfn/abs (diff-n n v))
         diff-1 (dfn/abs (diff v))
         trailing-sum (roll/rolling-window-reduce-zero-edge r/sum n diff-1)
         er (dfn// diff-n
                   trailing-sum)
         tfn (indicator
              [prev-ma0 (volatile! nil)
               prev-ma (volatile! nil)
               dsum (volatile! 0.0)]
              (fn [i]
                (let [x (nth v i)
                      prev-ma-nz (if (nil-or-nan? @prev-ma)
                                   x
                                   @prev-ma)
                      diff (Math/abs (- x prev-ma-nz))
                      next-sum (+ @dsum diff)
                      d (if (> i 0)
                          (/ (* next-sum g) i)
                          0.0)
                      y (adjust-src-val x d prev-ma-nz)
                      cur-er (nth er i)
                      a0 (ama-fn y cur-er @prev-ma0)
                      a (ama-fn a0 cur-er @prev-ma)
                      next-ma0 (if (>= i n) a0)
                      next-ma (if (>= i n) a)]
                  (vreset! prev-ma0 next-ma0)
                  (vreset! prev-ma next-ma)
                  (vreset! dsum next-sum)
                  (if (nil-or-nan? next-ma)
                    Double/NaN
                    next-ma))))]
     (into [] tfn (range 0 (count v))))))

(defn- ehlers-tfn
  [{:keys [c1 c2 c3]}]
  (let [f (fn [x y1 y2]
            (+ (* c1 x)
               (* c2 y1)
               (* c3 y2)))]
    (indicator
     [prev1 (volatile! nil)
      prev2 (volatile! nil)]
     (fn [x]
       (cond
          ; first x
         (and (not @prev1) (not @prev2))
         (let [res (f x 0.0 0.0)]
           (vreset! prev1 res)
           res)

          ; second x
         (and prev1 (not @prev2))
         (let [res (f x @prev1 0.0)]
           (vreset! prev2 @prev1)
           (vreset! prev1 res)
           res)

         :else
         (let [res (f x @prev1 @prev2)]
           (vreset! prev2 @prev1)
           (vreset! prev1 res)
           res))))))

(defn ehlers-supersmoother
  "Ehlers Supersmoother Filter"
  [n v]
  (let [a1 (Math/exp (/ (* -1 (Math/sqrt 2) Math/PI) n))
        b1 (* 2 a1 (Math/cos (/ (* (Math/sqrt 2) Math/PI) n)))
        c3 (* -1 a1 a1)
        c2 b1
        c1 (- 1 c2 c3)]
    (into [] (ehlers-tfn {:c1 c1 :c2 c2 :c3 c3}) v)))

(defn ehlers-gaussian
  "Ehlers Gaussian Filter"
  [n v]
  (let [cycle (/ (* 2 Math/PI) n)
        beta (* 2.415 (- 1 (Math/cos cycle)))
        alpha (+ (* -1 beta)
                 (Math/sqrt (+ (* beta beta) (* 2 beta))))
        c0 (* alpha alpha)
        a1 (* 2 (- 1 alpha))
        a2 (* -1 (- 1 alpha) (- 1 alpha))]
    (into [] (ehlers-tfn {:c1 c0 :c2 a1 :c3 a2}) v)))

(defn- chebyshev-tfn [g]
  (let [f (fn [x y1]
            (+ (* (- 1 g) x)
               (* g y1)))]
    (indicator
     [prev1 (volatile! nil)]
     (fn [x]
       (cond
          ; first
         (not @prev1)
         (let [res (f x x)]
           (vreset! prev1 res)
           res)

         :else
         (let [res (f x @prev1)]
           (vreset! prev1 res)
           res))))))

(defn chebyshev1
  "Chebyshev Type I Filter"
  ([n v]
   (chebyshev1 n v 0.05))
  ([n v r]
   (let [a (Math/cosh (/ (fmath/acosh (/ 1 (- 1 r))) n))
         b (Math/sinh (/ (fmath/asinh (/ 1 r)) n))
         g (/ (- a b) (+ a b))]
     (into [] (chebyshev-tfn g) v))))

(defn chebyshev2
  "Chebyshev Type II Filter"
  ([n v]
   (chebyshev2 n v 0.05))
  ([n v r]
   (let [a (Math/cosh (/ (fmath/acosh (/ 1 r)) n))
         b (Math/sinh (/ (fmath/asinh r) n))
         g (/ (- a b) (+ a b))]
     (into [] (chebyshev-tfn g) v))))

(defn macd
  ([col] (macd {:n 12 :m 26} col))
  ([{:keys [n m]} col]
   (let [ema-short (ema n col)
         ema-long (ema m col)]
     (dfn/- ema-short ema-long))))

(defn rsi
  "Relative strength index"
  [n col]
  (let [gain (upward-change col)
        loss (downward-change col)
        mma-gain (mma n gain)
        mma-loss (mma n loss)
        len (count gain)]
    (dtype/clone
     (dtype/make-reader
      :float64 len
      (if (= 0.0 (mma-loss idx))
        (if (= 0.0 (mma-gain idx)) 0 100)
        (- 100 (/ 100
                  (+ 1 (/ (mma-gain idx)
                          (mma-loss idx))))))))))

(defn hlc3
  "input: bar-ds with (:close :high :low) columns
   output: (high+low+close) / 3"
  [bar-ds]
  (assert (has-col bar-ds :low) "hlc3 needs :low column in bar-ds")
  (assert (has-col bar-ds :high) "hlc3 needs :high column in bar-ds")
  (assert (has-col bar-ds :close) "hlc3 needs :close column in bar-ds")
  (let [low (:low bar-ds)
        high (:high bar-ds)
        close (:close bar-ds)
        hlc3 (dfn// (dfn/+ low high close) 3.0)]
    hlc3))

(defn hl2
  "input: bar-ds with (:high :low) columns
   output: (high+low) / 2"
  [bar-ds]
  (assert (has-col bar-ds :low) "hlc3 needs :low column in bar-ds")
  (assert (has-col bar-ds :high) "hlc3 needs :high column in bar-ds")
  (let [low (:low bar-ds)
        high (:high bar-ds)
        hl2 (dfn// (dfn/+ low high) 2.0)]
    hl2))

(defn tr
  "input: bar-ds with (:low :high :close) columns
   output: Max [(H−L), abs(H−Cprev), abs(L−Cprev)]"
  [bar-ds]
  (assert (has-col bar-ds :low) "tr needs :low column in bar-ds")
  (assert (has-col bar-ds :high) "tr needs :high column in bar-ds")
  (assert (has-col bar-ds :close) "tr needs :close column in bar-ds")
  (let [{:keys [high low close]} bar-ds
        hl (dfn/- high low)
        hc (diff-2col high close (first hl))
        lc (diff-2col low close (first hl))]
    (dfn/max
     hl
     (dfn/abs hc)
     (dfn/abs lc))))

(defn atr
  "atr is a mma(n) on (tr bar)"
  [{:keys [n]} bar-ds]
  (assert n "atr needs :n option")
  (->> (tr bar-ds) (mma n)))

(defn atr-sma
  "a variation of atr 
   (sma n) on (tr bar)"
  [{:keys [n]} bar-ds]
  (assert n "atr needs :n option")
  (roll/rolling-window-reduce r/mean n (tr bar-ds)))

#_(defn atr-mma [{:keys [n]} bar-ds]
    (assert n "atr needs :n option")
    (roll/rolling-window-reduce (fn [col-name]
                                  {:column-name col-name
                                   :reducer (fn [col]
                                              (-> (mma n col) last))
                                   :datatype :float64})
                                n (tr bar-ds)))

(defn add-atr [opts bar-ds]
  (tc/add-column bar-ds :atr (atr opts bar-ds)))

(defn carry-forward
  "carries forward the last non-nil-non-nan value of vector x.
   carries the value forward indefinitely."
  [x]
  (let [p (volatile! Double/NaN); 
        n (count x)]
    ; dtype/clone is essential. otherwise on large datasets, the mapping will not
    ; be done in sequence, which means that the stateful mapping function will fail.
    (dtype/clone
     (dtype/make-reader
      :float64 n
      (let [v (x idx)]
        (if (or (nil? v) (NaN? v))
          @p
          (vreset! p v)))))))

(defn carry-forward-for
  "carries forward the last non-nil-non-nan value of vector x.
   carries the value a maximum of n bars."
  [n x]
  (let [p (volatile! Double/NaN)
        i (volatile! 0)
        l (count x)
        set-value (fn [v]
                    (vreset! i 0)
                    (vreset! p v))]
    ; dtype/clone is essential. otherwise on large datasets, the mapping will not
    ; be done in sequence, which means that the stateful mapping function will fail.
    (dtype/clone
     (dtype/make-reader
      :float64 l
      (let [v (x idx)]
        (if (or (nil? v) (NaN? v))
          (if (< @i n)
            (do (vswap! i inc)
                @p)
            Double/NaN)
          (set-value v)))))))

(comment
  (def ds
    (tc/dataset [{:open 100 :high 120 :low 90 :close 100}
                 {:open 100 :high 120 :low 90 :close 101}
                 {:open 100 :high 140 :low 90 :close 102}
                 {:open 100 :high 140 :low 90 :close 104}
                 {:open 100 :high 140 :low 90 :close 104}
                 {:open 100 :high 160 :low 90 :close 106}
                 {:open 100 :high 160 :low 90 :close 107}
                 {:open 100 :high 160 :low 90 :close 110}]))

  (prior (:close ds))

  (into [] (sma2 3) [4 5 6 7 8 6 5 4 3])

  (tr ds)

  (atr {:n 2} ds)
  (add-atr {:n 5} ds)

  (sma {:n 2} (:close ds))

  (carry-forward [nil Double/NaN 1.0 nil nil -1.0 2.0 nil])

  (carry-forward-for 1 [1.0 nil nil -1.0 2.0 nil])
  (carry-forward-for 1 [1.0 Double/NaN nil -1.0 2.0 nil])
;
  )



