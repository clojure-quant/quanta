(ns ta.xf.ago
  (:require
   [taoensso.timbre :refer [trace debug info error]]))

(defn xf-ago [xf]
  (let [ago-val (atom nil)]
    (fn
      ;; SET-UP
      ([]
       (reset! ago-val nil)
       (xf))
     	;; PROCESS
      ([result input]
       (let [v @ago-val]
         ;(println "input: " input "ago: " v " result: " result)
         (reset! ago-val input)
         (xf result v) ;@ago-val
         ))
      ;; TEAR-DOWN
      ([result]
       (xf result)))))

(defn xf-ago-pair [xf]
  (let [ago-val (atom nil)]
    (fn
      ;; SET-UP
      ([]
       (reset! ago-val nil)
       (xf))
     	;; PROCESS
      ([result input]
       (let [v @ago-val]
         ;(println "input: " input "ago: " v)
         (reset! ago-val input)
         (xf result [v input])))
      ;; TEAR-DOWN
      ([result]
       (xf result)))))

(defn xf-future [xf]
  (let [last-val (atom nil)]
    (fn
      ;; SET-UP
      ([]
       (reset! last-val nil)
       (xf))
     	;; PROCESS
      ([result input]
       (let [v @last-val]
         (reset! last-val input)
         ;(println "input: " input "result: " result)
         (if (nil? v)
           (xf)
           (xf result input))))
      ;; TEAR-DOWN
      ([result]
       (let [v @last-val]
         (when-not (nil? v)
           (xf result v))
         (xf result))))))

(comment

  (into [] (map inc) [3 4 5 6 7])

  (defn x10 [x] (* 10 x))
  (into [] (comp (map inc) (map x10)) [3 4 5 6 7])

  (into [] xf-ago [3 4 5 6 7])

  (into [] xf-ago-pair [3 4 5 6 7])

  (into [] xf-future [3 4 5 6 7])
;  
  )
