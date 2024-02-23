(ns ta.indicator
  (:require
   [tech.v3.datatype.functional :as fun]
   [tech.v3.dataset.rolling :as r :refer [rolling mean]]
   [tablecloth.api :as tc]
   [ta.helper.ds :refer [has-col]]))

(defn prior [{:keys [of]
              :or {of :close}}
             ds]
  (:sma (rolling ds {:window-size 1
                     :relative-window-position :left}
                 {:sma (r/last of)})))


(defn sma [{:keys [n of]
            :or {of :close}}
           ds]
  (:sma (rolling ds {:window-size n
                     :relative-window-position :left}
                 {:sma (mean of)})))


(defn tr [bar-ds]
  (assert (has-col bar-ds :low) "tr needs :low column in bar-ds")
  (assert (has-col bar-ds :high) "tr needs :high column in bar-ds")
  (let [low (:low bar-ds)
        high (:high bar-ds)
        hl (fun/- high low)]
    hl))

(defn atr [{:keys [n]} bar-ds]
  (assert n "atr needs :n option")
  (let [ds (tc/add-column bar-ds :tr (tr bar-ds))]
    (:atr (rolling ds {:window-size n
                       :relative-window-position :left}
                   {:atr (mean :tr)}))))

(defn add-atr [opts bar-ds]
  (tc/add-column bar-ds :atr (atr opts bar-ds)))

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

  (tr ds)

  (atr {:n 2} ds)
  (add-atr {:n 2} ds)

  (sma {:n 2} ds)


 ; 
  )



 