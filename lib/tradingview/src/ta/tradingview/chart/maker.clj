(ns ta.tradingview.chart.maker
  (:require
   [nano-id.core :refer [nano-id]]
   [ta.db.asset.db :refer [instrument-details]]
   [ta.tradingview.db.chart :refer [save-chart now-epoch chart-list load-chart load-chart-boxed]]
   [ta.tradingview.db.asset :refer [inst-type]]
   [ta.tradingview.chart.template.mainseries :refer [template-mainseries]]
   [ta.tradingview.chart.template.study :refer [template-study]]
   [ta.tradingview.chart.template.sessions :refer [template-sessions]]
   [ta.tradingview.chart.template.chart :refer [chart-template]]
   [ta.tradingview.chart.template.pane :refer [pane-template]]))

(defn create [template]
  (assoc template
         :id (nano-id 6) ; "BlBo4C" 
         ))

(defn create-symbol [template symbol]
  (-> template
      create
      (assoc-in [:state :shortName] symbol)
      (assoc-in [:state :symbol] symbol)))

(defn create-study [template symbol points]
  (-> (create-symbol template symbol)
      (assoc :points points)))

(defn make-pane [source-main #_source-study #_source-sessions source-drawings]
  (let [id-main (:id source-main)
        source-drawings (map #(assoc % :ownerSource id-main) source-drawings)
        ids-drawings (map :id source-drawings)
        sources (into [] (concat [source-main
                                  #_source-study
                                  #_source-sessions]
                                 source-drawings))]
    (assoc pane-template
           :mainSourceId id-main ; "pOQ6pA"
           :leftAxisSources []
           :rightAxisSources (into [id-main] ids-drawings) ; ["pOQ6pA"  "Co0ff2" "xy6qRv" "srISFZ" "8RaFG7" "pm68xf" "BlBo4C"]
           :sources sources)))

(defn chart-file [client-id user-id symbol name pane]
  (let [i (instrument-details symbol)
        charts [(assoc
                 chart-template
                 :panes [pane])]]
    {:client (str client-id) ; "77"
     :user (str user-id) ; "77"
     :symbol symbol
     :short_name symbol
     :name  name ; (:name i)
     :symbol_type (inst-type i)
     :exchange (:exchange i) ;  "NasdaqNM"
     :listed_exchange ""
     :resolution "D"
     :is_realtime "1"
     :publish_request_id "r5kl776mb6o"
     :legs [{:symbol symbol, :pro_symbol symbol}]
     :timestamp 1636555326
     :description ""
     :layout "s"
     :charts charts}))

(defn make-chart [client-id user-id chart-id symbol name source-drawings]
  (let [source-main (create-symbol template-mainseries symbol)
        ;source-study (create template-study)
        ;source-sessions (create template-sessions)
        pane (make-pane source-main #_source-study #_source-sessions source-drawings)]
    (->> (chart-file client-id user-id symbol name pane)
         (save-chart client-id user-id chart-id))))

(comment
  (create template-study)
  (create template-sessions)
  (create-symbol template-mainseries "MSFT")

  (make-chart 77 77 123 "MSFT" "test-empty-MSFT" [])

;
  )
