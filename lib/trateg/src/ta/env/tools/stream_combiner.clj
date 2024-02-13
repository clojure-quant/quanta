(ns ta.env.tools.stream-combiner
  (:require
   [manifold.stream :as s]))

(defn stream-combiner
  "Calculate a value that depends on the last 
  Value of multiple input streams and push it
  To the output stream"
  [combine-fn & streams]
  (let [result-s (s/stream)
        results-a (atom {})
        set-result (fn [idx val]
                     (swap! results-a assoc idx val))
        consume-stream (fn [idx s]
                         (s/consume (fn [msg]
                                      (set-result idx msg)
                                      (let [input (vals @results-a)]
                                      (try 
                                        (let [r (combine-fn input)]
                                            (s/put! result-s r))
                                        (catch Exception e
                                            (println "combine-fn input: " input " exception: " e) 
                                            ))))
                                    s))]
    (doall (map-indexed consume-stream streams))
    result-s))

(comment
  (def a (s/stream))
  (def b (s/stream))
  (defn add [results]
    (apply + results))
  (add [1 2 3])
  (add '(1 2 3))
  
  (def r (stream-combiner add a b))
  
  (defn print-result [r]
    (println "comboresult:" r))
  (s/consume print-result r)

  (s/put! a 0) ; 0 nil => nil
  (s/put! b 0) ; 0 0 => 0
  (s/put! a 1) ; 1 0  => 1
  (s/put! a 2) ; 2 0  => 2
  (s/put! b 10) ; 2 10  => 12

;
  )
