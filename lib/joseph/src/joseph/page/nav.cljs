(ns joseph.page.nav
  (:require
   [tick.core :as t]
   [goldly.page :as page]
   [container :refer [tab]]
   [ta.viz.nav-vega :refer [nav-vega]]
   [ta.viz.nav-table :refer [nav-table]]
   [ta.viz.trades-table :refer [trades-table]]
   [demo.goldly.lib.loader :refer [clj->p]]))

(defn hack-date [row]
  ; inst converts a tick data to a javascript/date
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
                ;vega nav chart
                [:div.w-full.h-full.overflow-scroll
                  [nav-vega (vega-data-hacks (:nav (:data @nav)))]]
                ;aggrid table (nav or trades)
                [:div.w-full.h-full.overflow-scroll
                 [tab {:box :fl}
                   "nav"
                   [nav-table (:nav (:data @nav))]
                   "trades"
                   [trades-table (:trades (:data @nav))]]]]
         [:p "unknown: status:" (pr-str @nav)]))))




(page/add page-joseph-nav :joseph/nav)

