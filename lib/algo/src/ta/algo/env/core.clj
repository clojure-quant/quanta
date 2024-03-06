(ns ta.algo.env.core
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [de.otto.nom.core :as nom]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.calendar.core :refer [trailing-window get-bar-window]]
   [ta.db.bars.protocol :as b]
   [ta.db.bars.aligned :as aligned]
   [ta.algo.env.protocol :as algo-env]
   [ta.algo.spec :as s]))

(defn get-bars
  "returns bars for asset/calendar/window"
  [env spec window]
  (let [calendar (s/get-calendar spec)
        asset (s/get-asset spec)
        bar-db (algo-env/get-bar-db env)]
    (assert bar-db "environment does not provide bar-db!")
    (assert asset "cannot get-bars for unknown asset!")
    (assert calendar "cannot get-bars for unknown calendar!")
    (assert window "cannot get-bars for unknown window!")
    (b/get-bars bar-db spec window)))

(defn get-bars-aligned-filled
  "returns bars for asset/calendar/window"
  [env {:keys [asset calendar] :as opts} calendar-seq]
  (let [bar-db (algo-env/get-bar-db env)]
    (assert bar-db "environment does not provide bar-db!")
    (assert asset "cannot get-bars for unknown asset!")
    (assert calendar "cannot get-bars for unknown calendar!")
    (assert calendar-seq "cannot get-bars-aligned for unknown window!")
    (aligned/get-bars-aligned-filled bar-db opts calendar-seq)))

(defn add-bars
  "returns bars for asset/calendar/window"
  [env {:keys [calendar] :as opts} ds-bars]
  (let [bar-db (algo-env/get-bar-db env)]
    (assert bar-db "environment does not provide bar-db!")
    (assert calendar "can not execute add-bars - needs calendar parameter.")
    (assert ds-bars "can not execute add-bars - needs ds-bars parameter.")
    (b/append-bars env opts ds-bars)))

(defn get-calendar-time [env calendar]
  (let [calendar-time (:calendar-time env)]
    (assert calendar-time "environment does not provide calendar-time!")
    (get @calendar-time calendar)))

(defn calendar-seq->window [calendar-seq]
  (let [dend  (first calendar-seq)
        dstart (last calendar-seq)
        dend-instant (t/instant dend)
        dstart-instant (t/instant dstart)]
    {:start dstart-instant
     :end dend-instant}))

(defn get-trailing-bars [env spec bar-close-date]
  ;(info "get-trailing-bars " bar-close-date)
  (let [trailing-n (s/get-trailing-n spec)
        calendar (s/get-calendar spec)
        calendar-seq (trailing-window calendar trailing-n bar-close-date)
        window (calendar-seq->window calendar-seq)]
    (get-bars env spec window)))

(defn get-bars-lower-timeframe [env spec lower-timeframe]
  (let [calendar (s/get-calendar spec)
        market (first calendar)
        calendar-lower [market lower-timeframe]
        asset (s/get-asset spec)
        time (get-calendar-time env calendar)
        window (get-bar-window calendar time)]
    (get-bars env {:asset asset
                   :calendar calendar-lower} window)))

(defn get-multiple-bars [env {:keys [assets] :as opts} cal-seq]
  (let [get-bars (fn [asset]
                   (info "loading: " asset)
                   (-> (get-bars-aligned-filled env (assoc opts :asset asset) cal-seq)
                       (tc/add-column :asset asset)))
        asset-map-seq (map (fn [asset]
                          {:asset asset
                           :bars (get-bars asset)}) assets)
        assets-bad (->> (filter #(nom/anomaly? (:bars %)) asset-map-seq)
                        (map :asset))
        assets-good (->> (remove #(nom/anomaly? (:bars %)) asset-map-seq)
                        (map :asset))
        bars-good (->> (remove #(nom/anomaly? (:bars %)) asset-map-seq)
                        (map :bars))]
      {:bad assets-bad
       :good assets-good
       :bars bars-good}))
  

(defn get-multiple-bars-trailing [env {:keys [calendar assets trailing-n] :as opts} end-dt]
  (let [cal-seq (trailing-window calendar trailing-n end-dt)]
    (get-multiple-bars env opts cal-seq)))

