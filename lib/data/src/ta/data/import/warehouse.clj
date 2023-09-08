(ns ta.data.import.warehouse
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tablecloth.api :as tc]
   [ta.warehouse :as wh]
   [ta.data.settings :refer [determine-wh]]
   [ta.data.import.sort :as sort]
))


 (defn save-symbol [symbol interval ds]
   (let [w (determine-wh symbol)
         ds-sorted (sort/ensure-sorted ds)]
     (wh/save-symbol w ds-sorted interval symbol)))
 

(defn has-symbol [symbol interval]
   (let [w (determine-wh symbol)]
     (wh/exists-symbol? w interval symbol)))

(defn load-symbol [symbol interval]
  (let [w (determine-wh symbol)]
    (wh/load-symbol w interval symbol)))
 
 

 
 