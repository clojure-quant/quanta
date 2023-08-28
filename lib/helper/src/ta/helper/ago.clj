(ns ta.helper.ago)

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
  (let [first (atom true)]
    (fn
      ;; SET-UP
      ([]
       (reset! first true)
       (xf))
     	;; PROCESS
      ([result input]
         (if @first
           (do (reset! first false)
               result) ; unchanged collection for first element
           (xf result input) ; add current element thereafter
           ))
      ;; TEAR-DOWN
      ([result]
       (when-not @first
         (xf result nil))
         (xf result)))))


(comment

  (into [] (map inc) [3 4 5 6 7])

  (defn x10 [x] (* 10 x))
  (into [] (comp (map inc) (map x10)) [3 4 5 6 7])

  (into [] xf-ago [3 4 5 6 7])

  (into [] xf-ago-pair [3 4 5 6 7])

  (into [] xf-future [3 4 5 6 7])
;  
  )
