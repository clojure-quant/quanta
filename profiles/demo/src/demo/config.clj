(ns demo.config
  (:require
   [clojure.pprint]
   [ta.warehouse :as wh]))

(def w (wh/init {:series "../db/"
                 :list "../resources/etf/"}))

