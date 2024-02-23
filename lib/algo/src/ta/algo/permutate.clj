(ns ta.algo.permutate)



(defn ->assets [algo assets]
  (map (fn [asset]
         (assoc algo :asset asset))
       assets))