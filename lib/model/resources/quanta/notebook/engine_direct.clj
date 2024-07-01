(ns quanta.notebook.engine-direct
  (:require
   [quanta.model.javelin :refer [create-engine-javelin]]
   [quanta.model.protocol :as p]))

(def e (create-engine-javelin))

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