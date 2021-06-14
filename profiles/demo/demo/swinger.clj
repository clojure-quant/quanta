(ns demo.swinger
  (:require
   [clojure.edn :as edn]
   [clojure.pprint]
   [cljc.java-time.local-date :as ld]
   [cljc.java-time.format.date-time-formatter :refer [iso-date]]
   [tech.v3.dataset :as ds]
   [ta.warehouse :as wh]
   [ta.swings.core :refer [swings print-swings2]]
   [ta.swings.transduce :refer [xf-swings]]
   [ta.swings.viz :refer [swing-chart2 chart2]]
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
        swings (into [] (xf-swings 30) r)]
    (println "first: " f)
      ;(println "swings: " swings)
    (spit (str "reports/" symbol ".txt")
          (with-out-str
            (print-swings2 swings)))
    swings
    ))


(defn calc [_]
  ;(doall (map show symbols))
  (calc-swings "MSFT"))


(defn no-date [l]
  (map #(dissoc % :dt-first :dt-last) l)
  )

(defn dt-str [d]
  (when d  (ld/format d iso-date))
  )
(defn str-date [l]
  (map #(assoc % :dt-first (dt-str  (:dt-first %))
               :dt-last (dt-str (:dt-last %))
               ) l))


(comment
  (->> (calc-swings "XOM")
       ;(take 1600)
       ;(no-date)
       (str-date)
      ;print-swings2
      chart2
      ;pr-str
      )


 ; 
  )

;(swings (:spx data) 20)
;(swings (:spx data) 60)

;(conv (swings (:spx data) 20))

;(pt (swings (:spx data) 50))
;(pt (re-swing (swings (:spx data) 60) 60))

;(table (swings (:msft data) 30))
;(chart (swings (:msft data) 50))