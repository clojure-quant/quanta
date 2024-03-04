(ns ta.import.provider.kibot.ds
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer [info warn error]]
   [clojure.java.io :as io]
   [tick.core :as t]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.argops :as argops]
   [tablecloth.api :as tc]
   [de.otto.nom.core :as nom]
   [ta.calendar.validate :as cal-type]
   [ta.db.asset.db :as db]
   [ta.import.helper :refer [p-or-fail]]
   [ta.import.helper.daily :refer [date-col-to-exchange-close]]
   [ta.import.provider.kibot.raw :as kibot]))

(defn string->stream [s]
  (io/input-stream (.getBytes s "UTF-8")))

(defn kibot-result->dataset [exchange-kw csv]
  (-> (tds/->dataset (string->stream csv)
                     {:file-type :csv
                      :header-row? false
                      :dataset-name "kibot-bars"})
      (tc/rename-columns {"column-0" :date
                          "column-1" :open
                          "column-2" :high
                          "column-3" :low
                          "column-4" :close
                          "column-5" :volume})
      (date-col-to-exchange-close exchange-kw)))

(def category-mapping
  {:equity "stocks"
   :etf "ETF"
   :future "futures"
   :fx "forex"})

(defn symbol->provider [asset]
  (let [{:keys [category kibot] :as instrument} (db/instrument-details asset)
        type (get category-mapping category)
        asset (if kibot kibot asset)]
    {:type type
     :symbol asset}))

(def interval-mapping
  {:d "daily"})

(defn fmt-yyyymmdd [dt]
  (t/format (t/formatter "YYYY-MM-dd") (t/date-time dt)))

(defn make-one [range key kibot-name]
  (if-let [dt (key range)]
    (into {} [[kibot-name (fmt-yyyymmdd dt)]])
    {}))

(defn start-end->kibot [{:keys [start] :as range}]
  ; {:startdate "2023-09-01" :enddate "2024-01-01"}
  (merge (make-one range :start :startdate)
         (make-one range :end :enddate)))

(defn range->parameter [{:keys [start] :as range}]
  (cond
    (= range :full)
    {:period 100000}

    (int? range)
    {:period range}

    :else
    (start-end->kibot range)))

(defn get-bars [{:keys [asset calendar] :as opts} range]
  (nom/let-nom> [range (select-keys range [:start :end])
                 _ (info "get-bars kibot " asset " " calendar " " range " ..")
                 symbol-map (symbol->provider asset)
                 f (p-or-fail (cal-type/interval calendar)
                              opts range "kibot get-bars needs :calendar")
                 exchange (cal-type/exchange calendar)
                 period-kibot (p-or-fail (get interval-mapping f)
                                         opts range (str "kibot frequency not found: " f))
                 range-kibot (range->parameter range)
                 _ (assert symbol-map (str "kibot symbol not found: " asset))
                 _ (assert period-kibot (str "kibot does not support frequency: " f))
                 _ (info "kibot make request interval: " period-kibot " range: " range-kibot "asset-kibot: " symbol-map)
                 result (kibot/history (merge symbol-map
                                              range-kibot
                                              {:interval period-kibot
                                               :timezone "UTC"
                                               :splitadjusted 1}))
                 ds (kibot-result->dataset exchange result)]
                (info "kibot request finished!")
                ds))

(defn symbols->str [symbols]
  (->> (interpose "," symbols)
       (apply str)))

(defn provider->symbol [provider-symbol]
  (if-let [inst (db/get-instrument-by-provider :kibot provider-symbol)]
    (:symbol inst)
    provider-symbol))

(comment
  (def csv "09/01/2023,26.73,26.95,26.02,26.1,337713\r\n")
  (def csv
    (kibot/history {:symbol "SIL" ; SIL - ETF
                    :interval "daily"
                    :period 1
                    :type "ETF" ; Can be stocks, ETFs forex, futures.
                    :timezone "UTC"
                    :splitadjusted 1}))
  csv

  (-> (kibot-result->dataset :us csv)
      (tc/info :columns))


  (db/instrument-details "EUR/USD")

  (symbol->provider "MES0")
  (symbol->provider "EUR/USD")
  ;; => {:type "futures", :symbol "ES"}
  (provider->symbol "ES")


  (symbols->str ["MSFT" "ORCL"])
  (symbols->str ["ES"])

  (require '[ta.helper.date :refer [parse-date]])
  (def dt (parse-date "2024-02-26"))
  (class dt)
  dt

  (fmt-yyyymmdd dt)
  (def dt-inst (t/inst))
  (fmt-yyyymmdd dt-inst)
  (def dt-instant (t/instant))
  (fmt-yyyymmdd dt-instant)

  (t/year dt-inst)
  (t/month (t/date-time dt-inst))
  (t/month dt)

  (start-end->kibot {:start (t/inst)})
  (start-end->kibot {:end (t/inst)})
  (start-end->kibot {:start (t/inst) :end (t/inst)})

  (get-bars {:asset "MSFT" ; stock
             :calendar [:us :d]}
            {:start dt})
  
  (get-bars {:asset "MSFT"
             :calendar [:us :d]
             :import :kibot}
            {:start (t/instant "2020-01-01T00:00:00Z")
             :end (t/instant "2020-02-01T00:00:00Z")})


  (-> (get-bars {:asset "NG0" ; future
                 :calendar [:us :d]}
                {:start dt})
      (tc/info))

  (get-bars {:asset "EUR/USD" ; forex
             :calendar [:forex :d]}
            {:start (parse-date "2023-09-01")
             :end (parse-date "2023-10-01")
             })

  (get-bars  {:asset "IJH" ; ETF
              :calendar [:etf :d]}
             {:start (parse-date "2023-09-01")})

; 
  )
