(ns demo.goldly.repl.tradingview.execution
  (:require
   [reval.cljs-eval :refer [eval-code!]]))


(eval-code!
 (-> (tv/chart-active)
     (.setText "asdf")))

(eval-code!
 (let [ol (-> (chart-active)
              (.createOrderLine))]
   (-> ol
       (.setTooltip "Additional order information")
       (.setModifyTooltip "Modify order")
       (.setCancelTooltip "Cancel order")
       (.onMove (fn []
                  (.setText ol "onMove called")))
       (.onModify "onModify called" (fn [text]
                                      (.setText ol text)))
       (.onCancel "onCancel called" (fn [text]
                                      (.setText ol text)
                                      (.remove ol)))
       (.setText "STOP: 73.5 (5,64%)")
       (.setQuantity "2000"))
   nil))

(eval-code!
 (let [pl (-> (chart-active)
              (.createPositionLine))]
   (-> pl
       (.onModify (fn []
                    (.setText pl "onModify called")))
       (.onReverse "onReverse called" (fn [text]
                                        (.setText pl text)))
       (.onClose "onClose called" (fn [text]
                                    (.setText pl text)
                                    (.remove pl)))
       (.setText "PROFIT: 71.1 (3.31%)")
       (.setTooltip "Additional position information")
       (.setProtectTooltip "Protect position")
       (.setCloseTooltip "Close position")
       (.setReverseTooltip "Reverse position")
       (.setQuantity "8.235")
       (.setPrice 49000)
       (.setExtendLeft false)
       (.setLineStyle 0)
       (.setLineLength 1))))

(eval-code!
 (:from (tv/get-range)))

(eval-code!
 (let [t (:to (tv/get-range))]
   (-> (chart-active)
       (.createExecutionShape)
       (.setText "@1,320.75 Limit Buy 1")
       (.setTooltip "@1,320.75 Limit Buy 1")
       (.setTextColor "rgba(0,255,0,0.5)")
       (.setArrowColor "#0F0")
       (.setDirection "buy")
       (.setTime t)
       (.setPrice 160))
   nil))


