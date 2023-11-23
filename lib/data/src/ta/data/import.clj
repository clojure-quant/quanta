(ns ta.data.import
   (:require
    [taoensso.timbre :refer [info warn error]]
    [tablecloth.api :as tc]
    [ta.warehouse.symbollist :refer [load-list]]
     ; import
    [ta.warehouse :refer [load-series exists-series?]]
    [ta.data.import.warehouse :refer [save-series]]
    [ta.warehouse.symbol-db :as db]
    [ta.data.import.append :as append]
     ; providers
    [ta.data.api-ds.kibot :as kibot]
    [ta.data.api-ds.alphavantage :as av]
    [ta.data.api-ds.bybit :as bybit]))

(def dict-provider
  {:kibot kibot/get-series
   :alphavantage av/get-series
   :bybit bybit/get-series
   })

(defn get-provider 
  "gets the get-sreies fn for the specified provider
   provider can be a keyword (so a fixed provider)
   provider can also be (fn [s]) to get-series depending on the symbol passed"
  [p symbol]
   (if (fn? p)
       (p symbol)
        p))

(defn get-provider-fn
  "returns the get-series fn for the specified provider
   provider can be a keyword (so a fixed provider)
   provider can also be (fn [s]) to get-series depending on the symbol passed"
  [p symbol]
  (let [p (get-provider p symbol)]
    (get dict-provider p)))

(defn import-series 
  "downloads timeseries from provider and saves it to warehouse
   potentially existing series will be replaced"
  ([provider series-opts range]
    (import-series provider series-opts range {}))
  ([provider series-opts range opts]
    (let [provider-kw (get-provider provider (:symbol series-opts))
          _ (info "import provider: " provider-kw " symbol: " (:symbol series-opts) " range: " range)      
          get-series (get-provider-fn provider (:symbol series-opts))
          series-ds (get-series series-opts range opts)
          c  (tc/row-count series-ds)]
      (info "imported " (:symbol series-opts) " - " c "bars.")
      (when (> c 0)
        (save-series series-opts series-ds)))))

(defn append-series 
   "downloads timeseries from provider and appends it to the existing series in the  warehouse"
  ([provider series-opts]  
   (append-series provider series-opts {}))
  ([provider series-opts opts]
   (let [provider-kw (get-provider provider (:symbol series-opts))
         _ (info "append provider: " provider-kw " symbol: " (:symbol series-opts) " interval: " (:frequency series-opts))
         get-series (get-provider-fn provider (:symbol series-opts))]
     (append/append-series get-series series-opts opts))))

(defn import-one
  [provider series-opts range]
  (cond
    (= range :full)
    (import-series provider series-opts :full)

    (= range :append)
    (append-series provider series-opts)

    :else
    (info "no import task for range: " range)))

(comment 
   ;; import -full
  (import-series :alphavantage {:symbol "MSFT" :frequency "D"} :full)
  (import-series :alphavantage {:symbol "EURUSD":frequency "D"} :full) 
  (import-series :alphavantage {:symbol "BTCUSD" :frequency"D"} :full) 
  (import-series :alphavantage {:symbol "FSDCX" :frequency "D"} :full) 
  (import-series :alphavantage {:symbol "FSDCX" :frequency "D"} :append) 
  
  (import-series :kibot {:symbol "SIL0":frequency "D"} :full)
  (import-series :bybit {:symbol "BTCUSD" :frequency "D"} :full)


  ; append 
  (append-series :alphavantage {:symbol "MSFT" :frequency "D"} )
  (append-series :alphavantage {:symbol "EURUSD" :frequency "D"} )
  (append-series :alphavantage {:symbol "QQQ" :frequency "D"} )
  (append-series :alphavantage {:symbol "FSDCX" :frequency "D"})
  (append-series :alphavantage {:symbol "FMCDX" :frequency "D"})
  (append-series :bybit {:symbol "BTCUSD" :frequency "D"} )
  (append-series :bybit {:symbol "LTCUSD" :frequency "D"} )
  (append-series :kibot {:symbol "SIL0" :frequency "D"})
  (append-series :kibot {:symbol "EURUSD" :frequency "D"} )
  



 ; 
  )

;; LIST 

(defn missing-symbols [frequency]
  (let [all (db/get-symbols)]
    (remove #(exists-series? {:symbol % :frequency frequency}) all)))

(comment
  (-> (db/get-symbols) count)
  (missing-symbols "D")
  (missing-symbols "30")
;  
  )

(defn import-list
   [provider symbols series-opts range]
   (let [symbols (cond 
                   (string? symbols) (load-list symbols)
                   (= symbols :all) (db/get-symbols)
                   (= symbols :missing) (missing-symbols (:frequency series-opts))
                   :else symbols)]
     (doall (map
             #(import-one provider (assoc series-opts :symbol %) range)
             symbols))))


(comment 
 

  ; symbol list
  (import-list :kibot ["SIL0" "NG0" "IBM"] {:frequency "D"}  :full)
  (import-list :kibot "joseph" {:frequency "D"} :full)
  (import-list :kibot "futures-kibot" {:frequency "D"} :full)
  (import-list :bybit "crypto" {:frequency "D"} :full) 


  (import-list :bybit "crypto" {:frequency "D"} :append)
  (import-list :kibot "joseph" {:frequency "D"} :append)
  (import-list :kibot ["SIL0" "NG0" "IBM"] {:frequency "D"} :append)

  (import-list :kibot :all {:frequency "D"} :append)

  (import-list :kibot :missing {:frequency "D"} :full)
  

  (require '[demo.env.provider :refer [instrument-category->provider]])

  (get-provider instrument-category->provider "IBM")
  (get-provider instrument-category->provider "BTCUSD")
  (get-provider instrument-category->provider "EURUSD")

  (import-list instrument-category->provider 
               ["SIL0" "NG0" "IBM" "FSDCX" "FMCDX"
                                           "XME"
                                           "EU0"
                                           "QG0"] 
               {:frequency "D"} 
               :append)
  
  (import-list instrument-category->provider
               ["FSDCX" "FMCDX" "XME" "EU0" "QG0"]
               {:frequency "D"}
               :append)


 (import-list instrument-category->provider
             :all
             {:frequency "D"}
             :append)


  ;
  )
