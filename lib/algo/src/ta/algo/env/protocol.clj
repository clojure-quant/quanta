(ns ta.algo.env.protocol)

(defprotocol algo-env
  (get-bar-db [this])
  (get-engine [this])
  ; algo
  (set-watcher [this w])
  (add-algo [this spec])
  (remove-algo [this spec]))
