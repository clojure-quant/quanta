(ns demo.goldly.repl.customer
  (:require
   [goldly.scratchpad :refer [show! show-as clear! eval-code!]]))

(def customer {:name "Fred Feuerstein"
               :country "Wonderworld"})

(show!
 [:div
  [:h1 "customer ui:"]
  [:p/customer customer]])
