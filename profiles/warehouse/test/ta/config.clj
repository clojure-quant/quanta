(ns ta.config
  (:require
   [ta.warehouse :as wh]))


(def w (wh/init {:series "/tmp/"
                 :list "../resources/etf/"}))


(def w2 (ta.warehouse/init
         {:series "/tmp/"
          :list "../resources/etf/"}))




