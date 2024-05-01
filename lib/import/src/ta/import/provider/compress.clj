(ns ta.import.provider.compress
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [de.otto.nom.core :as nom]
   [tablecloth.api :as tc]
   [ta.calendar.validate :as cal]
   [ta.calendar.compress :as compress]
   [ta.db.bars.protocol :refer [barsource] :as b]))

(defn add-date-group [ds interval]
  (case interval
    :month (compress/add-date-group-month ds)
    :year  (compress/add-date-group-year ds)
    :h (compress/add-date-group-hour ds)))

(defrecord compressing-provider [provider interval-config]
  barsource
  (get-bars [this opts window]
    (let [exchange (cal/exchange  (:calendar opts))
          interval (cal/interval (:calendar opts))
          generate? (contains? (:interval-config this) interval)]
      (if generate?
        (let [calendar [exchange (get (:interval-config this) interval)]
              opts (assoc opts :calendar calendar)]
          (warn "compressing bars. opts: " opts)
          (-> (b/get-bars (:provider this) opts window)
              (add-date-group interval)
              (compress/compress-ds)))
        (do
          (warn "forwarding request to import-provider: " opts window)
          (b/get-bars (:provider this) opts window))))))

(defn start-compressing-provider  [provider interval-config]
  (compressing-provider. provider interval-config))

(comment
  (def interval-config
    {:h :m
     :5m :m
     :10m :m
     :30m :m
     :month :d
     :year :d})

  (contains? interval-config :m)
  (contains? interval-config :h)

;  
  )

