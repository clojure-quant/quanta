(ns ta.db.bars.dynamic.import
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [tick.core :as t]
   [ta.import.core :as i]
   [ta.db.bars.protocol :as bardb]
   [ta.db.bars.dynamic.overview-db :as overview]))

(defn- import-tasks-map [req-window db-window]
  {:db-empty (when (not db-window)
               {:type :db-empty
                :start (:start req-window)
                :end (:end req-window)
                :db {:start (:start req-window)
                     :end (:end req-window)}})
   :missing-prior (when  (and db-window (t/> (:start db-window) (:start req-window)))
                    {:type :missing-prior
                     :start (:start req-window)
                     :end (:start db-window)
                     :db {:start (:start req-window)}})
   :missing-after (when (and db-window (t/< (:end db-window) (:end req-window)))
                    {:type :missing-after
                     :start (:end db-window)
                     :end (:end req-window)
                     :db {:end (:end req-window)}})})

(defn import-tasks [req-window db-window]
  (->> (import-tasks-map req-window db-window)
       (vals)
       (remove nil?)))

(defn import-needed? [tasks]
  (not (empty? tasks)))

(defn get-bars-safe [opts task]
  (try
    (i/get-bars opts task)
    (catch Exception ex
      (error "dynamic-import.get-bars exception! opts: " opts)
      nil)))

(defn append-bars-safe [state opts task bar-ds]
  (try
    (when bar-ds
      (info "dynamically received ds-bars! appending to db...")
      (bardb/append-bars (:bar-db state) opts bar-ds)
      (overview/update-range (:overview-db state) opts (:db task)))
    (catch Exception ex
      (error "dynamic-import.append-bars exception! asset: " (:asset opts) "calendar: " (:calendar opts))
      nil)))

(defn run-import-task [state opts task]
  (let [bar-ds (get-bars-safe opts task)]
    (append-bars-safe state opts task bar-ds)))

(defn run-import-tasks [state opts tasks]
  (doall (map #(run-import-task state opts %) tasks)))

(defn tasks-for-request [state {:keys [asset calendar import] :as opts} req-window]
  (if import
    (let [db-window (overview/available-range (:overview-db state) opts)
          tasks (import-tasks  req-window db-window)]
      tasks)
    (do (warn "no import defined for asset: " asset " calendar: " calendar)
        '())))

(defn import-on-demand [state {:keys [asset calendar] :as opts} req-window]
  (info "import-on-demand " opts req-window)
  (let [tasks (tasks-for-request state opts req-window)]
    (info "tasks: " tasks)
    (when (import-needed? tasks)
      (run-import-tasks state opts tasks))))
