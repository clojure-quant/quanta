(ns ta.model.stats
  (:require
   [ta.data.print :as pr]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;util;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def diff-tolerance 0.0000000001)

(defn fuzzy=
  ([x y] (fuzzy= diff-tolerance x y))
  ([tolerance x y]
   (let [diff (Math/abs (- x y))]
     (< diff tolerance))))

(defn all-fuzzy= [a b]
  (and (= (count a) (count b))
       (every? true? (map (fn [a b] (fuzzy= a b)) a b))))

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

(defn nominal-profit [{:keys [px-entry px-exit side]}]
  (nominal-return side px-entry px-exit))

(defn ratio-profit [{:keys [side px-entry px-exit]}]
  (ratio-return side px-entry px-exit))

(defn roundtrip-complete? [roundtrip] (-> roundtrip :px-exit some?))

(defn win? [{:keys [side px-entry px-exit]}]
  (when (and px-entry px-exit)
    (case side
      :long  (> px-exit px-entry)
      :short (< px-exit px-entry))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Calculations on multiple roundtrips;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn win-rate [roundtrips]
  (if (empty? roundtrips)
    0
    (double
     (/ (count (filter win? roundtrips))
        (count roundtrips)))))

(defn total-return [roundtrips]
  (->> roundtrips
       (map ratio-profit)
       (reduce *)))

(defn profit-factor [roundtrips]
  (let [{:keys [profit loss]}
        (reduce (fn [acc roundtrip] ; {:keys [px-entry px-exit] :as roundtrip}
                  (if (roundtrip-complete? roundtrip)
                    (let [win?  (win? roundtrip)
                          loss? (not win?)]
                      (cond
                        win?  (update acc :profit + (nominal-profit roundtrip))
                        loss? (update acc :loss + (nominal-profit roundtrip))))
                    acc))
                {:profit 0 :loss 0}
                roundtrips)]
    (/ profit (Math/abs loss))))

(defn mean-return [roundtrips]
  (mean (map ratio-profit roundtrips)))

(defn mean-nominal-return [roundtrips]
  (mean (map nominal-profit roundtrips)))

(defn sharpe-ratio [roundtrips risk-free-rate]
  (/ (- (total-return roundtrips) risk-free-rate)
     (standard-deviation (map ratio-profit roundtrips))))

(defn max-drawdown [returns]
  (let [peaks (reductions max returns)]
    (reduce max (map (fn [p x] (/ (- p x) p)) peaks returns))))

(defn cash-flow-over-roundtrips [roundtrips]
  (reductions * 1 (map ratio-profit roundtrips)))

(defn max-drawdown-over-roundtrips [roundtrips]
  (max-drawdown (cash-flow-over-roundtrips roundtrips)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Calculations on roundtrips and bars;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn cash-flow [bars roundtrips]
  (loop [bars     bars
         roundtrips   roundtrips
         last-ret 1.0
         rets     []
         ix       0]
    (let [bar (first bars)
          {:keys [px-entry idx-entry idx-exit side] :as roundtrip}
          (first roundtrips)

          ret    (if (and roundtrip
                          (and (> ix idx-entry) (<= ix idx-exit)))
                   (* (get rets idx-entry)
                      (ratio-return side px-entry (:close bar)))
                   last-ret)
          roundtrips (if (= ix idx-exit) (rest roundtrips) roundtrips)]
      (if-let [bars (next bars)]
        (recur bars roundtrips ret (conj rets ret) (inc ix))
        (conj rets ret)))))

(defn max-drawdown-over-bars [bars roundtrips]
  (max-drawdown (cash-flow bars roundtrips)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Tools and charts to analyze result sets;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn gauntlet [{:keys [roundtrips bars]}]
  {:rt-ct     (count roundtrips)
   :total-return (total-return roundtrips)
   :mean-return  (mean (map ratio-profit roundtrips))
   :sd-return    (standard-deviation (map ratio-profit roundtrips))
   :win-rate     (win-rate roundtrips)
   :sharpe       (sharpe-ratio roundtrips 1.01)
   :max-drawdown (max-drawdown-over-bars bars roundtrips)})

(defn gauntlet2 [{:keys [roundtrips]}]
  {:rt-ct     (count roundtrips)
   :total-return (total-return roundtrips)
   :mean-return  (mean (map ratio-profit roundtrips))
   :sd-return    (standard-deviation (map ratio-profit roundtrips))
   :win-rate     (win-rate roundtrips)
   :sharpe       (sharpe-ratio roundtrips 1.01)
   ;:max-drawdown (max-drawdown-over-bars bars roundtrips)
   })

(defn print-result-table [label-key rows]
  (let [cols (->> rows first keys (remove #{label-key}) (cons label-key))]
    (pr/print-table-neat cols (map pr/format-row rows))))
