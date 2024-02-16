(ns ta.import.provider.alphavantage.raw
  (:require
   [taoensso.timbre :refer [info warn error]]
   [clj-http.client :as client]
   [cheshire.core] ; JSON Encoding
   [throttler.core]
   [ta.helper.date :refer [parse-date]]
   [ta.data.helper :refer [str->float]]))

;; https://www.alphavantage.co/documentation/#
;; https://github.com/RomelTorres/alpha_vantage/issues/13
;; https://github.com/prediqtiv/alpha-vantage-cookbook/blob/master/symbol-lists.md

; 5 API requests per minute
; 500 requests per day

;; Fingerprint Cards (STO: FING-B) == FING-B.STO
;; Novo Nordisk (CPH: NOVO-B) == NOVO-B.CPH
;; BMW (DE: BMW) == BMW.DE

;; valid symbols:
;; NYSE:WIT
;; BSE:507685
;; FLN.AX
;; NSE:HDFC
;; NSE:SBIN
;; NSE:LT&
;; NSE:ARVSMART
;; GBG.L
;; LON:RR

;; AlphaVantage ApiKey Management

(defonce api-key (atom ""))

(defn set-key!
  "to use alphavantage api, call at least once set-key! api-key"
  [key]
  (info "setting alphavantage key..")
  (reset! api-key key)
  nil ; Important not to return by chance the key, as this would be shown in the repl.
  )

;; helper

(defn- fix-keywords-
  "Alphavantage responses are unusually structured.
   field '1. open'  will be converted to :open keyword"
  [m]
  (let [ks (keys m)
        vs (vals m)]
    (zipmap (map #(keyword (second (re-find #"\d+.\s(\w+)" (name %)))) ks) vs)))

; Response of throtteled:
;
; {:Note "Thank you for using Alpha Vantage!
;        Our standard API call frequency is 5 calls per minute and 500 calls per day.
;        Please visit https://www.alphavantage.co/premium/ if you would like to target
;        a higher API call frequency."}

(defn- throtteled? [response]
  (contains? response :Note))

(defn- error? [response]
  (contains? response :Information))

(defn- success-if [response process-success]
  (cond 
    (throtteled? response)
     :throttled ;awb99: perhaps better return :throttled ???
    
    (error? response)
    (do (error "Alphavantage error : " (:Information response))
        nil)
    
    :else
    (process-success response)))

(defn- get-av-raw [params process-success]
  (-> (client/get "https://www.alphavantage.co/query"
                  {:accept :json
                   :query-params (assoc params :apikey @api-key)})
      (:body)
      (cheshire.core/parse-string true)
      (success-if process-success)))

(defonce get-av-throttled
  (throttler.core/throttle-fn get-av-raw 5 :minute))

(defn get-av [params process-success] ; throtteled version
  (get-av-throttled
   params
   (fn [result]
     (if (= result :throttled)
       (do (warn "alphavantage request was throttled (this should NOT happen), retrying..")
           (get-av-throttled params (fn [result2]
                                      (if (= result2 :throttled)
                                        (do (error "alphavantage request was throttled the second time.")
                                            nil)
                                        (process-success result2)))))
       (process-success result)))))

;; Search Symbol

(defn search
  "searches for available symbols by keyword"
  [keywords]
  (get-av {:function "SYMBOL_SEARCH"
           :keywords keywords}
          (fn [response]
            (->> response
                 :bestMatches
                 (map fix-keywords-)
                 (into [])))))

;; Timeseries Requests

(defn get-field [data fieldname]
  (if data
    (get data (keyword fieldname))
    (do (error "cannot get field " fieldname "- data nil.")
        nil)))

(defn extract-fields [data fields]
  ;(println "extracting fields: " fields)
  (if data (->>
            (map (fn [[k field-name]]
                   [k (get-field data field-name)])
                 fields)
            (into {}))
      (do (error "cannot extract fields from nil: " fields)
          nil)))

(def bar-fields-standard- {:open  "1. open"
                           :high  "2. high"
                           :low  "3. low"
                           :close  "4. close"
                           :volume  "5. volume"})

(def bar-fields-adjusted- {:open  "1. open"
                           :high  "2. high"
                           :low  "3. low"
                           :close  "4. close"
                           :close-adj "5. adjusted close"
                           :volume  "6. volume"
                           :dividend "7. dividend amount"
                           :split-coefficient  "8. split coefficient"})

(def bar-fields-crypto- {:open "1a. open (USD)"
                         :high  "2a. high (USD)"
                         :low  "3a. low (USD)"
                         :close  "4a. close (USD)"
                         :volume  "5. volume"
                         :marketcap  "6. market cap (USD)"})

;; META DATA

(def meta-fields
  {:info "1. Information"
   :symbol "2. Symbol"
   :last-refresh "3. Last Refreshed"
   :size-type "4. Output Size"
   :time-zone "5. Time Zone"})

(defn extract-meta [data]
  (let [d (get-field data "Meta Data")
        m (extract-fields d meta-fields)]
    ;(println "meta data: " m)
    m))

(defn type-convert-bar [b series-type]
  (let [volume? (or (= series-type :daily)
                    (= series-type :adjusted))
        bar {:date (parse-date (subs (str (:date b)) 1))
             :open (str->float (:open b))
             :high (str->float (:high b))
             :low (str->float (:low b))
             :close (str->float (:close b))}
        volume (if volume?
                 {:volume (str->float (:volume b))}
                 {})
        adjusted (if (= series-type :adjusted)
                   {:close-adj (str->float (:close-adj b))
                    :split-coefficient (str->float (:split-coefficient b))
                    :dividend (str->float (:dividend b))}
                   {})]
    (merge b bar volume adjusted)))

(defn- convert-bar- [bar-fields series-type item]
  (let [d (first item)
        bars (second item)
        bar (extract-fields bars bar-fields)]
    (-> bar
        (assoc :date d)
        (type-convert-bar series-type))))

(defn- get-field-series [series-type response]
  (let [f (case series-type
            :daily "Time Series (Daily)"
            :adjusted  "Time Series (Daily)"
            :fx "Time Series FX (Daily)"
            :crypto "Time Series (Digital Currency Daily)")]
    (get-field response f)))

(defn extract-error [response]
  (get response (keyword "Error Message")))

(defn- convert-bars- [symbol series-type response]
  (let [bar-format (case series-type
                     :crypto bar-fields-crypto-
                     :adjusted bar-fields-adjusted-
                     bar-fields-standard-)
        err (extract-error response)]
    (if err
      (do
        (error "get series error " symbol " error message: " err)
        {:error err})
      {:meta (extract-meta response)
       :series (->> response
                    (get-field-series series-type)
                    (seq)
                    (map (partial convert-bar- bar-format series-type))
                    (sort-by :date))})))

(defn get-daily
  "size: compact=last 100 days. full=entire history"
  [size symbol]
  (get-av {:function "TIME_SERIES_DAILY"
           :symbol symbol
           :outputsize (name size)
           :datatype "json"}
          (fn [response]
            ;(println "response: " response)
            ;(println "information: " (:Information response))
            (convert-bars- symbol :daily response))))

(defn get-daily-adjusted
  "size: compact=last 100 days. full=entire history"
  [size symbol]
  (get-av {:function "TIME_SERIES_DAILY_ADJUSTED"
           :symbol symbol
           :outputsize (name size)
           :datatype "json"}
          (fn [response]
            ;(println "response: " response)
             (let [{:keys [meta series] :as result} (convert-bars- symbol :adjusted response)]
              (when (= 0 (count series))
                (warn "no data returned for: " symbol)
                (warn "response: " response))
              result)
            ;(println "data adjusted: " (pr-str response))
            )))


(defn get-daily-fx
  "size: compact=last 100 days. full=entire history"
  [size symbol]
  (get-av {:function "FX_DAILY"
           :from_symbol (subs symbol 0 3)
           :to_symbol (subs symbol 3)
           :outputsize (name size)
           :datatype "json"}
          (fn [response]
            (convert-bars- symbol :fx response))))

(defn get-daily-crypto
  "size: compact=last 100 days. full=entire history"
  [size symbol]
  (get-av {:function "DIGITAL_CURRENCY_DAILY"
           :symbol (subs symbol 0 3) 
           :market (subs symbol 3)
           :outputsize (name size)
           :datatype "json"}
          (fn [response]
            (convert-bars- symbol :crypto response))))

(def kwCryptoRating- (keyword "Crypto Rating (FCAS)"))

(defn get-crypto-rating
  "size: compact=last 100 days. full=entire history"
  [symbol]
  (get-av {:function "CRYPTO_RATING"
           :symbol symbol
           :datatype "json"}
          (fn [response]
            (-> response
                kwCryptoRating-
                fix-keywords-))))

(comment

   ; alphavantage secret has to be set first.
  (require '[clojure.pprint])

  (-> (search "MO")
      (clojure.pprint/print-table))

  (-> (try (get-daily "compact" "MO")
         (catch Exception ex
           (println "ex: " (ex-data ex))
           (println "status: " (:status ex) "reason: " (:reason-phrase ex))
           )  
           )
      
      :series
      first
      :date
      ;(clojure.pprint/print-table)
      )
;
  )
