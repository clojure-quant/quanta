(ns ta.import.provider.compress
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [de.otto.nom.core :as nom]
   [tablecloth.api :as tc]
   [ta.calendar.validate :as cal]
   [ta.calendar.compress :as compress]
   [ta.db.bars.protocol :refer [bardb] :as b]))


(defn add-date-group [ds interval]
  (case interval 
    :month (compress/add-date-group-month ds)
    :year  (compress/add-date-group-year ds)))

(defrecord compressing-provider [provider interval-config]
  bardb
  (get-bars [this opts window]
    (let [interval (cal/interval (:calendar opts))
          calendar (cal/calendar (:calendar opts))
          generate? (contains? (:interval-config this) interval)]
      (if generate?
        (-> (b/get-bars (:provider this)
                        (assoc opts :calendar
                               [calendar (:interval-config interval)])
                        window)
            (add-date-group interval)
            (compress/compress-ds))
        (b/get-bars (:provider this) opts window))))
  (append-bars [this opts ds-bars]
    (error "compressing-provider does not support appending bars!")))

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

