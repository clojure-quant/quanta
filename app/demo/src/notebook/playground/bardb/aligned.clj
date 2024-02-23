(ns notebook.playground.bardb.aligned
  (:require
   [tick.core :as t]
   [modular.system]
   [taoensso.timbre :refer [trace debug info warn error]]
   [tablecloth.api :as tc]
   [ta.env.core :refer [get-bars-aligned-filled]]
   [ta.calendar.core :as cal]
   [ta.backtest.core :refer [run-backtest]]
   [ta.env.javelin :refer [create-env]]
   [ta.env.javelin.algo :as dsl]))

(defn get-aligned [env {:keys [calendar trailing-n] :as opts} time]
  (let [cal-seq (cal/trailing-window calendar trailing-n time)
        bars (get-bars-aligned-filled env opts cal-seq)
        ]
    {:time time
     :cal-seq cal-seq
     :bars bars}))




(def window (-> (cal/trailing-range [:us :d] 1)
                   ;(window-as-date-time)
                ))

window

(def spec {:asset "MSFT"
           :calendar [:us :d]
           :import :kibot
           :trailing-n 100})


(def env (create-env :bardb-dynamic))
(def strategy (dsl/add-time-strategy env spec get-aligned))
(run-backtest env window)
@strategy


(t/inst)

(ta.env.core/at-time (t/inst))

(-> (t/inst)
    (t/date)
    (t/at (t/time "00:04:00"))
    )
