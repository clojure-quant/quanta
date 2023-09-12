(ns joseph.page.live
  (:require
   [reagent.core :as r]
   [clojure.string :as str]
   [goldly.page :as page]
   [goldly :refer [eventhandler]]
   [ta.viz.trades-table :refer [trades-table-live]]
   [demo.goldly.lib.loader :refer [clj->p]]
   [demo.goldly.lib.layout :as layout]
   [joseph.lib.trade-filter :refer [filter-trades]]
   ))

(defn select-string [{:keys [items value on-change]}]
  (into [:select {:value value
                  :on-change on-change}]
        (map (fn [s] [:option {:value s} s]) items)))


(defn accounts [trades]
  (->> trades
       (map :account)
       (into #{})
       (into [])))


(defn live-trade-ui [trades]
  (let [account-a (r/atom "")
        on-change-account (fn [account _e]
                            (println (str "selected: " account))
                            (reset! account-a account))]
    (fn [trades]
      (let [live-trades (filter-trades {:account @account-a
                                        :status :live} trades)]
      [layout/main-top
       {:top [:div
              "Live-Trades "
              (str "#" (count live-trades) " ")
              [select-string {:value @account-a
                              :items (concat [""] (accounts trades))
                              :on-change (eventhandler on-change-account)}]]
        :main [trades-table-live live-trades]}]))))


(defn page-joseph-live [_route]
  (let [trades-ra (clj->p 'joseph.trades/load-trades)]
    (fn [_route]
      (case (:status @trades-ra)
        :loading [:p "loading"]
        :error [:p "error!"]
        :data [live-trade-ui (:data @trades-ra)]
        [:p "unknown: status:" (pr-str @trades-ra)]))))
               
               
(page/add page-joseph-live :joseph/live)
