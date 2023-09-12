(ns joseph.lib.trade-filter
  (:require
   [clojure.string :as str]
   
   ))


(defn live-trade? [{:keys [exit-date]}]
  (nil? exit-date))


(defn filter-status [status trades]
  (case status
    :all trades
    :live (filter live-trade? trades)
    :closed (remove live-trade? trades)
    trades))

(defn filter-account [account trades]
  (println "filtering account:" account)
 (if (or (nil? account) (str/blank? account) (= account "*"))
   trades
   (filter #(= account (:account %)) trades)))

(defn filter-symbol [symbol trades]
  (println "filtering symbol:" symbol)
  (if (or (nil? symbol) (str/blank? symbol) (= symbol "*"))
    trades
    (filter #(= symbol (:symbol %)) trades)))

 (defn filter-trades [{:keys [account status symbol]} trades]
   (->> trades
       (filter-account account)
       (filter-status status)
       (filter-status symbol)
       ;(sort-by :account)
       (sort-by (juxt :account :symbol))
       ))