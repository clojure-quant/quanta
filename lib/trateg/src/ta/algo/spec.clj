(ns ta.algo.spec)


(defn get-asset [spec] 
  (:asset spec))

(defn get-calendar [spec]
  (:calendar spec))

(defn get-trailing-n [spec]
  (:trailing-n spec))
  
(defn get-feed [spec]
  (:feed spec))