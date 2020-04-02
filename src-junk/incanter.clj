(ns db.incanter
  (:require
            [clojure.string :as str]

            [clj-time.core :as t]
            [clj-time.format :as fmt]

            [taoensso.tufte :as tufte :refer (defnp p profiled profile)]
            [incanter.io :as i-io]

   ))


(defn load-csv [symbol]
  (let [fn (str "../DAILY/" symbol ".csv")]
    (i-io/read-dataset fn :header true)
    ))


(comment

  ; (use 'incanter.io)

  (def data (load-csv "AAPL US Equity"))

  (println data)

  (tufte/add-basic-println-handler! {})

  (profile {} (p :incanter (do (load-csv "AAPL US Equity") nil )  ))


  (get (load-csv "AAPL US Equity") )

  )



