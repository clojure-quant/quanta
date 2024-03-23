(ns ta.trade.robot.track)

; robot
; Env: 
; broker (account credentials; socket; order-stream orderupdate-stream execution-stream)
; Position-db ()

; Target-       Target.      -> 
; Signal  ->    position.    ->    trade
; Kw.           Map
;               :asset
;               :qty
;               :entry-px
;               :entry-idx
;               :entry-date

(defprotocol position-db
  (get-actual-position [db asset])
  (set-actual-position [db asset side qty entry-price entry-date])
  (set-target-position [db asset side qty]))

;(Get-actual-position db {:asset asset})

(defn track-position [env bar-ds asset idx target-position]
  (let [pm (position-manager env)
        p (position/actual-position pm asset)]
    (position/set-actual-position pm asset
                                  {:entry-price (:close bar-ds)
                                   :entry-idx idx})))


