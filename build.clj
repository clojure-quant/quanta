(ns build
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.build.api :as b]
   [org.corfield.build :as bb] ; https://github.com/seancorfield/build-clj
   [deps-deploy.deps-deploy :as dd]))


(def lib 'org.pink-gorilla/ta)
(def version (format "0.3.%s" (b/git-count-revs nil)))

(defn jar [opts]
  (-> opts
      (assoc :lib lib
             :version version
             :src-pom "pom-template.xml"
             :transitive true)
      ;(bb/run-tests)
      ;(bb/clean)
      (bb/jar)))


(defn deploy [opts]
  (println "Deploying to Clojars.")
  (-> opts
      (assoc :lib lib
             :version version)
      (bb/deploy)))

