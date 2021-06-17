(ns demo.swinger2
  (:require
   [clojure.pprint]
   [tick.alpha.api :as t]
   [cljc.java-time.local-date :as ld]
   [cljc.java-time.format.date-time-formatter :refer [iso-date]]
   [tech.v3.dataset :as ds]
   [ta.warehouse :as wh]
   [ta.data.random :refer [process-until random-ts]]
   [ta.swings.trade :refer [pf trade]]
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

(defn calc-pf [start end symbols]
  (let [dt-start (parse-date start)
        dt-end (parse-date end)
        p1d (t/new-period 1 :days)
        data (into {} (map (fn [s]
                             [s (calc-swings s)]) symbols))
        p (atom (pf 100000))]
    (loop [dt dt-start]
      ;(println "processing " dt)
      (doall (map (fn [[s process]]
                    ;(println "process: " s)
                    (process dt)) data))
      (let [cur (map (fn [[s process]]
                       {:symbol s :data (process nil)}) data)
            buy (filter (fn [{:keys [symbol data]}]
                          (when (and symbol data)
                          (let [{:keys [dir len prct]} data]
                            (and dir len prct (= dir :up) (> len 10) (> prct 3.0))))) cur)
            ;_ (println dt "buy: " buy)
            buy (sort-by (fn [x] (get-in x [:data :prct])) buy)
            buy (reverse buy)
            ;_ (println "b:" buy)
            buy-s (into #{} (map :symbol buy))
            getp (fn [s] 
                   ;(println "cur:" cur)
                   (-> (filter #(= s (:symbol %)) cur)
                             first
                             (get-in [:data :last])
                             ))]
        ;(println cur)
        ;(println "buy" (count buy))
        (reset! p (trade @p buy-s getp dt))
          ; {:dir :up, :low 64.4, :high 71.5, :len 3, :last 71.3, :prct 11.0}

        (when (t/< dt dt-end)
          (recur (t/+ dt p1d)))))
          @p
          ))

(comment
  
(into #{} (map :symbol []))

  (def symbols ["MSFT" "XOM"])
  (def symbols (wh/load-list  "fidelity-select"))
  symbols
  
  (def p (calc-pf "2000-06-18" "2021-05-01" symbols))

  (spit "pf.txt" 
        (with-out-str 
          (clojure.pprint/print-table (:roundtrips p))))


  (reduce + (map #(get-in % [:pl]) (:roundtrips p)))
  

 ; 
  )
(rd (parse-date "2021-06-18"))
(rd nil)