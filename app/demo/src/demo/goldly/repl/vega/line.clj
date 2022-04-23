(ns demo.goldly.repl.vega.line
  (:require
   [goldly.scratchpad :refer [show! show-as clear! eval-code!]]))

(def d [1 3 5 7 9 5 4 6 9 8 3 5 6])

d
(def hdata
  (into [] (repeatedly 1000 #(rand 10))))

(show!
 [:div
  [:h1 "list plot"]
  [:p/vega-line d]])
