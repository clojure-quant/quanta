(ns demo.goldly.page.tvalgo
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [goldly.service.core :refer [run-a]]
   [goldly.page :as page]
   [input]
   [ta.tradingview.goldly.algo.context :as c]
   ;[ta.tradingview.goldly.interact :refer [set-symbol state track-range reset-data]]
   [demo.goldly.lib.ui :refer [link-href]]
   [demo.goldly.algo.dialog :refer [show-algo-dialog show-table-dialog]]
   [ta.tradingview.goldly.algo.tradingview :refer [tradingview-algo]]
   ))

(defonce algo-ctx
  (c/create-algo-context "moon" {:symbol "QQQ" :frequency "D"}))

;; symbol/algo switcher

(defonce algo-state
  (r/atom {:algos []
           :symbols ["TLT" "SPY" "QQQ" "EURUSD"]}))

(run-a algo-state [:algos]
       'ta.algo.manager/algo-names) ; get once the names of all available algos

(defn algo-info [algo]
  (let [algo-loaded (r/atom nil)]
    (when algo
      (when-not (= @algo-loaded algo)
        (run-a algo-state [:algoinfo] 'ta.algo.manager/algo-info algo)
        nil))))


(defn tradingview-modifier [symbol _frequency]
  (let [symbol-showing (r/atom symbol)]
    (fn [symbol frequency]
      (when-not (= symbol @symbol-showing)
        (reset! symbol-showing symbol)
        (.log js/console "tv symbol change detected!")
        ;(set-symbol symbol frequency)
        nil))))

(defn algo-modifier [_algo algoinfo]
  (let [showing (r/atom algoinfo)]
    (fn [algo algoinfo]
      (when-let [charts (:charts algoinfo)]
        (when (> (count charts) 0)
          (when-not (= algoinfo @showing)
            (reset! showing algoinfo)
            (println "TV ALGO CHANGED TO: " algo " charts: "  charts)
            ;(set! (.-datafeed @tv-widget-atom) (tradingview-algo-feed algo))
            ;(set! (-> js/window .-widget .-datafeed) (tradingview-algo-feed algo))
            ;(set! (.-text obj) text)
            ;Object.getPrototypeOf (widget) .datafeed
            ;(set! (.-datafeed
            ;       (.getPrototypeOf js/Object js/widget))
            ;(js/setTimeout #(add-algo-studies charts) 300)
           ; (js/setTimeout #(track-range) 300)
            ;(add-algo-studies charts)
            nil))))))

#_(defn tv-status []
  (fn []
    [:span (pr-str @state)]))

(defn algo-menu []
  (let [algo-input (c/get-algo-input-atom algo-ctx)]
    (fn []
  [:div.flex.flex-row.bg-blue-500
   [link-href "/" "main"]
   [input/select {:nav? false
                  :items (or (:algos @algo-state) [])}
    algo-input [:algo]]
   [input/select {:nav? false
                  :items (:symbols @algo-state)}
    algo-input [:opts :symbol]]
   [input/button {:on-click #(show-algo-dialog algo-ctx)} "options"]
   ;[input/button {:on-click #(reset-data)} "R!"]
   [input/button {:on-click #(show-table-dialog algo-ctx)} "table"]
   ;[input/button {:on-click get-window-demo} "get window"]
   ;[tv-status]
   ])))




(defn algo-ui []
  (fn []
    (let [{:keys [_algos algo algoinfo]} @algo-state]
      [:div.flex.flex-col.h-full.w-full
        [algo-menu]
        [algo-info algo]
        ;[tradingview-modifier symbol frequency]
        ;[algo-modifier algo algoinfo]
        [:div.h-full.w-full
           [tradingview-algo algo-ctx]
          ]

;[page-renderer data page]
         ])))

(defn tvalgo-page [_route]
  [:div.h-screen.w-screen.bg-red-500
   [algo-ui]])

(page/add tvalgo-page :algo/tv)
