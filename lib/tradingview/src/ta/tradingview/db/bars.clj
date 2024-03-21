(ns ta.tradingview.db.bars
  (:require
   [tick.core :as t]
   [cljc.java-time.instant :refer [of-epoch-milli]]
   [de.otto.nom.core :as nom]
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [ta.db.bars.protocol :as b]
   [ta.tradingview.db.clip :as clip]
   [ta.tradingview.db.asset :refer [get-asset-exchange]]))

;; series

; https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=1567457308&to=1568321308
; {"s":"no_data",
; "nextTime":1522108800
;}

; https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=1487289600&to=1488499199
; {
;  "t":[1487289600,1487635200,1487721600,1487808000,1487894400,1488153600,1488240000,1488326400,1488412800],
;  "o":[135.1,136.23,136.43,137.38,135.91,137.14,137.08,137.89,140],
;  "h":[135.83,136.75,137.12,137.48,136.66,137.435,137.435,140.15,140.2786],
;  "l":[135.1,135.98,136.11,136.3,135.28,136.28,136.7,137.595,138.76],
;  "c":[135.72,136.7,137.11,136.53,136.66,136.93,136.99,139.79,138.96],
;  "v":[22198197,24507156,20836932,20788186,21776585,20257426,23482860,36414585,26210984],
; "s":"ok"}

; https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=1487289600&to=1488499199
   ; demo has data for 2017

;  https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=1554076800&to=1556668800
;  {"s":"no_data","nextTime":1522108800}
;  no data found -> nextTime returns a PRIOR data

  ;  https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=323395200&to=325987200
  ; {"t":[],"o":[],"h":[],"l":[],"c":[],"v":[],"s":"no_data"}

(defn- epoch-second->instant [unix-epoch]
  (of-epoch-milli (* 1000 unix-epoch)))

(def dict-tv->interval
  {"D" :d
   "1D" :d ; tradingview sometimes queries daily as 1D
   "60" :h})

(def tv-no-data-response
  {:s "no_data"
   :t []
   :o []
   :h []
   :l []
   :c []
   :v []})

(defn epoch
  "add epoch column to ds"
  [ds]
  (dtype/emap t/long :long (:date ds)))

(defn add-epoch-second [ds]
  (tc/add-column
   ds
   :epoch (epoch ds)))

(defn tv-response [bar-ds]
  (let [bar-epoch-ds (add-epoch-second bar-ds)
        col  (fn [k]
               (into [] (k bar-epoch-ds)))]
    {:t (col :epoch)
     :o (col :open)
     :h (col :high)
     :l (col :low)
     :c (col :close)
     :v (col :volume)
     :s "ok"}))

(defn load-series [asset resolution from to]
  (let [bar-db (clip/get-bar-db)
        exchange (get-asset-exchange asset)
        opts {:calendar [exchange
                         (dict-tv->interval resolution)]
              :asset asset}
        opts (merge opts (clip/import-for-exchange exchange))
        window {:start (epoch-second->instant from)
                :end (epoch-second->instant to)}
        bar-ds (b/get-bars bar-db opts window)]
    (if (or (nom/anomaly? bar-ds)
            (= (tc/row-count bar-ds) 0))
      tv-no-data-response
      (tv-response bar-ds))))

(comment
  (-> (t/instant)
      (t/long)
      ;(t/instant)
      )
  ;; => 
  (of-epoch-milli (* 1000 1710101243))

; 
  )