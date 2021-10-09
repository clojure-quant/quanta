(ns demo.studies.helper.sma
  (:require
   [net.cgrand.xforms :as x]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.functional :as dfn]
   [ta.series.indicator :as ind]))

(defn sma-study [d]
  (let [sma30 (ind/sma 30 (d :close))
        sma200 (ind/sma 200 (d :close))]
    (-> d
        (assoc :sma30 sma30)
        (assoc :rsma30 (dfn// (d :close) sma30))
        ;(assoc :rsma30 (-> (dfn// (d :close) sma30)
        ;                   (dfn/- 1))
        (assoc :sma200 sma30)
        (assoc :rsma200 (dfn// (d :close) sma200)))))

(def sma-chart
  [{:sma200 "line"
    :sma30 "line"}
   {:open "line"}
   {:volume "column"}])
