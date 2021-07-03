(ns demo.config
  (:require
   [clojure.pprint]
   [ta.warehouse :as wh]
  ))


(wh/init-tswh {:series "../../db/"
               :list "../../resources/etf/"})

(wh/init-tswh {:series "./db/"
               :list "./resources/etf/"})

