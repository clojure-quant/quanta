(ns ta.import.provider.kibot-http.ds
  (:require
   [taoensso.timbre :refer [info warn error]]
   [clojure.java.io :as io]
   [tick.core :as t]
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [ta.db.asset.db :as db]
   [ta.import.provider.kibot.raw :refer [login]]
   [ta.import.provider.kibot-http.raw :as raw]))

(defn date-time->localdate [dt time]
  (t/at dt time))

(defn date-time-adjust [bar-ds]
  (let [date-vec (:date bar-ds)
        time-vec (:time bar-ds)
        date-time-vec  (dtype/emap date-time->localdate
                                   :local-date-time
                                   date-vec time-vec)]
    (tc/add-or-replace-column bar-ds :date date-time-vec)))

(defn string->stream [s]
  (io/input-stream (.getBytes s "UTF-8")))

(defn kibot-result->dataset [csv]
  (-> (tds/->dataset (string->stream csv)
                     {:file-type :csv
                      :header-row? false
                      :dataset-name "kibot-bars"})
      ; 05/22/2023,01:27,0.409523291,0.409582704,0.409314088,0.409314088,4
      (tc/rename-columns {"column-0" :date
                          "column-1" :time
                          "column-2" :open
                          "column-3" :high
                          "column-4" :low
                          "column-5" :close
                          "column-6" :volume})
      ;(tc/convert-types :date [[:local-date-time date->localdate]])
      date-time-adjust
      (tc/drop-columns [:time])))

(defn get-csv [{:keys [asset calendar]} range]
  (info "get-bars kibot-http " asset " " calendar " " range " ..")
  (let [{:keys [kibot-http]} (db/instrument-details asset)]
    (assert kibot-http "kibot-http-feed needs to have asset-db: kibot-http")
    (login)
    (raw/download-link-csv kibot-http)))

(defn get-bars [opts range]
  (let [csv (get-csv opts range)]
    (info "parsing csv...")
    (kibot-result->dataset csv)))

(comment
  ; test importing previously imported csv files.
  (def csv (raw/load-csv-data "AUDCAD"))
  csv
  (kibot-result->dataset csv)

  ; test if EURUSD has kibot-http
  (db/instrument-details "EURUSD")

  (def csv (get-csv {:asset "EURUSD"
                     :calendar [:us :m]}
                    :full))
  (kibot-result->dataset csv)
  csv

  (def eurusd (get-bars {:asset "EURUSD"
                         :calendar [:us :m]}
                        :full)))


