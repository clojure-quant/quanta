(require '[clojure.edn :as edn])

(defn all [el]
  (let [children (:children el)
        children (if (map? children)
                     children
                     {})
        this-result (keys children)
        search-child (fn [c]
                       (all (get children c)))
        child-results (map search-child (keys children))
        result (->> (apply concat this-result child-results)
                    (remove nil?))]
        ; (println "all result:" result)
     result))

(defn full-name [s]
  (let [nn (namespace s)
        ss (symbol s)]
      (str (name nn) "/" (name ss))

    )
  )

(defn print-all [result]
   (doall (->> result
        (into #{})
        (map full-name)
        (sort)
        (map println)
        )))

(defn search
([el s]
   (search [] el s))
([path el s]
  (let [children (keys (:children el))
        filtered (filter #(= % s) children)
        this-result (if (empty? filtered)
                      []
                      [{:path path}])
        search-child (fn [c]
                       (search (conj path c)
                               (get (:children el) c)
                               s))
        child-results (map search-child children)
        result (->> (apply concat this-result child-results)
                    (remove empty?))]
    #_(when (not (lempty? result))
      (println "result for path:" path "result:" result))
    result)))

(defn search-dependency [d]
  (let [data (-> "tree.edn" slurp edn/read-string)]
    ;(println "data: " (pr-str  (keys (:children data))))
    ;(println "all deps: " (pr-str (all data)))
    (print-all (all data))
    (println "search results for: " d)
    (search data d)))




;(search-dependency 'scicloj/tablecloth)
(require '[babashka.cli :as cli])

(def cli-options {:search {:default 'scicloj/tablecloth :coerce :symbol}})

(def cli-opts (cli/parse-opts *command-line-args* {:spec cli-options}))

(require '[clojure.pprint :refer [print-table]])

(-> (search-dependency (:search cli-opts))
    (print-table)
    )
