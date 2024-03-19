(ns ta.indicator
  (:require
    [tech.v3.datatype :as dtype]
    [tech.v3.datatype.functional :as fun]
    [tech.v3.datatype.statistics :as stats]
    [tech.v3.dataset.rolling :as r :refer [rolling mean]]
    [tablecloth.api :as tc]
    [ta.helper.ds :refer [has-col]]
    [ta.math.series :refer [gauss-summation]]))

(defn prior [{:keys [of]
              :or {of :close}}
             bar-ds]
  (:prior (rolling bar-ds {:window-size 1
                     :relative-window-position :left}
                 {:prior (r/last of)})))


(defn sma [{:keys [n of]
            :or {of :close
                 n 100}}
           bar-ds]
  (:sma (rolling bar-ds {:window-size n
                     :relative-window-position :left}
                 {:sma (mean of)})))

;; https://www.investopedia.com/ask/answers/071414/whats-difference-between-moving-average-and-weighted-moving-average.asp
(defn wma-f [series len & [norm]]
  "series with asc index order (not reversed like in pine script)"
  (let [sum (reduce + (for [i (range len)] (* (nth series i) (+ i 1))))
        norm (if norm norm (gauss-summation len))]
    (double (/ sum norm))))

(defn wma [n of ds]
  (let [norm (gauss-summation n)]
    (:wma (r/rolling ds {:window-type :fixed
                         :window-size n
                         :relative-window-position :left}
                     {:wma {:column-name [of]
                            :reducer (fn [window]
                                       (wma-f window n norm))}}))))

; TODO:
(defn ema-reader [price indicator]
  (let [n (count price)
        k (/ 2 (+ n 1))]
    (dtype/make-reader
      :float64 n
      (if (= idx 0)
        (stats/mean price)  ; SMA
        (+ (* k
              (- (price idx) (indicator (dec idx))))
           (indicator (dec idx)))))))

(defn ema [n of ds]
  ; 1) EMA initial value = SMA initial
  ; 2) k = (/ 2 (+ n 1))
  ; 3) EMA-next = (cur-close-price - prev-ema) * k + prev-ema
  ;
  ; Formula transformations:
  ;(cur-p - prev-ema) * k + prev-ema
  ;=> cur-p * k - prev-ema * k + prev-ema
  ;=> cur-p * k + prev-ema * (1 - k)

  ; TODO:
  (let [prev (sma {:n n} ds)
        k (/ 2 (+ n 1))]
    (:ema (r/rolling ds {:window-type :fixed
                         :window-size n
                         :relative-window-position :left}
                     {:ema {:column-name [of]
                            :reducer (fn [window]
                                       (ema-reader of window))}}))))



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



 