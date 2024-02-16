(ns ta.import.provider.quandl.raw
  (:require
   [clojure.string :as str]
   [clojure.set]
   [taoensso.timbre :refer [info warn error]]
   [charred.api :as charred]
   [clojure.edn :as edn]
   [clj-http.client :as http]
   [cheshire.core :as cheshire] ; JSON Encoding
   [throttler.core]
   [ta.helper.date :refer [parse-date]]
   [ta.data.helper :refer [str->float]]))

; quandl has been purchased by nasdaq
; https://docs.data.nasdaq.com/v1.0/docs/getting-started
; https://data.nasdaq.com/search

; continuous futures
; https://static.quandl.com/Ticker+CSV%27s/Futures/continuous.csv


; university of michigan - consumer sentiment
;https://data.nasdaq.com/data/UMICH/SOC35-university-of-michigan-consumer-surveybuying-conditions-for-large-household-goods

;; merrill lynch yield data
; https://data.nasdaq.com/data/ML-corporate-bond-yield-rates

; inflation data
; https://data.nasdaq.com/data/RATEINF-inflation-rates

; london buillion market
; https://data.nasdaq.com/data/LBMA-london-bullion-market-association

(defn- space->dash [s]
   (str/replace s #" " "-"))

(defn load-cfutures-csv [filename]
(let [[header & data] 
      (charred/read-csv (java.io.File. filename))
      header (map (comp keyword space->dash) header)
      ->map (fn [cols]  
              (->> (interleave header cols) 
                   (partition 2)
                   (map #(into [] %))
                   (into [])
                   (into {})))]
  (map ->map data)))

(defn convert-instrument-spec [quandl-format]
  (-> 
    (clojure.set/rename-keys quandl-format 
                             {:Name :name
                              :Ticker :symbol})
    (assoc :category :cfuture)                         
  ))

(defn write-symbollist []
  (let [data (->> (load-cfutures-csv "../resources/cfutures.csv")
                  (map convert-instrument-spec)
                  (into []))]
    (spit "../resources/symbollist/futures-quandl.edn" data)))

(comment
  (write-symbollist)
  ;
  )

;; ApiKey Management

(defonce api-key (atom ""))

(defn set-key!
  "to use quandl api, call at least once set-key! api-key"
  [key]
  (info "setting quandl key..")
  (reset! api-key key)
  nil ; Important not to return by chance the key, as this would be shown in the repl.
  )

(defn make-request [url query-params]
  (let [result (-> (http/get url
                             {:accept :json
                              :query-params query-params})
                   (:body)
                   (cheshire/parse-string true)
                   )]
    result
    ;  (throw (ex-info (:retMsg result) result))
    ))

; 4.6 million datasets
#_(make-request "https://www.quandl.com/api/v3/datasets/" 
              {:per_page 5000})



; CHRIS/CME_HG13.xml?api_key=JFT3-kxtkx-SS-o5wSee
; :Quandl-Code "CHRIS/CME_B3"
(def base-url "https://data.nasdaq.com/api/v3/datasets/")

(defn quandl-request-raw [quandl-symbol opts]
  (let [query-params (merge opts {:api_key @api-key})
        url (str base-url quandl-symbol ".json")
        _ (info "quandl-request url: " url " opts: " opts)
        result (make-request url query-params)]
    ; on invaid request: exception
    ; {:dataset
    ;   {:description {:column_names ["Date" "Previous Settlement"],}
    ;    :data [["2021-06-29" 302.0] ...]
    result
    ))

(defn quandl-request [quandl-symbol opts]
  (let [result (quandl-request-raw quandl-symbol opts)]
    {:data (get-in result [:dataset :data])
     :columns (get-in result [:dataset :column_names])}))

(defn quandl-metadata [quandl-symbol opts]
   (let [result (quandl-request-raw quandl-symbol opts)]
     (-> (get-in result [:dataset])
         (dissoc :data))))

; single date: end_date=2021-06-29
; date range: start_date=2021-06-25 end_date=2021-06-29
; all data: no query params

; Continuous Future Contract Nomenclature
; The format for continuous contracts is CHRIS/{EXCHANGE}_{CODE}{NUMBER}, 
; where {NUMBER} is the "depth" associated with the chained contract. 
; For instance, the front month contract has depth 1, 
; the second month contract has depth 2, and so on.

; Federal Reserve Economic Data – 
; This dataset includes things such as 
; growth, employment, inflation, labor, manufacturing and many more US economic data.


(comment 
   (quandl-request "CHRIS/CME_BB_1" {})
  
  ; https://data.nasdaq.com/api/v3/datasets/CHRIS/ASX_WM1.xml 
  (quandl-request "CHRIS/ASX_WM1" {})
  
  (quandl-request "CHRIS/EUREX_FDAX1" {})
  
  ; bad request - invalid symbol
  (quandl-request "CHRIS/AS" {})

  ; fdax:
  ; :columns ["Date" "Open" "High" "Low" "Settle" "Volume" "Prev. Day Open Interest"
  ;
  )

(def quandl-symbol-dict (atom {}))

(defn load-db []
  (->> (slurp "../resources/symbollist/futures-quandl.edn")
      (edn/read-string)
      (map (fn [row]
             [(:symbol row) (:Quandl-Code row)]))
       (into {})
       (reset! quandl-symbol-dict)
      ))

(load-db)

(defn symbol->quandl [s]
  (get @quandl-symbol-dict s))


(defn cfuture-request [symbol opts]
  (let [quandl-symbol (symbol->quandl symbol)
        cfut-symbol (str quandl-symbol "1")]
     (quandl-request cfut-symbol opts)))

(comment 
   (cfuture-request "FDAX" {})  
  
  )

