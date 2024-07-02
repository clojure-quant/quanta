(ns quanta.notebook.studio-template
  (:require
   [modular.system]
   [quanta.studio.template :refer [load-template get-options]]
   [quanta.studio :refer [backtest subscribe get-subscription-state unsubscribe]]))

(def s (modular.system/system :studio))

(load-template s :alex/bollinger)
(get-options s :alex/bollinger)

(backtest s :alex/bollinger {} :table)
(backtest s :alex/bollinger {} :chart)



(def id 
   (subscribe s :alex/bollinger {} :table))


id

(get-subscription-state s id)

(unsubscribe s id)