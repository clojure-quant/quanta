(ns demo.goldly.repl.tradingview.move
  (:require
   [ta.helper.date :as dt :refer [parse-date ->epoch-second epoch-second->datetime]]
   [reval.cljs-eval :refer [eval-code!]]))

(eval-code!
 (ta.tradingview.goldly.interact2/get-range
   @ta.tradingview.goldly.interact/tv-widget-atom
  ))
;; {:from 1644796800, :to 1697155200}

(eval-code!
 (ta.tradingview.goldly.interact/track-range))



(defn epoch [yyyy-mm-dd]
  (-> yyyy-mm-dd parse-date ->epoch-second))

(epoch "2023-01-01")
;; => 1672531200

(epoch "2023-12-31")
;; => 1703980800

(epoch "2022-01-01")
;; => 1640995200

(epoch "2022-12-31")
;; => 1672444800

(epoch "2019-04-01")
;; => 1554076800

;; => Syntax error compiling at (src/demo/goldly/repl/tradingview/move.clj:32:1).
;;    Unable to resolve symbol: epoch in this context

;; => 1672444800
(eval-code! ; year 2023
 (let [p (ta.tradingview.goldly.interact2/set-range
          @ta.tradingview.goldly.interact/tv-widget-atom
          {:from 1672531200
           :to 1703980800 }
          ;{:percentRightMargin 5}
          {}
          )]
   (.then p (fn [] 
              (println "new visible range applied!")
              ;widget.activeChart () .refreshMarks ();              
              ))))

(eval-code!  ; year 2022
 (let [p (ta.tradingview.goldly.interact2/set-range
          @ta.tradingview.goldly.interact/tv-widget-atom
          {:from 1640995200
           :to 1672444800}
          {}
          ;{:percentRightMargin 5}
          )]
   (.then p (fn []
              (println "new visible range applied!")
              ;widget.activeChart () .refreshMarks ();              
              ))))


(eval-code!  ; 2019-04-01 (as midpoint in the chart)
  (ta.tradingview.goldly.interact2/goto-date!
    @ta.tradingview.goldly.interact/tv-widget-atom
    1554076800))
;set-visible-range  {:from 1545868800, :to 1562284800} {:percentRightMargin 5}

(epoch-second->datetime 1554076800)
(epoch-second->datetime 1545868800)
(epoch-second->datetime 1562284800)

; widget.onChartReady(() => {
;    const chart = widget.chart();
;    chart.setTimeFrame({val: {type: 'period-back', value: '12M'}, res: '1W'});
; });