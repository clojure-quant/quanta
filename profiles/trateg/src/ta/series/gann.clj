(ns ta.series.gann
  (:require
   [tablecloth.api :as tc]
   [ta.helper.ago :refer [xf-ago]]
   [ta.backtest.signal :refer [running-index-vec]]))

;; box
;; a: left point of square
;; b: right point of square
;; bt > at
;; bp > ap

(defn box-dp
  [{:keys [ap bp]}]
  (- bp ap))

(defn box-dt
  [{:keys [at bt]}]
  (- bt at))

(defn box-r [box]
  (/ (box-dp box) (box-dt box)))

;; up/down gann diagonal (for base box)

(defn gann-up [{:keys [at ap] :as box}  t]
  (->> (- t at)
       (* (box-r box))
       (+ ap)))

(defn gann-down [{:keys [at bp] :as box}  t]
  (->> (- t at)
       (* (box-r box))
       (- bp)))

;; quadrant
;; q-t and q-p are positive/negative integers. 
;; 0 returns base box
;; quadrant means base box is shifted:
;; right/left (* q-t box-dt) 
;; up/down (* q-p box-dp) 

(defn get-quadrant [{:keys [ap bp at bt] :as box} qt qp]
  (let [d-p (* qp (box-dp box))
        d-t (* qt (box-dt box))]
    {:ap (+ ap d-p)
     :bp (+ bp d-p)
     :at (+ at d-t)
     :bt (+ bt d-t)}))

(defn- quot-inc [a b]
  (-> (quot a b) inc))

(defn- quot-dec [a b]
  (-> (quot a b) dec))

(defn find-quadrant [{:keys [ap bp at bt] :as box} t p]
  (let [time-right-shift? (> t bt)
        time-left-shift? (< t at)
        price-up-shift? (> p bp)
        price-down-shift? (< p ap)]
    {:qt (cond
           time-right-shift? (quot-inc (- t bt) (box-dt box))
           time-left-shift? (quot-dec (- t at) (box-dt box))
           :else 0)
     :qp (cond
           price-up-shift? (quot-inc (- p bp) (box-dp box))
           price-down-shift? (quot-dec (- p ap) (box-dp box))
           :else 0)}))

(defn sr [box t p]
  (when p
    (let [{:keys [qt qp]} (find-quadrant box t p)
          qbox   (get-quadrant box qt qp)
          up (gann-up qbox t)
          down (gann-down qbox t)
          dp (box-dp box)]
      {:qp qp
       :qt qt
       :up-0 (if (> p up) up (- up dp))
       :up-1 (if (< p up) up (+ up dp))
       :down-0 (if (> p down) (+ down dp) down)
       :down-1 (if (< p down) down (- down dp))})))

(defn algo-gann [ds {:keys [box]}]
  (println "running gann on box: " box)
  (let [idx (:index ds)
        idx (if idx idx (running-index-vec ds))
        px (:close ds)
        px-1 (into [] xf-ago px)
        bands (into [] (map (partial sr box) idx px-1))]
    (-> ds
        (tc/add-columns {:index idx
                         :px-1 px-1
                         :qp (map :qp bands)
                         :qt (map :qt bands)
                         :sr-down-0 (map :down-0 bands)
                         :sr-down-1 (map :down-1 bands)
                         :sr-up-0 (map :up-0 bands)
                         :sr-up-1 (map :up-1 bands)}))))

(comment

  (def box {:ap 100.0
            :at 10
            :bp 250.0
            :bt 30})

  (box-dt box)
  (box-dp box)
  (box-r box)

  (get-quadrant box 1 0)
  (get-quadrant box 0 1)

  ;; unit tests:

  (gann-up box (:at box))
  (gann-up box (:bt box))

  (gann-down box (:at box))
  (gann-down box (:bt box))

  (find-quadrant box 40 240)
  (find-quadrant box 80 240)
  (find-quadrant box 20 340)
  (find-quadrant box 20 440)
  (find-quadrant box 80 440)

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

;  
  )
