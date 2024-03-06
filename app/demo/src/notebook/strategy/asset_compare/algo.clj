(ns notebook.strategy.asset-compare.algo
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [tablecloth.api :as tc]
   [ta.algo.env.core :as env]))


(defn asset-compare-algo
  "for a point in time, does calculation of all spreads on a trailing-n window.
   all spreads that can be calculated will be added as columns."
  [env {:keys [calendar trailing-n assets] :as opts} end-dt]
  (info "calculating asset-compare ending: " end-dt)
  (let [bar-ds-seq (env/get-multiple-bars-trailing env opts end-dt)
        bars (:bars bar-ds-seq)
        bar-ds (apply tc/concat bars)
      ]
    ;(info "assets: " assets)
    ;bar-ds-seq
    bar-ds
    ))

    
