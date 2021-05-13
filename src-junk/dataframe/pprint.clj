(in-ns 'ta.dataframe)

(require '[clojure.pprint])

;; ADD PATH to map

(defn- assoc-path [row path]
  (assoc row path (get-in row path)))

(defn- assoc-paths [paths row]
  (reduce assoc-path row paths))

(defn- path-cols [cols]
  (filter vector? cols))

(defn add-paths [cols rows]
  (let [paths (path-cols cols)]
    (if (= (count paths) 0)
      rows
      (map (partial assoc-paths paths) rows))))

(comment

  (assoc-paths [[:a :b] [:d :e]] {:a {:b 1 :c 2} :d {:e 7 :f 3}})

  (assoc-paths [[:a :b] [:d :e]] {:a {:b 1 :c 2} :d {:e 7 :f 3}})

   ; Clojure print-table does not support nested properties
  (clojure.pprint/print-table [:a [:b :c]]
                              [{:a 1 :b 2 :c {:c 3}}
                               {:a 4 :b 5 :c {:c 6}}])

  (add-paths [:a [:b :c]]
             [{:a 1 :b {:c 2}}
              {:a 3 :b {:c 3}}])

  ; comment
  )



;; PRINT FILTERED TABLE


(defn filter-rows [v show & [p]]
  (if (= show :all)
    v
    (let [size (count v)
          idx-filter (case show
                       :first #(< % p)
                       :last #(>= % (- size p))
                       :range #(and (<= % (last p)) (>= % (first p))))]
      (->> (map-indexed (fn [idx val] [idx val]) v)
           (filter (fn [[idx _ #_val]] (idx-filter idx)))
           (map last)
           (vec)))))

(defn print-table
  "like clojure.pprint/print-table
   - supports nested properties [:a [:b :c]]
   - allows filtering which rows should be displayed
     :all
     :first 5
     :last 5
     :range [2 3]
     the numbers are row-indices
  "
  [data cols show & [p]]
  (->> (filter-rows data show p)
       (add-paths cols)
       (clojure.pprint/print-table cols)))

(comment
  (print-table [{:a "A" :b {:c 2} :d {:e 7}}
                {:a "B" :b {:c 5} :d {:e 7}}
                {:a "C" :b {:c 5} :d {:e 7}}
                {:a "D" :b {:c 5} :d {:e 7}}
                {:a "E" :b {:c 2} :d {:e 7}}
                {:a "F" :b {:c 5} :d {:e 7}}
                {:a "G" :b {:c 5} :d {:e 7}}
                {:a "H" :b {:c 5} :d {:e 7}}]
               [:a [:b :c] [:d :e]] :range [2 4])
  ; comment end
  )


; FLIPPER


(defn get-col [map-of-vecs col]
  (if (vector? col)
    (get-in map-of-vecs col)
    (get map-of-vecs col)))

(defn prepare-row  [map-of-vecs cols idx]
 ;(println "prepare-row cols:" cols " idx:" idx " map of vecs:" map-of-vecs)
  (into {}
        (map #(vector % (nth (get-col map-of-vecs %) idx)) cols)))

(defn pp-flip [map-of-vecs cols show & [p]]
  (let [first-series (first (vals map-of-vecs))
        size (count first-series)
        one-row (fn [i] (println "converting index " i)
                  (prepare-row map-of-vecs cols i))
        row-idx (case show
                  :all (range size)
                  :first (range p)
                  :last (range (- size p) size)
                  :range (range (first p) (last p)))
        ;_ (println "show:" show "row-idx: " row-idx)
        ]
    (vec (map one-row row-idx))))

(defn print-dataframe
  "like print-table, but input is a dataframe.
   A dataframe stores vectors inside (nested) maps

   example:   
    (print-dataframe
     {:a [1 2 3 4 5 6 7 8 9] :p {:lt [1 2 3 4 5 6 7 8 9]}}
     [:a [:p :lt]]
     :range [3 5])
  "
  [dataframe cols show & [p]]
  (->> (pp-flip dataframe cols show p)
       ;(println "data:" )
       (clojure.pprint/print-table cols)))

(comment
; https://github.com/cldwalker/table
; https://github.com/joegallo/doric

  (def m1 {:series-a [1 2 3 4] :series-b [5 6 7 8] :c {:b [9 10 11 12]}})

  (pp-flip m1 [:series-a :series-b [:c :b]] :all)
  (pp-flip m1 [:series-a [:c :b]] :first 2)
  (nth (get-col m1 :series-a) 2)
  (nth (get-col m1 [:c :b]) 2)
  (prepare-row m1 [:series-a [:c :b]] 2)
  (print-dataframe m1 [:series-a [:c :b]] :last 2)

  (print-dataframe
   {:a [1 2 3 4 5 6 7 8 9] :p {:lt [1 2 3 4 5 6 7 8 9]}}
   [:a [:p :lt]]
   :range [3 5])

  ; comment 
  )

(defn t [m f]
  (into {} (for [[k v] m] [k (f v)])))

(defn chart-filter [show p m]
  (if (vector? m)
    (path-cols m show p)
    (t m (partial chart-filter show p))))


;(defn chart-filter [map-of-vecs show & [p]]
;    (map #(chart-one % show p) map-of-vecs))


(comment
  (chart-filter  :last 2 m1))
