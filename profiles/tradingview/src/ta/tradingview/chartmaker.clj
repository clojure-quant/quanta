(ns ta.tradingview.chartmaker
  (:require
   [clojure.pprint :refer [print-table]]
   [nano-id.core :refer [nano-id]]
   [tick.core :as tick]
   [differ.core :as differ]
   [ta.data.date :refer [now-datetime datetime->epoch-second epoch-second->datetime]]
   [ta.tradingview.db-ts :refer [save-chart now-epoch chart-list load-chart load-chart-boxed]]
   [ta.tradingview.db-instrument :refer [instrument inst-type inst-exchange inst-name category-name->category inst-crypto?]]
   [ta.tradingview.template.mainseries :refer [template-mainseries]]
   [ta.tradingview.template.study :refer [template-study]]
   [ta.tradingview.template.sessions :refer [template-sessions]]
   [ta.tradingview.template.chart :refer [chart-template]]
   [ta.tradingview.template.pane :refer [pane-template]]))

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
      (assoc :points points)
      ))

(defn make-pane [source-main source-study #_source-sessions source-drawings]
  (let [id-main (:id source-main)
        source-drawings (map #(assoc % :ownerSource id-main) source-drawings)
        ids-drawings (map :id source-drawings)
        sources (into [] (concat [source-main
                                  source-study
                                  #_source-sessions]
                                 source-drawings))]
    (assoc pane-template
           :mainSourceId id-main ; "pOQ6pA"
           :leftAxisSources []
           :rightAxisSources (into [id-main] ids-drawings) ; ["pOQ6pA"  "Co0ff2" "xy6qRv" "srISFZ" "8RaFG7" "pm68xf" "BlBo4C"]
           :sources sources)))

(defn chart-file [client-id user-id symbol name pane]
  (let [i (instrument symbol)
        charts [(assoc
                 chart-template
                 :panes [pane])]]
    {:client (str client-id) ; "77"
     :user (str user-id) ; "77"
     :symbol symbol
     :short_name symbol
     :name  name ; (inst-name symbol i)
     :symbol_type (inst-type i)
     :exchange (inst-exchange i) ;  "NasdaqNM"
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
        source-study (create template-study)
        ;source-sessions (create template-sessions)
        pane (make-pane source-main source-study #_source-sessions source-drawings)]
    (->> (chart-file client-id user-id symbol name pane)
         (save-chart client-id user-id chart-id))))

(defn dt [s]
  (-> s tick/date-time datetime->epoch-second))

;; debugging

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


(comment
  (create template-study)
  (create template-sessions)
  (create-symbol template-mainseries "MSFT")

  (make-chart 77 77 123 "MSFT" "test-empty-MSFT" [])


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
