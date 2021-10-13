;;; # Trateg Library for backtesting

(ns demo.studies.rsi
  (:require [clojure.pprint :refer :all]
            ;[trateg.csv :refer [load-csv-bars]]
            [medley.core :as m]
            ;[trateg.core :refer :all]
            ;[trateg.chart :refer :all]
            ;[trateg.indicator :as ind]
            ;[trateg.ta4j :as ta4j]
            :reload-all))

; show bar data
;(def spx-bars  (load-csv-bars "trateg/spx.csv"))
{:first-bar  (first spx-bars)
 :bar-count (count spx-bars)}
;(def spx-bars (take 100 spx-bars))
;(count spx-bars)

(def cached-rsi (memoize ind/rsi))
(def cached-atr (memoize ind/atr))

;;We will augment our OHLC data with an RSI and ATR. Note that we use the same
;;RSI and ATR periods for all strategies in this file, but differen parameters
;;could easily be explored
(defn mkbars [bar-maps {:keys [rsi-period atr-period]}]
  (let [bar-cols   (row-maps->column-map bar-maps)
        indicators {:atr (cached-atr atr-period
                                     (:high bar-cols)
                                     (:low bar-cols)
                                     (:close bar-cols))
                    :rsi (cached-rsi rsi-period (:close bar-cols))}]
    (doall (column-map->row-maps (merge bar-cols indicators)))))

(def BARS (mkbars spx-bars {:rsi-period 14 :atr-period 20}))

; show bars that have been aumented with :rs and :atr
(nth BARS 10)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Static holding period strategy;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;First let's try a simple long/short RSI strategy that exits after n days

(defn rsi-hold-for-strat [n]
  (fn [ctx]
    (-> ctx
        (cross-trigger :under :rsi 30 :enter :long)
        (cross-trigger :over :rsi 70 :enter :short)
        (hold-for-exit n)
        enforce-single-position
        enforce-no-entry-on-exit-bar
        check-exits
        record-exit-orders
        execute-pending)))

; now lets print some stats about all the results
(def results1 (into (sorted-map)
                    (for [n (concat (range 1 10) (range 10 21 5))]
                      [n (run-strat BARS (rsi-hold-for-strat n))])))

; Now let's check print a bunch of stats on these results
(do (println "Results for holding-period exits:")
    (->> results1
         (map (fn [[n result]]
                (-> (gauntlet result)
                    (assoc :n n))))
         (print-result-table :n)))

;;let's check out a chart of the performance for the 20 day holding period
(performance-chart (get results1 20))

;;we can also see trades on a trade-chart. Longs are blue shorts are red, the
;;darker shade is a winning trade, while the ligher shade is a loser.
(second (trade-chart (get results1 20) :rsi))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;ATR based stops;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;next we'll define a strategy that sets an initial stop loss and profit target
;;that is a multiple of the Average true range at the bar of entry

(defn rsi-atr-exit-strat [stop-mult tp-mult]
  (fn [ctx]
    (-> ctx
        (cross-trigger :under :rsi 30 :enter :long)
        (cross-trigger :over :rsi 70 :enter :short)

        (initial-stop
         (fn [ctx]
           (let [{:keys [atr]} (:current-bar ctx)]
             (when-let [{:keys [entry-price side]} (:pending-entry ctx)]
               (side-add side entry-price  (- (* stop-mult atr)))))))
        (initial-tp
         (fn [ctx]
           (let [{:keys [atr]} (:current-bar ctx)]
             (when-let [{:keys [entry-price side]} (:pending-entry ctx)]
               (side-add side entry-price  (* tp-mult atr))))))

        enforce-single-position
        enforce-no-entry-on-exit-bar
        check-exits
        record-exit-orders
        execute-pending)))

(def results2 (into (sorted-map)
                    (for [stop-mult (range 1 5)
                          tp-mult (range 1 5)]
                      [[stop-mult tp-mult]
                       (run-strat BARS (rsi-atr-exit-strat stop-mult tp-mult))])))

(do (println "Results for ATR multiple stops:")
    (->> results2
         (map (fn [[[stop-mult tp-mult] result]]
                (-> (gauntlet result)
                    (assoc :exits
                           [stop-mult tp-mult]))))
         (print-result-table :exits)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Trailing stop;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;finally we will begin working on a trailing stop
;;it will be somewhat simplistic, as it currently will only use a
;;nominal distance from the entry, which will move up along with price

(defn trail-stop [ctx distance]
  (let [{:keys [current-bar position pending-entry]} ctx
        {:keys [high low]}                           current-bar
        {:keys [entry-price side stop]}              position
        pos-key                                      (cond
                                                       position      :position
                                                       pending-entry :pending-entry
                                                       :else         nil)]

    ;;long only first
    (cond-> ctx
      (and pos-key (= side :long)
           (or (nil? stop)
               (> (- high distance) stop)))
      (assoc-in [pos-key :stop] (- high distance))

      (and pos-key (= side :short)
           (or (nil? stop)
               (< (+ low distance) stop)))
      (assoc-in [pos-key :stop] (+ low distance)))))

(defn rsi-trailing-stop-strat [distance]
  (fn [ctx]
    (-> ctx
        (cross-trigger :under :rsi 30 :enter :long)
        (cross-trigger :over :rsi 70 :enter :short)
        (trail-stop distance)
        enforce-single-position
        enforce-no-entry-on-exit-bar
        check-exits
        record-exit-orders
        execute-pending)))

;;now lets print some stats about all the results
(def results3 (into (sorted-map)
                    (for [n (range 5 105 5)]
                      [n (run-strat BARS (rsi-trailing-stop-strat n))])))

;;Now let's check print a bunch of stats on these results
(do (println "Results for trailing stops")
    (->> results3
         (map (fn [[n result]]
                (-> (gauntlet result)
                    (assoc :stop n))))
         (print-result-table :stop)))

;;let's check out a chart of the performance for the 20 day holding period
(performance-chart (get results3 20))

(trade-chart (get results3 100) :rsi)

