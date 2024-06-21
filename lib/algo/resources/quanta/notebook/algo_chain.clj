(ns quanta.notebook.algo-chain
  (:require [ta.algo.spec.parser.chain :as chain]))

(defn store-time [_env _opts time]
  {:time time})

(defn store-secret [_env opts result]
  (assoc result :big-question (:secret opts)))

(defn store-opts [_env opts result]
  (assoc result :opts opts))

; chain with only one function

(def chain-simple
  'quanta.notebook.algo-chain/store-time)

(def simple-fun
  (chain/make-chain chain-simple))

(simple-fun nil {:asset "EUR/USD"} :future)

; undefined symbol (namespace exists, but function is not defined)

(def chain-simple-bad
  'quanta.notebook.algo-chain/store-time-bad)

(def undefined-fun
  (chain/make-chain chain-simple-bad))

undefined-fun

; compile error 

(def chain-simple-compile-err
  'willy.the.unknown/calculate-bad)

(def compile-err-fun
  (chain/make-chain chain-simple-compile-err))

compile-err-fun


; chain with 3 functions that are chained together

(def chain-3
  [{:secret 42
    :sma 30}
   store-time
   store-secret
   'quanta.notebook.algo-chain/store-opts])

(def time-fun
  (chain/make-chain chain-3))

time-fun

(time-fun nil {:asset "EUR/USD"} :now)
  ;; => {:time :now, :big-question 42, :opts {:asset "EUR/USD", :secret 42, :sma 30}}


; chain with with one bad function

(def chain-3-bad
  [{:secret 42
    :sma 30}
   store-time
   'willy.the.unknown/calculate-bad
   store-secret
   'quanta.notebook.chain/store-opts])

(def chain-3-fun
  (chain/make-chain chain-3-bad))

chain-3-fun