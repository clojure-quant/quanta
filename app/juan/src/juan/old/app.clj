(ns juan.app
  (:require
   [taoensso.timbre :refer [info warn error]]
   [juan.data :refer [settings]]
   [juan.fxcm :as fxcm]
   [juan.sentiment :as sentiment]
   [juan.core :refer [calc-core]]
   [juan.realtime :refer [calc-realtime]]))

;; fxcm 

(defonce fxcm-dict (atom {}))

(defn refresh-fxcm-dict []
  (info "fxcm data refreshing ..")
  (->> (fxcm/fxcm-download)
       fxcm/fxcm-parse
       (map (juxt :Symbol identity))
       (into {})
       (reset! fxcm-dict))
  (info "fxcm data refresh finished!"))

(defn fxcm-data-for-symbol [s]
  (get @fxcm-dict s))


(defn fxcm-last-price [s]
  (when-let [data (get @fxcm-dict s)]
    (:Bid data)))

;; sentiment

(defonce sentiment-dict (atom {}))

(defn refresh-sentiment-dict []
  (info "sentiment data refresh ..")
  (->> (sentiment/download-sentiment)
       (sentiment/sentiment-dict)
       (reset! sentiment-dict))
  (info "sentiment data refresh finished!")
  )

(defn get-sentiment [s]
  (get @sentiment-dict s))


;; core

(defonce core-seq (atom []))

(defn refresh-core []
  (->> (calc-core get-sentiment)
       (reset! core-seq)))

(defn get-core []
  @core-seq)

;; realtime 

(defonce realtime-seq (atom []))

(defn calculate-realtime []
  (->> (calc-realtime get-core fxcm-data-for-symbol)
       (reset! realtime-seq)))

(defn get-realtime []
  @realtime-seq)


;; TASKS

(defn task-hour [& args]
  (refresh-sentiment-dict)
  (refresh-core))

(defn task-minute [& args]
  (refresh-fxcm-dict)
  (calculate-realtime))


(comment

  (refresh-fxcm-dict)
  (fxcm-last-price "AUDJPY")
  (fxcm-data-for-symbol "AUDJPY")

  (refresh-sentiment-dict)
  (get-sentiment "EURNOK")

  (refresh-core)
  (require '[clojure.pprint :refer [print-table]])
  (-> (refresh-core) print-table)
  (get-core)


  (task-day)
  (task-hour)
  (task-minute)




 ; 
  )