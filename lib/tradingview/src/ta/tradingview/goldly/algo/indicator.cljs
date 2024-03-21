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
  {"line" {:visible true
           :transparency 0
           :linestyle 0
           :trackPrice false ; Show price line (horizontal line with last price)
           :linewidth 2 ; Make the line thinner
           :plottype 2 ; Plot type is Line
           :color "#880000" ; Set the plotted line color to dark red
           }
   "chars" {:visible true
            :transparency 0
            :trackPrice false ; Show price line (horizontal line with last price)
            :char "*"
            :location "AboveBar" ; AboveBar BelowBar Top Bottom Right Left Absolute AbsoluteUp AbsoluteDown
            :textColor "#ffff00" ; Set the plotted line color to dark red
            :size "small"}
   "shape" {:visible true
            :transparency 0
            :trackPrice false ; Show price line (horizontal line with last price)
            :style "xcross"
            :location "AboveBar" ; AboveBar BelowBar Top Bottom Right Left Absolute AbsoluteUp AbsoluteDown

            :color "#880000" ; Set the plotted line color to dark red
            :textColor "#ffff00"}
   "arrows" {:visible true
             :transparency 0
             :trackPrice false
             :location "AboveBar" ; AboveBar BelowBar Top Bottom Right Left Absolute AbsoluteUp AbsoluteDown
             :colorup "#880000"
             :colordown "#00ff00"}})

(defn get-col-style [spec]
  (let [type (get-col-type spec)
        style-default (get default-plot-styles type)]
    (if (string? spec)
      style-default
      (merge style-default (dissoc spec :type)))))

(comment
  (get-col-style "line")
  (get-col-style {:type "line" :color "green"}))

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
     :is_hidden_study true ; dont allow selection in indicator tab
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
   (remove nil?)))

(comment
  (get-panes "moon" [nil {:volume "column"}])
;  
  )

(defn calc-chart-pane [algo-ctx pane-idx PineJS]
  (fn []
    (clj->js
     {:init (fn [_context _inputCallback]
              (println "algo-pane init pane: " pane-idx)
                ; nothing needed to initialize
              nil)
      :main (fn [context _inputCallback]
              ;(println "calculating algo-pane " pane-idx)
              (let [time-update (-> PineJS .-Std (.updatetime context))
                    time (-> PineJS .-Std (.time context))
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
                    time-at-midnight (- time 32400000)
                    row-vals-vec (get-pane-data algo-ctx pane-idx time-at-midnight)]
                 ;(println "time: " time "row-vals-vec: " row-vals-vec ) 
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
    (map #(study-chart-pane algo-ctx PineJS algo-name %) panes)))

(defn all-algo->studies [algo-ctx PineJS data]
  (mapcat #(algo-spec-to-study algo-ctx PineJS (:name %) (:charts %)) data))

(defn get-indicator-names [algo-name chart-spec]
  (println "get-indicator-names algo: " algo-name "chart-spec: " chart-spec)
  (->>
   (map-indexed (fn [pane-idx pane-spec]
                  (when pane-spec
                    (str "algo-" algo-name "-" pane-idx)))
                chart-spec)
   (remove nil?)))

(comment
  (get-indicator-names
   "bongo"
   [nil ; nothing to add in price pane
    {:volume "column"}]))

(defn study-chart-studies [algo-ctx PineJS]
  (let [chart-spec (get-chart-spec algo-ctx)
        algo-name (get-algo-name algo-ctx)]
    (algo-spec-to-study algo-ctx PineJS algo-name chart-spec)))


