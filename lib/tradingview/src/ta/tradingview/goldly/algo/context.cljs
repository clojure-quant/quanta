(ns ta.tradingview.goldly.algo.context
  (:require
   [reagent.core :as r]))

(defn create-algo-context [algo opts]
  (let [data (r/atom nil)
        input (r/atom {:algo algo
                       :opts opts})
        context {:input input
                 :data data}]
    context))

(defn get-algo-input [algo-ctx]
  (-> algo-ctx :input deref))

(defn get-algo-name [algo-ctx]
  (-> algo-ctx :input deref :algo))

(defn get-algo-input-atom [algo-ctx]
  (-> algo-ctx :input))

(defn get-data [algo-ctx]
  (-> algo-ctx :data deref))

(defn get-chart-spec [algo-ctx]
  (-> algo-ctx :data deref :charts))

(defn get-pane-columns [algo-ctx pane-id]
  (let [chart-spec (get-chart-spec algo-ctx)
        pane-spec (get chart-spec pane-id)]
    (keys pane-spec)))

(defn get-chart-series [algo-ctx]
  (-> algo-ctx :data deref :ds-study))

(defn get-chart-series-window [algo-ctx period]
  (let [{:keys [from to first? count-back]} period
        series (get-chart-series algo-ctx)
        in-window? (fn [{:keys [epoch]}]
                     (and (>= epoch from) (<= epoch to)))
        prior-to? (fn [{:keys [epoch]}]
                    (<= epoch to))]
    (if first? ; perhaps change this to count-back.
      (take-last count-back series)
      (if count-back
        (->> (filter prior-to? series)
             (take-last count-back))
        (filter in-window? series)))))

(defn epoch= [epoch row]
  (let [epoch-row (-> row :epoch (* 1000))]
    ;(println "epoch-target:" epoch "epoch-row: " epoch-row)
    (= epoch epoch-row)))

(defn find-row [series epoch]
  (-> (filter #(epoch= epoch %) series) first))

(defn get-pane-data [algo-ctx pane-id epoch]
  (let [series (get-chart-series algo-ctx)
        cols (get-pane-columns algo-ctx pane-id)
        row (find-row series epoch)]
    (when-not row
      (println "no row found for epoch: " epoch))
    (if (empty? cols)
      []
      (let [get-data (apply juxt cols)]
        (get-data row)))))

(defn set-algo-data [algo-ctx data]
  (reset! (:data algo-ctx) data))

; cache reset is needed because on algo change tradingview would
; otherwise not forget the loaded bar data. The "signal" gets
; picked up in the barSubscription

(def need-cache-reset-atom (atom false))

(defn set-cache-needed []
  (reset! need-cache-reset-atom true))

