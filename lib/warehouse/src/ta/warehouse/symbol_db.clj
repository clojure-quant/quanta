(ns ta.warehouse.symbol-db
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [ta.warehouse :refer [symbols-available]]))

#_(defn determine-wh [s]
    (info "determining wh for: " s)
    (case s
      "ETHUSD" :crypto
      "BTCUSD" :crypto
      :stocks))

(defn symbols-in-warehouse [w]
  [w (symbols-available w "D")])

(defn warehouse-status []
  (into {}
        (map symbols-in-warehouse [:crypto :stocks])))

(def warehouse-status-memo (memoize warehouse-status))

(defn determine-wh [symbol]
  (let [status (warehouse-status-memo)
        matches (fn [[w symbols]]
                  (when (some #(= % symbol) symbols) w))
        m (some matches status)]
    m))

(comment
  (symbols-in-warehouse :crypto)
  (warehouse-status)
  (warehouse-status-memo)

  (determine-wh "BTCUSD")
  (determine-wh "ETHUSD")
  (determine-wh "SPY")
  (determine-wh "QQQ")
   ; not existing:
  (determine-wh "BAD")

;   
  )


