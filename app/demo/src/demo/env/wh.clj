(ns demo.env.wh
  (:require
   [ta.warehouse :as wh]
   [ta.data.settings :refer [determine-wh]]
   ))


(alter-var-root #'wh/*default-warehouse* (constantly determine-wh))


(comment 

wh/*default-warehouse*  
  
;  
  )

