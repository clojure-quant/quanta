(ns notebook.playground.bardb.dynamic
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [modular.system]
   [ta.db.bars.protocol :as b]
   [ta.db.bars.duckdb :as duck]
   [ta.db.bars.dynamic :as dynamic]))

;; Test if duckdb get/append works

(def db-duck (duck/start-bardb-duck "/tmp/demo7"))
db-duck
(duck/init-tables (:state db-duck))

(def ds
  (tc/dataset [{:date (t/instant "1999-12-31T00:00:00Z")
                :open 1.0 :high 1.0 :low 1.0 :close 1.0
                :volume 1.0 :epoch 1 :ticks 1 :asset "MSFT"}
               {:date (t/instant "2000-12-31T00:00:00Z")
                :open 1.0 :high 1.0 :low 1.0 :close 1.0
                :volume 1.0 :epoch 1 :ticks 1 :asset "MSFT"}]))
ds

(b/append-bars db-duck {:asset "MSFT"
                        :calendar [:us :m]
                        :import :kibot}
               ds)

(def window {:start (t/instant "1999-02-01T20:00:00Z")
             :end (t/instant "2001-03-01T20:00:00Z")})

(b/get-bars db-duck
            {:asset "MSFT"
             :calendar [:us :m]
             :import :kibot}
            window)

;; Test if DYAMIC get/append works

(def db-dynamic (dynamic/start-bardb-dynamic db-duck "/tmp/overview"))
db-dynamic

(b/get-bars db-dynamic
               {:asset "MSFT"
                :calendar [:us :m]
                :import :kibot}
               window)


(b/append-bars db-dynamic {:asset "MSFT"
                           :calendar [:us :m]
                           :import :kibot
                           } ds )

;; TEST if import-missing works

(b/get-bars db-dynamic
            {:asset "EURUSD"
             :calendar [:us :m]
             :import :kibot}
            window)

