(ns ta.viz.ds.rtable
  (:require
   [tick.core :as t]
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.helper.date :as dt]
   [ta.series.signal :refer [select-signal-is select-signal-has]]))



(defn rtable-cols [spec]
  (concat [:date :open :high :low :close]
          (map :path (:cols spec))))


(defn rtable-spec? [spec]
  (and (map? spec)
       (:cols spec)
; all cols have :path
       ))

(defn rtable-render-spec
  "returns a render specification {:render-fn :spec :data} . spec must follow r-table spec format.
        The ui shows a table with specified columns,
        Specified formats, created from the bar-algo-ds"
  [env spec bar-algo-ds]
  (assert (rtable-spec? spec) "see rtable spec docs")
  {:render-fn 'ta.viz.ui/rtable
   :data (-> bar-algo-ds
             (tc/select-rows (rtable-cols spec))
             (tc/rows :as-maps))
   :spec spec})

(comment

  (def ds
    (tc/dataset [{:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5 :sma 10}
                 {:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5 :sma 10}
                 {:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5 :sma 10}
                 {:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5 :sma 10}]))

  (def spec {:render-fn 'ta.viz/rtable
             :class "table-head-fixed padding-sm table-red table-striped table-hover"
             :style {:width "50vw"
                     :height "40vh"
                     :border "3px solid green"}
             :cols [{:path :sma :max-width "60px"}]})

  ds

  (rtable-render-spec nil spec ds)



 ; 
  )

