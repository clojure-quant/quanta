(ns ta.indicator.rolling
  (:require
   [tech.v3.dataset.rolling :as r]
   [tablecloth.api :as tc]))

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
    (tc/dataset {:price [1 2 3 4 5 6 7]}))
  (def price (:price ds))

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

; 
  )




