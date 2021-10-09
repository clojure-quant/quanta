(ns demo.warehouse.import-bybit
  (:require
   [taoensso.timbre :refer [trace debug info infof warn error]]
   [tick.alpha.api :as t] ; tick uses cljc.java-time
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tablecloth]
   [ta.data.alphavantage :as av]
   [ta.data.date :as d]
   [ta.warehouse :as wh]
   [ta.data.bybit :as bybit]
   [ta.dataset.helper :as h]
   [demo.env.warehouse :refer [w]]))

(defn get-history-ds [frequency since symbol]
  (-> (bybit/get-history frequency since symbol)
      (tds/->dataset)))

(defn make-filename [frequency symbol]
  (str symbol "-" frequency))

; init symbols - download complete timeseries once and save to disk.

(defn init-symbol [frequency since symbol]
  (let [ds (get-history-ds frequency since symbol)]
    (info "imported " symbol " - " (count ds) "bars.")
    ;(println (pr-str d))
    (wh/save-ts w ds (make-filename frequency symbol))))

(defn init-symbols [symbols interval since]
  (doall (map
          (partial init-symbol interval since)
          symbols)))

; append symbol - add missing bars at the end.

(defn append-symbol [frequency symbol]
  (let [ds-old (wh/load-ts w (make-filename frequency symbol))
        last-row (h/last-row ds-old)
        last-date (:date last-row)]
    (if last-date
      (let [s-new (bybit/get-history frequency last-date symbol)
            s-new-no-duplicate (bybit/remove-first-bar-if-timestamp-equals s-new last-date)
            ds-new (tds/->>dataset s-new-no-duplicate)
            count-new (tablecloth/row-count ds-new)]
        (if (> count-new 0)
          (let [ds-combined (tablecloth/concat ds-old ds-new)]
            (info "adding " count-new "bars to " symbol frequency "since:" last-date " total: " (tablecloth/row-count ds-combined))
            (wh/save-ts w ds-combined (make-filename frequency symbol)))
          (warn "no new bars for " symbol frequency "since" last-date)))
      (error "no existing series for " symbol frequency  "SKIPPING APPEND."))))

;; BYBIT UNIVERSE

(def bybit-symbols ["BTCUSD" "ETHUSD"])

(defn init-all-daily []
  (init-symbols bybit-symbols "D" (t/instant "1999-12-31T00:00:00Z")))

(defn print-symbol [interval symbol]
  (-> (wh/load-ts w (make-filename interval symbol))
      (h/pprint-dataset)))

(defn print-all-daily []
  (doall (map
          (partial print-symbol "D")
          bybit-symbols)))

(defn init-all-15 []
  (init-symbols bybit-symbols "15" (t/instant "2019-12-31T00:00:00Z")))

(defn print-all-15 []
  (doall (map
          (partial print-symbol "15")
          bybit-symbols)))

(defn append-all-15 []
  (doall (map
          (partial append-symbol "15")
          bybit-symbols)))

(defn append-all-daily []
  (doall (map
          (partial append-symbol "D")
          bybit-symbols)))

; ********************************************************************************************+
(comment

  ; init daily - one
  (init-symbol "D" (d/days-ago 20) "ETHUSD")
  (print-symbol "D" "ETHUSD")
  (print-symbol "15" "ETHUSD")
  (print-symbol "15" "BTCUSD")

  ; init daily - all
  (init-all-daily)
  (print-all-daily)

  ; init 15 - all
  (init-all-15)
  (print-all-15)

  (append-symbol "D" "ETHUSD")
  (append-symbol "15" "ETHUSD")
  (append-symbol "15" "BTCUSD")

  (append-all-daily)
  (append-all-15)
;
  )

