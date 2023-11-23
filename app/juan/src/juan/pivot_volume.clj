(ns juan.pivot-volume
   (:require
    [taoensso.timbre :refer [info warn error]]
    [tablecloth.api :as tc]
    [tech.v3.datatype.functional :as fun]
    [ta.warehouse :refer [load-symbol]]
    [juan.data :refer [settings instruments]]))
  
  


(load-symbol :juan "D" "JY1223")


(load-symbol :juan "D" "EU1223")

; |            :date |   :open |   :high |    :low |  :close | :volume | :symbol |
; | 2023-11-13T00:00 | 1.07010 | 1.07220 | 1.06795 | 1.07170 |  138511 |  EU1223 |
; | 2023-11-14T00:00 | 1.07130 | 1.09020 | 1.06725 | 1.08965 |  316911 |  EU1223 |
; | 2023-11-15T00:00 | 1.08920 | 1.09010 | 1.08460 | 1.08605 |  250800 |  EU1223 |

;; close is near high, so high is the pivot. 
;; spike was a increase in price => high is the pivot.