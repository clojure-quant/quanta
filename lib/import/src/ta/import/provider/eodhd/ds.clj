(ns ta.import.provider.eodhd.ds
  (:require
   [taoensso.timbre :refer [info warn]]
   [de.otto.nom.core :as nom]
   [tick.core :as t] ; tick uses cljc.java-time
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.helper.date :refer [parse-date-only]]
   [ta.import.provider.eodhd.raw :as eodhd]
   [ta.db.bars.protocol :refer [barsource] :as b]))

(def eodhd-frequencies
  {:m "1"
   :h "60"
   :d "d"})

(def yyyy-mm-dd-formatter
  (t/formatter "YYYY-MM-dd"))

(defn fmt-yyyymmdd [dt]
  (t/format yyyy-mm-dd-formatter dt))

(defn convert-date [dt-s]
  (-> (parse-date-only dt-s)
      (t/at (t/time "00:00:00"))
      (t/in "UTC")
      (t/instant)))

(defn sort-ds [ds]
  (tc/order-by ds [:date] [:asc]))

(defn ds-fix-date [ds]
  (tc/add-column ds :date (map convert-date (:date ds))))

(defn eodhd-result->dataset [result]
  (-> result
      (tds/->dataset)
      (ds-fix-date)
      ;(sort-ds) ; bybit returns last date in first row.
      ;(tc/select-columns [:date :open :high :low :close :volume])
      ))

(defn error? [body]
  (-> body first :warning))

(defn get-bars-eodhd [api-token {:keys [asset calendar] :as opts} {:keys [start end] :as window}]
  (warn "get-bars: " opts window)
  (let [start-str (fmt-yyyymmdd start)
        end-str (fmt-yyyymmdd end)
        r (eodhd/get-bars api-token asset start-str end-str)]
    (warn "r: " r)
    (if-let [e (error? r)]
      (nom/fail ::eodhd-get-history {:message e
                                     :opts opts
                                     :window window})
      (eodhd-result->dataset r))))

(defrecord import-eodhd [api-token]
  barsource
  (get-bars [this opts window]
    (get-bars-eodhd (:api-token this) opts window)))

(defn create-import-eodhd [api-token]
  (import-eodhd. api-token))

(comment
  (fmt-yyyymmdd (t/zoned-date-time))
  (fmt-yyyymmdd (t/instant))

  (error? [{:warning "Data is limited by one year as you have free subscription "}])


  (require '[ta.import.provider.eodhd.raw :refer [d]])
  d

  (-> d
      (eodhd-result->dataset)
      tc/info)

  (-> (parse-date-only "2024-01-01")
      (t/at (t/time "00:00:00"))
      (t/in "UTC"))

  (convert-date "2024-01-01")

  (def eodhd (create-import-eodhd "65f0ad82c56400.56029279"))

  (b/get-bars eodhd
              {:asset "MCD.US"
               :calendar [:us :d]}
              {:start (t/zoned-date-time "2024-01-01T00:00:00Z")
               :end (t/zoned-date-time "2024-03-15T00:00:00Z")})



   ; 
  )