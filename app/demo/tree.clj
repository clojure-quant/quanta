(ns tree
  (:require
   [clojure.edn :as edn]
   [clojure.tools.cli.api :refer [tree]]))


(defn demo [& _]
  (println "loading deps.edn..")
  (let [deps-map (-> "deps.edn" slurp edn/read-string)
        data (-> (tree {:project deps-map
                        :format :edn})
                 (with-out-str)
                 (edn/read-string))]
      (spit "tree.edn" data)))
