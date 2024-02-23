(ns juan.study.pivot-price
  (:require
   [tablecloth.api :as tc]
   [ta.backtest.core :refer [backtest-algo]]))

;; 1. calculate algo

(def algo-spec {:calendar [:us :d]
                :algo  'juan.algo.pivot-price/add-pivots-price
                :asset "EURUSD"
                :import :kibot
                :trailing-n 200
                :step 10.0
                :percentile 70
                })

(def pivots
  (backtest-algo :bardb-dynamic algo-spec))


(-> pivots tc/last :pivots-price last)
;; => _unnamed [6 2]:
;;    
;;    |       :name |  :price |
;;    |-------------|--------:|
;;    |     :p0-low | 1.07615 |
;;    |    :p0-high | 1.08387 |
;;    |     :p1-low | 1.07615 |
;;    |    :p1-high | 1.08387 |
;;    | :pweek-high | 1.08387 |
;;    |  :pweek-low | 1.06949 |


;; get pivot points 5 days ago.
(-> pivots :pivots-price (get -5))
;; => _unnamed [6 2]:
;;    
;;    |       :name |  :price |
;;    |-------------|--------:|
;;    |     :p0-low | 1.07235 |
;;    |    :p0-high | 1.07848 |
;;    |     :p1-low | 1.06949 |
;;    |    :p1-high | 1.07344 |
;;    | :pweek-high | 1.08055 |
;;    |  :pweek-low | 1.06949 |

