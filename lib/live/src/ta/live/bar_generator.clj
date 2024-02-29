(ns ta.live.bar-generator
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [manifold.stream :as s]
   [tablecloth.api :as tc]
   [tick.core :as t]
   [ta.env.live.quote-manager :as qm]
   [ta.live.bar-generator.bar :as bar]
   [ta.live.bar-generator.db :as db]
   [ta.live.bar-generator.save-bars :refer [save-finished-bars]]))

(defn process-quote [state {:keys [feed asset] :as quote}]
  ;(info "process tick...")
  (let [bar (or (db/get-bar (:db state) asset)
                (db/create-bar! (:db state) asset))
        bar-new (bar/aggregate-tick bar quote)]
    ;(info "bar: " bar)
    ;(info "bar-new: " bar-new)
    (bar/aggregate-tick bar quote)
    bar-new))

(defn create-bar-generator [{:keys [feeds bar-db]}]
  (assert feeds "bar-generator needs :feeds")
  (assert bar-db "bar-generator needs :bar-db")
  (let [q (qm/create-quote-manager feeds)
        state {:db (db/create-db)
               :bar-db bar-db
               :quote-manager q}
        quote-stream (qm/get-quote-stream q)]
    (s/consume (partial process-quote state) quote-stream)
    state))

(defn finish-bar state [state {:keys [calendar time]}]
  (let [bars (db/get-bars-calendar calendar)
        bars-with-data (remove bar/empty-bar? bars)
        time (t/instant time)
        bar-seq (->> bars-with-data
                     (map #(assoc % :date time)))
        bar-ds (tc/dataset bar-seq)]
    (save-finished-bars (:bar-db state) calendar bar-ds)))




(defn add-algo [state {:keys [feed calendar asset]}]
  (if (and feed calendar asset)
    (let [])
    (warn "not adding algo. needs: :feed :calendar :asset")))

