(ns quanta.notebook.trade-stats
  (:require
   [tablecloth.api :as tc]
   [ta.trade.roundtrip.metrics :as m]))

(def ds
  (tc/dataset
   {:side [:long :short :long :short]
    :ret-log [-0.30103000  0.17609126  -0.12493874 0.09691001]
    :win? [true false true false]
    :bars [1 1 1 1]}))

(m/calc-roundtrip-stats ds [:win?])
   ;; => _unnamed [2 6]:
   ;;    
   ;;    | :win? | :bars | :trades | :pl-log-cum | :pl-log-mean | :pl-log-max-dd |
   ;;    |-------|------:|--------:|------------:|-------------:|---------------:|
   ;;    |  true |   2.0 |       2 | -0.42596874 |  -0.21298437 |     0.42596874 |
   ;;    | false |   2.0 |       2 |  0.27300127 |   0.13650064 |     0.00000000 |

(m/win-loss-stats ds)
  ;; => _unnamed [2 6]:
  ;;    
  ;;    | :win? | :bars | :trades | :pl-log-cum | :pl-log-mean | :pl-log-max-dd |
  ;;    |-------|------:|--------:|------------:|-------------:|---------------:|
  ;;    |  true |   2.0 |       2 | -0.42596874 |  -0.21298437 |     0.42596874 |
  ;;    | false |   2.0 |       2 |  0.27300127 |   0.13650064 |     0.00000000 |

(m/side-stats ds)
  ;; => _unnamed [2 6]:
  ;;    
  ;;    |  :side | :bars | :trades | :pl-log-cum | :pl-log-mean | :pl-log-max-dd |
  ;;    |--------|------:|--------:|------------:|-------------:|---------------:|
  ;;    |  :long |   2.0 |       2 | -0.42596874 |  -0.21298437 |     0.42596874 |
  ;;    | :short |   2.0 |       2 |  0.27300127 |   0.13650064 |     0.00000000 |

(def side-s (m/side-stats ds))

side-s

(m/get-group-of side-s :side :long)
(m/get-group-of side-s :side :short)
(m/get-group-of side-s :side :parrot)

(m/calc-roundtrip-metrics ds)
   ;;    
   ;;    entry-price | :exit-price | :ret-abs |    :ret-prct |    :ret-log | :win? | :bars | :cum-ret-log |       :nav |
   ;;   ------------:|------------:|---------:|-------------:|------------:|-------|------:|-------------:|-----------:|
   ;;              1 |           2 |        1 | 100.00000000 |  |  true |     1 |  -0.30103000 | 1.69897000 |
   ;;              2 |           3 |       -1 | -50.00000000 | | false |     1 |  -0.12493874 | 1.87506126 |
   ;;              3 |           4 |        1 |  33.33333333 | |  true |     1 |  -0.24987747 | 1.75012253 |
   ;;              4 |           5 |       -1 | -25.00000000 |   | false |     1 |  -0.15296746 | 1.84703254 |
