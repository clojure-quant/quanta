(ns ta.env.bars.import
  (:require 
    [tick.core :as t]
    [tablecloth.api :as tc]
    [ta.import.core :as i]))

(defn- import-tasks-map [req-window db-window]
  {:db-empty (when (not db-window)
                 {:start (:start req-window) 
                  :end (:end req-window)
                  :db {:start (:start req-window) 
                       :end (:end req-window)}})
 :missing-prior (when  (t/> (:start db-window) (:start req-window))
   {:start (:start req-window) 
    :end (:start db-window)
    :db {:start (:start req-window)}})
  :missing-after (when (t/< (:end db-window) (:end req-window))
   {:start (:end db-window) 
    :end (:end req-window)
    :db {:end (:end req-window)}})})

(defn import-tasks [req-window db-window]
  (->> (import-tasks-map req-window db-window)
          (vals)
          (remove nil?)))

(defn import-needed? [tasks]
  (not (empty? tasks)))



(defn import-task [opts task]
  (let [ds-bars (i/get-bars opts task)]
    (db/append-bars ds-bars)
    (set-overview task)))

(defn import-tasks [opts tasks]
   (doall (map #(import-task opts %) tasks)))

(defn import-on-demand [{:keys [asset calendar] :as opts} req-window]
  (let [db-window (db/available-range asset calendar)
        tasks (import-tasks req-window db-window)]
    (when (import-needed? tasks)
      (run-import tasks))))

; ns overview-db 

(defn start [path]
   (Datahike/connect path))

(defn available-range [asset calendar]
)

(defn update-range [asset calendar range]
)



; ta.env.bars.db




(defn get-bars [env opts range]
  (let [get-bars-db (:get-bars-db env)
        get-bars-import import/get-bars
        bars-ds-db (get-bars-db opts range)
        range-db {:start  (-> bars-ds-db tc/first :date)
                  :end (-> bars-ds-db tc/last :date)}]
    
    
    )
  
  
  )



 
(import/get-bars asset-opts range)