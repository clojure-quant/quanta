(ns demo.playground.dataset.meta
  (:require
   [tablecloth.api :as tablecloth]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype.functional :as fun]
   [tech.v3.dataset.print :as print]))



(def ds1
  (let [n 10]
    (-> {:w (map #(>= % (quot n 2)) (range n))
         :x (range n)
         :y (fun/+ 0.1 (range n))
         :z (map str (range n))}
        (tablecloth/dataset {:dataset-name "ds1"}))))
ds1

(-> ds1
    meta)

; {:name "ds1"}

(->> ds1
     tablecloth/columns
     (map meta))

; # how it looks in repl: 
; _unnamed: descriptive-stats [4 12]:
; | :col-name | :datatype | :n-valid | :n-missing | :min | :mean | :mode | :max | :standard-deviation |          :skew | :first | :last |
; |-----------|-----------|---------:|-----------:|-----:|------:|-------|-----:|--------------------:|---------------:|--------|-------|
; |        :w |  :boolean |       10 |          0 |      |       | false |      |                     |                |  false |  true |
; |        :x |    :int64 |       10 |          0 |  0.0 |   4.5 |       |  9.0 |          3.02765035 | 0.00000000E+00 |      0 |     9 |
; |        :y |  :float64 |       10 |          0 |  0.1 |   4.6 |       |  9.1 |          3.02765035 | 1.42233056E-16 | 0.1000 | 9.100 |
; |        :z |   :string |       10 |          0 |      |       |     9 |      |                     |                |      0 |     9 |

(->> ds1
     tablecloth/info)

; [10 4]
(-> ds1
    tablecloth/shape)

; {:name :x, :datatype :int64, :n-elems 10, :hidden? true}
(-> ds1
    (update :x #(vary-meta % assoc :hidden? true))
    :x
    meta)

; {:hidden? true}
(-> [4 1 :A "v" 2]
    (with-meta {:hidden? true})
    meta)

; {:hidden? true}
(-> [4 1 :A "v" 2]
    (with-meta {:hidden? true})
    (conj 99)
    meta)

; {:name "ds1"}
(-> ds1
    meta)

(-> ds1
    (tablecloth/group-by [:w]))

(-> ds1
    (tablecloth/group-by [:w])
    meta)

(-> ds1
    (tablecloth/group-by [:w])
    (print/print-policy :repl))

(-> ds1
    (tablecloth/group-by [:w])
    (print/print-policy :markdown))

(-> ds1
    (->> (repeat 10)
         (apply tablecloth/bind))
    (tablecloth/set-dataset-name :many-ds1)
    (print/print-range 4))

(-> ds1
    (->> (repeat 10)
         (apply tablecloth/bind))
    (tablecloth/set-dataset-name :many-ds1)
    (print/print-range :all))

(-> ds1
    (->> (repeat 10)
         (apply tablecloth/bind))
    (tablecloth/set-dataset-name :many-ds1)
    (print/print-range (concat (range 4)
                               (range 96 100))))

(-> ds1
    (print/print-types true))

(-> ds1
    (print/print-width 2))

