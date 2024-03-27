(ns ta.indicator.rolling
  (:require
   [tech.v3.dataset.rolling :as r]
   [tech.v3.datatype.functional :as dfn]
   [tablecloth.api :as tc]
   [ta.indicator.returns :as ret]))

(defn rolling-window-reduce [rf n vec]
  (let [ds (tc/dataset {:in vec})]
    (:out (r/rolling ds
                     {:window-type :fixed
                      :window-size n
                      :relative-window-position :left}
                     {:out (rf :in)}))))

(defn trailing-max
  "returns the trailing-maximum over n bars of column v.
   the current row is included in the window."
  [n v]
  (rolling-window-reduce r/max n v))

(defn trailing-min
  "returns the trailing-minimum over n bars of column v.
   the current row is included in the window."
  [n v]
  (rolling-window-reduce r/min n v))

(defn trailing-return-stddev
  "returns the trailing-stddev over n bars of column v.
   the current row is included in the window."
  [n bar-ds]
  (rolling-window-reduce (fn [col-name]
                           (println "col: " col-name)
                           {:column-name col-name
                            :reducer ret/return-stddev
                            :datatype :float64})
                         n (:close bar-ds)))

(defn trailing-stddev
  "returns the trailing-stddev over n bars of column v.
   the current row is included in the window."
  [n bar-ds]
  (rolling-window-reduce (fn [col-name]
                           (println "col: " col-name)
                           {:column-name col-name
                            :reducer dfn/standard-deviation
                            :datatype :float64})
                         n (:close bar-ds)))

(defn prior-window
  "this does not work!"
  [ds]
 ;(tc/remove-last-row ds)
  )

#_(defn prior-window-reducer
    "this does not work!"
    [rf n vec]
    (let [ds (tc/dataset {:in vec})
         ;rf-prior (fn [p c] )
          ]
      (:out (r/rolling ds
                       {:window-type :fixed
                        :window-size (inc n)
                        :relative-window-position :left}
                       {:out (rf-prior :in)}))))

(comment
  (def ds
    (tc/dataset {:close [1.1 2.2 3.3 4.4 5.5 6.6 7.7]}))
  (def price (:close ds))

  price
  ;; => #tech.v3.dataset.column<int64>[7]
  ;;    :price
  ;;    [1, 2, 3, 4, 5, 6, 7]

  (trailing-max 3 price)
  ;; => #tech.v3.dataset.column<float64>[7]
  ;;    :out
  ;;    [1.000, 2.000, 3.000, 4.000, 5.000, 6.000, 7.000]

  (trailing-min 3 price)
  ;; => #tech.v3.dataset.column<float64>[7]
  ;;    :out
  ;;    [1.000, 1.000, 1.000, 2.000, 3.000, 4.000, 5.000]

  (trailing-return-stddev 3 ds)

; 
  )




