(ns demo.algo.moon
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [ta.series.moon :refer [inst->moon-phase-kw]]
   [ta.algo.manager :refer [add-algo]]))

(defn add-moon-indicator [ds-bars _]
  (tc/add-column
   ds-bars
   :phase  (dtype/emap inst->moon-phase-kw :object (:date ds-bars))))

(defn calc-moon-signal [phase]
  (if phase
    (case phase
      :i1 :flat
      :full :buy
      :hold)
    :hold))

(defn moon-signal [ds-bars options]
  (let [ds-study (add-moon-indicator ds-bars options)
        signal (into [] (map calc-moon-signal (:phase ds-study)))]
    (tc/add-columns ds-study {:signal signal})))

(add-algo
 {:name "moon"
  :comment "very good - 2:1"
  :algo moon-signal
  :charts [nil ; {:trade "flags"}
           {:volume "column"}]
  :options {:w :stocks
            :symbol "SPY"
            :frequency "D"}})
