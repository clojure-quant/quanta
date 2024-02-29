(ns ta.live.bar-generator
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [manifold.stream :as s]
   [tablecloth.api :as tc]
   [tick.core :as t]
   [ta.live.bar-generator.bar :as bar]
   [ta.live.bar-generator.db :as db]
   [ta.live.bar-generator.save-bars :refer [save-finished-bars]]))

(defn process-quote [state {:keys [feed asset] :as quote}]
  (when-let [bar (db/get-bar (:db state) asset)]
    (bar/aggregate-tick bar quote)))

(defn create-bar-generator [quote-stream bar-db]
  (assert quote-stream "bar-generator needs quote-stream")
  (assert bar-db "bar-generator needs bar-db")
  (let [state {:db (db/create-db)
               :bar-db bar-db}]
    (s/consume (partial process-quote state) quote-stream)
    state))

(defn finish-bar [state {:keys [calendar time]}]
  (let [bars (db/get-bars-calendar calendar)
        bars-with-data (remove bar/empty-bar? bars)
        time (t/instant time)
        bar-seq (->> bars-with-data
                     (map #(assoc % :date time)))
        bar-ds (tc/dataset bar-seq)]
    (save-finished-bars (:bar-db state) calendar bar-ds)))


(defn start-generating-bars-for [state bar-asset]
  (db/create-bar! (:db state) bar-asset))
