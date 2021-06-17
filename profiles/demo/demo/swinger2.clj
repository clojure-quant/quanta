(ns demo.swinger2
  (:require
   [clojure.pprint]
   [tick.alpha.api :as t]
   [cljc.java-time.local-date :as ld]
   [cljc.java-time.format.date-time-formatter :refer [iso-date]]
   [tech.v3.dataset :as ds]
   [ta.warehouse :as wh]
   [ta.data.random :refer [process-until random-ts]]

   [ta.swings.core :refer [swings print-swings2]]
   [ta.swings.transduce :refer [xf-swings]]
   [ta.swings.viz :refer [swing-chart]]
   [ta.swings.date :refer [parse-date]]))


(wh/init-tswh {:series "../../db/"
               :list "../../resources/etf/"})

(wh/init-tswh {:series "./db/"
               :list "./resources/etf/"})
(defn calc-swings [symbol]
  (let [d (wh/load-ts symbol)
        r (ds/mapseq-reader d)]
    (process-until (xf-swings true 30) r)))

(def xx (calc-swings "XOM"))

(xx (parse-date "2000-06-18"))

(defn calc-pf []
  (let [dt-start (parse-date "2000-06-18")
        dt-end (parse-date "2021-06-01")
        p1d (t/new-period 30 :days)
        symbols ["MSFT" "XOM"]
        data (into {} (map (fn [s]
                             [s (calc-swings s)]) symbols))]
    (loop [dt dt-start]
      ;(println "processing " dt)
      (doall (map (fn [[s process]]
                    ;(println "process: " s)
                    (process dt)) data))

      (let [cur (map (fn [[s process]]
                       {:symbol s :data (process nil)}) data)
            buy (filter (fn [{:keys [symbol data]}]
                          (let [{:keys [dir len prct]} data]
                            (and (= dir :up) (> len 4) (> prct 3.0)))) cur)]
        (println cur)
        (println "buy" (count buy))
        )
      ; {:dir :up, :low 64.4, :high 71.5, :len 3, :last 71.3, :prct 11.0}

      (when (t/< dt dt-end)
        (recur (t/+ dt p1d))))))

(comment
  (calc-pf)


 ; 
  )
(rd (parse-date "2021-06-18"))
(rd nil)