(ns notebook.algo.sma3
  (:require
   [tablecloth.api :as tc]
   [ta.indicator :refer [sma]]))

(def info
  {:name "sma-trendfollow"
   :comment "best strategy so far!"})

(def algo-opts-default
  {:symbol "ETHUSD"
   :frequency "15"
   :sma-length-st 20
   :sma-length-lt 200})

(defn- add-sma-indicator
  [ds {:keys [sma-length-st sma-length-lt] #_:as #_options}]
  (-> ds
      (tc/add-column :sma-st (sma {:n sma-length-st} ds))
      (tc/add-column :sma-lt (sma {:n sma-length-lt} ds))))

(defn- calc-sma-signal [sma-st sma-lt]
  (if (and sma-st sma-lt)
    (cond
      (> sma-st sma-lt) :buy
      (< sma-st sma-lt) :sell
      :else :hold)
    :hold))

(defn bar-strategy [_env opts ds-bars]
  (let [ds-study (add-sma-indicator ds-bars opts)
        sma-st (:sma-st ds-study)
        sma-lt (:sma-lt ds-study)
        signal (into [] (map calc-sma-signal sma-st sma-lt))]
    (tc/add-columns ds-study {:signal signal})))


(def algo-charts
  [{:sma-lt "line"
    :sma-st "line"
               ;:trade "flags"
    }
   {:volume "column"}])
