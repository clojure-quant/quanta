(ns demo.goldly.repl.tradingview
  (:require
   [goldly.scratchpad :refer [eval-code!]]))


(eval-code!
 (+ 5 5))

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


(eval-code!
  (deref tvalgo/algo-state))

(eval-code!
 (deref tvalgo/window-state))

(eval-code!
 (do (tv/demo-crosshair)
     (tv/demo-range)))

(eval-code!
 (deref tv/state))

(eval-code!
 (tv/get-symbol))

(eval-code!
 (tv/add-demo-trendline))

(eval-code!
 (let [to   (-> (.now js/Date) (/ 1000))
       from (- to  (* 500 24 3600)) ; 500 days ago
       ]
   (println "adding trendline from:" from " to: " to)
   (tv-add-shapes
    [{:time from :price 30000}
     {:time to :price 40000}]
    {:shape "trend_line"
      ;:lock true
     :disableSelection true
     :disableSave true
     :disableUndo true
     :text "mega trend"})))

(eval-code!
 (tv/save-chart))

(eval-code!
 (tv/get-chart))

;; SHAPES

;; the MD file is copied in lib/tradingview
;; https://github.com/bitblockart/tradingview-charting-library/blob/master/wiki/Shapes-and-Overrides.md

(eval-code!
 (tv/add-shapes [{:time 1649791880}] {:shape "vertical_line"}))

; this adds a flag (it might be a bug, or I dont know..)
(eval-code!
 (tv/add-shapes [{:time 1649791880}] [{:shape "vertical_line"}]))

(eval-code!
 (tv/add-shapes [{:time 1649791880
                  :price 40000}]
                {:shape "arrow_up" ; arrow_down arrow_left arrow_right price_label arrow_marker flag
                 :text "🚀"
                 ;:location=location.belowbar
                 :color "#32CD32"
                 ;textcolor=color.new(color.white, 0)
                 :offset 0
                 ;:size size.auto
                 }))


; getPanes () Returns an array of instances of the PaneApi that allows you to interact with the panes.
; widget.activeChart () .getPanes () [1] .moveTo (0);


;widget.save (function (data) {savedWidgetContent = data;
;                           alert ('Saved');
;});


(eval-code!
 (do 
 (let [c (chart-active)
       panes (.getPanes c)    
       ]
    (.log js/console panes))
 (tv/add-shapes [{:time 1641748561
                  ;:price 40000
                  }]
                {:shape "arrow_up" ; arrow_down arrow_left arrow_right price_label arrow_marker flag
                 :text "🚀"
                 :location "above_bar" ;"belowbar"
                 :color "#32CD32"
                 ;textcolor=color.new(color.white, 0)
                 :offset 300
                 ;:size size.auto
                 })))

(eval-code!
  (let [c (chart-active)
        shapes-js (.getAllShapes c)
        ct (.-length shapes-js)
        ;shapes (js->clj shapes-js)
        ]
    ;(count shapes)
    ct
    ))
 

 ; this removes all drawings from the current tradingview widget
 (eval-code!
  (let [c (chart-active)]
    (.removeAllShapes c)
    ))
 



;xxx.activeChart ()  ()
;tv.setLayout

; plotshape(buy == 1, text=, style=shape., 

(eval-code!
 (tv/add-shapes
  [{:time 1625764800 :price 45000}
   {:time 1649191891 :price 50000}]
  {:shape "gannbox_square"}))

(eval-code!
 (tv/add-shapes
  [{:time 1625764800 :price 45000}
   {:time 1649191891 :price 50000}
   {:time 1649291891 :price 55000}
   {:time 1649391891 :price 50000}
   {:time 1649491891 :price 40000}]
  {:shape "xabcd_pattern"}))

(eval-code!
 (tv/add-shapes [{:price 49000}]
                {:shape "horizontal_line"
                 :lock true
                 :disableSelection true
                 :showInObjectsTree false
                 :text "T1"
                 :overrides {:showLabel true
                             :horzLabelsAlign "right"
                             :vertLabelsAlign "middle"
                             :textcolor "#19ff20"
                             :bold true
                             :linewidth "1"
                             :linecolor "#19ff20"}}))

(eval-code!
 (tv/add-study "MACD" [14 30 "close" 9]))


(eval-code!
 (tv/add-study "Compare" ["open" "AAPL"]))
;Compare study has 2 inputs: [dataSource, symbol]. 
;Supported dataSource values are: ["close", "high", "low", "open"].

(eval-code!
 (tv/add-study "CLJMAIN DESC ISA" ["volume"]))

(eval-code!
 (tv/add-study "CLJ DESC ISA" ["volume"]))

(eval-code!
 (tv/add-study "CLJ DESC ISA" ["high"]))




(eval-code!
 (study-list))





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






widget.activeChart().getStudyById(id).setVisible(false);



widget.activeChart().setVisibleRange(
    { from: 1420156800, to: 1451433600 },
    { percentRightMargin: 20 }
).then(() => console.log('New visible range is applied'));
widget.activeChart().refreshMarks();

Plot
r1 = plot(highUsePivot, color=ta.change(highUsePivot) ? na : #FF0000, linewidth=3, offset=-(rightBars + 1), title='Resistance')
s1 = plot(lowUsePivot, color=ta.change(lowUsePivot) ? na : #233dee, linewidth=3, offset=-(rightBars + 1), title='Support')


shape
plotshape(buy == 1, text='🚀', style=shape.arrowup, location=location.belowbar, color=color.new(#32CD32, 0), textcolor=color.new(color.white, 0), offset=0, size=size.auto)
plotshape(sell == 1, text='🔨', style=shape.arrowdown, location=location.abovebar, color=color.new(#FF0000, 0), textcolor=color.new(color.white, 0), offset=0, size=size.auto)
plotshape(buy == 1, title='Buy Signal', text='BUY', textcolor=color.new(#FFFFFF, 0), style=shape.labelup, size=size.normal, location=location.belowbar, color=color.new(#1B8112, 0))
plotshape(sell == 1, title='Sell Signal', text='SELL', textcolor=color.new(#FFFFFF, 0), style=shape.labeldown, size=size.normal, location=location.abovebar, color=color.new(#FF5733, 0))
plotshape(downFractal, style=shape.triangledown, location=location.belowbar, offset=-n, color=#F44336, size = size.small)
plotshape(upFractal, style=shape.triangleup,   location=location.abovebar, offset=-n, color=#009688, size = size.small)




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
