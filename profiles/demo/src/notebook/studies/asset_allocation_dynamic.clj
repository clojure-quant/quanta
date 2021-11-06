(ns notebook.studies.asset-allocation-dynamic
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [ta.helper.print :refer [print-all]]
   [ta.helper.ds :refer [ds->str ds->map]]
   ;[ta.helper.stats :refer [mean]]
   [ta.warehouse :as wh]
   ;[medley.core :as m]
   ;[clj-time.core :as t]
   ;[clj-time.coerce :as tc]
   ;[ta.series.indicator :as ind :refer [change-n]]
   ;[ta.model.trade :refer [set-ts get-ts trade]]
   ;[ta.model.rank :refer [rank]]
   ;[ta.model.stats :refer [gauntlet2]]
   ;[ta.series.compress :refer [compress group-month]]
   [ta.backtest.date :refer [month-begin? month-end? add-year-and-month-date-as-local-date]]
   [ta.series.indicator :refer [sma]]
   ;[ta.backtest.signal :refer [buy-above]]
   [ta.backtest.roundtrip-backtest :refer [run-backtest run-backtest-parameter-range]]
   [ta.backtest.roundtrip-stats :refer [roundtrip-performance-metrics backtests->performance-metrics]]
   [ta.backtest.print]
   [reval.document.manager :refer [save]];  save [data ns name-no-ext format]
   ))

;;; # Dynamic Asset Allocation
;;; 
;;; Backtest of a sector rotation strategy 
;;; described in https://drive.google.com/open?id=1FB1IpTC6WzfQkNoScCL3mQmOUVqTSENy

;; column views

(def col-study [:date :close :sma-v  :signal
                   ; :signal2
                   ;:sma-r
                ])

(def col-rt
  (concat [:year-month :symbol :sma-r] ta.backtest.print/cols-rt))

;; single symbol

(defn long-during-month-when-above-ma [mb? me? close sma-v]
  (if me?
    :flat  ; flat on month-end
    (if mb?
      (if (> close sma-v)
        :buy  ; long when above ma
        :flat) ; flat when below ma
      :hold ;  hold when not month-begin or month-end
      )))

(defn trade-sma-monthly
  [ds-bars {:keys [sma-length] #_:as #_options
            :or {sma-length 30}}]
  (let [{:keys [date close]} ds-bars
        sma-v (sma sma-length close)
        sma-r (dfn// close sma-v)
        m-b (month-begin? date)
        m-e (month-end? date)]
    (-> ds-bars
        add-year-and-month-date-as-local-date
        (tc/add-columns {:sma-v sma-v
                         :sma-r sma-r
                         :m-b m-b
                         ;:signal1 (dtype/emap buy-above :object close sma-v)
                         :signal (dtype/emap long-during-month-when-above-ma :object m-b m-e close sma-v)}))))

(-> (wh/load-symbol :stocks "D" "QQQ")
    (trade-sma-monthly {:sma-length 20})
    (tc/select-columns col-study)
    (save *ns* "qqq-sma" :txt)
    (save *ns* "qqq-sma" :edn))

(defn calc-rts-symbol [options]
  (-> (run-backtest trade-sma-monthly options)
      :ds-roundtrips
      (tc/select-rows (fn [{:keys [trade]}]
                        (= trade :buy)))))

(calc-rts-symbol {:w :stocks
                  :frequency "D"
                  :symbol "QQQ"
                  :sma-length 20
                  :entry-cols [:symbol :sma-r :year :month]})

;(tc/select-columns col-rt)

;; roundtrips for all symbols

(defn take-max [ds max-pos sort-by where]
  (let [c (tc/row-count ds)
        nr-available (min max-pos c)
        middle-idx-start (-> (- c nr-available)
                             (/ 2)
                             (long)) ; range has to be made of longs, otherwise tc cannot filter
        r (case where
            :bottom [0 nr-available]
            :top    [(- c nr-available) c]
            :middle [middle-idx-start (long (+ middle-idx-start nr-available))])
        rg (apply range r)]
  ;(println "range: " r "vals: " rg " type: " (type (first r)) (type (last r)) )
    (-> ds
        (tc/order-by sort-by)
        (tc/select-rows rg))))

(defn max-positions [ds-rts max-pos sort-by where]
  (as-> ds-rts x
    (tc/group-by x [:year-month])
    (:data x)
    (map #(take-max % max-pos sort-by where) x)
    (apply tc/concat x)))

; make sure we take the highest :sma-r
(-> (tc/dataset {:year-month [202001 202001 202001 202001 202001 202001 202001]
                 :sma-r [3 4 6 2 3 4 5]})
    (max-positions 2 [:sma-r] :middle)
      ;(max-positions 2 [:sma-r] :bottom)
      ;(max-positions 2 [:sma-r] :top)
    )
(defn trade-sma-monthly-portfolio
  [algo {:keys [list max-pos sort-by where]
         :or {max-pos 0
              sort-by [:sma-r]
              where :middle}
         :as options}]
  (let [symbols (wh/load-list list)
        rt-seq (map (fn [s]
                      ;(println "calculating: " s)
                      (calc-rts-symbol (assoc options :symbol s))) symbols)
        ds-rts-all (-> (apply tc/concat rt-seq)
                       (tc/order-by [:year-month :symbol]))
        ds-rts (if (> max-pos 0)
                 (max-positions ds-rts-all max-pos sort-by where)
                 ds-rts-all)
        ds-rts (tc/set-dataset-name ds-rts (str "max-pos-" max-pos))]
    {:ds-roundtrips ds-rts}))

(def o {:w :stocks
        :frequency "D"
        :list "fidelity-select"
        :backtest-runner trade-sma-monthly-portfolio
        :max-pos 5
        :sort-by [:sma-r]
        :where :middle
        :symbol ""
        :sma-length 60
        :entry-cols [:symbol :sma-r :year-month]})

(def backtest-all (trade-sma-monthly-portfolio nil (assoc o :max-pos 0)))
(def backtest-5 (trade-sma-monthly-portfolio nil (assoc o :max-pos 5)))

(roundtrip-performance-metrics backtest-5)
(roundtrip-performance-metrics backtest-all)

^:R
[:p/table [{:avg-win-log 0.01641819674252593
            :avg-bars-win 19.92799213565987
            :win-nr-prct 57.954707306651464
            :pf 1.3448179108532547
            :avg-log 0.002439722908207893
            :pl-log-cum 17.129294538527617
            :avg-loss-log -0.016828031167618693
            :trades 7021
            :p "max-pos-0"
            :avg-bars-loss 19.97831978319783}]]

^:R
[:p/table (->> (roundtrip-performance-metrics backtest-all)
               ds->map
               first
               (merge {}))]

(:ds-roundtrips backtest-5)

^:R
[:div
 ['line-plot {:cols [:close]} (:ds-roundtrips backtest-5)]
 ['text (ds->str (:ds-roundtrips backtest-5))]]

^:R
[:div
 ['line-plot {:cols [:close]} (:ds-roundtrips backtest-5)]
 ['text (ds->str (:ds-roundtrips backtest-5))]]

  ; 1. load resources
  ;    1-nippy
  ;    2-text    http-get-text  /api/notebook/resource/demo.studies.asset-allocation-dynamic/2

(-> backtest-5
    :ds-roundtrips
    (tc/select-columns col-rt)
    (save *ns* "top5-sma-60" :edn)
      ;(save "top5-sma-60" :nippy)
      ;print-all
    )

(-> (run-backtest-parameter-range
     nil o
     :max-pos [1 2 3 4 5 10 15 0])
    backtests->performance-metrics)

  ;; result:
  ;; when taking the lowest ratios, for max-pos=1 we get the BEST result
  ;; this means that by taking the least trendy stock, that this trend might
  ;; be in the beginning ???

; | :p | :trades |        :pf | :pl-log-cum |    :avg-log | :win-nr-prct | :avg-win-log | :avg-loss-log | :avg-bars-win | :avg-bars-loss |
; |---:|--------:|-----------:|------------:|------------:|-------------:|-------------:|--------------:|--------------:|---------------:|
; |  1 |     255 | 0.92527074 | -0.24138932 | -0.00094662 |  52.15686275 |   0.02247215 |   -0.02647692 |   19.87969925 |    19.98360656 |
; |  2 |     502 | 1.04259002 |  0.23097847 |  0.00046012 |  53.58565737 |   0.02101963 |   -0.02327597 |   19.85873606 |    20.00429185 |
; |  3 |     748 | 1.05487110 |  0.42800533 |  0.00057220 |  54.94652406 |   0.02001995 |   -0.02314598 |   19.85401460 |    20.01186944 |
; |  4 |     988 | 1.06831708 |  0.68423684 |  0.00069255 |  55.87044534 |   0.01938377 |   -0.02297157 |   19.86956522 |    20.00229358 |
; |  5 |    1225 | 1.08555584 |  1.04857711 |  0.00085598 |  56.00000000 |   0.01939451 |   -0.02273851 |   19.87609329 |    19.99257885 |
; | 10 |    2364 | 1.26049552 |  5.22373502 |  0.00220970 |  57.02199662 |   0.01875134 |   -0.01973727 |   19.91468843 |    19.96751969 |
; | 15 |    3431 | 1.30892157 |  8.41670162 |  0.00245313 |  57.53424658 |   0.01806592 |   -0.01869968 |   19.90121581 |    19.98284146 |
; |  0 |    7021 | 1.34481791 | 17.12929454 |  0.00243972 |  57.95470731 |   0.01641820 |   -0.01682803 |   19.92799214 |    19.97831978 |

(-> (run-backtest-parameter-range
     nil (assoc o :sma-length 90
                :where :top)
     :max-pos [1 2 3 4 5 10 15 0])
    backtests->performance-metrics)

 ; 90 days sma works much better.   

; | :p | :trades |        :pf | :pl-log-cum |   :avg-log | :win-nr-prct | :avg-win-log | :avg-loss-log | :avg-bars-win | :avg-bars-loss |
; |---:|--------:|-----------:|------------:|-----------:|-------------:|-------------:|--------------:|--------------:|---------------:|
; |  1 |     258 | 1.05863166 |  0.17169585 | 0.00066549 |  53.87596899 |   0.02230271 |   -0.02460825 |   19.88489209 |    20.00000000 |
; |  2 |     506 | 1.15088955 |  0.78327342 | 0.00154797 |  55.53359684 |   0.02126090 |   -0.02307128 |   19.87900356 |    19.99555556 |
; |  3 |     754 | 1.16957656 |  1.25372204 | 0.00166276 |  56.10079576 |   0.02044202 |   -0.02233610 |   19.90307329 |    19.96072508 |
; |  4 |     997 | 1.16409455 |  1.55547873 | 0.00156016 |  55.96790371 |   0.01977534 |   -0.02159262 |   19.91577061 |    19.93394077 |
; |  5 |    1235 | 1.16206540 |  1.86822223 | 0.00151273 |  55.62753036 |   0.01949899 |   -0.02103573 |   19.89956332 |    19.96350365 |
; | 10 |    2389 | 1.31324122 |  6.16849632 | 0.00258204 |  57.76475513 |   0.01873984 |   -0.01951683 |   19.88260870 |    20.00991080 |
; | 15 |    3462 | 1.39451215 | 10.32867035 | 0.00298344 |  58.34777585 |   0.01807403 |   -0.01815594 |   19.88267327 |    20.02149792 |
; |  0 |    7182 | 1.39109306 | 19.27119228 | 0.00268326 |  58.31244779 |   0.01636734 |   -0.01645799 |   19.89398281 |    20.00033400 |

(-> (run-backtest-parameter-range
     nil (assoc o :sma-length 90
                :where :bottom)
     :max-pos [1 2 3 4 5 10 15 0])
    backtests->performance-metrics
    (save *ns* "metrics-middle-sma90" :txt))

; | :p | :trades |        :pf | :pl-log-cum |   :avg-log | :win-nr-prct | :avg-win-log | :avg-loss-log | :avg-bars-win | :avg-bars-loss |
; |---:|--------:|-----------:|------------:|-----------:|-------------:|-------------:|--------------:|--------------:|---------------:|
; |  1 |     258 | 1.31198847 |  0.60859812 | 0.00235891 |  56.97674419 |   0.01741024 |   -0.01757394 |   19.84353741 |    20.06306306 |
; |  2 |     506 | 1.25198557 |  0.99693540 | 0.00197023 |  56.12648221 |   0.01744104 |   -0.01782126 |   19.85563380 |    20.02702703 |
; |  3 |     754 | 1.17926206 |  1.09288647 | 0.00144945 |  56.63129973 |   0.01683717 |   -0.01864399 |   19.86651054 |    20.00917431 |
; |  4 |     997 | 1.21114018 |  1.66635884 | 0.00167137 |  57.27181545 |   0.01674002 |   -0.01852627 |   19.86690018 |    20.00000000 |
; |  5 |    1235 | 1.22551578 |  2.20795726 | 0.00178782 |  57.24696356 |   0.01697123 |   -0.01854299 |   19.86421499 |    20.01325758 |
; | 10 |    2389 | 1.27667792 |  4.97987322 | 0.00208450 |  58.97865216 |   0.01630850 |   -0.01836613 |   19.88218595 |    20.01428571 |
; | 15 |    3462 | 1.31739688 |  7.90826511 | 0.00228431 |  58.14558059 |   0.01630615 |   -0.01719532 |   19.89021361 |    20.01035197 |
; |  0 |    7182 | 1.39109306 | 19.27119228 | 0.00268326 |  58.31244779 |   0.01636734 |   -0.01645799 |   19.89398281 |    20.00033400 |

;; bottom works with 1-2 stocks. So it means the 2 stocks on bottom are good?

;; since bot TOP and BOTTOM do not improve results, it must be that MIDDLE ratio works best !!

(-> (run-backtest-parameter-range
     nil (assoc o :sma-length 90
                :where :middle)
     :max-pos [1 2 3 4 5 10 15 0])
    backtests->performance-metrics)

; | :p | :trades |        :pf | :pl-log-cum |   :avg-log | :win-nr-prct | :avg-win-log | :avg-loss-log | :avg-bars-win | :avg-bars-loss |
; |---:|--------:|-----------:|------------:|-----------:|-------------:|-------------:|--------------:|--------------:|---------------:|
; |  1 |     258 | 1.19045791 |  0.41617286 | 0.00161307 |  56.20155039 |   0.01793993 |   -0.01933732 |   19.98620690 |    19.87610619 |
; |  2 |     506 | 1.20408690 |  0.85515708 | 0.00169003 |  57.90513834 |   0.01721952 |   -0.01967212 |   19.97952218 |    19.86384977 |
; |  3 |     754 | 1.28659170 |  1.67309365 | 0.00221896 |  58.48806366 |   0.01703173 |   -0.01865144 |   19.98185941 |    19.85303514 |
; |  4 |     997 | 1.31585134 |  2.35075206 | 0.00235783 |  58.67602808 |   0.01674076 |   -0.01806454 |   19.96068376 |    19.87135922 |
; |  5 |    1235 | 1.27843995 |  2.58570494 | 0.00209369 |  57.89473684 |   0.01660434 |   -0.01785846 |   19.95664336 |    19.88846154 |
; | 10 |    2389 | 1.36475556 |  6.07395988 | 0.00254247 |  58.09962327 |   0.01637327 |   -0.01663550 |   19.93587896 |    19.93706294 |
; | 15 |    3462 | 1.37703211 |  8.97710048 | 0.00259304 |  58.14558059 |   0.01628764 |   -0.01643196 |   19.91852956 |    19.97101449 |
; |  0 |    7182 | 1.39109306 | 19.27119228 | 0.00268326 |  58.31244779 |   0.01636734 |   -0.01645799 |   19.89398281 |    20.00033400 |

  ;; so it is not the middle pos that makes it.
  ;; when there are more trades, it is better.
  ;; so we need a market breath indicator!

; 

;; Use ROC instead of SMA
;; Exit trades also with exit-rank ?
;; (list-plot (:equity pf) :joined true :color "blue")
;; (gauntlet2 pf)

(def lists ["bonds"
            "commodity-sector"
            "commodity-industry"
            "equity-region"
            ;"equity-region-country" 
            "equity-sector"
            ;"equity-sector-industry" ; not yet finished
            "equity-style"
            "currency"])

;(def crisis [{:start (tc/to-long (t/date-time 2020 03 03))
;              :end (tc/to-long (t/date-time 2020 03 20))
;              :color "orange"}])
;crisis

