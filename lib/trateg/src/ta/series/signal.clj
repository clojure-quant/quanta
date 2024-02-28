(ns ta.series.signal
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tablecloth.api :as tc]))

(defn prior-int [price n-ago]
  (let [l (count price)]
    (dtype/make-reader
     :int32 l
     (if (>= idx n-ago)
       (price (- idx n-ago))
       0))))

(defn cross-up [price indicator]
  (println "cross up running! " price indicator)
  (let [n (count price)]
    (dtype/make-reader
     :bool n
     (if
      (= idx 0)
       false
       (and (> (price idx)
               (indicator idx))
            (< (price (dec idx))
               (indicator (dec idx))))))))

(defn cross-down [price indicator]
  (let [n (count price)]
    (dtype/make-reader
     :bool n
     (if
      (= idx 0)
       false
       (and (< (price idx)
               (indicator idx))
            (> (price (dec idx))
               (indicator (dec idx))))))))

(defn price-when [price signal]
  (let [n (count price)]
    (dtype/make-reader
     :float32 n
     (if (signal idx)
       (price idx)
       0.0))))

(defn select-signal-contains [ds signal-col v]
  (tc/select-rows ds
                  (fn [row]
                    (contains? v (signal-col row)))))

(defn select-signal-is [ds signal-col v]
  (tc/select-rows ds
                  (fn [row]
                    (= (signal-col row) v))))

(defn select-signal-has [ds signal-col]
  (tc/select-rows ds
                  (fn [row]
                    (signal-col row))))

(comment

  (cross-up [1 2 3 5 6 7 8 9]
            [4 4 4 4 4 4 4 4])

  (def px-d [9 8 8 6 5 3 2 1])

  px-d
  (prior-int px-d 1)

  (dfn/eq [1 2 3] [1 2 4])

  (let [c [(float 1) 2 2 (float 2) 3]
        p (prior-int c 1)]
    (dfn/eq c p))

  (let [c [1 2 2 2 3]
        p (prior-int c 1)]
    (dfn/eq c p))

  (dfn/eq px-d (prior-int px-d 1))

  (def ind [4 4 4 4 4 4 4 4])
  (cross-down px-d ind)
  (->> (cross-down px-d ind)
       (price-when px-d))

  (-> (tc/dataset [{:idx 1 :signal false}
                   {:idx 2 :signal false}
                   {:idx 3 :signal true}
                   {:idx 4 :signal false}])
      (select-signal-is :signal true))

; 
  )
