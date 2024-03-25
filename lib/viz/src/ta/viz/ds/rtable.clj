(ns ta.viz.ds.rtable
  (:require
   [tick.core :as t]
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [de.otto.nom.core :as nom]
   [ta.viz.error :refer [error-render-spec]]))

(defn rtable-cols [spec]
  (map :path (:columns spec)))

(defn rtable-spec? [spec]
  (and (map? spec)
       (:columns spec)
        ; TODO: all cols have :path
       ))

(defn ds->map [ds]
  ;(tc/rows :as-maps) ; this does not work, type of it is a reified dataset. 
  ; this works in repl, but when sending data to the browser it fails.
  (into []
        (tds/mapseq-reader ds)))

(def default-spec
  {:class "table-head-fixed padding-sm table-blue table-striped table-hover"
   :style {:width "100%"
           :height "100%"
           :border "1px solid blue"}})

(defn rtable-render-spec-impl
  "returns a render specification {:render-fn :spec :data}. 
   spec must follow r-table spec format.
   The ui shows a table with specified columns,
   Specified formats, created from the bar-algo-ds"
  [spec bar-algo-ds]
  (assert (rtable-spec? spec) "rtable-spec needs to have :columns key")
  {:render-fn 'ta.viz.renderfn.rtable/rtable
   :data (-> bar-algo-ds
             (tc/select-columns (rtable-cols spec))
             ds->map)
   :spec (merge default-spec spec)})

(defn rtable-render-spec
  "returns a render specification {:render-fn :spec :data}. 
   spec must follow chart-pane format.
   The ui shows a barchart with extra specified columns 
   plotted with a specified style/position, 
   created from the bar-algo-ds"
  [spec bar-signal-ds]
  (if (nom/anomaly? bar-signal-ds)
    (error-render-spec bar-signal-ds)
    (rtable-render-spec-impl spec bar-signal-ds)))

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
             :columns [{:path :sma :max-width "60px"}]})

  ds

  (rtable-render-spec spec ds)

; 
  )

