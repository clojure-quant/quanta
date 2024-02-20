(ns demo.env.provider
  (:require
   [ta.db.asset.db :as db]))



(defn get-category [s]
  (-> s db/instrument-details :category))

(defn instrument-category->provider [s]
  (let [category (get-category s)
        provider (case category
                   :crypto :bybit
                   :future :kibot
                   :mutualfund :alphavantage
                   :kibot)]
    provider))


(comment
  (db/instrument-details "MSFT")
  (get-category "MSFT")
  (get-category "MSFT2")
  (db/instrument-details "BTCUSD")
  (instrument-category->provider "MSFT")
  (instrument-category->provider "BTCUSD")

 ; 
  )
