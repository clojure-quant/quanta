(ns ta.config
   (:require
   [ta.warehouse :as wh]))


(def w (wh/init {:series "/tmp/"
                 :list "../resources/etf/"}))