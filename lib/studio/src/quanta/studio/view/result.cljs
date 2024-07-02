(ns quanta.studio.view.result
  (:require
   [ta.viz.renderfn :refer [render render-spec]]
   [quanta.studio.view.state :refer [get-result-a]]))

(defn error-view [error-message]
  [:div.h-full.w-full.bg-blue-100
   "no viz-result-data received yet!"])

(defn result-view [state]
  (let [result-a (get-result-a state)]
      (if result-a
        [:div.w-full.h-full.bg-red-200
          ;[:p "topic: " (str topic)]
          ;[:p "viz-spec: "  (pr-str result)]
         (if @result-a 
            [render-spec @result-a]  
            [error-view "result-a has value of nil."])]
        [error-view "result-a has not been initialized."])))
