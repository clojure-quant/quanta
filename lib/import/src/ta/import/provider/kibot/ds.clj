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
      (date-col-to-exchange-close exchange-kw)
      ))

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

 ;
  )

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
                (info "kibot csv: " csv)
    (info "kibot request finished!")
                
    ds
    ))

(defn symbols->str [symbols]
  (->> (interpose "," symbols)
       (apply str)))

(defn provider->symbol [provider-symbol]
  (if-let [inst (db/get-instrument-by-provider :kibot provider-symbol)]
    (:symbol inst)
    provider-symbol))

(comment
  (db/instrument-details "EUR/USD")

  (symbol->provider "MES0")
  (symbol->provider "EUR/USD")
  ;; => {:type "futures", :symbol "ES"}
  (provider->symbol "ES")


  (symbols->str ["MSFT" "ORCL"])
  (symbols->str ["ES"]))

; 

(defn symbol-conversion [col-symbol]
  (map provider->symbol col-symbol))

(defn kibot-snapshot-result->dataset [csv]
  (-> (tds/->dataset (string->stream csv)
                     {:file-type :csv
                      :header-row? true
                      :key-fn (comp keyword str/lower-case)
                      :dataset-name "kibot-snapshot"})
      (tc/update-columns {:symbol symbol-conversion})
      (tc/rename-columns {(keyword ":404 symbol not foundsymbol")
                          :symbol})
      ;(tc/convert-types :date [[:local-date-time date->localdate]])
      ))

(defn get-snapshot [symbol]
  (let [symbols-kibot (->> symbol
                           (map symbol->provider)
                           (map :symbol))
        result (kibot/snapshot {:symbol (symbols->str symbols-kibot)})]
    (if-let [error? (:error result)]
      (do (error "kibot request error: " error?)
          nil)
      (kibot-snapshot-result->dataset result))))

(comment

  (get-snapshot ["AAPL"])
  (get-snapshot ["NG0"])
  (get-snapshot ["CL0"])
  (get-snapshot ["MES0"])
  (get-snapshot ["RIVN" "AAPL" "MYM0"])

  "RIVN" "MYM0" "RB0" "GOOGL" "FCEL"
  "NKLA" "M2K0" "INTC" "MES0" "RIG"
  "ZC0" "FRC" "AMZN" "HDRO" "MNQ0"
  "BZ0" "WFC" "DAX0" "PLTR" "NG0"

  (symbol->provider "MSFT")
  (symbol->provider "EURUSD")
  (symbol->provider "IJH")

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

  (-> (get-bars {:asset "NG0" ; future
                 :calendar [:us :d]}
                {:start dt})
      (tc/info))

  (get-bars {:asset "EURUSD" ; forex
             :calendar [:forex :d]}
            {:start (parse-date "2023-09-01")})

  (get-bars  {:asset "IJH" ; ETF
              :calendar [:etf :d]}
             {:start (parse-date "2023-09-01")})

;
  )
;; symbollist 

(defn kibot-symbollist->dataset [tsv skip col-mapping]
  (->
   (tds/->dataset (string->stream tsv)
                  {:file-type :tsv
                   :header-row? true
                   :n-initial-skip-rows skip
                   :dataset-name "kibot-symbollist"})
   (tc/rename-columns col-mapping)))

(def list-mapping
  {:stocks {:url "http://www.kibot.com/Files/2/All_Stocks_Intraday.txt"
            :skip 5
            :cols {"column-0" :#
                   "column-1" :symbol
                   "column-2" :date-start
                   "column-3" :size-mb
                   "column-4" :desc
                   "column-5" :exchange
                   "column-6" :industry
                   "column-7" :sector}}
   :etf {:url  "http://www.kibot.com/Files/2/All_ETFs_Intraday.txt"
         :skip 5
         :cols {"column-0" :#
                "column-1" :symbol
                "column-2" :date-start
                "column-3" :size-mb
                "column-4" :desc
                "column-5" :exchange
                "column-6" :industry
                "column-7" :sector}}
   :futures {:url "http://www.kibot.com/Files/2/Futures_tickbidask.txt"
             :skip 4
             :cols {"column-0" :#
                    "column-1" :symbol
                    "column-2" :symbol-base
                    "column-3" :date-start
                    "column-4" :size-mb
                    "column-5" :desc
                    "column-6" :exchange}}
   :forex {:url "http://www.kibot.com/Files/2/Forex_tickbidask.txt"
           :skip 3
           :cols {"column-0" :#
                  "column-1" :symbol
                  "column-2" :date-start
                  "column-3" :size-mb
                  "column-4" :desc}}})

(defn download-symbollist [category]
  (kibot/make-request-url (-> list-mapping category :url)))

(defn row-delisted [ds-data]
  (-> (argops/argfilter #(= "Delisted:" %) (:# ds-data))
      first))

(defn filter-delisted-rows [ds-data]
  (let [idx-delisted (row-delisted ds-data)]
    (if idx-delisted
      (tc/select-rows ds-data (range idx-delisted))
      ds-data)))

(defn filter-empty-rows [ds-data]
  (tc/select-rows ds-data (comp #(not (nil? %)) :#)))

(defn parse-list [t tsv]
  (let [{:keys [skip cols]} (t list-mapping)
        ds-data (kibot-symbollist->dataset tsv skip cols)]
    (-> ds-data
        filter-delisted-rows
        filter-empty-rows)))

(defn symbol-list [t]
  (let [tsv (download-symbollist t)]
    (parse-list t tsv)))

(comment

  (def tsv-etf (download-symbollist :etf))
  (def ds-etf (parse-list :etf tsv-etf))
  ds-etf
  (:date-start ds-etf)

  (tc/row-count ds-etf)
  ; max #: 1667
  ; listed:    2889
  ; delisted:  1667
  ; all        4556
  ; row-count: 4562
  ; diff          6

  (symbol-list :etf)

;  
  )

