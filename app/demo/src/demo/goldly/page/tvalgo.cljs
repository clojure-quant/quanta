(ns tvalgo
  (:require 
    [r :refer :all]
    [user :refer [println run-a link-href add-page tradingview-chart set-symbol study-table tv-widget-atom]]
    [tv]
   ))

(defonce algo-state
  (r/atom {:algos []
           :algo "buy-hold"
           :algoinfo {:options {}
                      :charts []}
           :symbols ["TLT" "SPY" "QQQ" "EURUSD"]
           :symbol "SPY"
           :frequency "D"}))

(defonce window-state
  (r/atom {:data []}))

;; ALGO

(run-a algo-state [:algos] :algo/names) ; get once the names of all available algos

(defn algo-info [algo]
  (let [algo-loaded (r/atom nil)]
    (when algo
      (when-not (= @algo-loaded algo)
        (run-a algo-state [:algoinfo] :algo/info algo)
        nil))))

(defn algo-dialog []
  [:div.bg-blue-300.p-5
    [:h1.text-blue-800.text-large "algo options"]
    [:p (pr-str (get-in @algo-state [:algoinfo :options]))]
    
    [:h1.text-blue-800.text-large "charts"]
    [:p (pr-str (get-in @algo-state [:algoinfo :charts]))]
   ])




;; WINDOW

(defn get-window [epoch-start epoch-end]
  (let [algo (:algo @algo-state)
        symbol (:symbol @algo-state)
        frequency (:frequency @algo-state)
        options (get-in @algo-state [:algoinfo :options])]
     (run-a window-state [:data] :algo/run-window
            algo symbol frequency options epoch-start epoch-end)
  ))

(defn get-window-demo []
  (let [epoch-start 1642726800 ; jan 21 2022
        epoch-end 1650499200 ; april 21 2022
         ]
    (get-window epoch-start epoch-end)))

(defn get-window-current []
  (let [state @tv/state
        from (get-in state [:range :from])
        to (get-in state [:range :to])]
    (println "get-window-current from:" from "to: " to "state: " state)
    (when (and from to)
      (get-window from to))))


(defn table-dialog-table []
  (fn []
     (let [data (get-in @window-state [:data])]
       ;[:p (pr-str data)]
       [study-table nil data])))

(defn table-dialog []

  [:div.bg-blue-300.p-5.w-full;.h-64
     {:style {:height "10cm"}}
     [table-dialog-table]])

(defn tradingview-modifier [symbol frequency]
  (let [symbol-showing (r/atom symbol)]
    (fn [symbol frequency]
      (when-not (= symbol @symbol-showing)
        (reset! symbol-showing symbol)
        (.log js/console "tv symbol change detected!")
        (set-symbol symbol frequency)
        nil))))

(defn algo-modifier [algo algoinfo]
  (let [showing (r/atom algoinfo)]
    (fn [algo algoinfo]
      (when-let [charts (:charts algoinfo)]
        (when (> (count charts) 0)
          (when-not (= algoinfo @showing)
            (reset! showing algoinfo)
            (println "TV ALGO CHANGED TO: " algo " charts: "  charts)
            ;(set! (.-datafeed @tv-widget-atom) (tv/tradingview-algo-feed algo))
            ;(set! (-> js/window .-widget .-datafeed) (tv/tradingview-algo-feed algo))
            ;(set! (.-text obj) text)
            ;Object.getPrototypeOf (widget) .datafeed
            ;(set! (.-datafeed 
            ;       (.getPrototypeOf js/Object js/widget)) 
            (js/setTimeout #(tv/add-algo-studies charts) 300)
            (js/setTimeout #(tv/track-range) 300)
            ;(tv/add-algo-studies charts)
            nil))))))

(defn tv-status []
  (fn []
    [:span (pr-str @tv/state)]))

(defn algo-menu []
  [:div.flex.flex-row.bg-blue-500
   [link-href "/" "main"]
   [input/select {:nav? false
                  :items (or (:algos @algo-state) [])}
    algo-state [:algo]]
   [input/select {:nav? false
                  :items (:symbols @algo-state)}
    algo-state [:symbol]]
   [input/button {:on-click #(rf/dispatch [:modal/open (algo-dialog)
                                          :medium])} "options"]
   [input/button {:on-click #(do (get-window-current)
                                  (rf/dispatch [:modal/open (table-dialog)
                                              :large]))} "table"]
      
   ;[input/button {:on-click get-window-demo} "get window"]
   [tv-status]
  
   ])


(defn get-algo-and-options []
  (let [state @algo-state
        algo (or (:algo state) "buy-hold")
        options (or (get-in state [:algoinfo :options]) {})]
    {:algo algo
     :options options}))

(defn algo-ui []
  (let [symbol-initial (:symbol @algo-state)]
  (fn []
    (let [{:keys [algos algo algoinfo symbol frequency]} @algo-state]
      [:div.flex.flex-col.h-full.w-full
       ;(do (run-algo algo opts data-loaded)
       ;    nil)
       [algo-menu]
       [algo-info algo ]
       ;(when-let [wd (get-in @window-state [:data])]
       ;   [:div (pr-str wd)]  
       ;  )
       [tradingview-modifier symbol frequency]
       [algo-modifier algo algoinfo]

       [:div.h-full.w-full
        [tradingview-chart {:feed :ta
                            :options {:autosize true
                                      :symbol symbol-initial
                                      :datafeed (tv/tradingview-algo-feed get-algo-and-options)
                                      }}]
        
        ]
       ;[page-renderer data page]
       ]))))

(defn tvalgo-page [route]
  [:div.h-screen.w-screen.bg-red-500
   [algo-ui]])

(add-page tvalgo-page :algo/tv)
