(ns ta.indicator.atr
  (:require 
    [tech.v3.datatype.functional :as fun]
    [tech.v3.dataset.rolling :refer [rolling mean]]
    [tablecloth.api :as tc]
   ))

(defn sma [ds {:keys [n of]
               :or {of :close}}]
    (:sma (rolling ds {:window-size n
                 :relative-window-position :left}
             {:sma (mean of)})))


(defn tr [ds]
  (let [low (:low ds)
        high (:high ds)
        hl (fun/- high low)]
    hl))

(defn atr [ds {:keys [n]}]
  (let [ds (tc/add-column ds :tr (tr ds))]
    (rolling ds {:window-size n
                 :relative-window-position :left}
           {:atr (mean :tr)
          })
    ))

(comment 
   (def ds
    (tc/dataset [{:open 100 :high 120 :low 90 :close 100}
                 {:open 100 :high 120 :low 90 :close 101}
                 {:open 100 :high 140 :low 90 :close 102}
                 {:open 100 :high 140 :low 90 :close 104}
                 {:open 100 :high 140 :low 90 :close 104}
                 {:open 100 :high 160 :low 90 :close 106}
                 {:open 100 :high 160 :low 90 :close 107}
                 {:open 100 :high 160 :low 90 :close 110}]))
  
  (sma ds {:n 2})

  (tr ds)

  (atr ds {:n 2})
  
 ; 
  )



 