(ns juan.algo)



(defn juan-algo-impl [pivots ds-bars]
   ; calc change from close
   ; for each bar (find-nearest-pivot pivots close)
   ; long: strong move down. Pivot near. Doji detected)
   ; short vice versa
   ; adds position column to ds
  )

(defn juan-algo [env opts ds-bars]
  (let [pivots (get-result env [:us :d])]
    (juan-algo-impl pivots ds-bars)))
         
