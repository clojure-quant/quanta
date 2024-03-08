(ns notebook.playground.bardb.init
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.db.bars.protocol :as b]
   [ta.db.bars.duckdb :as duck]
   [modular.system]))

(def db (modular.system/system :duckdb))


(duck/create-table db [:forex :d])
(duck/create-table db [:forex :m])

(duck/create-table db [:crypto :d])
(duck/create-table db [:crypto :m])


(duck/create-table db [:forex :month])
(duck/create-table db [:us :month])
(duck/create-table db [:crypto :month])

(def bar-ds
  (-> {:date [(t/instant "2022-03-05T00:00:00Z")
              (t/instant "2023-03-06T20:00:00Z")
              (t/instant "2024-03-06T20:00:00Z")]
       :open [10.0 20.0 30.0]
       :high [10.0 20.0 30.0]
       :low [10.0 20.0 30.0]
       :close [10.0 20.0 30.0]
       :volume [10.0 20.0 30.0]
       :asset "XXX"}
      tc/dataset))

bar-ds

(b/append-bars db {:asset "XXX"
                   :calendar [:us :d]}
               bar-ds)

(b/get-bars db {:asset "XXX"
                :calendar [:us :d]}
            {:start (t/instant "2022-03-05T00:00:00Z")
             :end (t/instant "2025-03-06T20:00:00Z")})

(b/get-bars db {:asset "EUR/USD"
                :calendar [:forex :d]}
            {:start (t/instant "2022-03-05T00:00:00Z")
             :end (t/instant "2025-03-06T20:00:00Z")})

(b/get-bars db {:asset "EUR/USD"
                :calendar [:forex :d]}
            {:start (t/zoned-date-time "2024-03-07T16:30-05:00[America/New_York]")
             :end (t/zoned-date-time "2020-05-08T16:30-04:00[America/New_York]")})

