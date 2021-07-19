;;; # Dynamic Asset Allocation
;;; 
;;; Backtest of a sector rotation strategy described in https://drive.google.com/open?id=1FB1IpTC6WzfQkNoScCL3mQmOUVqTSENy

(ns demo.studies.asset-allocation-dynamic
  (:require
   [clojure.pprint]
   ;[medley.core :as m]
   [clj-time.core :as t]
   [clj-time.coerce :as tc]
   [pinkgorilla.notebook.repl :refer [secret load-edn-resource]]
   [pinkgorilla.ui.gorilla-plot.core :refer [list-plot multi-plot]]
   [ta.data.random :refer [random-series]]
   [ta.data.csv :refer [load-bars-file save-bars-file]]
   [ta.data.alphavantage :refer [set-key! search get-daily get-daily-fx get-daily-crypto get-crypto-rating]]
   ;[ta.model.chart :refer :all]
   [ta.series.indicator :as ind :refer [change-n]]
   ;[ta.series.ta4j :as ta4j]
   [ta.model.trade :refer [set-ts get-ts trade]]
   [ta.model.rank :refer [rank]]
   [ta.model.stats :refer [gauntlet2]]
   [ta.series.compress :refer [compress group-month]]
   ;[ta.model.single :refer :all]
   ;:reload-all
   ))
;; CONFIGURATION 
; What is the path to the sector fund csv files?
;(def path "../../quant/trateg/resources/sector/")
(def path "resources/sector/")

(def symbols [:A :B :C])

(def model (atom {}))

(def default-params
  {:length 30
   :roc 2})

(defn calculate [params]
  (let [length (:length params)]
    (swap! model assoc :length length)
    ; calculations on individual symbols  
    (doall (map
            (fn [symbol]
        ;(println "calc" symbol)
              (let [save (partial set-ts model symbol)]
                (save :symbol symbol)
                (save :price (random-series length))
                (save :roc (change-n (:roc params) (get-ts model symbol :price)))))
            symbols))
    ; calculate ranks
    (rank model length :roc false :rank)))

(calculate default-params)

;@model
(get-in @model [:symbols :B])

(defn plot-field [symbol field]
  (let [data (get-ts model symbol field)]
    (list-plot data :joined true :color "blue")))

(plot-field :C :price)
(plot-field :C :rank)

(defn exit-rank [model index position]
  (let [symbol (:symbol position)
        rank (get-ts model symbol :rank index)]
      ;(println index "exit-rank" symbol)
    (when (>= rank 3)
      (do
          ;(println index "exit-rank" symbol "triggered.")
        true))))

(defn entry-rank [model index symbol]
  (let [rank (get-ts model symbol :rank index)]
   ;(println index "entry-rank" symbol)
    (when (<= rank 1)
      (do
      ;(println index "entry-rank" symbol "triggered.")
        [symbol 10]))))

(def pf (trade model exit-rank entry-rank))
pf

(clojure.pprint/print-table (:roundtrips pf))

(list-plot (:equity pf) :joined true :color "blue")

(gauntlet2 pf)

; SETUP / TEST ALPHA VANTAGE FEED
; secrets should not be saved in a notebook
; secret loads a key from user defined secrets.
; the current implementation does just read the file test/creds.edn
; in the future the notebook will save creds only in webbrowser local storage

(set-key! (secret :alphavantage))
;(secret :alphavantage)
;(clojure.pprint/print-table (take 5 (reverse (get-daily :compact "FNARX"))))
;(get-daily :compact "BONGO1")
;(get-daily :compact "TLT")

(defn filename [symbol] (str path symbol ".csv"))

(defn load-list [name]
  (println "loading list: " name)
  (load-edn-resource (str "etf/" name ".edn")))

(def lists ["bonds"
            "commodity-sector"
            "commodity-industry"
            "equity-region"
            ;"equity-region-country" 
            "equity-sector"
            ;"equity-sector-industry" ; not yet finished
            "equity-style"
            "currency"])
;(load-list "commodity-sector")
;(def instruments (load-list "fidelity-select"))
(def instruments (mapcat load-list lists))
(def symbols (map :symbol instruments))
;(clojure.pprint/print-table instruments)
symbols
;instruments
;(count instruments)

(defn no-data? [series]
  (or (nil? series) (= 0 (count series))))

(defn update-needed? [symbol]
  (->> symbol filename load-bars-file no-data?))

(defn save-series [symbol series]
  (if (no-data? series)
    (println "No data for: " symbol " data: " series)
    (save-bars-file (filename symbol) series)))

(defn download-save [symbol]
  (println "downloading" symbol)
  (->> symbol
       (get-daily :compact)
       (save-series symbol)))

;(def tlt (download-save "TLT"))
;(download-save "FNARX")
;(update-needed? "FNARX")

(filter update-needed? symbols)

;(download-save "FSAIX")
(do (doall (map download-save (filter update-needed? symbols)))
    (println "updating data finished!"))

(def symbols-ok (remove update-needed? symbols))
symbols-ok

(defn get-bar-series [symbol]
  (->> symbol filename load-bars-file))

(defn get-close-series [symbol]
  (->> symbol get-bar-series (map :close)))

(defn get-date-series
  "time-index series as unix timestamps"
  [symbol]
  (->> symbol get-bar-series (map :date) (map tc/to-long)))

(defn get-name [symbol]
  (if-let [instrument (first (filter #(= symbol (:symbol %)) instruments))]
    (:name instrument)
    "??"))

; unchanged: 
;2019-11-14T00:00:00.000Z,5.81,5.81,5.81,5.81,0.0

(defn plot1 [symbol]
  ^:R [:div {:style {:width 500 :max-width 500 :display "inline-block"}}
       [:div {:style {:display "flex" :flex-direction "column" :width 500}}
        [:div  {:style {:background-color "orange"}}
         symbol [:b {:style {:color "blue"}} (get-name symbol)]]
        [:div
         (list-plot (get-close-series symbol))]]])
;(get-name "FSAGX")
(pr-str (plot1 "FIDSX"))

(into ^:R [:<> [:h1 "Last 100 Days.."]] (map plot1 symbols-ok))

(defn plot2 [symbol]
  (multi-plot {:width 300 :time (get-date-series symbol)}
              [{:data (get-close-series symbol) :orient :left :title symbol :color "blue" :height 250 :type :line :scale "linear" :zero? false}]))
(plot2 "FXC")

(into ^:R [:<> [:h1 "Last 100 Days.."]] (map plot3 symbols-ok))

(def crisis [{:start (tc/to-long (t/date-time 2020 03 03))
              :end (tc/to-long (t/date-time 2020 03 20))
              :color "orange"}])
crisis

(defn plot4 [symbol]
  (multi-plot {:width 300 :time (get-date-series symbol)}
              [{:data crisis :type "rect" :height 250}
               {:data (get-close-series symbol) :orient :left :title symbol :color "blue" :height 250 :type :line :scale "linear" :zero? false}]))
 ;^:R [:p/json (plot4 "FXC")]
(plot-wrapper plot4 "FXC")

(into ^:R [:<> [:h1 "Last 100 Days.."]] (map (partial plot-wrapper plot4) symbols-ok))
