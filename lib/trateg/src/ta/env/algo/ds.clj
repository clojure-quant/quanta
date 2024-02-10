(ns ta.env.algo.ds
  (:require
    [tablecloth.api :as tc]))

(defn last-result-row [ds-algo]
  (tc/select-rows ds-algo [(dec (tc/row-count ds-algo))]))

(defn last-ds-row [results]
  (let [last-rows (->> results
                       (map :result)
                       (map last-result-row))]
    (if (> (count last-rows) 0)
      (apply tc/concat last-rows)
      :error/no-data)))