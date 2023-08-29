(ns demo.goldly.page.backtest
  (:require
   [reagent.core :as r]
   [goldly.service.core :refer [run-a]]
   [goldly.page :as page]
   [ui.highcharts :refer [highstock]]
   [input]
   [ta.tradingview.goldly.tradingview :refer [ tradingview-chart]]
   [ta.tradingview.goldly.interact :refer [tv-widget-atom  wrap-chart-ready set-symbol chart-active add-shape]]
   [demo.goldly.lib.ui :refer [link-href]]
   [demo.goldly.view.backtest :refer [navs-chart navs-view roundtrips-view metrics-view]]
   [demo.goldly.view.aggrid :refer [study-table]]
   ))

(defonce algo-state
  (r/atom {:algos []
           :algo nil
           :opts {:symbol "SPY"}
           :data-loaded nil
           :tradingview-state nil
           :data {}
           :page :metrics}))

(def symbol-list ["TLT" "SPY" "QQQ" "EURUSD"])

(run-a algo-state [:algos]
       'ta.algo.manager/algo-names) ; get once the names of all available algos

(defn run-algo [algo opts data-loaded]
  (when algo
    ;(info (str "run-algo check: " algo " opts: " opts))
    (when (not (= [algo opts] data-loaded))
      (swap! algo-state assoc :data {})
      (swap! algo-state assoc :data-loaded [algo opts])
      (run-a algo-state [:data]
             'ta.algo.manager/algo-run-browser
             algo opts)
      nil)))




(defn pr-data [_context data]
  [:div.bg-red-500 (pr-str data)])

(defn pr-highchart [_context data]
  (if data
    [:div {:style {:width "100%"
                   :min-width "100%"
                   :max-width "100%"
                   :height "100%"
                   :min-height "100%"
                   :max-height "100%"}}
    #_[:div (pr-str data)]
    [highstock {:style {:width "100%"
                        :height "100%"
                        :overflow-y "scroll"
                        }
                :box :fl ; :lg
                :data (assoc data :height "100%")}]]
    [:div "no data."]))

(defn add-marks-to-tv [tradingview-server]
  (when-let [marks (:marks tradingview-server)]
    ;(println "adding " (count marks) "marks to tv")
    (doall (map #(add-shape (:points %) (assoc (:override %) :disableUndo true)) marks)))
    )

(defn clear-marks-tv []
  ;(println "TV CLEAR MARKS!")
  (let [c (chart-active)]
    (.removeAllShapes c)))

(defn tv-events [algo opts tradingview-state]
  (when algo
    (when (not (= [algo opts] tradingview-state))
      ;(info (str "changing tv data for running algo for: " algo "opts: " opts))
      (swap! algo-state assoc :tradingview-state [algo opts])
      (wrap-chart-ready
       (fn []
         (clear-marks-tv)
         (set-symbol (:symbol opts) "1D")
         nil))
      nil)))

(defn tv-data [tradingview-server]
  (when tradingview-server
    (wrap-chart-ready
     (fn []
       (add-marks-to-tv tradingview-server)))
    nil))

(defn tv-page [_context _data]
  (let [{:keys [algo opts tradingview-state data]} @algo-state
        tradingview-server (:tradingview data)]
    [:div.h-full.w-full
     (when @tv-widget-atom
       [tv-events algo opts tradingview-state])
     (when @tv-widget-atom
       [tv-data tradingview-server])
     [tradingview-chart {:feed :ta
                         :options {:autosize true}}]]))

(defonce pages
  {:metrics  [metrics-view [:stats]]
   :roundtrips [roundtrips-view [:ds-roundtrips]]
   :nav-table [navs-view [:stats :nav]]
   :nav-chart [navs-chart [:stats :nav]]
   :highchart [pr-highchart [:highchart]]
   :study-table [study-table [:ds-study]]
   :tradingview [tv-page [:tradingview]]})


(defn context [data]
  (:study-extra-cols data))

(defn page-renderer [data page]
  (if data
    (let [[view-fn view-data] (page pages)]
      ;(println "page renderer context: " context)
      (if view-fn
        (if view-data
          [view-fn (context data) (get-in data view-data)]
          [:div "no data for view: " page])
        [:div "no view-fn for view: " page]))
    [:div "no data "]))

(defn algo-menu []
  [:div.flex.flex-row.bg-blue-500
   [link-href "/" "main"]
   [input/select {:nav? false
                  :items (or (:algos @algo-state) [])}
    algo-state [:algo]]
   [input/select {:nav? false
                  :items symbol-list}
    algo-state [:opts :symbol]]
   [input/select {:nav? false
                  :items (keys pages)}
    algo-state [:page]]])

(defn algo-ui []
  (fn []
    (let [{:keys [_algos algo opts data-loaded data page]} @algo-state]
      [:div.flex.flex-col
       (do (run-algo algo opts data-loaded)
           nil)
       [algo-menu]
       [page-renderer data page]])))

(defn algo-page [_route]
  [:div.h-screen.w-screen.bg-red-500
   [algo-ui]])

(page/add algo-page :algo/backtest)
