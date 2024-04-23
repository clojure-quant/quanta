(ns build
  (:require
   [org.corfield.build :as bb] ; https://github.com/seancorfield/build-clj
   [clojure.tools.build.api :as b]))

(def lib 'org.pinkgorilla/ta)
(def version (format "0.3.%s" (b/git-count-revs nil)))

(defn jar [opts]
  (-> opts
      (assoc :lib lib
             :version version
             :src-pom "pom-template.xml"
             :transitive true)
      (bb/jar)))

(defn deploy [opts]
  (println "Deploying to Clojars.")
  (-> opts
      (assoc :lib lib
             :version version)
      (bb/deploy)))

