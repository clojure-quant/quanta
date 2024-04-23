(ns quanta.notebook.math-bin
  (:require [ta.math.bin :refer [bin] :as b]))

  ; For example, we can bin range 0-14 into 5 bins like so:
(bin {:n 5} (range 15))
  ;; (0 0 0 1 1 1 2 2 2 3 3 3 4 4 4)  

  ; we can use bin for tml-datasets:

(require '[tablecloth.api :as tc])
(def ds (tc/dataset {:close (range 15)}))

(:close ds)

(bin {:n 5} (:close ds))
  ;; => (0 0 0 1 1 1 2 2 2 3 3 3 4 4 4)
  ;; same result.

(b/bin-full {:n 5} (range 15))

  ; 5 bins 10-110
  ; 0 10-30
  ; 1 30-50
  ; 2 50-70
  ; 3 70-90
  ; 4 90-110

(b/bin-lower-bound {:n-bins 5 :min-x 10 :max-x 110 :range-x 100} 4)
  ; 90
(b/bin-upper-bound {:n-bins 5 :min-x 10 :max-x 110 :range-x 100} 4)
  ; 110
(b/bin-middle {:n-bins 5 :min-x 10 :max-x 110 :range-x 100} 4)
  ; 100

(def v (range 15))
(def br (b/bin-full {:step 3.0} v))

(b/bin-result br)

(b/bin-middle br 0)




