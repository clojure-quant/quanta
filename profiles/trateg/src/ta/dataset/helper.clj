(ns ta.dataset.helper
  (:require [tech.v3.dataset :as dataset]
            [tablecloth.api :as tablecloth]
            [tick.alpha.api :as tick]
            [fastmath.core :as math]
            [fastmath.stats :as stats]
            [tech.v3.datatype.functional :as fun]
            [tech.v3.datatype :as dtype]))

(-> (tick/now)
    (tick/+ (tick/new-duration 5 :minutes)))

(defn days-ago [i]
  (-> (tick/now)
      (tick/- (tick/new-duration i :days))
      (tick/date)))

(defn random-dataset [n]
  (tablecloth/dataset
   {:date (->> (range n)
               (map days-ago)
               reverse)
    :close (repeatedly n rand)}))

(comment
  (random-dataset 3)
  ;;   _unnamed [3 2]:

  ;; |      :date |     :close |
  ;; |------------|-----------:|
  ;; | 2021-07-10 | 0.53077401 |
  ;; | 2021-07-11 | 0.49329818 |
  ;; | 2021-07-12 | 0.52461530 |
  )

(comment
  (-> (random-dataset 1000)
      (tablecloth/rows :as-maps)
      first
      type)
  ;; tech.v3.dataset.FastStruct
  )

(defn select-recent-rows [ds date]
  (-> ds
      (tablecloth/select-rows
       (fn [row]
         (-> row
             :date
             (tick/>= date))))))

(comment
  (-> (random-dataset 1000)
      (select-recent-rows (days-ago 6)))

  ;; _unnamed [7 2]:

  ;; |      :date |     :close |
  ;; |------------|-----------:|
  ;; | 2021-07-06 | 0.50941281 |
  ;; | 2021-07-07 | 0.26352255 |
  ;; | 2021-07-08 | 0.39483388 |
  ;; | 2021-07-09 | 0.99924154 |
  ;; | 2021-07-10 | 0.17211258 |
  ;; | 2021-07-11 | 0.78036337 |
  ;; | 2021-07-12 | 0.40634932 |
)

(comment
  (def ds-1 (random-dataset 1000000))
  (let [date (days-ago 6)]
    (-> ds-1
        (tablecloth/select-rows
         (fn [row]
           (-> row
               :date
               (tick/>= date))))
        time))
  ;; ~ 200 msecs
  )

(defn random-datasets [m n]
  (repeatedly m #(random-dataset n)))

(comment

  (def datasets (random-datasets 12 1000))


  (let [n  100
        xs (repeatedly n #(* 100 (rand)))
        ys (map #(+ % (* 200 (rand))) xs)]
    [(stats/covariance xs ys)
     (stats/correlation xs ys)])

  ;; [1044.9983800434406 0.5564513730783689]


  (let [n  100
        xs (repeatedly n #(* 100 (rand)))
        ys (map #(+ % (* 20 (rand))) xs)
        zs (map #(+ % (* 20 (rand))) xs)]
    (stats/covariance-matrix [xs ys zs]))

  ;; ([866.5593826345645 866.1596099370006 863.7837377357379]
  ;;  [866.1596099370006 892.5838246583603 867.4019138907449]
  ;;  [863.7837377357379 867.4019138907449 893.7935176171376])


  (defn standardize [xs]
    (-> xs
        (fun/- (fun/mean xs))
        (fun// (fun/standard-deviation xs))))


  (let [n  100
        xs (repeatedly n #(* 100 (rand)))
        ys (map #(+ % (* 20 (rand))) xs)
        zs (map #(+ % (* 20 (rand))) xs)]
    (->> [xs ys zs]
         (map standardize)
         stats/covariance-matrix))

  ;; ([1.0 0.9808654216691534 0.9796397336485948]
  ;;  [0.9808654216691534 1.0000000000000002 0.9623289410906197]
  ;;  [0.9796397336485948 0.9623289410906197 0.9999999999999994])



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

  ;; ([1.0000000000000004 0.9810616836180861 -0.03194657497857545]
  ;;  [0.9810616836180861 0.9999999999999994 -0.16697297521203897]
  ;;  [-0.03194657497857545 -0.16697297521203897 1.0000000000000009])

  (defn rand-numbers [n]
    (dtype/clone
     (dtype/make-reader :float32 n (rand))))

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

  ;; ([1.0000000000000004 0.6458853505397182 -0.6050256801614664]
  ;;  [0.6458853505397182 1.0000000000000004 -0.9985414236008836]
  ;;  [-0.6050256801614664 -0.9985414236008836 1.0000000000000009])

  )
