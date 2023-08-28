(ns demo.goldly.repl.tradingview.admin
  (:require
   [reval.cljs-eval :refer [eval-code!]]))

(eval-code!
 (+ 5 5))

;; CLJS

(eval-code!
 (let [o (clj->js {:a 1 :b "test"})]
   (js->clj o)))

(eval-code!
 (let [o (clj->js {:a 1 :b "test"})]
   (js->clj o :keywordize-keys true)))

(eval-code!
 (do
   (defn foo [] (clj->js {:hello (fn [] (println "foo hello"))}))
   (new foo)
   (set! (.-foo js/globalThis) foo)
   (js/eval "new foo().hello()")))

;(set! (.-bongo js/globalThis) i-clj)

(eval-code!
 (add-header-button
  "re-gann" "my tooltip"
  (fn []
    (println "button clicked "))))

(eval-code!
 (ta.tradingview.goldly.interact/add-context-menu
  [{"position" "top"
    "text" (str "First top menu item"); , time: " unixtime  ", price: " price)
    "click" (fn [] (js/alert "First clicked."))}
   {:text "-"
    :position "top"}
   {:text "-Objects Tree..."}
   {:position "top"
    :text "Second top menu item 2"
    :click (fn [] (js/alert "second clicked."))}
   {:position "bottom"
    :text "Bottom menu item"
    :click (fn [] (js/alert "third clicked."))}]))

(eval-code!
 (deref tvalgo/algo-state))

(eval-code!
 (deref tvalgo/window-state))

(eval-code!
 (do (ta.tradingview.goldly.interact/demo-crosshair)
     (ta.tradingview.goldly.interact/demo-range)))

(eval-code!
 (ta.tradingview.goldly.interact/get-range))

(eval-code!
 (ta.tradingview.goldly.interact/track-range))

(eval-code!
 (deref tv/state))

(eval-code!
 (ta.tradingview.goldly.interact/get-symbol))

(eval-code!
 (tv/save-chart))

(eval-code!
 (tv/get-chart))

; js->clj and jsx->clj DO NOT WORK!
(eval-code!
 (let [d (tv/get-chart)
       c (.-charts d)
       c0 (aget c 0)
       ;c-clj (jsx->clj c0)
       c-clj (js->clj c0)]
   (.log js/console d)
   (.log js/console c)
   (.log js/console c0)

   (println "charts: " (pr-str c-clj))
   13))

(eval-code!
 (tv/reset-data))

(eval-code!
 (tv/refresh-marks))

; get list of all features (that supposedly can be set in the widget constructor)
(eval-code!
 (tv/show-features))

; getPanes () Returns an array of instances of the PaneApi that allows you to interact with the panes.
; widget.activeChart () .getPanes () [1] .moveTo (0);

;widget.save (function (data) {savedWidgetContent = data;
;                           alert ('Saved');
;});

;; not working..
(eval-code!
 (do (defn foo [] #js {:a (fn [] “hello”)})
     (new foo)
      ;(set! (.-foo js/globalThis) foo)
     (js/eval "new foo().a")))

(eval-code!
 (tv/add-demo-menu))

(eval-code!
 (set-symbol "TLT" "1D"))

(eval-code!
 (set-symbol "BTCUSD" "1D"))

;"BB:BTCUSD"

(eval-code!
 (let [p (tv/set-range
          {:from 1420156800 :to 1451433600}
          {:percentRightMargin 30})]
   (.then p (fn [] (println "new visible range applied!")
                   ;widget.activeChart () .refreshMarks ();              
              ))))
;widget.selectedLineTool () ; returns "cursor"
;widget.activeChart () .getAllShapes () .forEach (({name}) => console.log (name));
;widget.activeChart().setPriceToBarRatio(0.7567, { disableUndo: true });
;widget.activeChart () .getPanes () [1] .moveTo (0);
;widget.activeChart () .getTimeScaleLogicalRange ()