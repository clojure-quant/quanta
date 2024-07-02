(ns quanta.notebook.model-direct
  (:require
   [quanta.model.javelin :refer [create-model-javelin]]
   [quanta.model.protocol :as p]))

(def e (create-model-javelin))

(def c (p/value-cell e 15))
@c

(def t (p/calendar-cell e identity [:us :d]))
@t

(p/set-calendar! e {:calendar [:us :d] :time :sometime-later})
@t

(defn fun [c] (* c 1000))

(def x (p/formula-cell e fun [c]))

@x

(reset! c 5)
@c

@x