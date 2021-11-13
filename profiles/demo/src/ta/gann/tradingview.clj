(ns ta.gann.tradingview
  (:require
   [clojure.pprint]
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

(defn determine-wh [s]
  (case s
    "ETHUSD" :crypto
    "BTCUSD" :crypto
    :stocks))

(defn make-boxes-symbol [s dt-start dt-end]
  (let [client-id 77
        user-id 77
        chart-id 201
        chart-name (str "autogen gann-" s)
        boxes  (get-gann-boxes {:symbol s
                                :wh :crypto
                                :dt-start (parse-date dt-start)
                                :dt-end (parse-date dt-end)
                                ;:px-min (Math/log10 284) 
                                ;:px-max (Math/log10 3482)
                                })]
    (print-boxes boxes)
    (->>
     boxes
     (map gann-box->tradingview-study)
     (apply concat)
     (into [])
     (make-chart client-id user-id chart-id s chart-name))))

(comment

  ; create one boxes for one symbol
  (make-boxes-symbol "BTCUSD" "2005-01-01" "2022-06-30")

;
  )
(defn make-boxes-symbols [dt-start dt-end symbols]
  (let [client-id 77
        user-id 77
        chart-id 202
        chart-name (str "autogen gann " (count symbols) " symbols")
        boxes-for-symbol (fn [s]
                           (get-gann-boxes {:symbol s
                                            :wh (determine-wh s)
                                            :dt-start (parse-date dt-start)
                                            :dt-end (parse-date dt-end)}))]
    (->> symbols
         (map boxes-for-symbol)
         (apply concat)
         (map gann-box->tradingview-study)
         (apply concat)
         (into [])
         (make-chart client-id user-id chart-id (first symbols) chart-name))))

(defn make-boxes-all [dt-start dt-end]
  (make-boxes-symbols dt-start dt-end (gann-symbols)))

(comment

  (make-boxes-symbols  "2005-01-01" "2022-03-31"
                       ["BTCUSD"])

  (make-boxes-all  "2005-01-01" "2022-04-01")

 ; 
  )
