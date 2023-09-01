(ns demo.algo.moon
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [ta.algo.manager :refer [add-algo]]
   [astro.moon :refer [inst->moon-phase-kw phase->text]]
   [ta.tradingview.chart.shape :as shapes] 
   [ta.tradingview.chart.plot :refer [plot-type]]
   [ta.tradingview.chart.color :refer [color]]
   ))

(defn add-moon-indicator [ds-bars _]
  (tc/add-column
   ds-bars
   :phase
   (dtype/emap inst->moon-phase-kw :object (:date ds-bars))))

(defn calc-moon-signal [phase]
  (if phase
    (case phase
      :i1 :flat
      :full :buy
      :hold)
    :hold))

(defn buy-signal->text [signal]
  (if (= signal :buy)
    1.0
    nil))

(defn add-buy-signal-bool [ds-bars]
  (tc/add-column
   ds-bars
   :signal-text
   (dtype/emap buy-signal->text :bool (:signal ds-bars))))

(defn moon-signal [ds-bars options]
  (let [ds-study (add-moon-indicator ds-bars options)
        signal (into [] (map calc-moon-signal (:phase ds-study)))]
    (-> ds-study
        (tc/add-columns {:signal signal})
        (add-buy-signal-bool))))

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
  (shapes/algo-col->shapes
   "moon"
   user-options epoch-start epoch-end
   :phase cell->shape))

(defn fixed-shapes [user-options epoch-start epoch-end]
  [(shapes/line-vertical 1644364800) ; feb 9
   (shapes/line-vertical 1648944000) ; april 3
   (shapes/line-horizontal 350.55)
   (shapes/gann-square 1643846400 350.0 1648944000  550.0)])



(add-algo
 {:name "moon"
  :comment "very good - 2:1"
  :algo moon-signal
  :charts [;nil ; {:trade "flags"}
           ;{:trade "chars" #_"arrows"}
           {:signal-text {:type "chars" 
                          :char "!" 
                          :textColor (color :steelblue)
                          :title "moon-phase-fullmoon" ; title should show up in pane settings
                          }}
           {:volume {:type "line" :plottype (plot-type :columns)}}] 
  :shapes moon-phase-shapes ; fixed-shapes
  :options {:symbol "SPY"
            :frequency "D"}})

