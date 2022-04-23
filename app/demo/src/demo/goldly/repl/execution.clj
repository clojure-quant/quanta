


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
