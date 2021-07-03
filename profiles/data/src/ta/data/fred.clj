(ns ta.data.fred
  (:require
   [clj-http.client :as client]
   [cheshire.core] ; JSON Encoding
   [cljc.java-time.local-date :as ld]
   [throttler.core]))

;; ApiKey Management

;Get a FRED API key
; Sign up for a Fred API key: http://api.stlouisfed.org/api_key.html
; https://research.stlouisfed.org/useraccount/apikeys
; https://fred.stlouisfed.org/categories

(def api-key (atom "demo"))

(defn set-key!
  "to use fred api, call at least once set-key! api-key"
  [key]
  (reset! api-key key)
  nil ; Important not to return by chance the key, as this would be shown in the notebook.
  )

;; helper

(defn- fix-keywords-
  "Alphavantage responses are unusually structured.
   field '1. open'  will be converted to :open keyword"
  [m]
  (let [ks (keys m)
        vs (vals m)]
    (zipmap (map #(keyword (second (re-find #"\d+.\s(\w+)" (name %)))) ks) vs)))

(def base-url "https://api.stlouisfed.org/fred/")

(defn sget [service params]
  (-> (client/get (str base-url service)
                  {:accept :json
                   :query-params (assoc params :api_key @api-key
                                               :file_type "json"
                                        )})
      (:body)
      (cheshire.core/parse-string true)
      ))


(defn series-search [q] ; "GDP"
  (sget "series/search" {:search_text q}))


(defn category [id]
  (sget "category" {:category_id id}))

(defn categories [id] ;EXJPUS
  (sget "series/categories" {:series_id id}))

(defn observations [id] ;GNPCA
  (sget "series/observations" {:series_id id}))




#_(def get-av-throttled
  (throttler.core/throttle-fn get-av-raw 5 :minute))

