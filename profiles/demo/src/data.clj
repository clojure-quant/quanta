(ns demo.data
  (:require
   [clojure.edn :as edn]
   [clojure.pprint]
   [ta.data.alphavantage :as av]))

(-> "creds.edn" slurp edn/read-string
    :alphavantage av/set-key!)

(av/search "S&P 500")





(defn new-swing [dir p]
  {:dir dir
   :low p
   :high p
   :len 0})

(defn round2
  "Round a double to the given precision (number of significant digits)"
  [precision d]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/round (* d factor)) factor)))

(defn swing-range-p [{:keys [high low]}]
  (round2 1
          (/ (* 100 (- high low)) low)))

(defn process [{:keys [high low] :as swing} p]
  (let [dir (:dir swing)
        up? (= :up dir)]
    (-> (if up?
            (if (> p high)
              (assoc swing :high p :reset true)
              (dissoc swing :reset))
            (if (< p low)
              (assoc swing :low p :reset true)
              (dissoc swing :reset)))
         (assoc :len (inc (:len swing))
                :last p
                ))))

(defn swing-range [{:keys [high low]}]
  (- high low))



(defn counter% [current next]
  (let [c (swing-range current)
        n (swing-range next)
        p (if (> c 0)
                 (/ (* 100 n) c)
               0)
        ]
    ;(println "p: " p "c: " c " n:" n)
    p
    ))


(defn toggle-swing [current p]
  (let [dir (:dir current)
        up? (= :up dir)
        dir-new (if up? :down :up)]
    (new-swing dir-new p)
  ))

(defn process-price [ret-prct {:keys [swings current next]} p]
    (let [current (process current p)
          current (assoc current :prct (swing-range-p current))
          next (process next p)
          next (if (:reset current)
                 (do ;(println "resetting")
                 (toggle-swing current p))
                 next)
          ]
    ;(println current)
    (if (> (counter% current next) ret-prct )
      {:swings (conj swings current)
       :current next
       :next (toggle-swing next p)}
      {:swings swings
       :current current
       :next next
       })))


(defn swings [series ret-prct]
  (let [p (first series)
        c (new-swing :up p)
        n (toggle-swing c p)
        ]
    (reduce (partial process-price ret-prct)
            {:swings []
             :current c
             :next n}
            series)))


(defn swing-close [{:keys [dir high low]}]
  (if (= :up dir) 
    high
    low))

(defn re-swing [sd prct]
  (let [s (:swings sd)
        ps (map swing-close s)
        ]
    (swings ps prct)
    )
  )

(defn pt [d]
  (let [da (conj (:swings d)
                (:current d))
        da (map #(assoc % :close (swing-close %)) da)
        ]
  (clojure.pprint/print-table
   [:dir :prct :len :close] 
   da
   )))

(defn g [s]
  (->> s
       (av/get-daily "compact")
       (map :close)))

(defn gf [s]
  (->> s
       (av/get-daily "full")
       (map :close)))

(def m (g "MSFT"))
(def p (g "GOOG"))
(def s (g "SPY"))
(def x (gf "XOM"))

(swings  p 20)
(swings s 60)

(pt (swings m 50))
(pt (swings s 50))
(pt (swings x 50))
(pt (re-swing (swings s 60) 60))



