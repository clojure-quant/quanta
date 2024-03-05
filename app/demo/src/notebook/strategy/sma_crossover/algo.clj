(ns notebook.strategy.sma-crossover.algo
  (:require
   [tablecloth.api :as tc]
   [ta.indicator :refer [sma]]))

(defn- calc-sma-signal [sma-st sma-lt]
  (if (and sma-st sma-lt)
    (cond
      (> sma-st sma-lt) :buy
      (< sma-st sma-lt) :sell
      :else :hold)
    :hold))

(defn sma-crossover-algo [_env {:keys [sma-length-st sma-length-lt] :as opts} bar-ds]
  (let [sma-st (sma {:n sma-length-st} bar-ds)
        sma-lt (sma {:n sma-length-lt} bar-ds)
        signal (into [] (map calc-sma-signal sma-st sma-lt))]
    (tc/add-columns bar-ds {:sma-st sma-st
                            :sma-lt sma-lt
                            :signal signal})))


