(ns demo.lib.gann
  (:require
   [cljc.java-time.duration :as duration]
   [tick.core :as tick]
   [tick.alpha.interval :as t.i]
   [ta.data.date :refer [parse-date now-datetime]]))

;; data

(def btc
  {:ap 0.01
   :at (parse-date "2010-07-18")
   :bp 11.0
   :bt (parse-date "2014-04-13")})

(def btc-option2
  {:ap 0.01
   :at (parse-date "2010-07-18")
   :bp 0.04
   :bt (parse-date "2011-04-17")})

;; box
;; a: left point of square
;; b: right point of square
;; bt > at
;; bp > ap

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

(comment
  (def root (make-root-box btc))
  root
;
  )

(defn zoom-root [{:keys [ap bp at bt dp dt zoom] :as box}]
  (let [dp-2 (* dp 2)
        dt-2 (duration/multiplied-by dt 2)]
    {:zoom (inc zoom)
     :idx-p 0
     :idx-t 0
     :ap ap ; a stays the same
     :at at
     :dp dp-2 ; delta is double
     :dt dt-2
     :bp (+ bp dp-2) ; b gets moved by current deltas
     :bt (tick/>> at dt-2)}))

(defn zoom-level [{:keys [ap bp at bt dp dt] :as root-box} zoom]
  (loop [i 1
         box root-box]
    (if (> i zoom)
      box
      (recur (inc i) (zoom-root box)))))

(comment
  (zoom-root root)
  (zoom-level root 1)
  (zoom-level root 2)
  (zoom-level root 3)

  (clojure.pprint/print-table
   [root
    (zoom-root root)
    (zoom-root (zoom-root root))
    (zoom-root (zoom-root (zoom-root root)))])
;  
  )

;; quadrant
;; q-t and q-p are positive/negative integers. 
;; 0 returns base box
;; quadrant means base box is shifted:
;; right/left (* q-t box-dt) 
;; up/down (* q-p box-dp) 

(defn get-quadrant [{:keys [ap bp at bt dp dt] :as box} d-idx-t d-idx-p]
  (let [dp-q (* dp d-idx-p)
        dt-q (duration/multiplied-by dt d-idx-t)]
    (assoc box
           :idx-p (+ (:idx-p box) d-idx-p)
           :ap (+ ap dp-q)
           :bp (+ bp dp-q)
           :idx-t (+ (:idx-t box) d-idx-t)
           :at (tick/>> at dt-q)
           :bt (tick/>> bt dt-q))))

(defn move-right [box]
  (get-quadrant box 1 0))

(defn move-up [box]
  (get-quadrant box 0 1))

(comment

  root
  (get-quadrant root 0 0)
  (get-quadrant root 1 0)
  (get-quadrant root 2 0)
  (get-quadrant root 3 0)

  (get-quadrant root 0 1)
  (get-quadrant root 0 2)

  (get-quadrant box 1 0)
  (get-quadrant box 0 1)
  (get-quadrant box 1000 1)

  (move-right root)
  (move-up root)

  (->> 5
       (iterate inc)
       (take 4))

  (->> root
       (iterate move-right)
       (take-while #(tick/< (:at %) (now-datetime)))
       (clojure.pprint/print-table))

  (->> root
       (iterate zoom-root)
       (take 4)
       (clojure.pprint/print-table))

 ; 
  )


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

(comment
  (->> (move-right-in-window root (parse-date "2021-01-01") (parse-date "2021-12-31"))
      ;(clojure.pprint/print-table)
       (map :idx-t))

  (-> (move-up-in-window root (Math/log10 1000) (Math/log10 70000))
      (clojure.pprint/print-table))
 ; 
  )


(defn get-boxes-in-window [box dt-start dt-end px-start px-end]
  (for [idx-t  (->> (move-right-in-window box dt-start dt-end)
                    (map :idx-t))
        idx-p  (->> (move-up-in-window root px-start px-end)
                    (map :idx-p))]
    (get-quadrant root idx-t idx-p)))


(comment
  (-> (get-boxes-in-window root (parse-date "2021-01-01") (parse-date "2021-12-31")
                           (Math/log10 1000) (Math/log10 70000))
      (clojure.pprint/print-table))
 ; 
  )




;; finder

(defn- quot-inc [a b]
  (-> (quot a b) inc int))

(defn- quot-dec [a b]
  (-> (quot a b) dec int))

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

(comment
  (find-quadrant box 40 240)
  (find-quadrant box 80 240)
  (find-quadrant box 20 340)
  (find-quadrant box 20 440)
  (find-quadrant box 80 440)
;  
  )


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





