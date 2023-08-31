(ns demo.goldly.repl.tradingview.menu
  (:require
   [reval.cljs-eval :refer [eval-code!]]))




(eval-code!
 (ta.tradingview.goldly.interact2/add-header-button
  @ta.tradingview.goldly.interact/tv-widget-atom
  "re-gann" "my tooltip"
  (fn []
    (js/alert "re-gann button clicked "))))


(eval-code!
 (ta.tradingview.goldly.interact2/add-context-menu
  @ta.tradingview.goldly.interact/tv-widget-atom
  [{"position" "top"
    "text" (str "First top menu item"); , time: " unixtime  ", price: " price)
    "click" (fn [] (js/alert "First clicked."))}
   {:text "-"
    :position "top"}
   {:text "-Paste"} ; Removes the existing item from the menu
   {:text "-" ; Adds a separator between buttons
    :position "top"}
   {:position "top"
    :text "Second top menu item 2"
    :click (fn [] (js/alert "second clicked."))}
   {:position "bottom"
    :text "Bottom menu item"
    :click (fn [] (js/alert "third clicked."))}]))