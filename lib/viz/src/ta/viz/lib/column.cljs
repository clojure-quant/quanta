(ns ta.viz.lib.column)


(defn trading-signal [s]
  (cond
    (= :long s) {:class "bg-green-400" :style {:float "right"}}
    (= :short s) {:class "bg-red-400" :style {:float "right"}}
    :else {:class "bg-yellow-100" :style {:float "right"}}))


(defn gray-column [__]
  {:class "bg-gray-200.border.border-round.border-zinc-500"
   :style {:float "right"}})