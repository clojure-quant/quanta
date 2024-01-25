(ns notebook.live.bargenerator-system
  (:require
   [modular.system]
   [clojure.pprint :refer [print-table]]
   [ta.tickerplant.bar-generator :as bg]))

(def state (modular.system/system :bargenerator))

(defn print-current-bars []
  (let [bars (vals @(:db state))]
     (print-table bars)))

(print-current-bars)

