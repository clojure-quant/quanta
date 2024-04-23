(ns quanta.notebook.math-correlation
  (:require
   [tech.v3.datatype.functional :as fun]
   [fastmath.stats :as stats]
   [ta.math.stats :refer [standardize rand-numbers]]))

(let [n  100
      xs (repeatedly n #(* 100 (rand)))
      ys (map #(+ % (* 200 (rand))) xs)]
  [(stats/covariance xs ys)
   (stats/correlation xs ys)])

(let [n  100
      xs (repeatedly n #(* 100 (rand)))
      ys (map #(+ % (* 20 (rand))) xs)
      zs (map #(+ % (* 20 (rand))) xs)]
  (stats/covariance-matrix [xs ys zs]))

(let [n  100
      xs (repeatedly n #(* 100 (rand)))
      ys (map #(+ % (* 20 (rand))) xs)
      zs (map #(+ % (* 20 (rand))) xs)]
  (->> [xs ys zs]
       (map standardize)
       stats/covariance-matrix))

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

(fun/quartiles (range 1000))

(fun/quartile-1 (range 1000))

