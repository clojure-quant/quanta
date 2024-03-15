(ns ta.import.core
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [de.otto.nom.core :as nom]
   [tablecloth.api :as tc]
   [ta.db.bars.protocol :refer [bardb barsource] :as b]
   [ta.import.logger :as logger]))

(defn- get-provider
  "returns the get-series fn for the specified provider
   provider can be a keyword (so a fixed provider)
   provider can also be (fn [s]) to get-series depending on the symbol passed"
  [dict-provider p asset]
  (let [r (get dict-provider p)]
    (or r (nom/fail ::get-importer
                    {:message (str "import provider [" p "] not found!")}))))

(defn- get-bars-impl
  "downloads timeseries from provider"
  [dict-provider {:keys [asset import calendar] :as asset-opts} window]
  (when import
    (let [r (nom/let-nom> [p (get-provider dict-provider import asset)
                           series-ds (b/get-bars p asset-opts window)
                           series-ds (tc/add-columns series-ds {:asset asset :epoch 0 :ticks 0})]
                          series-ds)]
      (logger/add import asset calendar window r)
      r)))

(defrecord import-manager [feeds]
  barsource
  (get-bars [this opts window]
    (get-bars-impl (:feeds this) opts window)))

(defn start-import-manager [feeds]
  (import-manager. feeds))

(comment

  ; see notebook.playground.import.bars for how to use get-series

;  
  )



