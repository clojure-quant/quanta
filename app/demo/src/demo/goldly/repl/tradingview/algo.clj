(ns demo.goldly.repl.tradingview.algo
  (:require
   [reval.cljs-eval :refer [eval-code!]]))

(eval-code!
 (+ 5 5))

(eval-code!
 ;(-> (deref algo-state) :data keys)
 ;(-> (deref algo-state) :data :name)
 ;(-> (deref algo-state) :data :ds-roundtrips)
 ;(-> (deref algo-state) :data :stats keys)
 ;(-> (deref algo-state) :data :stats :nav)
 ;(-> (deref algo-state) :data :highchart)
 ;(-> (deref algo-state) :data :study-extra-cols)
 (-> (deref algo-state) :data :tradingview)
 ;(-> (deref algo-state) :data :tradingview-marks)
 )


