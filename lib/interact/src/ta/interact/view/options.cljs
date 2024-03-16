(ns ta.interact.view.options
  (:require
   [options.core :as o]
   [ta.interact.view.state :as s]
   [ta.interact.view.asset-picker :refer [editor-asset-picker]]))


(o/register-editor :asset-picker editor-asset-picker)

(defn make-chart-button  [state]
  {:type :button
   :name "Chart!"
   :class "bg-blue-500 hover:bg-blue-700 text-white font-bold rounded" ; py-2 px-4
   :on-click #(s/start-algo state :chart)})

(defn make-trade-metrics-button  [state]
  {:type :button
   :name "Metrics!"
   :class "bg-blue-500 hover:bg-blue-700 text-white font-bold rounded" ; py-2 px-4
   :on-click #(s/start-algo state :metrics)})

(defn options-ui [state]
  (let [options-a (s/get-view-a state :options)
        current-a (s/get-view-a state :current)]
    (fn [state]
      (let [options (->> (concat (:options @options-a) [(make-chart-button state)
                                                        (make-trade-metrics-button state)
                                                        ])
                         (into []))
            config (assoc @options-a :state current-a :options options)]
        [o/options-ui {:class "bg-blue-300 options-label-left" ; options-debug
                       :style {:width "100%"
                               :height "50px"}} config]
        ;[:span "options-ui: " (pr-str config)]
        ))))
