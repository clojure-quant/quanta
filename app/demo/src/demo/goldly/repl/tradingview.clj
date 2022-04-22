(ns demo.goldly.repl.tradingview
  (:require
   [goldly.scratchpad :refer [eval-code!]]))


(eval-code!
 (+ 5 5))

;; CLJS

(eval-code!
  (let [o (clj->js {:a 1 :b "test"})]
    (js->clj o)))

(eval-code!
 (let [o (clj->js {:a 1 :b "test"})]
   (js->clj o :keywordize-keys true)
   ))

(eval-code!
 (do
   (defn foo [] (clj->js {:hello (fn [] (println "foo hello"))}) )
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
  (tv/add-context-menu
   [{"position" "top"
     "text" (str "First top menu item"); , time: " unixtime  ", price: " price)
     "click" (fn [] (alert "First clicked."))}
    #_{:text "-"
       :position "top"}
    #_{:text "-Objects Tree..."}
    #_{:position "top"
       :text "Second top menu item 2"
       :click (fn [] (alert "second clicked."))}
    #_{:position "bottom"
       :text "Bottom menu item"
       :click (fn [] (alert "third clicked."))}]))


(eval-code!
  (deref tvalgo/algo-state))

(eval-code!
 (deref tvalgo/window-state))

(eval-code!
 (do (tv/demo-crosshair)
     (tv/demo-range)))


(eval-code!
 (tv/get-range))

(eval-code!
 (tv/track-range))


(eval-code!
 (deref tv/state))

(eval-code!
 (tv/get-symbol))

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
       c-clj (js->clj c0)
       ]
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






;xxx.activeChart ()  ()
;tv.setLayout





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


 
; returns "cursor"
widget.selectedLineTool ()
 widget.activeChart () .getAllShapes () .forEach (({name}) => console.log (name));

widget.activeChart().setPriceToBarRatio(0.7567, { disableUndo: true });

widget.activeChart () .getPanes () [1] .moveTo (0);

widget.onChartReady(function() {
    widget.onContextMenu(function(unixtime, price) {
        return [{
            position: "top",
            text: "First top menu item, time: " + unixtime + ", price: " + price,
            click: function() { alert("First clicked."); }
        },
        { text: "-", position: "top" },
        { text: "-Objects Tree..." },
        {
            position: "top",
            text: "Second top menu item 2",
            click: function() { alert("Second clicked."); }
        }, {
            position: "bottom",
            text: "Bottom menu item",
            click: function() { alert("Third clicked."); }
        }];
    });

(eval-code!
  (-> (chart-active)
      (.createOrderLine)
      (.setTooltip "Additional order information")
      (.setModifyTooltip "Modify order")
      (.setCancelTooltip "Cancel order")
      (.onMove (fn [] (.setText js/globalThis "onMove called")))
      (.onModify "onModify called" (fn [text] (.setText js/this text)))
      (.onCancel "onCancel called" (fn [text] 
                                     (this-as this
                                       (.setText this text)
                                       (.remove this))))
      (.setText "STOP: 73.5 (5,64%)")
      (.setQuantity "2000")))
       
     

        function positionLine() {
            window.tvWidget.chart().createPositionLine()
                .onModify(function() {
                    this.setText("onModify called");
                })
//                .onReverse("onReverse called", function(text) {
//                    this.setText(text);
//                })
                .onClose("onClose called", function(text) {
                    this.setText(text);
                    this.remove()
                })
                .setText("PROFIT: 71.1 (3.31%)")
                .setTooltip("Additional position information")
                .setProtectTooltip("Protect position")
                .setCloseTooltip("Close position")
                .setReverseTooltip("Reverse position")
                .setQuantity("8.235")
                .setPrice(49000)
                .setExtendLeft(false)
                .setLineStyle(0)
                .setLineLength(1);
        }

        function executionLine() {
            window.tvWidget.activeChart().createExecutionShape()
                .setText("@1,320.75 Limit Buy 1")
                .setTooltip("@1,320.75 Limit Buy 1")
                .setTextColor("rgba(0,255,0,0.5)")
                .setArrowColor("#0F0")
                .setDirection("buy")
                .setTime(widget.activeChart().getVisibleRange().from)
                .setPrice(160);
        }

        // https://github.com/tradingview/charting_library/wiki/Shapes-and-Overrides
