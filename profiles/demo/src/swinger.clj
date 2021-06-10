

(ns demo.data
  (:require
   [clojure.edn :as edn]
   [clojure.pprint]
   [ta.data.alphavantage :as av]
   [ta.series.swings :refer [swings]]
   [ta.viz.swings :refer [swing-chart2]]
   ))

:gorilla/on

(ns demo.data)
(println "hello from trateg")



{:a 1 :b 2}


(ns bongo.billd)
(in-ns 'demo.data)
*ns*


(-> "creds.edn" slurp edn/read-string
    :alphavantage av/set-key!)

(av/search "S&P 500")

(defn g [s]
  (->> s
       (av/get-daily "compact")
       (map :close)))

(defn gf [s]
  (->> s
       (av/get-daily "full")
       (map :close)))

(def data
  {:msft (gf "MSFT")
   :goog (gf "GOOG")
   :spx (gf "SPY")
   :xom (gf "XOM")})

(defn swing-close [{:keys [dir high low]}]
  (if (= :up dir)
    high
    low))

(defn pt [d]
  (let [da (conj (:swings d)
                 (:current d))
        da (map #(assoc % :close (swing-close %)) da)]
    (clojure.pprint/print-table
     [:dir :prct :len :close]
     da)))


(defn re-swing [sd prct]
  (let [s (:swings sd)
        ps (map swing-close s)]
    (swings ps prct)))


(defn conv [sd]
  (assoc sd :swings
         (map-indexed (fn [i v]
                        (->
                         (assoc v
                                :idx i
                                :High (:high v)
                                :Low (:low v))
                         (dissoc :high :low)))
                      (:swings sd))))

(swings (:spx data) 20)
(swings (:spx data) 60)

(conv  [{:low 18 :high 22 :dir "up" :idx 1 :idx2 2}
        {:low 12 :high 22 :dir "down" :idx 2  :idx2 3}])

(conv (swings (:spx data) 20))

(pt (swings (:spx data) 50))
(pt (re-swing (swings (:spx data) 60) 60))

(require '[pinkgorilla.vega.plot.swings :refer [swing-chart]])


(swing-chart2
 {:data {:swings [{:Low 18 :High 22 :dir "up" :idx 1 :idx2 2}
                  {:Low 12 :High 22 :dir "down" :idx 2  :idx2 3}
                  {:Low 12 :High 14 :dir "up" :idx 3  :idx2  4 :note "wow"}
                  {:Low 12 :High 22 :dir "down" :idx 4,  :idx2  5}
                  {:Low 12 :High 14 :dir "up" :idx 5  :idx2  6}]}})

(defn chart [swings]
  (let [swings (conv swings)]
    (swing-chart2 {:data swings})
    ))
  
(defn table [swings]
 ^:R [:p/aggrid {:size :big
             ;:columns columnDefs
             :data (:swings swings )}])

(table (swings (:msft data) 30))
(chart (swings (:msft data) 50))