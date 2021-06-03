(in-ns 'ta.dataframe)

;(require '[clojure.pprint])

(require '[clj-time.coerce])

(defn get-ts
  "gets a timeseries or timeseries value
   for a symbol"
  ([rows path]
   (map #(get-in % path) rows))
  ([rows path index]
   (get (get-ts rows path) index)))

(defn set-ts [rows path series]
  (map (fn [row point]
         (assoc-in row path point)) rows series))

(defn get-time [model]
  (map clj-time.coerce/to-long (get-ts model [:date])))

