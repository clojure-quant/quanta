(ns notebook.playground.bardb.import
  (:require
   [tick.core :as t]
   [ta.import.core :refer [get-bars]]))

(def dt (t/date-time "2024-02-01T00:00:00"))
dt

;; BYBIT
(get-bars {:asset "BTCUSD" ; crypto
           :calendar [:crypto :d]
           :import :bybit}
          {:start dt})

;; ALPHAVANTAGE
(get-bars {:asset "FMCDX" ; mutual fund
           :calendar [:us :d]
           :import :alphavantage}
          {:start dt
           :mode :append})

(defn date-type [ds]
  (-> ds :date meta :datatype))

;; KIBOT
(-> (get-bars {:asset "NG0" ; future
           :calendar [:us :d]
           :import :kibot}
          {:start dt})
 date-type)
    




