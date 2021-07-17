
(ns demo.studies.test-correlation
  (:require
   [tablecloth.api :as tablecloth]
   [tick.alpha.api :as tick]
   [ta.dataset.helper :refer [random-dataset random-datasets
                              days-ago select-recent-rows]]))


(random-dataset 3)
  ;;   _unnamed [3 2]:

  ;; |      :date |     :close |
  ;; |------------|-----------:|
  ;; | 2021-07-10 | 0.53077401 |
  ;; | 2021-07-11 | 0.49329818 |
  ;; | 2021-07-12 | 0.52461530 |


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


(def datasets (random-datasets 12 1000))
(count datasets)
(first datasets)

(-> (random-dataset 1000)
    (tablecloth/rows :as-maps)
    first
    type)
  ;; tech.v3.dataset.FastStruct