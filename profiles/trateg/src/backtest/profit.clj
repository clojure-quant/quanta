(ns backtest.profit)


;  (:require [cheshire.core :as json]
;            [cheshire.generate :as json-gen]
;            [clojure.java.shell :as shell]
;            [medley.core :as m]
;            [clojure.java.io :as io]))


(defn mean [coll]
  (/ (reduce + coll) (count coll)))

;;for sample (not population)
(defn standard-deviation [coll]
  (let [avg     (mean coll)
        squares (map #(Math/pow (- % avg) 2) coll)]
    (-> (reduce + squares)
        (/ (dec (count coll)))
        Math/sqrt)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Calculations on single trade;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn side-add [side initial gain]
  (case side
    :long  (+ initial gain)
    :short (- initial gain)))

(defn nominal-return [side entry exit]
  (case side
    :long  (- exit entry)
    :short (- entry exit)))

(defn ratio-return [side entry exit]
  (case side
    :long  (/ exit entry)
    :short (/ entry exit)))

(defn nominal-profit [{:keys [entry-price exit-price side] :as trade}]
  (nominal-return side entry-price exit-price))

(defn ratio-profit [{:keys [side entry-price exit-price] :as trade}]
  (ratio-return side entry-price exit-price))

(defn trade-complete? [trade] (-> trade :exit-price some?))

(defn win? [{:keys [side entry-price exit-price] :as trade}]
  (when (and entry-price exit-price)
    (case side
      :long  (> exit-price entry-price)
      :short (< exit-price entry-price))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Calculations on multiple trades;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn win-rate [trades]
  (if (empty? trades)
    0
    (double
     (/ (count (filter win? trades))
        (count trades)))))

(defn total-return [trades]
  (->> trades
       (map ratio-profit)
       (reduce *)))

(defn profit-factor [trades]
  (let [{:keys [profit loss]}
        (reduce (fn [acc {:keys [entry-price exit-price] :as trade}]
                  (if (trade-complete? trade)
                    (let [win?  (win? trade)
                          loss? (not win?)]
                      (cond
                        win?  (update acc :profit + (nominal-profit trade))
                        loss? (update acc :loss + (nominal-profit trade))))
                    acc))
                {:profit 0 :loss 0}
                trades)]
    (/ profit (Math/abs loss))))

(defn mean-return [trades]
  (mean (map ratio-profit trades)))

(defn mean-nominal-return [trades]
  (mean (map nominal-profit trades)))

(defn sharpe-ratio [trades risk-free-rate]
  (/ (- (total-return trades) risk-free-rate)
     (standard-deviation (map ratio-profit trades))))

(defn max-drawdown [returns]
  (let [peaks (reductions max returns)]
    (reduce max (map (fn [p x] (/ (- p x) p)) peaks returns))))

(defn cash-flow-over-trades [trades]
  (reductions * 1 (map ratio-profit trades)))

(defn max-drawdown-over-trades [trades]
  (max-drawdown (cash-flow-over-trades trades)))

  
