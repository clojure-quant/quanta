(ns notebook.gann.algo
  (:require
   [ta.helper.date :refer [parse-date now-datetime]]
   [ta.gann.algo :refer [sr]]
   [goldly.scratchpad :refer [show! show-as clear!]]))

(comment

  (sr box 29 240)
  (sr box 20 200)
  (sr box 85 700)
  (sr box 80 440)

  (-> (tc/dataset {:index [-80 -40  -6    0   5  10  15  20  25  30  35  40  45  50  55  60   80]
                   :close [230 230  230 230 230 260 265 270 300 280 290 330 350 400 430 430 2455]})
      (algo-gann {:box box}))

  (require '[ta.backtest.study :refer [run-study]])

  (run-study algo-gann {:w :crypto
                        :frequency "D"
                        :symbol "BTCUSD"
                        :box box})

  (defn show-meta [ds]
    (->> ds tc/columns (map meta) (map (juxt :name :datatype))))

  (->
   (run-study algo-gann-signal {:w :crypto
                                :frequency "D"
                                :symbol "BTCUSD"
                                :box {:ap 8000.0
                                      :at 180
                                      :bp 12000.0
                                      :bt 225}})
   :ds-study
   (tc/select-columns [:date :close
                       :sr-up-0 :sr-up-1 :sr-up-2
                       ;:qp :qp-1 
                       :same-qp
                       :cross-up-0
                       :cross-up-1
                       :cross-up
                       :cross-close
                       ;:qt-jump? 
                       :qt-jump-close])
   (tc/select-rows (fn [row] (:cross-up row)))
   ;(tc/select-rows (fn [row] (:qt-jump? row) ))
   (show-meta))

  (map-indexed (fn [i v] [i v])  [:a :b :c])

;  
  )