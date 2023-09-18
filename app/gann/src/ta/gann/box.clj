(ns ta.gann.box
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [cljc.java-time.duration :as duration]
   [cljc.java-time.local-date-time :as ldt]
   [tick.core :as tick]
   [tick.alpha.interval :as t.i]
   [ta.helper.date :refer [parse-date now-datetime]]))

(defn convert-gann-dates [{:keys [symbol at bt] :as gann}]
  (try
    (assoc gann
           :at (parse-date at)
           :bt (parse-date bt))
    (catch Exception _
      (error "Error converting: " symbol)
      gann)))

(defn exponentialize-prices [{:keys [ap bp] :as box}]
  (assoc box
         :apl (Math/pow 10 ap)
         :apr (Math/pow 10 bp)))

(defn make-root-box [{:keys [ap bp at bt] :as box}]
  (let [ap (Math/log10 ap)
        bp (Math/log10 bp)
        interval (t.i/new-interval at bt)]
    (assoc box
           :ap ap
           :bp bp
           :zoom 1
           :idx-p 0
           :idx-t 0
           :dp (- bp ap)
           :dt (tick/duration interval))))

;; quadrant
;; q-t and q-p are positive/negative integers. 
;; 0 returns base box
;; quadrant means base box is shifted:
;; right/left (* q-t box-dt) 
;; up/down (* q-p box-dp) 

(defn get-quadrant [{:keys [ap bp at bt dp dt idx-p idx-t] :as box} d-idx-t d-idx-p]
  (let [dp-q (* dp d-idx-p)
        dt-q (duration/multiplied-by dt d-idx-t)]
    (assoc box
           :idx-p (+ idx-p d-idx-p)
           :ap (+ ap dp-q)
           :bp (+ bp dp-q)
           :idx-t (+ idx-t d-idx-t)
           :at (tick/>> at dt-q)
           :bt (tick/>> bt dt-q))))

(defn move-right [box]
  (get-quadrant box 1 0))

(defn move-up [box]
  (get-quadrant box 0 1))

(defn move-down [box]
  (get-quadrant box 0 -1))

(defn move-left [box]
  (get-quadrant box -1 0))

;; zoom

(defn zoom-out [{:keys [ap bp at bt dp dt zoom] :as box}]
  (let [dp-2 (* dp 2.0)
        dt-2 (duration/multiplied-by dt 2)]
    {:zoom (inc zoom)
     :idx-p 0
     :idx-t 0
     :ap ap ; a stays the same
     :at at
     :dp dp-2 ; delta is double
     :dt dt-2
     :bp (+ ap dp-2) ; b gets moved by current deltas
     :bt (tick/>> at dt-2)}))

(defn zoom-in [{:keys [ap bp at bt dp dt zoom] :as box}]
  (let [dp-2 (/ dp 2.0)
        dt-2 (duration/divided-by dt 2)]
    {:zoom (dec zoom)
     :idx-p 0
     :idx-t 0
     :ap ap ; a stays the same
     :at at
     :dp dp-2 ; delta is half
     :dt dt-2
     :bp (+ ap dp-2) ; b gets moved by current deltas
     :bt (tick/>> at dt-2)}))

(defn zoom-level [{:keys [ap bp at bt dp dt] :as root-box} zoom]
  (loop [i 1
         box root-box]
    (if (> i zoom)
      box
      (recur (inc i) (zoom-out box)))))

;; boxes in window

(defn move-right-in-window [box dt-start dt-end]
  (->> box
       (iterate move-right)
       (take-while #(tick/< (:at %) dt-end))
       (remove  #(tick/< (:bt %) dt-start))))

(defn move-up-in-window [box px-min px-max]
  (->> box
       (iterate move-up)
       (take-while #(< (:ap %) px-max))
       (remove  #(< (:bp %) px-min))))

(defn left-window-box [box dt-start]
  (->> box
       (iterate move-left)
       (take-while #(tick/> (:bt %) dt-start))
       last))

(defn bottom-window-box [box px-min]
  (->> box
       (iterate move-down)
       (take-while #(> (:bp %) px-min))
       last))

(defn root-box-bottom-left [box dt-start px-min]
  (let [left (left-window-box box dt-start)
        bottom (bottom-window-box box px-min)
        adjust-left (fn [b]
                      (if left
                        (assoc b :at (:at left)
                               :bt (:bt left))
                        b))
        adjust-bottom (fn [b]
                        (if bottom
                          (assoc b :ap (:ap bottom)
                                 :bp (:bp bottom))
                          b))]
    (-> box
        adjust-left
        adjust-bottom)))

(defn get-boxes-in-window [box dt-start dt-end px-min px-max]
  (let [box (root-box-bottom-left box dt-start px-min)]
    (for [idx-t  (->> (move-right-in-window box dt-start dt-end)
                      (map :idx-t))
          idx-p  (->> (move-up-in-window box px-min px-max)
                      (map :idx-p))]
      (get-quadrant box idx-t idx-p))))

;; finder

(defn- quot-inc [a b]
  (-> (quot a b) inc int))

(defn- quot-dec [a b]
  (-> (quot a b) dec int))

(defn find-quadrant [{:keys [ap bp at bt] :as box} t p]
  (let [time-right-shift? (ldt/is-after t bt)
        time-left-shift? (ldt/is-before t at)
        price-up-shift? (> p bp)
        price-down-shift? (< p ap)]
    {:qt (cond
           time-right-shift? (quot-inc (- t bt) (:dt box))
           time-left-shift? (quot-dec (- t at) (:dt box))
           :else 0)
     :qp (cond
           price-up-shift? (quot-inc (- p bp) (:dp box))
           price-down-shift? (quot-dec (- p ap) (:dp box))
           :else 0)}))

(comment

  ; time experiments
  (now-datetime)
  (tick/new-duration 100 :days)
  (tick/new-period 100 :days)

  (tick/duration
   (t.i/new-interval (now-datetime) (now-datetime)))

  (tick/duration
   (t.i/new-interval (tick/today) (tick/tomorrow)))

  (tick/>> (now-datetime) (tick/new-period 2 :days))

  (tick/> (tick/new-period 100 :days) 2)

  (t.i/divide (tick/new-duration 100 :days) 2)
  (t.i/* (tick/new-duration 100 :days) 2)

  (cljc.java-time.duration/divided-by (tick/new-duration 100 :days) 2)
  (cljc.java-time.duration/multiplied-by (tick/new-duration 100 :days) 2)

;(t.i/scale (tick/new-period 2 :days) 3)

  (tick/>> (now-datetime) (:dt root))
 ; 
  )






