(ns demo.swinger
  (:require
   [clojure.edn :as edn]
   [clojure.pprint]
   [tech.v3.dataset :as ds]
   [ta.warehouse :as wh]
   [ta.swings.core :refer [swings print-swings2]]
   [ta.swings.transduce :refer [xf-swings]]
   ;[ta.swings.viz :refer [swing-chart2]]
   ;[pinkgorilla.vega.plot.swings :refer [swing-chart]]
   ))

:gorilla/on
(println "hello from trateg")
{:a 1 :b 2}

(wh/init-tswh "../../db/")

(wh/init-tswh "./db/")

(defn calc-swings [symbol]
  (let [d (wh/load-ts symbol)
        r (ds/mapseq-reader d)
        f (take 1 r)
        swings (into [] (xf-swings 20) r)]
    (println "first: " f)
      ;(println "swings: " swings)
    (spit (str "reports/" symbol ".txt")
          (with-out-str
            (print-swings2 swings)))))


(defn calc [_]
  ;(doall (map show symbols))
  (calc-swings "MSFT"))


(comment
  (calc-swings "MSFT")

 ; 
  )

;(swings (:spx data) 20)
;(swings (:spx data) 60)

;(conv (swings (:spx data) 20))

;(pt (swings (:spx data) 50))
;(pt (re-swing (swings (:spx data) 60) 60))

;(table (swings (:msft data) 30))
;(chart (swings (:msft data) 50))