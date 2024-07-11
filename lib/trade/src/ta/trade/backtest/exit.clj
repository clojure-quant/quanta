(ns ta.trade.backtest.exit
  (:require
   [ta.trade.roundtrip.roundtrip :as rt]))

(defn- create-exit [position rule row]
  (assoc position
         :exit-rule rule
         :exit-idx  (:idx row)
         :exit-date (:date row)))

(defmulti exit
  "returns a closed roundtrip or nil.
   input: position + row"
  (fn [[type _opts] _position _row] type))

(defmethod exit :time
  [[type exit-time] position row]
  (when (> (:idx row)
           (+ (:entry-idx position) exit-time))
    (create-exit (assoc position :exit-price (:close row)) type row)))

(defn- extreme-profit-price [{:keys [_entry-price side] :as _roundtrip} row]
  (if (= side :long)
    (:high row)
    (:low row)))

(defmethod exit :profit-percent
  [[type profit-percent] position row]
  (let [rt (assoc position :exit-price (extreme-profit-price position row))]
    ;(println "return-prct: " (rt/return-prct rt) "profit-target-prct: " profit-percent)
    (when (>= (rt/return-prct rt) profit-percent)
      (-> (rt/set-exit-price-percent position profit-percent)
          (create-exit type row)))))

(defn- extreme-loss-price [{:keys [_entry-price side] :as _roundtrip} row]
  (if (= side :short)
    (:high row)
    (:low row)))

(defmethod exit :loss-percent
  [[type loss-percent] position row]
  (let [rt (assoc position :exit-price (extreme-loss-price position row))]
    (when (<= (rt/return-prct rt) (- 0.0 loss-percent))
      (-> (rt/set-exit-price-percent position (- 0.0 loss-percent))
          (create-exit type row)))))

; trailing stop loss is stateful. 

(defmethod exit :default
  [[_type _opts] _position _row]
  ; no exit if it is an unknown exit rule
  nil)

(defn eventually-exit-position
  "Runs exit-rules sequentially; first exited position
   is returned. Returns nil if no exit rule fired."
  [exit-rules position row]
  (loop [rules (partition 2 exit-rules)]
    (let [rule (vec (first rules))
          p (exit rule position row)
          rules (rest rules)]
      (if (and (not p) (seq rules))
        (recur rules)
        p))))

(comment
  (require '[tick.core :as t])

  (eventually-exit-position
   [:time 15
    :loss-percent 2.5
    :profit-percent 5.0]
   {:side :long
    :entry-idx 0
    :entry-price 100.0
    :entry-date (t/instant)}
   {:idx 10
    :date (t/instant)
    :close 103.0
    :high 107.0
    :low 95.0
    :open 101.5})

;  
  )

