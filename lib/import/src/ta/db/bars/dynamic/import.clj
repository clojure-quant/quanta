(ns ta.db.bars.dynamic.import
  (:require 
    [taoensso.timbre :as timbre :refer [debug info warn error]]
    [tick.core :as t]
    [ta.import.core :as i]
    [ta.db.bars.protocol :as bardb]
    [ta.db.bars.dynamic.overview-db :as overview]))

(defn- import-tasks-map [req-window db-window]
  {:db-empty (when (not db-window)
                 {:start (:start req-window) 
                  :end (:end req-window)
                  :db {:start (:start req-window) 
                       :end (:end req-window)}})
 :missing-prior (when  (and db-window (t/> (:start db-window) (:start req-window)))
   {:start (:start req-window) 
    :end (:start db-window)
    :db {:start (:start req-window)}})
  :missing-after (when (and db-window (t/< (:end db-window) (:end req-window)))
   {:start (:end db-window) 
    :end (:end req-window)
    :db {:end (:end req-window)}})})

(defn import-tasks [req-window db-window]
  (->> (import-tasks-map req-window db-window)
          (vals)
          (remove nil?)))

(defn import-needed? [tasks]
  (not (empty? tasks)))

(defn run-import-task [state opts task]
  (let [ds-bars (i/get-bars opts task)]
    (info "appending bars: state: " state)
    (info "appending bars: " ds-bars)
    (bardb/append-bars (:bar-db state) opts ds-bars)
    (overview/update-range (:overview-db state) opts task)))

(defn run-import-tasks [state opts tasks]
   (doall (map #(run-import-task state opts %) tasks)))

(defn import-on-demand [state {:keys [asset calendar] :as opts} req-window]
  (info "import-on-demand " opts req-window)
  (let [db-window (overview/available-range state opts)
        tasks (import-tasks  req-window db-window)]
    (info "import tasks: " tasks)
    (when (import-needed? tasks)
      (run-import-tasks state opts tasks))))
