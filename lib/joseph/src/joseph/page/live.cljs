(ns joseph.page.live
  (:require
   [reagent.core :as r]
   [clojure.string :as str]
   [goldly.page :as page]
  
   ; ui
   [demo.goldly.lib.layout :as layout]
   [joseph.lib.select :refer [select-string]]
   [ta.viz.trades-table :refer [trades-table-live]]
   ; load data
   [demo.goldly.lib.loader :refer [clj->p]]
   ;data helper
   [joseph.lib.live-pl :refer [trades-with-pl]]   
   [joseph.lib.trade-filter :refer [filter-trades]]
   [joseph.lib.quote-table :refer [quote-table]]
   ))

(defn accounts [trades]
  (->> trades
       (map :account)
       (into #{})
       (into [])))

(defn symbols [trades]
  (->> trades
       (map :symbol)
       (into #{})
       (into [])))

(defn live-trade-ui [trades]
  (let [account-a (r/atom "")
        on-change-account (fn [account]
                            (println (str "selected: " account))
                            (reset! account-a account))
        live-symbols (into [] (symbols trades))
        quotes-ra (clj->p {:timeout 120000} 'joseph.realtime/realtime-snapshot-stocks live-symbols)
        quotes-futures-ra (clj->p 'joseph.realtime/daily-snapshot-futures live-symbols)
        ]
    (fn [trades]
      (let [live-trades (filter-trades {:account @account-a
                                        :status :live} trades)
            quotes (concat (or (:data @quotes-ra) [])
                           (or (:data @quotes-futures-ra) []))
                           ]
        (println "quotes: " quotes)
      [layout/left-right-top
       {:top [:div
              "Live-Trades "
              (str "#" (count live-trades) " ")
              [select-string {:value @account-a
                              :items (concat [""] (accounts trades))
                              :on-change on-change-account}]]
        :left [trades-table-live (trades-with-pl live-trades quotes)]
        :right ; [:div "quotes" (:status @quotes-ra) " - " (pr-str @quotes-ra)]
               (case (:status @quotes-ra)
                  :loading [:p "loading"]
                  :error [:p "error!"]
                  :data ;[:div "quotes (loaded)"]
                        [quote-table quotes]
                  [:p "unknown: status:" (pr-str @quotes-ra)])
        
        }]))))


(defn page-joseph-live [_route]
  (let [trades-ra (clj->p 'joseph.trades/load-trades)]
    (fn [_route]
      (case (:status @trades-ra)
        :loading [:p "loading"]
        :error [:p "error!"]
        :data [live-trade-ui (:data @trades-ra)]
        [:p "unknown: status:" (pr-str @trades-ra)]))))
               
               
(page/add page-joseph-live :joseph/live)
