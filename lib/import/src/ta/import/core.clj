(ns ta.import.core
  (:require 
    ; import providers
    [ta.import.provider.kibot.ds :as kibot]
    [ta.import.provider.alphavantage.ds :as av]
    [ta.import.provider.bybit.ds :as bybit]))

(def dict-provider
  {:kibot kibot/get-series
   :alphavantage av/get-series
   :bybit bybit/get-series})

(defn get-provider
  "gets the get-sreies fn for the specified provider
   provider can be a keyword (so a fixed provider)
   provider can also be (fn [s]) to get-series depending on the symbol passed"
  [p asset]
  (if (fn? p)
    (p asset)
    p))

(defn get-provider-fn
  "returns the get-series fn for the specified provider
   provider can be a keyword (so a fixed provider)
   provider can also be (fn [s]) to get-series depending on the symbol passed"
  [p asset]
  (let [p (get-provider p asset)]
    (get dict-provider p)))

(defn get-series
  "downloads timeseries from provider"
  [{:keys [asset import] :as asset-opts} range]
  (let [get-series (get-provider-fn import asset)
        _ (assert get-series (str "provider not found: " import))
        series-ds (get-series asset-opts range)]
    series-ds))


(comment
  
  (get-provider-fn :kibot "")

  ; see demo.data-import.download how to use get-series
 
;  
)



  