(ns ta.tradingview.goldly.interact
  (:require
   [reagent.core :as r]
   [ta.tradingview.goldly.interact2 :as i]))

(defonce state (r/atom {}))

(def tv-widget-atom (r/atom nil))

; in scratchpad:
; (show-tradingview-widget "scratchpadtest" {:feed :ta})
;@tv-widget-atom

(defn track-range []
  (let [f (fn [{:keys [_from _to] :as vis-range}]
            ;(.log js/console "visible range changed from: " from "to: " to)
            (swap! state assoc :range vis-range))]
    (swap! state assoc :range (i/get-range @tv-widget-atom))
    (i/on-range-change @tv-widget-atom f)))
