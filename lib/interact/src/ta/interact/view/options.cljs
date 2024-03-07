(ns ta.interact.view.options
  (:require
   [options.core :as o]
   [ta.interact.view.state :as s]))

(defn make-go-button  [state]
  {:name "Go!"
   :spec :button
   :class "bg-blue-500 hover:bg-blue-700 text-white font-bold rounded" ; py-2 px-4
   :on-click #(s/start-algo state)})

(defn options-ui [state]
  (let [options-a (s/get-view-a state :options)
        current-a (s/get-view-a state :current)]
    (fn [state]
      (let [options (->> (concat (:options @options-a) [(make-go-button state)])
                         (into []))
            config (assoc @options-a :state current-a :options options)]
        [o/options-ui {:class "bg-blue-300 options-label-left" ; options-debug
                       :style {:width "100%"
                               :height "50px"}} config]
        ;[:span "options-ui: " (pr-str config)]
        ))))
