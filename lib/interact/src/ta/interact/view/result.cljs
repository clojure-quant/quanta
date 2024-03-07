(ns ta.interact.view.result
  (:require
   [re-frame.core :as rf]
   [ta.viz.renderfn :refer [render render-spec]]
   [ta.interact.view.state :as s]))

; to get data out of reframe, we need to create
; a subscription "type"

(rf/reg-sub
 :interact/subscription
 (fn [db _]
   (:interact/subscription db)))

; by registering reframe event, the incoming websocket event 
; will be stored in reframe db.

(defn start-listening! [state]
  (let [subscription-a (s/get-view-a state :subscription)]
    (rf/reg-event-db
     :interact/subscription
     (fn [db [_ t]]
       ; only store if we currently want to see it.
       (if (= (:subscription-id t) @subscription-a)
         (assoc db :interact/subscription (:result t))
         (assoc db :interact/subscription nil))))))

(defn no-data-view []
  [:div.h-full.w-full.bg-blue-100
   "no viz-result-data received yet!"])

(defn result-view [state]
  (start-listening! state)
  (let [viz-result-a (rf/subscribe [:interact/subscription])]
    (fn [state]
      (if @viz-result-a
        [:div.w-full.h-full.bg-red-200
          ;[:p "topic: " (str topic)]
          ;[:p "viz-spec: "  (pr-str result)]
         [render-spec @viz-result-a]]
        [no-data-view]))))
