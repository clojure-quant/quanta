(ns ta.gann.clip
  (:require 
     [modular.system]))


(defn get-bar-db []
  (modular.system/system :bardb-dynamic))

