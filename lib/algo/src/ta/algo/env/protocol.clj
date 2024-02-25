(ns ta.algo.env.protocol)

(defprotocol algo-env
  (get-bar-db [this])
  (get-engine [this])
  ; algo
  (add-algo [this spec])
  (remove-algo [this spec]))
