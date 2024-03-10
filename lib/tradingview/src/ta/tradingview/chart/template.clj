(ns ta.tradingview.chart.template
  (:require
   [tick.core :as tick]
   [modular.persist.protocol :refer [save loadr]]
   [modular.persist.edn] ; side effects
   [ta.helper.date :refer [now-datetime datetime->epoch-second epoch-second->datetime]]
   [ta.tradingview.chart.maker :refer [create-study make-chart]]
   [ta.tradingview.chart.template.trendline :as tl]
   [ta.tradingview.chart.template.pitchfork :as pf]
   [ta.tradingview.chart.template.gann :as g]
   [ta.tradingview.db.clip :refer [charts-path template-path marks-path]]))

; db

(defn filename-template  [name]
  (str template-path name ".edn"))

(defn save-template
  [name data]
  (save :edn (filename-template name) data))

(defn load-template [name]
  (loadr :edn (filename-template name)))

; helper

(defn dt [s]
  (-> s tick/date-time datetime->epoch-second))

; impl

(defn trendline [{:keys [symbol template ap bp at bt]
                  :or {template tl/trendline}}]
  (create-study template symbol
                [{:time_t at, :offset 0, :price ap}
                 {:time_t bt, :offset 0, :price bp}]))

(defn pitchfork [{:keys [symbol template ap bp cp at bt ct]
                  :or {template pf/pitchfork}}]
  (create-study template symbol
                [{:time_t at, :offset 0, :price ap}
                 {:time_t bt, :offset 0, :price bp}
                 {:time_t ct, :offset 0, :price cp}]))

(defn gann [{:keys [symbol template ap bp at bt]
             :or {;template g/gann
                  template (load-template "gann-nice")}}]
  (create-study template symbol
                [{:time_t at, :offset 0, :price ap}
                 {:time_t bt, :offset 0, :price bp}]))

(defn fib-circle [{:keys [symbol template ap bp at bt]
                   :or {;template g/gann
                        template (load-template "fib-circle")}}]
  (create-study template symbol
                [{:time_t at, :offset 0, :price ap}
                 {:time_t bt, :offset 0, :price bp}]))

(defn gann-vertical [symbol p-0 d-p n a-t b-t]
  (into []
        (for [i (range n)]
          (gann
           {:symbol symbol
            :ap (+ p-0 (* i d-p))  :at a-t
            :bp (+ p-0 (* (inc i) d-p)) :bt b-t}))))

(comment

  (def id-generated 1512)

  (make-chart 77 77 id-generated "MSFT" "test-empty-MSFT"
              [(trendline {:symbol "MSFT"
                           :a-p 300.0
                           :b-p 330.0
                           :a-t (dt "2021-08-04T00:00:00")
                           :b-t (dt "2021-11-04T00:00:00")})])

  (make-chart 77 77 id-generated "AMZN" "AMZN fibcircle"
              [(fib-circle {:symbol "AMZN"
                            :a-p 3200.0
                            :b-p 3320.0
                            :a-t (dt "2021-08-04T00:00:00")
                            :b-t (dt "2021-11-04T00:00:00")})])

  (gann-vertical "BTCUSD" 1000.0 200.0 5 1511879400 1515076200)

;  
  )