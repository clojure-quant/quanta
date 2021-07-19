(ns ta.backtest.core
  (:require
   [tick.alpha.api :as t]
   [tech.v3.dataset :as ds]
   [net.cgrand.xforms :as x]
   [ta.data.date :refer [parse-date]]
   [ta.warehouse :as wh]
   [ta.backtest.trade :refer [pf trade]]))

(defn process-until [xf source]
  (let [r (atom nil)
        d (atom (first (take 1 source)))
        s (atom (rest source))
        before? (fn [dt]
                  ;(println "before: " (:date @d))
                  (and @d (t/<= (:date @d) dt)))
        set-r (fn [& [R d]]
                ;(println "set R: " R " d:" d)
                (when d
                  ;(println "d: " d)
                  (reset! r d)))
        x (xf set-r)]
    (x)
    (fn [dt]
      (if dt
        (do (while (before? dt)
              ;(println "process: " @d)
              (x @r @d)
              (reset! d (first (take 1 @s)))
              (reset! s (rest @s)))
            @r)
        @r))))

(defn calc-xf [pre-process xf-algo symbol]
  (let [d (wh/load-ts symbol)
        d (if pre-process
            (pre-process d)
            d)
        r (ds/mapseq-reader d)]
    (process-until xf-algo r)))

(defn pf-backtest [{:keys [start end initial-equity pre-process algo buy-rule  trade-price-field]
                    :or {initial-equity 100000
                         pre-process nil
                         algo identity ; (comp identity x/last ) 
                         trade-price-field :last}} symbols]
  (let [dt-start (parse-date start)
        dt-end (parse-date end)
        p1d (t/new-period 1 :days)
        data (into {} (map (fn [s]
                             [s (calc-xf pre-process algo s)]) symbols))
        p (atom (pf initial-equity))]
    (loop [dt dt-start]
      ;(println "processing " dt)
      (doall (map (fn [[s process]]
                    ;(println "process: " s)
                    (process dt)) data))
      (let [cur (map (fn [[s process]]
                       {:symbol s :data (process nil)}) data)
            buy (buy-rule cur)
            ;_ (println "b:" buy)
            ;buy-s (into #{} (map :symbol buy))
            getp (fn [s]
                   ;(println "cur:" cur)
                   (-> (filter #(= s (:symbol %)) cur)
                       first
                       (get-in [:data  trade-price-field])))]
        ;(println cur)
        ;(println "buy" (count buy))
        (reset! p (trade @p buy getp dt))
          ; {:dir :up, :low 64.4, :high 71.5, :len 3, :last 71.3, :prct 11.0}

        (when (t/< dt dt-end)
          (recur (t/+ dt p1d)))))
    @p))