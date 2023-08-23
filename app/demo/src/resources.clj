(ns resources)

(defn get-deps-from-classpath [filename]
  (println "getting resources for: " filename)
  (let [deps
        (-> (Thread/currentThread)
            (.getContextClassLoader)
            (.getResources filename )
            (enumeration-seq)
            (->> (map (fn [url]
                        #_(-> (slurp url)
                              (edn/read-string)
                              (select-keys [:npm-deps])
                              (assoc :url url))
                        url))
                 (into [])))]
    deps))

(defn logback-xml [& _]
  (println (get-deps-from-classpath "logback.xml")))

(defn logback-test-xml [& _]
  (println (get-deps-from-classpath "logback-test.xml")))

(defn deps-cljs [& _]
  (println (get-deps-from-classpath "deps.cljs")))
