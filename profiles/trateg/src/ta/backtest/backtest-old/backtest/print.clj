(ns backtest.print
  (:require
   [backtest.random :refer [random-series]]
   [backtest.indicator :refer [sma ema]]
   [clojure.pprint]))

;; ADD SUB PROPERTIES

(defn add-sub-property [row prop]
  (assoc row prop (get-in row prop)))

(defn add-sub-properties [props row]
  ;(println "add-sub-properties props:" props " row:" row)
  (last (map (partial add-sub-property row) props)))

(defn vec-cols [cols]
  (filter vector? cols))

(defn add-sub-maps [cols rows]
  (let [sub-cols (vec-cols cols)]
    (if (= (count sub-cols) 0)
      rows
      (map (partial add-sub-properties sub-cols) rows))))

(comment
   ; Clojure print-table does not support nested properties
  (clojure.pprint/print-table [:a [:c :c]]
                              [{:a 1 :b 2 :c {:c 3}}
                               {:a 4 :b 5 :c {:c 6}}])

  (add-sub-maps [:a [:b :c]]
                [{:a 1 :b {:c 2}}
                 {:a 3 :b {:c 3}}]))

;; PRINT FILTERED TABLE

(defn filter-vector [v show & [p]]
  (if (= show :all)
    v
    (let [size (count v)
          idx-filter (case show
                       :first #(< % p)
                       :last #(>= % (- size p))
                       :range #(and (<= % (last p)) (>= % (first p))))]
      (->> (map-indexed (fn [idx val] [idx val]) v)
           (filter (fn [[idx val]] (idx-filter idx)))
           (map last)
           (vec)))))

(defn print-table
  "like clojure.pprint/print-table
   supports nested properties [:a [:b :c]]
  but allows filtering which rows should be displayed
    :all
    :first 5
    :last 5
    :range [2 3]
    the numbers are row-indices
  "
  [v cols show & [p]]
  (->> (filter-vector v show p)
       (add-sub-maps cols)
       (clojure.pprint/print-table cols)))

(comment
  (print-table [{:a "A" :b {:c 2}}
                {:a "B" :b {:c 5}}
                {:a "C" :b {:c 5}}
                {:a "D" :b {:c 5}}
                {:a "E" :b {:c 2}}
                {:a "F" :b {:c 5}}
                {:a "G" :b {:c 5}}
                {:a "H" :b {:c 5}}]
               [:a [:b :c]] :range [2 4]))

; FLIPPER

(defn get-col [map-of-vecs col]
  (if (vector? col)
    (get-in map-of-vecs col)
    (get map-of-vecs col)))

(defn prepare-row  [map-of-vecs cols idx]
 ;(println "prepare-row cols:" cols " idx:" idx " map of vecs:" map-of-vecs)
  (into {}
        (map #(vector % (nth (get-col map-of-vecs %) idx)) cols)))

(defn pp-flip [map-of-vecs cols show p]
  (let [first-series (first (vals map-of-vecs))
        size (count first-series)
        one-row (fn [i] (prepare-row map-of-vecs cols i))
        row-idx (case show
                  :all (range size)
                  :first (range p)
                  :last (range (- size p) size)
                  :range (range (first p) (last p)))
        ;_ (println "show:" show "row-idx: " row-idx)
        ]
    (vec (map one-row row-idx))))

(defn print-map-of-vecs [map-of-vecs cols show & [p]]
  (->> (pp-flip map-of-vecs cols show p)
       ;(println "data:" )
       (clojure.pprint/print-table cols)))

(comment
; https://github.com/cldwalker/table
; https://github.com/joegallo/doric

  (def m1 {:series-a [1 2 3] :series-b [4 5 6] :c {:b [7 8 9]}})

  (nth (get-col m1 :series-a) 2)
  (nth (get-col m1 [:c :b]) 2)
  (prepare-row m1 [:series-a [:c :b]] 2)
  (print-map-of-vecs m1 [:series-a [:c :b]] :all)

  (print-map-of-vecs
   {:a [1 2 3 4 5 6 7 8 9] :p {:lt [1 2 3 4 5 6 7 8 9]}}
   [:a [:p :lt]]
   :range [3 5]))

(defn t [m f]
  (into {} (for [[k v] m] [k (f v)])))

(defn chart-filter [show p m]
  (if (vector? m)
    (filter-vector m show p)
    (t m (partial chart-filter show p))))

;(defn chart-filter [map-of-vecs show & [p]]
;    (map #(chart-one % show p) map-of-vecs))

(comment
  (chart-filter  :last 2 m1))
