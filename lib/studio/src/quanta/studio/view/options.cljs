(ns quanta.studio.view.options
  (:require
   [options.core :as o]
   [quanta.studio.view.state :refer [backtest subscribe get-view-a]]
   [quanta.studio.view.asset-picker :refer [editor-asset-picker]]))

(o/register-editor :asset-picker editor-asset-picker)

(defn make-button-backtest  [state]
  {:type :button
   :name "Backtest!"
   :class "bg-blue-500 hover:bg-blue-700 text-white font-bold rounded" ; py-2 px-4
   :on-click #(backtest state)})

(defn make-button-subscribe  [state ]
  {:type :button
   :name "Start!"
   :class "bg-blue-500 hover:bg-blue-700 text-white font-bold rounded" ; py-2 px-4
   :on-click #(subscribe state)})

(def mode-selector 
{:type :select
 :path :mode
 :name "Mode"
 :spec [:chart :table :backtest]})


(defn options-ui [state]
  (let [options-a (get-view-a state :options)
        current-a (get-view-a state :current)]
    (fn [state]
      (let [options (->> (concat (:options @options-a) [mode-selector
                                                        (make-button-backtest state)
                                                        (make-button-subscribe state)])
                         (into []))
            config (assoc @options-a :state current-a :options options)]
        [o/options-ui {:class "bg-blue-300 options-label-left" ; options-debug
                       :style {:width "100%"
                               :height "50px"}} config]
        ;[:span "options-ui: " (pr-str config)]
        ))))
