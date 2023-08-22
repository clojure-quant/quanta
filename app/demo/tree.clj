(ns tree
  (:require
   [clojure.edn :as edn]
   [clojure.tools.cli.api :refer [tree]]))


(defn demo [& _]
  (println "loading deps.edn..")
  (let [deps-map (-> "deps.edn" slurp edn/read-string)]
    (-> (tree {:project deps-map
               :format :edn})
        (with-out-str)
        (edn/read-string)
        println)))
