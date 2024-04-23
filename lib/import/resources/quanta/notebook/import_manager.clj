(ns quanta.notebook.import-manager
  (:require
   [tick.core :as t]
   [ta.db.bars.protocol :as b]
   [modular.system]))

(def im (modular.system/system :import-manager))

im

(def dt (t/instant "2024-02-01T00:00:00Z"))
dt

;; BYBIT
(b/get-bars im {:asset "BTCUSDT" ; crypto
                :calendar [:crypto :d]
                :import :bybit}
            {:start  (t/instant "2020-01-01T00:00:00Z")
             :end (t/instant "2024-01-01T00:00:00Z")})

;; ALPHAVANTAGE
(b/get-bars im {:asset "FMCDX" ; mutual fund
                :calendar [:us :d]
                :import :alphavantage}
            {:start dt
             :mode :append})

(defn date-type [ds]
  (-> ds :date meta :datatype))

;; KIBOT
(-> (b/get-bars im {:asset "NG0" ; future
                    :calendar [:us :d]
                    :import :kibot}
                {:start  (t/instant "2020-01-01T00:00:00Z")
                 :end (t/instant "2024-01-01T00:00:00Z")})
    date-type)

(b/get-bars im
            {:asset "EUR/USD" ; forex
             :calendar [:forex :d]
             :import :kibot}
            {:start (t/instant "2023-09-01T00:00:00Z")
             :end (t/instant "2024-05-01T00:00:00Z")})

(b/get-bars im {:asset "EU0" ; future(forex)
                :calendar [:us :d]
                :import :kibot}
            {:start (t/instant "2023-09-01T00:00:00Z")
             :end (t/instant "2023-10-01T00:00:00Z")})

(b/get-bars im
            {:asset "MSFT"
             :calendar [:us :d]
             :import :kibot}
            {:start (t/instant "2019-12-01T00:00:00Z")
             :end (t/instant "2020-02-01T00:00:00Z")})

; eodhd

(b/get-bars im
            {:asset "AEE.AU"
             :calendar [:us :d]
             :import :eodhd}
            ; fails because more than a year ago
            {:start (-> "2023-12-01T00:00:00Z" t/instant)
             :end (-> "2024-04-01T00:00:00Z" t/instant)})

