(ns demo.studies.test-correlation
  (:require
   [fastmath.stats :as stats]
   [tech.v3.datatype.functional :as fun]
   [fastmath.stats :as stats]
   [ta.dataset.date :refer [days-ago select-rows-since]]
   [ta.math.stats :refer [;random-dataset random-datasets
                          standardize rand-numbers]]))

; [1044.9983800434406 0.5564513730783689]
(let [n  100
      xs (repeatedly n #(* 100 (rand)))
      ys (map #(+ % (* 200 (rand))) xs)]
  [(stats/covariance xs ys)
   (stats/correlation xs ys)])

; ([866.5593826345645 866.1596099370006 863.7837377357379]
;  [866.1596099370006 892.5838246583603 867.4019138907449]
;  [863.7837377357379 867.4019138907449 893.7935176171376])
(let [n  100
      xs (repeatedly n #(* 100 (rand)))
      ys (map #(+ % (* 20 (rand))) xs)
      zs (map #(+ % (* 20 (rand))) xs)]
  (stats/covariance-matrix [xs ys zs]))

; ([1.0 0.9808654216691534 0.9796397336485948]
;  [0.9808654216691534 1.0000000000000002 0.9623289410906197]
;  [0.9796397336485948 0.9623289410906197 0.9999999999999994])

(let [n  100
      xs (repeatedly n #(* 100 (rand)))
      ys (map #(+ % (* 20 (rand))) xs)
      zs (map #(+ % (* 20 (rand))) xs)]
  (->> [xs ys zs]
       (map standardize)
       stats/covariance-matrix))

; ([1.0000000000000004 0.9810616836180861 -0.03194657497857545]
;  [0.9810616836180861 0.9999999999999994 -0.16697297521203897]
;  [-0.03194657497857545 -0.16697297521203897 1.0000000000000009])
(let [n  1000
      xs (repeatedly n #(* 100 (rand)))
      ys (map #(+ % (* 20 (rand))) xs)
      zs (map #(+ %1
                  (- %2)
                  (* 20 (rand)))
              xs
              ys)]
  (->> [xs ys zs]
       (map standardize)
       stats/covariance-matrix))

; ([1.0000000000000004 0.6458853505397182 -0.6050256801614664]
;  [0.6458853505397182 1.0000000000000004 -0.9985414236008836]
;  [-0.6050256801614664 -0.9985414236008836 1.0000000000000009])
(let [n  1000
      xs (-> (rand-numbers n)
             (fun/* 100))
      ys (-> xs
             (fun/* 20 (rand-numbers n)))
      zs (-> xs
             (fun/- ys)
             (fun/+ (fun/* 20 (rand-numbers n))))]
  (->> [xs ys zs]
       (map standardize)
       stats/covariance-matrix))



