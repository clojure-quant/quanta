(ns demo.algo.sma-diff
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as dfn]
   [ta.series.indicator :refer [sma]]))

(defn sma-diff-indicator
  [ds-bars {:keys [sma-length-st sma-length-lt st-mult] #_:as #_options
            :or {st-mult 2.0
                 sma-length-st 5
                 sma-length-lt 20}}]
  (let [{:keys [close]} ds-bars
        sma-st (sma sma-length-st close)
        sma-lt (sma sma-length-lt close)
        ;_ (println "sma st:" sma-st)
        sma-diff (-> (dfn/* sma-st st-mult)
                     (dfn/- sma-lt))]
    (-> ds-bars
        (tc/add-column :sma-st sma-st)
        (tc/add-column :sma-lt sma-lt)
        (tc/add-column :sma-diff sma-diff))))

(comment

  (sma 5 [1 2 3 4 5 6])
 ; 
  )


