(ns joseph.page.live2
  (:require
   [reagent.core :as r]
   [reagent.ratom :refer [make-reaction]]
   [goldly.page :as page]
   ; trateg  
   ; pure ui-functions
   [demo.goldly.lib.layout :as layout]
   [ta.viz.trades-table :refer [trades-table-live]]
   [joseph.lib.quote-table :refer [quote-table]]
   [joseph.lib.select :refer [select-string]]
   ; data helper
   [joseph.lib.loader :refer [clj]]
   [joseph.lib.viz-filter :as vf]
   [joseph.lib.trade-filter :refer [filter-trades]]
   [joseph.lib.live-pl :refer [trades-with-pl]]
   ))

(defmacro reaction
  [& body]
  `(reagent.ratom/make-reaction
    (fn [] ~@body)))



(defn live-dashboard []
  (let [account (r/atom vf/all)
        trades (clj [] 'joseph.trades/load-trades)
        accounts (reaction (vf/unique :account @trades))
        vtrades (reaction (filter-trades {:account @account
                                          :status :live} @trades))
        vsymbols (reaction (vf/unique :symbol @vtrades))
        quotes-stocks (reaction (clj [] 'joseph.realtime/realtime-snapshot-stocks @vsymbols))
        quotes-futures (reaction (clj [] {:timeout 120000} 'joseph.realtime/daily-snapshot-futures @vsymbols))
        quotes (reaction (concat @@quotes-stocks @@quotes-futures))
        vtradesx (reaction (trades-with-pl @vtrades @quotes))]
    (fn []
       [layout/left-right-top
        {:top [:div
               "Live-Trades "
               (str "#" (count @vtrades) " ")
               [select-string {:value @account
                               :items (into ["*"] @accounts)
                               :on-change #(reset! account %)}]
               ]
         :left [trades-table-live @vtradesx]
         :right [quote-table @quotes] 
         }])))

                
(defn page-live-trading [_route]
  [live-dashboard])


(page/add page-live-trading :joseph/live2)