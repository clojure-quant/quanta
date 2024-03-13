(ns notebook.data.bardb.dynamic-importer
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [modular.system]
   [ta.calendar.core :as cal]
   [ta.db.bars.protocol :as b]
   [ta.db.bars.duckdb :as duck]
   [ta.db.bars.dynamic :as dynamic]
   [ta.db.bars.dynamic.import :as importer]
   [ta.db.bars.dynamic.overview-db :as overview]))



;; test if fetching further days back works

(overview/available-range
 (:overview-db db-dynamic)
 {:asset "MO"
  :calendar [:us :d]
  :import :kibot})

(importer/tasks-for-request db-dynamic
                            {:asset "MO"
                             :calendar [:us :d]
                             :import :kibot}
                            window)
