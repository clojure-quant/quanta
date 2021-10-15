(ns demo.playground.date
  (:require
   [tick.alpha.api :as tick]
   [tech.v3.datatype.datetime :as datetime]
   [ ta.backtest.date :refer [days-ago]]
   [tablecloth.api :as api]
   [java-time]))

(-> (tick/now) type)
; instant

(-> (days-ago 5) type)
; local-date

(->> (days-ago 5)
     (datetime/long-temporal-field :months))

(->> (tick/now)
     ;(tick/year)
     (tick/month)
     ;(.getLong 4)
       ;(datetime/long-temporal-field :hours)

       ;(datetime/long-temporal-field :months) ; this does not work.
     type)

(def ds-y-m
  (api/dataset {:min [1.0 2 3 4 5 6 7 8 9 10 11 12]
                :max [10.0 12 13 41 5 6 7 8 9 10 11 12]
                :month (map #(java.time.Month/of %) [1 2 3 4 5 6 7 8 9 10 11 12])
                :year (map #(java.time.Year/of %) [2022 2022 2023 2023])}))

(-> ds-y-m
    (api/pivot->wider :month [:min :max]
                      {:drop-missing? false}))
;; => _unnamed [2 5]:
;;    | :year | JANUARY-min | JANUARY-max | FEBRUARY-min | FEBRUARY-max |
;;    |-------|------------:|------------:|-------------:|-------------:|
;;    |  2022 |         1.0 |        10.0 |          2.0 |         12.0 |
;;    |  2023 |         3.0 |        13.0 |          4.0 |         41.0 |

(-> (api/dataset {:min [1.0 2 3 4]
                  :max [10.0 12 13 41]
                  :month (map #(java.time.Month/of %) [1 2 1 2])
                  :year (map #(java.time.Year/of %) [2022 2022 2023 2023])})
    (api/pivot->wider :year [:min :max]
                      {:drop-missing? false}))