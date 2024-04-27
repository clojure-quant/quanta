(ns ta.indicator.helper)

(defmacro indicator
  "creates a mapping-transducer.
   If only trans-fn is supplied, then the mapping transducer is stateless.
   If bindings are supplied, then it becomes a stateful mapping-transducer.
   stolen from: https://github.com/rereverse/tapassage"
  ([trans-fn] `(indicator [] ~trans-fn))
  ([bindings trans-fn]
   `(fn [xf#]
      (let ~bindings
        (fn
          ([] (xf#))
          ([result#] (xf# result#))
          ([result# input#]
           (if-let [r# (~trans-fn input#)]
             (if (reduced? r#)
               r# (xf# result# r#))
             result#)))))))

(comment

  (defn field-xf [f]
    (indicator
     []
     (fn [x]
       (f x))))

  (defn multiple-xf [m]
    (indicator
     (fn [x]
       (into {} (map (fn [[k v]]
                       (println k "x: " x "v: " (v x))
                       [k (v x)]) m)))))

; 
  )