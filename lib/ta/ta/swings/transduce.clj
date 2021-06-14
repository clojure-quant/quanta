(ns ta.swings.transduce
  (:require
   [ta.swings.core :refer [process new-swing toggle-swing swing-range-p counter% print-swings2]]))

(defn process-price [ret-prct xf-add {:keys [current next]} p dt]
  (let [current (process current p)
        current (assoc current :prct (swing-range-p current))
        next (process next p)
        next (if (:reset current)
               (do ;(println "resetting")
                 (toggle-swing current p))
               next)]
    ;(println current)
    (if (> (counter% current next) ret-prct)
      (do (xf-add (assoc current :dt-last dt))
          {:current (assoc next :dt-first dt)
           :next (toggle-swing next p)})
      {:current current
       :next next})))

(defn xf-swings
  [ret-prct]
  (fn [xf]
    (let [s (atom {:current nil
                   :next nil})]
      (fn
        ; init
        ([]
         (reset! s {:current nil
                    :next nil})
         (xf))
        ; stop
        ([acc]
         (when (:current @s)
           (xf acc (:current @s)))
         (reset! s {:current nil
                    :next nil})
         (xf (xf acc)))
        ; event
        ([acc {:keys [close date] :as row}]
         ;(println  "row " row)
         (if-not (:current @s)
           (let [C (assoc (new-swing :up close) :dt-first date)
                 N (toggle-swing C close)]
             (reset! s {:current C
                        :next N}))
           (let [S (process-price ret-prct (fn [d] (xf acc d)) @s close date)]
             (reset! s S)))
         acc)))))


(comment 
  
  (def series [{:date 1 :close 10}
               {:date 2 :close 30}
               {:date 3 :close 8}
               {:date 4 :close 6}
                {:date 5 :close 9}
                {:date 6 :close 11}
               ])

  (let [swings (into [] (xf-swings 20) series)]
    (println "swings: " swings)
    (print-swings2 swings)
  )
;
)