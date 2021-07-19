;;; # Sentiment Spreads
;;; 
;;; Backtest of a strategy described in 
;;; https://cssanalytics.wordpress.com/2010/09/19/creating-an-ensemble-intermarket-spy-model-with-etf-rewinds-sentiment-spreads/
;;; 
;;; The indicator according to Jeff Pietsch (who is the creator of ETF Rewind) is most valuable for intraday-trading as an indicator that captures the market’s sentiment towards risk assets. A positive spread or positive differential return implies that the market is willing to take risk and thus likely to go higher. By extension, the more spreads that are positive, or the greater the sum of the spreads, the more likely the market will go up and vice versa

(ns demo.studies.sentiment-spreads
  (:require
   [clojure.pprint]
   ;[medley.core :as m]
   [clj-time.core :as t]
   [clj-time.coerce :as tc]
   [clojisr.v1.r :as r :refer [r r->clj clj->r r+ colon bra bra<- rdiv r** r- r* ->code]]
   [clojisr.v1.require :refer [require-r]]
   [clojisr.v1.gorilla.repl :refer [pdf-off ->svg r-doc]]
   [tech.ml.dataset :as dataset]
   [pinkgorilla.notebook.repl :refer [secret load-edn-resource]]
   [pinkgorilla.ui.gorilla-plot.core :refer [list-plot multi-plot]]
   [ta.data.random :refer [random-series]]
   [ta.data.csv :refer [load-bars-file save-bars-file]]
   [ta.data.alphavantage :refer [set-key! search get-daily get-daily-fx get-daily-crypto get-crypto-rating]]
   ;[ta.model.chart :refer :all]
   [ta.series.indicator :as ind :refer [change-n]]
   [ta.series.algebra :refer [series-subtract]]
   ;[ta.series.ta4j :as ta4j]
   [ta.dataframe :refer [load-aligned print-table get-ts set-ts get-time]]
   [ta.model.trade :refer [trade]]
   [ta.model.rank :refer [rank]]
   [ta.model.stats :refer [gauntlet2]]
   [ta.series.compress :refer [compress group-month]]
   ;[ta.model.single :refer :all]
   ;:reload-all
   ))

(require-r '[base :as base :refer [$ <- $<-]]
           '[utils :as u]
           '[stats :as stats]
           '[graphics :as g]
           '[datasets :refer :all])

(base/options :width 220 :digits 2)

;; CONFIGURATION 
; (secret :alphavantage)
(set-key! (secret :alphavantage))
(def csv-path "resources/spreads/")

(def spreads [[:consumer-sentiment :XLY :XLP]
              [:smallcap-speculative-demand :IWM :SPY]
              [:em-speculative-demand :EEM :EFA]
              [:innovation-vs-safehaven :XLK :GLD]
              [:stocks-vs-bonds :SPY :AGG]
              [:quality-yield-spreads :HYG :AGG]
              [:yen-eur-currency :FXE :FXY]
              ; 8th spread- VXX-VXZ – due to insufficient historical data.
              ])
(def symbols (set (sort (mapcat rest spreads))))
symbols

(defn filename [symbol] (str csv-path (name symbol) ".csv"))

(defn load-series [symbol]
  (->> symbol
       filename
       ;(println "loading: ")
       load-bars-file
       (map #(select-keys % [:date :close]))))

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
       name
       (get-daily :compact)
       (save-series symbol)))

;(def tlt (download-save "TLT"))
;(download-save "FNARX")
;(update-needed? "FNARX")
 ;(load-series :SPY)

(filter update-needed? symbols)

;(download-save "FSAIX")
;(do (doall (map download-save (filter update-needed? symbols)))
;    (println "updating data finished!"))

(defn calc-spread [model spread]
  (let [[sname a b] spread
        close-a (get-ts model [a :close])
        close-b (get-ts model [b :close])
        chg-a (change-n 1 close-a)
        chg-b (change-n 1 close-b)
        chg-s (series-subtract chg-a chg-b)
        ;_ (println "chg: "  chg-a)
        ]
    (-> model
        (set-ts [a :chg] chg-a)
        (set-ts [b :chg] chg-b)
        (set-ts [:s sname] chg-s))))

(defn calc-spreads [model spreads]
  (reduce calc-spread model spreads))

;(defn calc-composite [model spreads]
;  (let [snames (map first spreads)
;        load (fn [sname] (get-ts model [:s sname]))
;        series (map load snames)
;        ]
;     ))

(def model (-> (load-aligned load-series symbols)
               (calc-spread (first spreads))
               (calc-spreads spreads)))

(second model)

; verify if our spread-calculation is correct
; [:stocks-vs-bonds :SPY :AGG]
(print-table model [:date [:AGG :close] [:AGG :chg]
                    [:SPY :close] [:SPY :chg]
                    [:s :stocks-vs-bonds]]  :first 5)

(defn splot [model spread]
  (let [[sname a b] spread]
    (multi-plot {:width 300 :time (get-time model)}
                [{:data (get-ts model [:s sname]) :orient :left :title sname :color "blue" :height 250 :type :line :scale "linear" :zero? false}])))

;(splot model (first spreads))
^:R [:div {:style {:display :grid
                   :grid-template-columns "400px 400px 400px 400px" :background-color "orange"}}
     (map (partial splot model) spreads)]

(def cs (get-ts model [:s :consumer-sentiment]))
(def spy (get-ts model [:SPY :chg]))

(stats/cor cs spy :method "spearman" :use "pairwise.complete.obs")

; ((def cs (get-ts model [:s :consumer-sentiment]))

(defn get-spread [s]
  (get-ts model [:s (first s)]))

;(map get-spread spreads)
; (get-s model ) 
;(base/matrix (vec (map get-spread spreads)) :ncol (count spreads))
(<- 'm (base/matrix (vec (map get-spread spreads)) :ncol (count spreads)))

; correlation between factors
(stats/cor 'm :method "spearman" :use "pairwise.complete.obs")

 ; correlation between factors and spx
(stats/cor 'm spy :method "spearman" :use "pairwise.complete.obs")

; correlation between factors and spx
(stats/cor 'm spy :method "pearson" :use "pairwise.complete.obs")

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

(defn plot-wrapper [plot-one symbol]
  ^:R [:div {:style {:width 350 :max-width 350 :display "inline-block"}}
       [:div {:style {:display "flex" :flex-direction "column" :width 350}}
        [:div  {:style {:background-color "orange" :overflow "hidden" :white-space "nowrap"}}
         symbol [:b {:style {:color "blue"}} (get-name symbol)]]
        [:div
         (plot-one symbol)]]])
(plot-wrapper plot2 "FXC")

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

