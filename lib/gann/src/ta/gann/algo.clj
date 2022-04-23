(ns ta.gann.algo
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as dfn]
   [ta.helper.date :refer [parse-date]]
   [ta.helper.ago :refer [xf-ago]]
   [ta.backtest.signal :refer [running-index-vec]]
   [ta.series.signal :refer [prior-int cross-up cross-down price-when]]
   [ta.algo.manager :refer [add-algo]]
   [ta.gann.box :refer [find-quadrant get-quadrant]]))

;; up/down gann diagonal (for base box)

(defn box-r [box]
  nil)

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
          dp (float (:dp box))]
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

(add-algo
 {:name "gann"
  :comment ""
  :algo algo-gann-signal
  :charts [{:sr-up-0 "line"
            :sr-up-1 "line"
            :sr-up-2 "line"
            :sr-down-0 {:type "line" :color "red"}
            :sr-down-1 {:type "line" :color "red"}
            :sr-down-2 {:type "line" :color "red"}}
           {:cross-up-close "column"
            :cross-down-close "column"}
           {:qp "column"
                         ;:qt "column"
            }
                       ;{:index "column"}
                       ; {:qt-jump-close "column"}
           ]
  :options {:w :crypto
            :symbol "BTCUSD"
            :frequency "D"
            :box {:ap 8000.0
                  :at 180
                  :bp 12000.0
                  :bt 225}
            :axes-spec [{:sr-up-0 "line"
                         :sr-up-1 "line"
                         :sr-up-2 "line"
                         :sr-down-0 {:type "line" :color "red"}
                         :sr-down-1 {:type "line" :color "red"}
                         :sr-down-2 {:type "line" :color "red"}}
                        {:cross-up-close "column"
                         :cross-down-close "column"}
                        {:qp "column"
                         ;:qt "column"
                         }
                       ;{:index "column"}
                       ; {:qt-jump-close "column"}
                        ]}})

