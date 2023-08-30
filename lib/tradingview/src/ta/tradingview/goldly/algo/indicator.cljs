(ns ta.tradingview.goldly.algo.indicator
  (:require
    [ta.tradingview.goldly.algo.context :refer [get-algo-name get-chart-spec get-pane-data]]))


; (def clj->js identity)

(comment 
  
[nil ; nothing to add in price pane
 {:volume "column"}]

[{:trade "flags"}
 {:volume "column"}]

[{;:trade "flags"
  :bb-lower "line"
  :bb-upper "line"}
 {:volume "column"}]

[{:sma-st "line"
  :sma-lt "line"
  :sma-diff {:type "line" :color "red"}}]
;
  )

(defn get-col-type [spec]
  (if (string? spec)
    spec
    (:type spec)))

(def default-plot-styles
  {"line" {:linestyle 0
           :visible true
           :linewidth 1 ; Make the line thinner
           :plottype 2 ; Plot type is Line
           :trackPrice true ; Show price line (horizontal line with last price)
           :color "#880000" ; Set the plotted line color to dark red
           }
   "chars" {:linestyle 0
            :visible true
            :char "*"
            :title "bongo"
            :location "AboveBar" ; AboveBar BelowBar Top Bottom Right Left Absolute AbsoluteUp AbsoluteDown
            :linewidth 1 ; Make the line thinner
            :plottype 2 ; Plot type is Line
            :trackPrice false ; Show price line (horizontal line with last price)
            :color "#880000" ; Set the plotted line color to dark red
            }
   "cols" {:linestyle 0
           :visible true
           :linewidth 1 ; Make the line thinner
           :plottype 5 ; Plot type is Column
           :trackPrice false ; Show price line (horizontal line with last price)
           :color "#880000" ; Set the plotted line color to dark red
           }})

(defn get-col-style [spec]
  (let [type (get-col-type spec)
        style-default (get default-plot-styles type)
        ]
    (if (string? spec)
       style-default
      (merge style-default (dissoc spec :type)))))

(comment 
(get-col-style "line")
(get-col-style {:type "line" :color "green"})  
  )

(defn plot-id [col]
  (str "plot-" (name col)))

(defn plots [col-spec]
  (->> (map (fn [[col spec]]
               {:id (plot-id col)
                :type (get-col-type spec)})
         col-spec)
       (into [])))

(defn plot-styles [col-spec]
  (->> (map (fn [[col spec]]
              [(plot-id col) (get-col-style spec)])
            col-spec)
       (into {})))

(defn plot-config [col-spec]
  (->> (map (fn [[col spec]]
              [(plot-id col)
               {:title (name col)}])
            col-spec)
       (into {})))

(comment 
  (plots {:sma-st "line"
          :sma-lt "line"
          :sma-diff {:type "line" :color "red"}})

  
  (get plot-styles "line")
  (plot-styles
   {:sma-st "line"
    :sma-lt "chars"
    :sma-diff {:type "cols" :color "red"}})  
  ;; => {"plot-sma-st" {:linestyle 0, :visible true, :linewidth 1, :plottype 2, :trackPrice true, :color "#880000"},
  ;;     "plot-sma-lt"
  ;;     {:linewidth 1,
  ;;      :color "#880000",
  ;;      :trackPrice false,
  ;;      :plottype 2,
  ;;      :title "bongo",
  ;;      :linestyle 0,
  ;;      :visible true,
  ;;      :location "AboveBar",
  ;;      :char "*"},
  ;;     "plot-sma-diff" {:linestyle 0, :visible true, :linewidth 1, :plottype 5, :trackPrice false, :color "red"}}
  (plot-config
   {:sma-st "line"
    :sma-lt "chars"
    :sma-diff {:type "cols" :color "red"}})
  ;
  )


(defn pane-meta [algo-name pane-idx col-spec]
  (let [name (str "algo-" algo-name "-" pane-idx)]
    {:_metainfoVersion 51
     :id name
     :name name
     :description name ; this is used in the api
     :shortDescription name
     :pane-idx pane-idx
     "isCustomIndicator" true
     "is_price_study" (if (= 0 pane-idx) true false) ; plot in main chart-pane
     "isTVScript" false
     "isTVScriptStub" false
     "format" {"type" "price"
               "precision" 4}
     "plots" (plots col-spec)
     "defaults" {"styles" (plot-styles col-spec)}

     "inputs" []

     "styles" (plot-config col-spec)}))


(comment 
   (pane-meta "buy-hold" 0
     {:sma-st "line"
      :sma-lt "chars"
      :sma-diff {:type "cols" :color "red"}})  
  ;
  )


(defn get-panes [algo-name chart-spec]
  (->> 
    (map-indexed (fn [pane-idx pane-spec]
                   (when pane-spec
                     (pane-meta algo-name pane-idx pane-spec))) 
                 chart-spec) 
    (remove nil?)
   ))

(comment 
  (get-panes "moon" [nil {:volume "column"}])  
;  
  )

(defn calc-chart-pane [algo-ctx pane-idx PineJS]
  (fn []
    (clj->js
     {:init (fn [_context _inputCallback]
                ; nothing needed to initialize
                nil)
      :main (fn [context _inputCallback]
              (println "calculating algo-pane " pane-idx)
              (let [;t (-> PineJS .-Std (.updatetime context)) ;var v = PineJS.Std.updatetime (this._context);
                     ;(this._context['symbol']['time'] !=NaN){
                    ;s (aget context "symbol")
                    ;t (aget s "time")
                     ;year (-> PineJS .-Std (.year context))
                     ;month (-> PineJS .-Std (.month context))
                     ;day (-> PineJS .-Std (.dayofmonth context))
                     ; updatetime
                    ;main-symbol (-> PineJS .-Std (.ticker context))
                    ;v Double/NaN
                    time-idx 100
                    row-vals-vec (get-pane-data algo-ctx pane-idx time-idx)
                    ]
                 ;(println "time: " t ) 
                 (clj->js row-vals-vec)))})))



(defn study-chart-pane [algo-ctx PineJS algo-name pane-meta]
  (let [data {:name (:name pane-meta)
              :metainfo pane-meta
              :constructor (calc-chart-pane algo-ctx (:pane-idx pane-meta) PineJS)}]
    (println "algo: " algo-name "meta: " pane-meta)
    data))

(defn algo-spec-to-study [algo-ctx PineJS algo-name chart-spec]
  (println "adding algo: " algo-name " chart-spec: " chart-spec)
  (let [panes (get-panes algo-name chart-spec)]
    (map #(study-chart-pane algo-ctx PineJS algo-name %) panes)
     )
    
    )

(defn all-algo->studies [algo-ctx PineJS data]
  (mapcat #(algo-spec-to-study algo-ctx PineJS (:name %) (:charts %)) data))

(defn study-chart-studies [algo-ctx PineJS]
  (let [chart-spec (get-chart-spec algo-ctx)
        algo-name (get-algo-name algo-ctx)]
    (algo-spec-to-study algo-ctx PineJS algo-name chart-spec)
    ))


  