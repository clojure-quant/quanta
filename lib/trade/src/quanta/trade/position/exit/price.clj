(ns quanta.trade.position.exit.price
  (:require
   [missionary.core :as m]
   [quanta.trade.position.exit.rule :refer [get-exit-rule]]
   [quanta.trade.position.working :refer [working-position]]))

(defn trailing-return [position]
  (let [working-position (working-position position)]
    (m/latest :ret-prct working-position)))

(defn profit-trigger
  "returns a missionary task that emits :profit when target is met.
   if no profit-percent exit-rule is specified it returns nil
   if not task continues running."
  [algo-opts position]
  (let [[_ target] (get-exit-rule algo-opts :profit-percent)]
    (when target
      (let [prct (trailing-return position)
            rf (fn [_ tp]
                 (println "trailing profit " (:asset position) ": " tp " target: " target)
                 (when (> tp target)
                   (reduced :profit)))]
        (m/reduce rf nil prct)))))

(defn loss-trigger
  "returns a missionary task that emits :loss when target is met.
   if no :loss-percent exit-rule is specified it returns nil.
   if not task continues running."
  [algo-opts position]
  (let [[_ target] (get-exit-rule algo-opts :loss-percent)]
    (when target
      (let [prct (trailing-return position)
            rf (fn [_ tp]
                 (println "trailing loss " (:asset position) ": " tp " target: " target)
                 (when (< tp (- 0.0 target))
                   (reduced :loss)))]
        (m/reduce rf nil prct)))))

  (comment
    (def algo-opts {:asset "BTCUSDT"
                    :exit [:profit-percent 1.0]})

    (def position
      {:asset "BTCUSDT"
       :qty 500
       :entry-price 10000.0})

    (m/? (profit-trigger algo-opts position))

;
    )


  