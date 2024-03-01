(ns ta.live.bar-generator.bar)

(defn empty-bar? [{:keys [open] :as bar}]
  (not open))

(defn switch [bar]
  (-> bar 
      (dissoc :open :high :low :close :volume :ticks)
      (update :epoch inc)))

(defn aggregate-tick [{:keys [open high low volume ticks] :as bar} {:keys [price size]}]
  (merge bar
         (if (empty-bar? bar)
           {:open price
            :high price
            :low price
            :close price
            :volume size
            :ticks 1}
           {:open open
            :high (max high price)
            :low (min low price)
            :close price
            :volume (+ volume size)
            :ticks (inc ticks)})))