(ns ta.import.core
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [de.otto.nom.core :as nom]
   [tablecloth.api :as tc]
   [ta.db.bars.protocol :refer [bardb] :as b]))

(defn- get-provider
  "gets the get-series fn for the specified provider
   provider can be a keyword (so a fixed provider)
   provider can also be (fn [s]) to get-series depending on the symbol passed"
  [p asset]
  (if (fn? p)
    (p asset)
    p))

(defn- get-provider-fn
  "returns the get-series fn for the specified provider
   provider can be a keyword (so a fixed provider)
   provider can also be (fn [s]) to get-series depending on the symbol passed"
  [dict-provider p asset]
  (let [p (get-provider p asset)
        r (get dict-provider p)
        ]
    (if r 
      r 
      (nom/fail ::get-importer
                {:message (str "import provider [" p "] not found!")}))))

(defn- get-bars-impl
  "downloads timeseries from provider"
  [dict-provider {:keys [asset import] :as asset-opts} range]
  (when import
    (nom/let-nom> [get-series (get-provider-fn dict-provider import asset)
                   series-ds (get-series asset-opts range)
                   series-ds (tc/add-columns series-ds {:asset asset :epoch 0 :ticks 0})] 
       series-ds)))

(defrecord import-manager [feeds]
  bardb
  (get-bars [this opts window]
    (get-bars-impl (:feeds this) opts window))
  (append-bars [this opts ds-bars]
    (error "import-manager does not support appending bars!")))

(defn start-import-manager [feeds]
  (import-manager. feeds))

(comment

  ; see notebook.playground.import.bars for how to use get-series

;  
  )



