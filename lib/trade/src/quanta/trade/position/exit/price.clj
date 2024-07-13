(ns quanta.trade.position.exit.price
  (:require
    [tick.core :as t]
    [missionary.core :as m]
    [quanta.trade.position.exit :refer [get-exit-rule]]))





(defn get-exit-profit [algo-opts position]
  (let [{:keys [calendar]} algo-opts
        [exchange-kw interval-kw] calendar
        bars (get-time-bars algo-opts)]
    (when bars
      (let [cal-seq (calendar-seq exchange-kw interval-kw entry-date)
            window (take bars cal-seq)]
        #_{:start (first window)
           :end (last window)}
        (last window)))))

(defn profit-trigger [exit-time]
  (let [exit-long (-> exit-time t/instant t/long)
        now-long (-> t/instant t/long)
        diff-ms (* 1000 (- exit-long now-long))
        diff-ms (max diff-ms 1)]
    (m/sleep diff-ms :time)))

  