(ns ta.env.protocol)

(defprotocol env
  (get-bar-db [this])
  ; algo
  (add-algo [this spec])
  (remove-algo [this spec]))

