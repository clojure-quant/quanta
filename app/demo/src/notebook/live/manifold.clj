(ns notebook.live.manifold
  (:require
    [manifold.stream :as s]))


(def stream 
  (->> [1 2 3]
  s/->source))
             
stream

(s/take! stream)

(def result 
  (s/map inc stream))

result

(s/consume #(println "result: " %) result)



