(ns ta.gann.tradingview
  (:require
   [clojure.pprint]
   [taoensso.timbre :refer [trace debug info warnf error]]
   [ta.helper.date :refer [parse-date now-datetime epoch-second->datetime ->epoch-second]]
   [ta.tradingview.chart.maker :refer [make-chart]]
   [ta.tradingview.chart.template :as t :refer [dt gann]]
   [ta.gann.gann :refer [gann-symbols print-boxes]]
   [ta.gann.window :refer [get-gann-boxes]]))

(defn gann-box->tradingview-study [{:keys [symbol ap bp at bt]}]
  (let [ap (Math/pow 10 ap)
        bp (Math/pow 10 bp)
        at (->epoch-second at)
        bt (->epoch-second bt)
        boxes [{:symbol symbol :ap ap :bp bp :at at :bt bt}
               {:symbol symbol :ap bp :bp ap :at at :bt bt}
               {:symbol symbol :ap ap :bp bp :at bt :bt at}
               {:symbol symbol :ap bp :bp ap :at bt :bt at}]]
    (map gann boxes)))

(defn make-boxes-symbol [s id dt-start dt-end]
  (info "making gann chart for " s  " id: " id)
  (let [client-id 77
        user-id 77
        chart-id id
        chart-name (str "autogen gann-" s)
        boxes (get-gann-boxes {:s s
                               :dt-start dt-start
                               :dt-end dt-end})]
    (->>
     boxes
     (map gann-box->tradingview-study)
     (apply concat)
     (into [])
     (make-chart client-id user-id chart-id s chart-name))
    (print-boxes boxes)
    boxes))

(comment

  ; create one boxes for one symbol
  (make-boxes-symbol "BTCUSD" 200 "2005-01-01" "2022-06-30")
  (make-boxes-symbol "GLD" 201 "2005-01-01" "2022-06-30")

;
  )
(defn make-boxes-symbols [dt-start dt-end symbols]
  (let [client-id 77
        user-id 77
        chart-id 202
        chart-name (str "autogen gann " (count symbols) " symbols")
        boxes-for-symbol (fn [s]
                           (get-gann-boxes {:s s
                                            :dt-start dt-start
                                            :dt-end dt-end}))]
    (->> symbols
         (map boxes-for-symbol)
         (apply concat)
         (map gann-box->tradingview-study)
         (apply concat)
         (into [])
         (make-chart client-id user-id chart-id (first symbols) chart-name))))

(defn make-boxes-all [dt-start dt-end]
  (make-boxes-symbols dt-start dt-end (gann-symbols)))

(defn make-boxes-all-individual [dt-start dt-end]
  (doall
   (map-indexed
    #(do (make-boxes-symbol %2 (+ 300 %1)  dt-start dt-end)
         nil) ; nil, so we dont accumulate memory.
    (gann-symbols))))

(comment

  (make-boxes-symbols  "2005-01-01" "2022-03-31"
                       ["BTCUSD"
                        "QQQ"])

  (make-boxes-all  "2005-01-01" "2022-04-01")

  (make-boxes-all-individual  "2000-01-01" "2022-04-01")

; 
  )
