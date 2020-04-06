

(defmacro and-as->
  "Similar to as->, except wraps an implicit and around each step: returns nil/false
   as soon as any expression returns nil/false."
  ([expr sym & body]
   (if (seq body)
     `(as-> ~expr ~sym
        ~(first body)
        ~@(map (fn [b] `(and ~sym ~b)) (next body)))
     expr)))