(ns demo.studies.meta
  (:require [tablecloth.api :as tablecloth]
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

(-> ds1
    meta)

{:name "ds1"}

(->> ds1
     tablecloth/columns
     (map meta))

({:name :w, :datatype :boolean, :n-elems 10}
 {:name :x, :datatype :int64, :n-elems 10}
 {:name :y, :datatype :float64, :n-elems 10}
 {:categorical? true, :name :z, :datatype :string, :n-elems 10})

(->> ds1
     tablecloth/info)

;; _unnamed: descriptive-stats [4 12]:

;; | :col-name | :datatype | :n-valid | :n-missing | :min | :mean | :mode | :max | :standard-deviation |          :skew | :first | :last |
;; |-----------|-----------|---------:|-----------:|-----:|------:|-------|-----:|--------------------:|---------------:|--------|-------|
;; |        :w |  :boolean |       10 |          0 |      |       | false |      |                     |                |  false |  true |
;; |        :x |    :int64 |       10 |          0 |  0.0 |   4.5 |       |  9.0 |          3.02765035 | 0.00000000E+00 |      0 |     9 |
;; |        :y |  :float64 |       10 |          0 |  0.1 |   4.6 |       |  9.1 |          3.02765035 | 1.42233056E-16 | 0.1000 | 9.100 |
;; |        :z |   :string |       10 |          0 |      |       |     9 |      |                     |                |      0 |     9 |

(-> ds1
    tablecloth/shape)

[10 4]

(-> ds1
    (update :x #(vary-meta % assoc :hidden? true))
    :x
    meta)

{:name :x, :datatype :int64, :n-elems 10, :hidden? true}

(-> [4 1 :A "v" 2]
    (with-meta {:hidden? true})
    meta)
{:hidden? true}

(-> [4 1 :A "v" 2]
    (with-meta {:hidden? true})
    (conj 99)
    meta)
{:hidden? true}

(-> ds1
    meta)

{:name "ds1"}

(-> ds1
    (tablecloth/group-by [:w]))

;; _unnamed [2 3]:

;; | :group-id |      :name |                    :data |
;; |----------:|------------|--------------------------|
;; |         0 | {:w false} | Group: {:w false} [5 4]: |
;; |         1 |  {:w true} |  Group: {:w true} [5 4]: |

(-> ds1
    (tablecloth/group-by [:w])
    meta)

{:name "_unnamed", :grouped? true, :print-line-policy :single}

(-> ds1
    (tablecloth/group-by [:w])
    (print/print-policy :repl))

;; _unnamed [2 3]:

;; | :group-id |      :name |                          :data |
;; |----------:|------------|--------------------------------|
;; |         0 | {:w false} | Group: {:w false} [5 4]:       |
;; |           |            |                                |
;; |           |            | \|    :w \| :x \|  :y \| :z \| |
;; |           |            | \|-------\|---:\|----:\|----\| |
;; |           |            | \| false \|  0 \| 0.1 \|  0 \| |
;; |           |            | \| false \|  1 \| 1.1 \|  1 \| |
;; |           |            | \| false \|  2 \| 2.1 \|  2 \| |
;; |           |            | \| false \|  3 \| 3.1 \|  3 \| |
;; |           |            | \| false \|  4 \| 4.1 \|  4 \| |
;; |         1 |  {:w true} | Group: {:w true} [5 4]:        |
;; |           |            |                                |
;; |           |            | \|   :w \| :x \|  :y \| :z \|  |
;; |           |            | \|------\|---:\|----:\|----\|  |
;; |           |            | \| true \|  5 \| 5.1 \|  5 \|  |
;; |           |            | \| true \|  6 \| 6.1 \|  6 \|  |
;; |           |            | \| true \|  7 \| 7.1 \|  7 \|  |
;; |           |            | \| true \|  8 \| 8.1 \|  8 \|  |
;; |           |            | \| true \|  9 \| 9.1 \|  9 \|  |

(-> ds1
    (tablecloth/group-by [:w])
    (print/print-policy :markdown))

;; _unnamed [2 3]:

;; | :group-id |      :name |                                                                                                                                                                                                                                                                      :data |
;; |----------:|------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
;; |         0 | {:w false} | Group: {:w false} [5 4]:<br><br>\|    :w \| :x \|  :y \| :z \|<br>\|-------\|---:\|----:\|----\|<br>\| false \|  0 \| 0.1 \|  0 \|<br>\| false \|  1 \| 1.1 \|  1 \|<br>\| false \|  2 \| 2.1 \|  2 \|<br>\| false \|  3 \| 3.1 \|  3 \|<br>\| false \|  4 \| 4.1 \|  4 \| |
;; |         1 |  {:w true} |         Group: {:w true} [5 4]:<br><br>\|   :w \| :x \|  :y \| :z \|<br>\|------\|---:\|----:\|----\|<br>\| true \|  5 \| 5.1 \|  5 \|<br>\| true \|  6 \| 6.1 \|  6 \|<br>\| true \|  7 \| 7.1 \|  7 \|<br>\| true \|  8 \| 8.1 \|  8 \|<br>\| true \|  9 \| 9.1 \|  9 \| |

(-> ds1
    (->> (repeat 10)
         (apply tablecloth/bind))
    (tablecloth/set-dataset-name :many-ds1)
    (print/print-range 4))

;; :many-ds1 [100 4]:

;; |    :w | :x |  :y | :z |
;; |-------|---:|----:|----|
;; | false |  0 | 0.1 |  0 |
;; | false |  1 | 1.1 |  1 |
;; | false |  2 | 2.1 |  2 |
;; | false |  3 | 3.1 |  3 |

(-> ds1
    (->> (repeat 10)
         (apply tablecloth/bind))
    (tablecloth/set-dataset-name :many-ds1)
    (print/print-range :all))

;; :many-ds1 [100 4]:

;; |    :w | :x |  :y | :z |
;; |-------|---:|----:|----|
;; | false |  0 | 0.1 |  0 |
;; | false |  1 | 1.1 |  1 |
;; | false |  2 | 2.1 |  2 |
;; | false |  3 | 3.1 |  3 |
;; | false |  4 | 4.1 |  4 |
;; |  true |  5 | 5.1 |  5 |
;; |  true |  6 | 6.1 |  6 |
;; |  true |  7 | 7.1 |  7 |
;; |  true |  8 | 8.1 |  8 |
;; |  true |  9 | 9.1 |  9 |
;; | false |  0 | 0.1 |  0 |
;; | false |  1 | 1.1 |  1 |
;; | false |  2 | 2.1 |  2 |
;; | false |  3 | 3.1 |  3 |
;; | false |  4 | 4.1 |  4 |
;; |  true |  5 | 5.1 |  5 |
;; |  true |  6 | 6.1 |  6 |
;; |  true |  7 | 7.1 |  7 |
;; |  true |  8 | 8.1 |  8 |
;; |  true |  9 | 9.1 |  9 |
;; | false |  0 | 0.1 |  0 |
;; | false |  1 | 1.1 |  1 |
;; | false |  2 | 2.1 |  2 |
;; | false |  3 | 3.1 |  3 |
;; | false |  4 | 4.1 |  4 |
;; |  true |  5 | 5.1 |  5 |
;; |  true |  6 | 6.1 |  6 |
;; |  true |  7 | 7.1 |  7 |
;; |  true |  8 | 8.1 |  8 |
;; |  true |  9 | 9.1 |  9 |
;; | false |  0 | 0.1 |  0 |
;; | false |  1 | 1.1 |  1 |
;; | false |  2 | 2.1 |  2 |
;; | false |  3 | 3.1 |  3 |
;; | false |  4 | 4.1 |  4 |
;; |  true |  5 | 5.1 |  5 |
;; |  true |  6 | 6.1 |  6 |
;; |  true |  7 | 7.1 |  7 |
;; |  true |  8 | 8.1 |  8 |
;; |  true |  9 | 9.1 |  9 |
;; | false |  0 | 0.1 |  0 |
;; | false |  1 | 1.1 |  1 |
;; | false |  2 | 2.1 |  2 |
;; | false |  3 | 3.1 |  3 |
;; | false |  4 | 4.1 |  4 |
;; |  true |  5 | 5.1 |  5 |
;; |  true |  6 | 6.1 |  6 |
;; |  true |  7 | 7.1 |  7 |
;; |  true |  8 | 8.1 |  8 |
;; |  true |  9 | 9.1 |  9 |
;; | false |  0 | 0.1 |  0 |
;; | false |  1 | 1.1 |  1 |
;; | false |  2 | 2.1 |  2 |
;; | false |  3 | 3.1 |  3 |
;; | false |  4 | 4.1 |  4 |
;; |  true |  5 | 5.1 |  5 |
;; |  true |  6 | 6.1 |  6 |
;; |  true |  7 | 7.1 |  7 |
;; |  true |  8 | 8.1 |  8 |
;; |  true |  9 | 9.1 |  9 |
;; | false |  0 | 0.1 |  0 |
;; | false |  1 | 1.1 |  1 |
;; | false |  2 | 2.1 |  2 |
;; | false |  3 | 3.1 |  3 |
;; | false |  4 | 4.1 |  4 |
;; |  true |  5 | 5.1 |  5 |
;; |  true |  6 | 6.1 |  6 |
;; |  true |  7 | 7.1 |  7 |
;; |  true |  8 | 8.1 |  8 |
;; |  true |  9 | 9.1 |  9 |
;; | false |  0 | 0.1 |  0 |
;; | false |  1 | 1.1 |  1 |
;; | false |  2 | 2.1 |  2 |
;; | false |  3 | 3.1 |  3 |
;; | false |  4 | 4.1 |  4 |
;; |  true |  5 | 5.1 |  5 |
;; |  true |  6 | 6.1 |  6 |
;; |  true |  7 | 7.1 |  7 |
;; |  true |  8 | 8.1 |  8 |
;; |  true |  9 | 9.1 |  9 |
;; | false |  0 | 0.1 |  0 |
;; | false |  1 | 1.1 |  1 |
;; | false |  2 | 2.1 |  2 |
;; | false |  3 | 3.1 |  3 |
;; | false |  4 | 4.1 |  4 |
;; |  true |  5 | 5.1 |  5 |
;; |  true |  6 | 6.1 |  6 |
;; |  true |  7 | 7.1 |  7 |
;; |  true |  8 | 8.1 |  8 |
;; |  true |  9 | 9.1 |  9 |
;; | false |  0 | 0.1 |  0 |
;; | false |  1 | 1.1 |  1 |
;; | false |  2 | 2.1 |  2 |
;; | false |  3 | 3.1 |  3 |
;; | false |  4 | 4.1 |  4 |
;; |  true |  5 | 5.1 |  5 |
;; |  true |  6 | 6.1 |  6 |
;; |  true |  7 | 7.1 |  7 |
;; |  true |  8 | 8.1 |  8 |
;; |  true |  9 | 9.1 |  9 |

(-> ds1
    (->> (repeat 10)
         (apply tablecloth/bind))
    (tablecloth/set-dataset-name :many-ds1)
    (print/print-range (concat (range 4)
                               (range 96 100))))

;; :many-ds1 [100 4]:

;; |    :w | :x |  :y | :z |
;; |-------|---:|----:|----|
;; | false |  0 | 0.1 |  0 |
;; | false |  1 | 1.1 |  1 |
;; | false |  2 | 2.1 |  2 |
;; | false |  3 | 3.1 |  3 |
;; |  true |  6 | 6.1 |  6 |
;; |  true |  7 | 7.1 |  7 |
;; |  true |  8 | 8.1 |  8 |
;; |  true |  9 | 9.1 |  9 |

(-> ds1
    (print/print-types true))

;; ds1 [10 4]:

;; |       :w |     :x |       :y |      :z |
;; | :boolean | :int64 | :float64 | :string |
;; |----------|-------:|---------:|---------|
;; |    false |      0 |      0.1 |       0 |
;; |    false |      1 |      1.1 |       1 |
;; |    false |      2 |      2.1 |       2 |
;; |    false |      3 |      3.1 |       3 |
;; |    false |      4 |      4.1 |       4 |
;; |     true |      5 |      5.1 |       5 |
;; |     true |      6 |      6.1 |       6 |
;; |     true |      7 |      7.1 |       7 |
;; |     true |      8 |      8.1 |       8 |
;; |     true |      9 |      9.1 |       9 |

(-> ds1
    (print/print-width 2))

;; ds1 [10 4]:

;; | :w | :x | :y | :z |
;; |----|---:|---:|----|
;; | fa |  0 | 0. |  0 |
;; | fa |  1 | 1. |  1 |
;; | fa |  2 | 2. |  2 |
;; | fa |  3 | 3. |  3 |
;; | fa |  4 | 4. |  4 |
;; | tr |  5 | 5. |  5 |
;; | tr |  6 | 6. |  6 |
;; | tr |  7 | 7. |  7 |
;; | tr |  8 | 8. |  8 |
;; | tr |  9 | 9. |  9 |

