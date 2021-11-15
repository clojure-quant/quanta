(ns ta.gann.gann
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [clojure.edn :as edn]
   [modular.config :refer [get-in-config]]
   [ta.helper.date :refer [parse-date now-datetime]]
   [cljc.java-time.duration :as duration]
   [tick.core :as tick]
   [tick.alpha.interval :as t.i]))

(defn convert-gann-dates [{:keys [symbol at bt] :as gann}]
  (try
    (assoc gann
           :at (parse-date at)
           :bt (parse-date bt))
    (catch Exception _
      (error "Error converting: " symbol)
      gann)))

(comment

  (def btc
    (convert-gann-dates
     {:symbol "BTCUSD"
      :ap 0.01
      :at "2010-07-18"
      :bp 11.0
      :bt "2014-04-13"}))

 ; 
  )
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
  (move-left root)
  (move-down root)

  (-> root move-left move-left)
  (-> root move-down move-down)

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

(comment
  root

  (->> (iterate move-left root)
       (take 5)
       last)

  (->> (iterate move-down root)
       (take 5)
       last)

  (->> (iterate move-down root)
       (take-while #(> (:bp %) -50))
       last)

  (left-window-box root (parse-date "2020-01-01")) ; nil -> no adjustment
  (left-window-box root (parse-date "1980-01-01")) ; moved root box

  (bottom-window-box root 50.0)
  (bottom-window-box root -10)

  (root-box-bottom-left root (parse-date "1980-01-01") -10)

  (->> (move-right-in-window root (parse-date "2021-01-01") (parse-date "2021-12-31"))
      ;(clojure.pprint/print-table)
       (map :idx-t))

  (-> (move-up-in-window root 2.197252998145341 2.2621424532947794)
      (move-up-in-window root (Math/log10 1000) (Math/log10 70000))
      (clojure.pprint/print-table))

  (-> (get-boxes-in-window root (parse-date "2021-01-01") (parse-date "2021-12-31")
                           (Math/log10 1000) (Math/log10 70000))
      (clojure.pprint/print-table))

  (-> (get-boxes-in-window root (parse-date "1990-01-01") (parse-date "2021-12-31")
                           (Math/log10 0.00001) (Math/log10 70000))
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
           time-right-shift? (quot-inc (- t bt) (:dt box))
           time-left-shift? (quot-dec (- t at) (:dt box))
           :else 0)
     :qp (cond
           price-up-shift? (quot-inc (- p bp) (:dp box))
           price-down-shift? (quot-dec (- p ap) (:dp box))
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

;; data

(defn gann-symbols []
  (let [filename (get-in-config [:demo :gann-data-file])
        ganns (-> filename slurp edn/read-string)]
    (map :symbol ganns)))

(defn load-ganns []
  (let [filename (get-in-config [:demo :gann-data-file])
        ganns (-> filename slurp edn/read-string)
        tuple (juxt :symbol identity)]
    (->> ganns
         (map convert-gann-dates)
         (map make-root-box)
         (map tuple)
         (into {}))))

(defn get-root-box [symbol]
  (get (load-ganns) symbol))

; (def gld-box (move-down (move-down (make-root-box gld))))

;                "QQQ" (zoom-in  (make-root-box qqq))
;                "GLD" (move-down (move-down (make-root-box gld)))
;                "SLV" (zoom-in (zoom-in (make-root-box slv)))
;                "EURUSD" (zoom-in (zoom-in (zoom-in (make-root-box eurusd))))})

;; printing

(defn exponentialize-prices [{:keys [ap bp] :as box}]
  (assoc box
         :apl (Math/pow 10 ap)
         :apr (Math/pow 10 bp)))

(defn print-boxes [boxes]
  (->> boxes
       (map exponentialize-prices)
       (clojure.pprint/print-table)))

(comment
  (get-in-config [:demo :gann-data-file])
  (gann-symbols)

  (-> (load-ganns)
      vals
      (print-boxes))

  (get-root-box "BTCUSD")
  (get-root-box "GLD")
  (get-root-box "BAD")

  ;
  )






