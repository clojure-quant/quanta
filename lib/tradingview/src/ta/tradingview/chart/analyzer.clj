(ns ta.tradingview.chart.analyzer
  (:require
   [clojure.pprint :refer [print-table]]
   [differ.core :as differ]
   [ta.tradingview.db.chart :refer [save-chart chart-list load-chart]]
   [ta.tradingview.chart.template :refer [save-template]]))

;; extract

(defn get-pane [chart]
  (-> chart
      :charts first :panes first))

(defn get-sources [chart]
  (-> chart
      :charts first :panes first :sources))

(defn sources-summary [chart]
  (let [sources (get-sources chart)]
    (map #(select-keys % [:id :type :zorder]) sources)))

(defn filter-type [chart t]
  (let [sources (get-sources chart)]
    (-> (filter #(= t (:type %)) sources)
        first)))

(defn source-state-summary [source]
  (select-keys (:state source) [:shortName :symbol]))

(defn keys-sorted [o]
  (-> o keys sort))

(defn diff-summary [preprocess id-generated id-compare]
  (let [g (-> (load-chart 77 77 id-generated) preprocess)
        c (-> (load-chart 77 77 id-compare) preprocess)]
    {:l (differ/diff g c)
     :r (differ/diff c g)}))

(defn extract-study [user-id client-id chart-id type name]
  (let [save #(save-template name %)]
    (-> (load-chart user-id client-id chart-id)
        (filter-type type)
        (save))))

(comment

  (extract-study 77 77 1636805136 "LineToolGannComplex" "gann-nice")

  (extract-study 77 77 1636839899 "LineToolFibCircles" "fib-circle")

  (def id-generated 123)
  (def id-compare 1636726545)

  ; test: chart-meta-data
  (-> [(-> (load-chart 77 77 id-generated)  (dissoc :charts :timeScale :legs))
       (-> (load-chart 77 77 id-compare)  (dissoc :charts :timeScale :legs))]
      print-table)

  ; test: sources list
  (-> (load-chart 77 77 id-generated) sources-summary)
  (-> (load-chart 77 77 id-compare)   sources-summary)

  ; test: mainseries keys
  (-> (load-chart 77 77 id-generated)       (filter-type "MainSeries") keys-sorted)
  (-> (load-chart 77 77 id-compare)  (filter-type "MainSeries") keys-sorted)

   ; test: mainseries state
  (-> (load-chart 77 77 id-generated)       (filter-type "MainSeries") source-state-summary)
  (-> (load-chart 77 77 id-compare)  (filter-type "MainSeries") source-state-summary)

; test mainseries differences (in both ways)
  (differ/diff
   (-> (load-chart 77 77 id-generated) (filter-type "MainSeries"))
   (-> (load-chart 77 77 id-compare)  (filter-type "MainSeries")))

  (differ/diff
   (-> (load-chart 77 77 id-compare)  (filter-type "MainSeries"))
   (-> (load-chart 77 77 id-generated) (filter-type "MainSeries")))

; test study differences (in both ways)
  (differ/diff
   (-> (load-chart 77 77 id-generated) (filter-type "Study"))
   (-> (load-chart 77 77 id-compare)  (filter-type "Study")))

  (differ/diff
   (-> (load-chart 77 77 id-compare)  (filter-type "Study"))
   (-> (load-chart 77 77 id-generated) (filter-type "Study")))

    ; test study differences (in both ways)
  (diff-summary
   #(dissoc % :id :charts :symbol_type :exchange :timestamp :symbol :name :short_name :publish_request_id :legs)
   id-generated id-compare)

   ;; no differences in mainseries
  (diff-summary
   #(filter-type % "MainSeries")
   id-generated id-compare)

   ;; no differences in study (except for ids)
  (diff-summary
   #(filter-type % "Study")
   id-generated id-compare)

   ;; no differences in except ids of sources
  (diff-summary
   #(-> (get-pane %) (dissoc :sources))
   id-generated id-compare)

  (diff-summary
   #(dissoc % :symbol :name :short_name :publish_request_id :legs :id :exchange :timestamp :symbol_type)
         ;identity
   id-generated id-compare)

  (-> (load-chart 77 77 1636558275) ; AMZN: Pitchfork   MSFT: LineTrend
      :content  ; :layout :charts
      :charts
      first
      :panes
      first ; (:sources :leftAxisSources  :rightAxisSources :leftAxisState :rightAxisState  :overlayPriceScales :mainSourceId)
   ; :sources
      :sources
    ;count
      (get 5)
    ;:type
    ;(get-in [:state :styles])
      )

;  
  )