(ns demo.data-import.download
  (:require
    [tick.core :as t]   
    [ta.import.core :refer [get-series]]))

(def dt (t/date-time "2024-02-01T00:00:00"))
dt

;; BYBIT
(get-series {:asset "BTCUSD" ; crypto
             :import :bybit
             :calendar [:crypto :d]}
            {:start dt})

;; ALPHAVANTAGE
(get-series {:asset "FMCDX" ; mutual fund
             :calendar [:us :d]
             :import :alphavantage}
            {:start dt
             :mode :append})

;; KIBOT
(get-series {:asset "NG0" ; future
             :calendar [:us :d]
             :import :kibot}
            {:start dt})

