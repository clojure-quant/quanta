(ns ta.indicator
  (:require
    [tech.v3.datatype :as dtype]
    [tech.v3.datatype.functional :as fun]
    [tech.v3.datatype.statistics :as stats]
    [tech.v3.dataset.rolling :as r :refer [rolling mean]]
    [tablecloth.api :as tc]
    [ta.indicator.helper :refer [indicator]]
    [ta.indicator.signal :refer [upward-change downward-change]]
    [ta.helper.ds :refer [has-col]]
    [ta.math.series :refer [gauss-summation]]))

(defn prior [{:keys [of]
              :or {of :close}}
             bar-ds]
  (:prior (rolling bar-ds {:window-size 1
                     :relative-window-position :left}
                 {:prior (r/last of)})))


(defn sma
  "Simple moving average"
  [{:keys [n of]
            :or {of :close
                 n 100}}
           bar-ds]
  (:sma (rolling bar-ds {:window-size n
                     :relative-window-position :left}
                 {:sma (mean of)})))

;; https://www.investopedia.com/ask/answers/071414/whats-difference-between-moving-average-and-weighted-moving-average.asp
(defn wma-f [series len norm]
  "series with asc index order (not reversed like in pine script)"
  (let [
        sum (reduce + (for [i (range len)] (* (nth series i) (+ i 1))))
        ; TODO use vector functions. fix reify
        ;sum (fun/+
        ;      (fun/* series
        ;             (range 1 (inc (count series)))))
        ]
    (double (/ sum norm))))

(defn wma
  "Weighted moving average"
  [n of ds]
  (let [norm (gauss-summation n)]
    (:wma (r/rolling ds {:window-type :fixed
                         :window-size n
                         :relative-window-position :left}
                     {:wma {:column-name [of]
                            :reducer (fn [window]
                                       (wma-f window n norm))}}))))

(defn- calc-ema-idx
  "EMA-next = (cur-close-price - prev-ema) * alpha + prev-ema"
  [alpha]
  (indicator [prev-ema (volatile! nil)]
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
  [n of ds]
  (let [alpha (/ 2 (inc n))
        series (of ds)]
    (into [] (calc-ema-idx alpha) series)))

(defn mma
  "Modified moving average"
  [n of ds]
  (let [alpha (/ 1 n)
        series (of ds)]
    (into [] (calc-ema-idx alpha) series)))

(defn macd
  ([of ds] (macd 12 26 of ds))
  ([n m of ds]
   (let [ema-short (ema n of ds)
         ema-long (ema m of ds)]
     (fun/- ema-short ema-long))))

(defn rsi
  "Relative strength index"
  [n of ds]
  (let [gain (upward-change (of ds))
        loss (downward-change (of ds))
        mma-gain (mma n of (tc/dataset {of gain}))
        mma-loss (mma n of (tc/dataset {of loss}))
        len (count gain)]
    (dtype/make-reader
      :float64 len
      (if (= 0.0 (mma-loss idx))
        (if (= 0.0 (mma-gain idx)) 0 100)
        (- 100 (/ 100
                  (+ 1 (/ (mma-gain idx)
                          (mma-loss idx)))))))))

(defn hlc3 [bar-ds]
  (assert (has-col bar-ds :low) "hlc3 needs :low column in bar-ds")
  (assert (has-col bar-ds :high) "hlc3 needs :high column in bar-ds")
  (assert (has-col bar-ds :close) "hlc3 needs :close column in bar-ds")
  (let [low (:low bar-ds)
        high (:high bar-ds)
        close (:close bar-ds)
        hlc3 (fun// (fun/+ low high close) 3.0)]
    hlc3))

(defn tr [bar-ds]
  (assert (has-col bar-ds :low) "tr needs :low column in bar-ds")
  (assert (has-col bar-ds :high) "tr needs :high column in bar-ds")
  (let [low (:low bar-ds)
        high (:high bar-ds)
        hl (fun/- high low)]
    hl))

(defn atr [{:keys [n]} bar-ds]
  (assert n "atr needs :n option")
  (let [ds (tc/add-column bar-ds :tr (tr bar-ds))]
    (:atr (rolling ds {:window-size n
                       :relative-window-position :left}
                   {:atr (mean :tr)}))))

(defn add-atr [opts bar-ds]
  (tc/add-column bar-ds :atr (atr opts bar-ds)))

(defn atr-band [{:keys [atr-n atr-m]} bar-ds]
  (assert atr-n "atr-band needs :atr-n option")
  (assert atr-m "atr-band needs :atr-m option")
  (let [atr-vec (atr {:n atr-n} bar-ds)
        atr-band (fun/* atr-vec atr-m)
        band-mid (prior {:of :close} bar-ds)
        band-upper (fun/+ band-mid atr-band)
        band-lower (fun/- band-mid atr-band)]
    {:atr-band-atr atr-vec
     :atr-band-mid band-mid
     :atr-band-upper band-upper
     :atr-band-lower band-lower}))

(defn add-atr-band [opts bar-ds]
  (tc/add-columns bar-ds (atr-band opts bar-ds)))

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

  (tr ds)

  (atr {:n 2} ds)
  (add-atr {:n 5} ds)

  (add-atr-band {:atr-n 5 :atr-m 2.0} ds)

  (sma {:n 2} ds)


 ; 
  )



 