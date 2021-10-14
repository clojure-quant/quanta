(ns ta.dataset.returns
  (:require
   [tech.v3.dataset.print :as print]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype.datetime :as datetime]
   [tech.v3.datatype :as dtype]
   [tablecloth.api :as tablecloth]
   [ta.dataset.helper :as helper]))

(defn returns [integrated-values]
  (let [n (count integrated-values)]
    (dtype/clone
     (dtype/make-reader
      :float32
      n
      (if (= idx 0)
        0
        (- (integrated-values idx)
           (integrated-values (dec idx))))))))

(comment
  (->> [1 8 0 -9 1 4]
       (reductions +)
       vec
       returns)

;; #array-buffer<float32> [6]
  ;; [0.000, 8.000, 0.000, -9.000, 1.000, 4.000]
  )


(defn forward-shift-col [col offset]
  (dtype/make-reader :float64 (count col) (if (> idx offset)
                                            (col (- idx offset))
                                            0)))

(comment

  (def d (tds/->dataset {:a [1.0 2.0 3.0 4.0 5.0]
                         :b [1.0 2.0 3.0 4.0 5.0]
                         :c [1.0 2.0 3.0 4.0 100.0]}))

  (-> d
      :a
      (forward-shift-col 2))

;  
  )



