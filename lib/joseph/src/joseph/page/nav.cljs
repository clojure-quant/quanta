(ns joseph.page.nav
  (:require
   [tick.core :as t]
   [goldly.page :as page]
   [demo.goldly.lib.loader :refer [clj->p]]
   [ta.viz.nav-table :refer [nav-table]]
   [ta.viz.nav-vega :refer [nav-vega]]
  ))
(defn d [s]
  (-> (str s "T00:00")
      (t/date-time)
      (t/inst)))

(defn hack-date [row]
  (update row :date t/inst))

(defn vega-data-hacks [nav]
  (->> nav
       (map hack-date)))

(defn page-joseph-nav [{:keys [query-params] :as route}]
   (let [symbol (:symbol query-params)
         nav (clj->p 'joseph.nav/calc-nav-browser symbol)]
     (fn [& args]
       (case (:status @nav)
         :loading [:p "loading"]
         :error [:p "error!"]
         :data [:div.w-screen.h-screen.flex.flex-cols
                [:div.w.full.h-full.overflow-scroll
                 [nav-vega (vega-data-hacks (:nav(:data @nav)))]]
                [nav-table (:nav (:data @nav))]]
         [:p "unknown: status:" (pr-str @nav)]))))




(page/add page-joseph-nav :joseph/nav)

