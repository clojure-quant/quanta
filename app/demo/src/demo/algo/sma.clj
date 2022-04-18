(ns demo.algo.sma
  (:require
   [tablecloth.api :as tc]
   [ta.series.ta4j :as ta4j]
   [ta.algo.manager :refer [add-algo]]))

(defn add-sma-indicator
  ""
  [ds {:keys [sma-length-st sma-length-lt] #_:as #_options}]
  (let [; input data needed for ta4j indicators
        ;bars (ta4j/ds->ta4j-ohlcv ds)
        close (ta4j/ds->ta4j-close ds)
        ; setup the ta4j indicators
        sma-st (ta4j/ind :SMA close sma-length-st)
        sma-st-values  (ta4j/ind-values sma-st)
        sma-lt (ta4j/ind :SMA close sma-length-lt)
        sma-lt-values  (ta4j/ind-values sma-lt)]
    (-> ds
        (tc/add-column :sma-st sma-st-values)
        (tc/add-column :sma-lt sma-lt-values))))

(defn calc-sma-signal [sma-st sma-lt]
  (if (and sma-st sma-lt)
    (cond
      (> sma-st sma-lt) :buy
      (< sma-st sma-lt) :sell
      :else :hold)
    :hold))

(defn sma-signal [ds-bars options]
  (let [ds-study (add-sma-indicator ds-bars options)
        sma-st (:sma-st ds-study)
        sma-lt (:sma-lt ds-study)
        signal (into [] (map calc-sma-signal sma-st sma-lt))]
    (tc/add-columns ds-study {:signal signal})))


(add-algo 
  {:name "sma-trendfollow"
   :comment "best strategy so far!"
   :algo sma-signal
   :options {:w :crypto
             :symbol "ETHUSD"
             :frequency "15"
             :sma-length-st 20
             :sma-length-lt 200}})