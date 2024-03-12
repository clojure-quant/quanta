(ns notebook.strategy.reversal-and-breakout.algo
  (:require
    [tablecloth.api :as tc]
    ;[ta.indicator :refer [wma]]
    [tech.v3.dataset.rolling :as r :refer [rolling mean min max]]
    ))

;; ALGO: https://www.tradingview.com/script/fpWwOup4-Reversal-and-Breakout-Signals-AlgoAlpha/

(defn gauss-summation [n]
  "gauss summation: n(n+1) / 2"
  (/ (* n (+ n 1)) 2))

(defn calc-weight-reversed [y i]
  "for series with desc order like pine script"
  (* (- y i) y))

(defn calc-weight [y i]
  (* y (inc i)))

;; https://www.tradingview.com/pine-script-reference/v5/#fun_ta.wma
(defn wma2 [series len]
  "series with asc index order. (less inefficient than wma function)"
  (let [norm (reduce + (for [i (range len)] (calc-weight len i)))
        sum (reduce + (for [i (range len)] (* (nth series i) (calc-weight len i))))]
    (/ sum norm)))

;; https://www.investopedia.com/ask/answers/071414/whats-difference-between-moving-average-and-weighted-moving-average.asp
(defn wma [series len]
  "series with asc index order. (same as wma2 function but with simplified formula)"
  (let [norm (gauss-summation len)
        sum (reduce + (for [i (range len)] (* (nth series i) (+ i 1))))]
    (/ sum norm)))

(defn wma-ds [ds len of]
  (rolling ds
           {:window-type :fixed
      :window-size len
      :relative-window-position :left}
           {:wma {:column-name [of]
            :reducer (fn [window]
                       (wma window len))}}))

(defn max-ds [ds len of]
  (rolling ds
           {:window-type :fixed
            :window-size len
            :relative-window-position :left}
           {:max (max of)}))

(defn min-ds [ds len of]
  (rolling ds
           {:window-type :fixed
            :window-size len
            :relative-window-position :left}
           {:min (min of)}))

(defn wma-info [ds len]
  (rolling ds {:window-type :fixed
               :window-size len
               :relative-window-position :left}
           {:weight {:column-name [:close]
                        :reducer (fn [window]
                                   (vec (for [i (range 0 (count window))]
                                     (calc-weight len i))))}
               :nth {:column-name [:close]
                     :reducer (fn [window]
                                (vec (for [i (range 0 (count window))]
                                       (nth window i))))}
               :norm {:column-name [:close]
                      :reducer (fn [window]
                                 (reduce + (for [i (range 0 (count window))]
                                             (calc-weight len i))))}
               :sum {:column-name [:close]
                     :reducer (fn [window]
                                (reduce + (for [i (range 0 (count window))]
                                            (* (nth window i) (calc-weight len i)))))}
               :wma {:column-name [:close]
                     :reducer (fn [window]
                                (wma2 window len))}}))

(defn rb-algo [_env {:keys [len vlen threshold] :as opts} bar-ds]
  (let [sh (wma-ds bar-ds len :high)
        sl (wma-ds bar-ds len :low)
        h (max-ds sh len :wma)
        l (min-ds sl len :wma)
        ; TODO
        ;signal {:sh sh}
        ]
    (tc/add-columns bar-ds {:sh (:wma sh)
                            :sl (:wma sl)
                            :h (:max h)
                            :l (:min l)
                            ;:signal signal
                            })))


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

  ds
  (:close ds)
  (wma-ds ds 5 :close)
  (wma-info ds 5)

  (let [sh (wma-ds ds 5 :high)]
    (max-ds sh 5 :wma))

  (let [sl (wma-ds ds 5 :low)]
    (min-ds sl 5 :wma))

  (float (wma2 (:close ds) 5))

  (calc-weight 8 0)

  ;
  )