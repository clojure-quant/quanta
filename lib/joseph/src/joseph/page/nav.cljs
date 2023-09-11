(ns joseph.page.nav
  (:require
   [tick.core :as t]
   [re-frame.core :as rf]
   [goldly.page :as page]
   [goldly :refer [eventhandler]]
   [container :refer [tab]]
   [ta.viz.nav-vega :refer [nav-vega]]
   [ta.viz.nav-table :refer [nav-table]]
   [ta.viz.trades-table :refer [trades-table]]
   [demo.goldly.lib.loader :refer [clj->p]]
   [demo.goldly.lib.layout :as layout]
   ))

(defn hack-date [row]
  ; inst converts a tick data to a javascript/date
  (update row :date t/inst))

(defn vega-data-hacks [nav]
  (->> nav
       (map hack-date)))

(defn layout-test [{:keys [top left right]}]
  [:div.w-screen.h-screen.flex.flex-cols
                  ;vega nav chart
   [:div.w-full.h-full ;.overflow-scroll
    left
   ]
                  ;aggrid table (nav or trades)
   [:div.w-full.h-full.overflow-scroll
    ]])


(defn select-string [{:keys [items value on-change]}]
  (into [:select {:value value
                  :on-change on-change
                  }]
        (map (fn [s] [:option {:value s} s]) items)))
  
(defn page-joseph-nav [{:keys [query-params] :as route}]
   (let [symbol (or (:symbol query-params)
                    "")
         account (or (:account query-params)
                    "")
         nav (clj->p 'joseph.nav/calc-nav-browser symbol account)
         on-change-symbol (fn [symbol _e]
                            (println (str "selected: " symbol))
                            (rf/dispatch [:bidi/goto :joseph/nav :query-params {:symbol symbol :account account}]))
         on-change-account (fn [account _e]
                            (println (str "selected: " account))
                            (rf/dispatch [:bidi/goto :joseph/nav :query-params {:symbol symbol :account account}]))
         ]
     (fn [& args]
       (case (:status @nav)
         :loading [:p "loading"]
         :error [:p "error!"]
         :data [layout/left-right-top
                {:top [:div 
                       [select-string {:value symbol
                                       :items (concat [""] (:symbols (:data @nav)))
                                       :on-change (eventhandler on-change-symbol)}]
                       [select-string {:value account
                                       :items (concat [""] (:accounts (:data @nav)))
                                       :on-change (eventhandler on-change-account)}]
                       ]
                 :left [:div.w-full.h-full.overflow-y-scroll
                        [nav-vega (vega-data-hacks (:nav (:data @nav)))]]
                 :right [tab {:box :fl}
                         "nav"
                         [nav-table (:nav (:data @nav))]
                         "trades"
                         [trades-table (:trades (:data @nav))]]}]
         [:p "unknown: status:" (pr-str @nav)]))))




(page/add page-joseph-nav :joseph/nav)

