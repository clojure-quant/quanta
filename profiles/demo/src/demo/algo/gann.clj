(ns demo.algo.gann
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as dfn]
   [ta.data.date :refer [parse-date]]
   [ta.helper.ago :refer [xf-ago]]
   [ta.backtest.signal :refer [running-index-vec]]
   [ta.series.signal :refer [prior-int cross-up cross-down price-when]]))



;; up/down gann diagonal (for base box)

(defn gann-up [{:keys [at ap] :as box}  t]
  (->> (- t at)
       (* (box-r box))
       (+ ap)))

(defn gann-down [{:keys [at bp] :as box}  t]
  (->> (- t at)
       (* (box-r box))
       (- bp)))


(comment
  (gann-up box (:at box))
  (gann-up box (:bt box))

  (gann-down box (:at box))
  (gann-down box (:bt box))


 ; 
  )



(defn sr [box t p]
  (when p
    (let [{:keys [qt qp]} (find-quadrant box t p)
          qbox   (get-quadrant box qt qp)
          up (float (gann-up qbox t))
          down (float (gann-down qbox t))
          dp (float (box-dp box))]
      {:qp qp
       :qt qt

       :up-0  (float (+ up dp)) ;(if (> p up) up (- up dp))
       :up-1 (float up) ; (if (< p up) up (+ up dp))
       :up-2 (float (- up dp)) ;(if (< p up) up (+ up dp))

       :down-0  (float (+ down dp)) ;(if (> p down) (+ down dp) down)
       :down-1  (float down)  ;(if (< p down) down (- down dp))
       :down-2  (float (- down dp))})))

(defn log-px [ds]
  (let [{:keys [open high low close]} ds]
    (tc/add-columns ds {:open (dfn/log10 open)
                        :high (dfn/log10 high)
                        :low (dfn/log10 low)
                        :close (dfn/log10 close)})))

(defn algo-gann [ds {:keys [box]}]
  (println "running gann on box: " box)
  (let [ds (log-px ds)
        box (assoc box :ap (Math/log10 (:ap box))
                   :bp (Math/log10 (:bp box)))
        idx (:index ds)
        idx (if idx idx (running-index-vec ds))
        px (:close ds)
        px-1 (into [] xf-ago px)
        bands (into [] (map (partial sr box) idx px-1))
        sr-down-0 (map :down-0 bands)
        sr-down-1 (map :down-1 bands)
        sr-down-2 (map :down-2 bands)
        sr-up-0 (map :up-0 bands)
        sr-up-1 (map :up-1 bands)
        sr-up-2 (map :up-2 bands)]
    (-> ds
        (tc/add-columns {:index idx
                         :px-1 px-1
                         :qp (map :qp bands)
                         :qt (map :qt bands)
                         :sr-down-0 sr-down-0
                         :sr-down-1 sr-down-1
                         :sr-down-2 sr-down-2
                         :sr-up-0 sr-up-0
                         :sr-up-1 sr-up-1
                         :sr-up-2 sr-up-2}))))

(defn algo-gann-signal [ds options]
  (let [ds-study (algo-gann ds options)
        ds-study (tc/select-rows ds-study (range 1 (tc/row-count ds-study)))
        {:keys [close sr-up-0 sr-up-1 qp qt]} ds-study
        qp-1 (prior-int qp 1)
        qt-1 (prior-int qt 1)
        same-qp (dfn/eq qp qp-1)
        sr-cross-up-0 (dfn/and same-qp (cross-up close sr-up-0))
        sr-cross-up-1 (dfn/and same-qp (cross-up close sr-up-1))
        sr-cross-up (dfn/or sr-cross-up-0 sr-cross-up-1)
        qt-jump? (dfn/not-eq qt qt-1)]
    ;(println "sr cross up 0 " sr-cross-up-0)
    (->
     ds-study
     (tc/add-columns {:qp-1 qp-1
                      :same-qp same-qp
                      :cross-up-0 sr-cross-up-0
                      :cross-up-1 sr-cross-up-1
                      :cross-up sr-cross-up
                      :cross-close (price-when close sr-cross-up)
                      :qt-jump? qt-jump?
                      :qt-jump-close (price-when close qt-jump?)})

;:cross (price-when px sr-cross)
     )))
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
