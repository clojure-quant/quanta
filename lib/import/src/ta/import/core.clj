(ns ta.import.core
  (:require
   [tablecloth.api :as tc]
    ; import providers
   [ta.import.provider.kibot.ds :as kibot]
   [ta.import.provider.kibot-http.ds :as kibot-http]
   [ta.import.provider.alphavantage.ds :as av]
   [ta.import.provider.bybit.ds :as bybit]))

(def dict-provider
  {:kibot kibot/get-bars
   :kibot-http kibot-http/get-bars
   :alphavantage av/get-bars
   :bybit bybit/get-bars})

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

(defn get-bars
  "downloads timeseries from provider"
  [{:keys [asset import] :as asset-opts} range]
  (when import
    (let [get-series (get-provider-fn import asset)
          _ (assert get-series (str "import-provider not found: [" import "]"))
          series-ds (get-series asset-opts range)]
      (tc/add-columns series-ds {:asset asset :epoch 0 :ticks 0}))))

(comment

  (get-provider-fn :kibot "")

  ; see notebook.playground.import.bars for how to use get-series

;  
  )



