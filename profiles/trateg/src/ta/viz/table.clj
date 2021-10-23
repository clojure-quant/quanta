(ns ta.viz.table
  (:require
   [tech.v3.dataset :as tds]))

(defn ds->table [ds]
  (let [;ds-safe (dissoc ds :date)
        ds-safe ds
        data (into [] (tds/mapseq-reader ds-safe))]
    data))

(defn print-table [ds]
  ^:R [:p/aggrid
       {:box :lg
        :data (ds->table ds)}])

(defn flex-item [name ui]
  [:div  {:style {:background-color "orange"}}
   [:b {:style {:color "blue"}} name]]
  [:div
   ui])

(defn flexbox [data]
  ^:R [:div {:style {:width 500 :max-width 500 :display "inline-block"}}
       [:div {:style {:display "flex" :flex-direction "column" :width 500}}

        data]])

