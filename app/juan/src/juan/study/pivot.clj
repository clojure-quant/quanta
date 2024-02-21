(ns juan.study.pivot
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]
   [ta.env.javelin.core :refer [backtest-single-bar-strategy]]))

;; 1. calculate algo

(def algo-spec {:calendar [:us :d]
                :algo  'juan.algo.pivot/pivots
                :asset "SPY"
                :import :kibot
                :trailing-n 1000
                :step 10.0
                :percentile 70
                })

(def pivots
  (backtest-single-bar-strategy :bardb-dynamic algo-spec))

(:dist pivots)
;; => _unnamed [23 4]:
;;    
;;    | :bin | :price | :count |        :volume |
;;    |-----:|-------:|-------:|---------------:|
;;    |    0 |  285.0 |     47 | 9.31526885E+08 |
;;    |    1 |  295.0 |     46 | 9.17658516E+08 |
;;    |    2 |  305.0 |     53 | 1.14461583E+09 |
;;    |    3 |  315.0 |     74 | 1.30015413E+09 |
;;    |    4 |  325.0 |     89 | 1.30249694E+09 |
;;    |    5 |  335.0 |    130 | 1.75982191E+09 |
;;    |    6 |  345.0 |     90 | 1.25481335E+09 |
;;    |    7 |  355.0 |     76 | 1.13116723E+09 |
;;    |    8 |  365.0 |    138 | 2.05921970E+09 |
;;    |    9 |  375.0 |    194 | 3.14345025E+09 |
;;    |  ... |    ... |    ... |            ... |
;;    |   12 |  405.0 |    239 | 3.78659025E+09 |
;;    |   13 |  415.0 |    408 | 6.30932193E+09 | x
;;    |   14 |  425.0 |    277 | 4.56072114E+09 | x
;;    |   15 |  435.0 |    355 | 6.03641807E+09 | x
;;    |   16 |  445.0 |    347 | 5.09942248E+09 | x
;;    |   17 |  455.0 |    289 | 4.08646220E+09 | x
;;    |   18 |  465.0 |    164 | 2.30552674E+09 |
;;    |   19 |  475.0 |    132 | 1.80701281E+09 |
;;    |   20 |  485.0 |     34 | 5.25707944E+08 |
;;    |   21 |  495.0 |     48 | 6.53028148E+08 |
;;    |   22 |  505.0 |     12 | 1.40922789E+08 |

(:pivots pivots)
;; => _unnamed [7 4]:
;;    
;;    | :bin | :price | :count |        :volume |
;;    |-----:|-------:|-------:|---------------:|
;;    |   10 |  385.0 |    267 | 4.50991579E+09 |
;;    |   11 |  395.0 |    351 | 5.80092628E+09 |
;;    |   13 |  415.0 |    408 | 6.30932193E+09 |
;;    |   14 |  425.0 |    277 | 4.56072114E+09 |
;;    |   15 |  435.0 |    355 | 6.03641807E+09 |
;;    |   16 |  445.0 |    347 | 5.09942248E+09 |
;;    |   17 |  455.0 |    289 | 4.08646220E+09 |


