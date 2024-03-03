(ns notebook.strategy.moon.algo
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [astro.moon :refer [inst->moon-phase-kw phase->text]]
   [ta.tradingview.chart.shape :as shapes] 
   [ta.tradingview.chart.plot :refer [plot-type]]
   [ta.tradingview.chart.color :refer [color]]))

(defn moon-phase [col-date]
   (dtype/emap inst->moon-phase-kw :object col-date))

(defn moon-phase->signal [phase]
  (if phase
    (case phase
      :i1 :flat
      :full :buy
      :hold)
    :hold))

(defn moon-signal [moon-phase]
  (dtype/emap moon-phase->signal :object moon-phase))


(defn buy-signal->text [signal]
  (if (= signal :buy)
    1.0
    nil))

(defn signal-text [signal]
  (dtype/emap buy-signal->text :object signal))

(defn moon-indicator [env spec ds-bars]
  (let [col-date (:date ds-bars)
        col-moon-phase (moon-phase col-date)
        col-moon-signal (moon-signal col-moon-phase)
        col-signal-text (signal-text col-moon-signal)]
    (-> ds-bars
        (tc/add-columns {:moon-phase col-moon-phase
                         :signal col-moon-signal
                         :signal-text col-signal-text
                         })
       )))

;; SHAPES 

(defn cell->shape [epoch value]
  (let [text (phase->text value)]
    (shapes/text epoch text)))

(comment
  (cell->shape 3 :full)
  (cell->shape 3 :new)
 ;
  )

(defn moon-phase-shapes [user-options epoch-start epoch-end]
  ; todo: change api, so that it will add the ds also.
  (shapes/algo-col->shapes
   "moon"
   user-options epoch-start epoch-end
   :phase cell->shape))

(defn fixed-shapes [user-options epoch-start epoch-end]
  [(shapes/line-vertical 1644364800) ; feb 9
   (shapes/line-vertical 1648944000) ; april 3
   (shapes/line-horizontal 350.55)
   (shapes/gann-square 1643846400 350.0 1648944000  550.0)])



(def charts [;nil ; {:trade "flags"}
           ;{:trade "chars" #_"arrows"}
           {:signal-text {:type "chars" 
                          :char "!" 
                          :textColor (color :steelblue)
                          :title "moon-phase-fullmoon" ; title should show up in pane settings
                          }}
           {:volume {:type "line" :plottype (plot-type :columns)}}])



(comment 
  
 (require '[ta.helper.date :refer [parse-date]])  
 (def ds
   (tc/dataset [{:date (parse-date "2023-01-01")
                 :b 2 
                 :c 3} 
                {:date (parse-date "2023-01-01")
                 :b 5 
                  :c 6}]))
  
  (def phase (moon-phase (:date ds)))
  (def signal (moon-signal phase))
  (def ds-demo (tc/add-columns
                ds
                {:phase phase
                 :signal signal})
    )
  
   (require '[ta.warehouse :as wh]) 
   (require '[ta.data.settings :refer [determine-wh]])
   (require '[tablecloth.api :as tc])

   (def w (determine-wh "GOOGL"))
   (def ds-bars (wh/load-symbol :stocks "D" "SPY")) 
   (tc/info ds-bars)
  
  (require '[ta.trade.signal :refer [trade-signal]])
  (require '[ta.trade.roundtrip-backtest :refer [calc-roundtrips]])   
  (trade-signal ds-demo)

  (-> (trade-signal ds-demo)
      (calc-roundtrips {})
      )

  (trade-signal ds-bars)


     
   (am/algo-run "moon" {:symbol "SPY" :frequency "D"})
   (am/algo-run "moon" {:symbol "GOOGL" :frequency "D"})
   
  
     
 ; 
     )

 
