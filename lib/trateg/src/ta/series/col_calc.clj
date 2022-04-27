(ns ta.series.col-calc
  (:require
   [clojure.walk :refer [prewalk postwalk]]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tc]))

(defn past-shift [col n]
  (dtype/make-reader
   :float64
   (count col)
   (if (>= idx n)
     (col (- idx n))
     0)))

(comment
  (def ds2 (tc/dataset [{:x 1 :y 2 :z 3}
                        {:x 4 :y 5 :z 6}
                        {:x 5 :y 5 :z 6}
                        {:x 6 :y 5 :z 6}]))
  ds2
  (tc/row-count ds2)
  [(dtype/->float-array 2) (dtype/->float-array 2)]
  (:x ds2)
  (past-shift (:x ds2) 1)
  (past-shift (:x ds2) 2)

 ; 
  )

(defn cur? [expr]
  (keyword? expr))

(defn past? [expr]
  (and (vector? expr)
       (= 2 (count expr))
       (keyword? (first expr))
       (int? (second expr))))

(defmacro curm [ds expr]
  (let [col expr]
    `'(~col ~ds)))

(defmacro pastm [ds expr]
  (let [[col n] expr]
    `'(past-shift (~col ~ds))))

(defn cur [ds expr]
  (println "cur expr: " expr)
  (let [col expr]
    (col ds)))

(defn past [ds expr]
  (println "past expr: " expr)
  ;(println "past ds: " ds)
  (let [[col n] expr]
    (past-shift col n)))

(defn walk-cur [ds expr]
  (let [exp-expr (postwalk
                  (fn [e]
                       ;(println "walk: " e)
                    (cond
                      (cur? e)  (list cur ds e) ; (quote (list 'cur)) ;(list e) ;:Z ;[e] ;`(list ~e ds)
                      :else e))
                  expr)]
    exp-expr))

(defn walk-past [ds expr]
  (let [exp-expr (postwalk
                  (fn [e]
                    ;(println "walk: " e)
                    (cond
       ;(cur? e)  (list 'cur ds e) ; (quote (list 'cur)) ;(list e) ;:Z ;[e] ;`(list ~e ds)
                      (past? e)  (list past-shift (first e) (last e))
                      :else e))
                  expr)]
    exp-expr))

(defn walk [ds expr]
  (println "walk ds:" ds " expr: " expr)
  (->> expr
       (walk-past ds)
       (walk-cur ds)))

(defmacro calc-expr [ds expr]
  "calculates column based on a expression 
   expr: a vectorized expression that has special
   syntax for current and past column-values
   - current column value:  :x     => (:x ds) 
   - past column value:     (:x 1) => (-> ds :x (past 1))"
  (let [expr2 (walk ds expr)]
    (println "expr2: " expr2)
    expr2))

(defmacro calc [ds col expr]
  "adds column 'col' to  a techml-dataset
    col gets calculated based on a special expression:
   expr: a vectorized expression that has special
   syntax for current and past column-values
   - current column value:  :x     => (:x ds) 
   - past column value:     (:x 1) => (-> ds :x (past 1))"
  (let [expr2 (walk ds expr)]
    (println "expr2: " expr2)
    `(tc/add-column
      ~ds
      ~col
      ~expr2)))

(comment
  (cur? :x)
  (cur? 5)
  (cur? ['a 'b])

  (past? [:x 1])
  (past? ['a 1])
  (past? 1)
  (past? nil)
  (past? ['a 1])
  (past? [:z 1])

  (cur ds2 :x)
  (past ds2 [:x 1])

  (fun/+ (:x ds2) (:z ds2) 8.9)

  (walk-cur 'ds2 :x)
  (walk-past 'ds2 [:z 1])
  (walk 'ds2 [:x [:y 1]])

  ;
  (calc-expr ds2 (fun/+ 1.1 :y)) ; add 1.1 to current y value
  (calc-expr ds2 (fun/+ 1.1 [:x 1])) ; add 1.1 to x value (1 row ago)
  (calc-expr ds2 (fun/+ :x 8.9 [:y 1]))

  (calc ds2 :a (fun/+ :x 8.9 [:y 1]))
  (calc ds2 :b (fun/* :x [:y 2]))

  ds2
;
  )
