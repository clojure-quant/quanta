(ns quanta.notebook.engine-ops
  (:require
   [quanta.model.javelin :refer [create-engine-javelin]]
   [quanta.model.ops :refer [add-ops]]
   [quanta.model.protocol :as p]))

(def e (create-engine-javelin))

(defn time-as-map [t] {:time t})

(def ops-1
  [[0 {:calendar [:us :d] :time-fn time-as-map}]])

(def c (add-ops e ops-1))
c
@c

(p/set-calendar! e {:calendar [:us :d]
                    :time :much-later})

@c

(def ops-multiple
  [[0 {:calendar [:us :d] :time-fn time-as-map}]
   [1 {:calendar [:us :h] :time-fn time-as-map}]
   [2 {:formula [0 1] :formula-fn concat}]
   [3 {:value 27}]
   [4 {:formula [3] :formula-fn inc}]
   [5 {:formula [3 4] :formula-fn +}]])

(def cells (add-ops e ops-multiple))

cells
(get cells 0)

(def c (get cells 3))
@c
(reset! c 100)
@c

(def d (get cells 4))
@d