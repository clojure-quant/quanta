(ns ta.indicator.volume
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as dfn]
   [ta.indicator :as ind]
   [ta.indicator.rolling :as roll]))

(defn vwap
  "vwap (volume weighted average price)
   over n bars: rolling-sum (hlc3*vol) / rollig-sum (vol)"
  [n bar-ds]
  (assert n "vwap needs :n parameter")
  (let [hlc3  (ind/hlc3 bar-ds)
        vol (dfn/* hlc3 (:volume bar-ds))
        trail-cum-vol (roll/trailing-sum n vol)
        trail-cum-qty (roll/trailing-sum n (:volume bar-ds))]
    (dfn// trail-cum-vol trail-cum-qty)))

(comment
  (def ds
    (tc/dataset [{:open 100 :high 120 :low 90 :close 100 :volume 100}
                 {:open 100 :high 120 :low 90 :close 101 :volume 100}
                 {:open 100 :high 140 :low 90 :close 102 :volume 100}
                 {:open 100 :high 140 :low 90 :close 104 :volume 100}
                 {:open 100 :high 140 :low 90 :close 104 :volume 100}
                 {:open 100 :high 160 :low 90 :close 106 :volume 100}
                 {:open 100 :high 160 :low 90 :close 107 :volume 100}
                 {:open 100 :high 160 :low 90 :close 110 :volume 100}]))

  (vwap 2 ds)

;
  )