(ns joseph.page.live
  (:require
   [reagent.core :as r]
   [clojure.string :as str]
   [goldly.page :as page]
   [goldly :refer [eventhandler]]
   [goldly.js :refer [to-fixed]]
   [ta.viz.trades-table :refer [trades-table-live]]
   [demo.goldly.lib.loader :refer [clj->p]]
   [demo.goldly.lib.layout :as layout]
   [joseph.lib.trade-filter :refer [filter-trades]]
   [joseph.lib.quote-table :refer [quote-table]]
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

(defn symbols [trades]
  (->> trades
       (map :symbol)
       (into #{})
       (into [])))

(defn round-number-digits
  [digits number] ; digits is first parameter, so it can easily be applied (data last)
  (if (nil? number) "" (to-fixed number digits)))


(defn current-pl [{:keys [qty side entry-price]} current-price]
  (let [change (- current-price entry-price)
        qty2 (if (= side :long)
                 qty
                 (- 0.0 qty))
        pl (* qty2 change)]
    (round-number-digits 0 pl) 
    ))


(defn trades-with-pl [trades quotes]
  (let [quote-dict (->> quotes 
                        (map (juxt :symbol identity))
                        (into {}))]
    (println "quote-dict: " quote-dict)
    (map (fn [{:keys [symbol] :as trade}]
           (if-let [q (get quote-dict symbol)]
             (let [current-price (:close q)]
               (assoc trade :current-price current-price
                      :current-pl (current-pl trade current-price)
                      ))
             (assoc trade :current-price ""
                          :current-pl ""))) 
         trades)))


(defn live-trade-ui [trades]
  (let [account-a (r/atom "")
        on-change-account (fn [account _e]
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
                              :on-change (eventhandler on-change-account)}]]
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
