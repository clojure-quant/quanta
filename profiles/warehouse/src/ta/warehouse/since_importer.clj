(ns ta.warehouse.since-importer
  (:require
   [taoensso.timbre :refer [trace debug info infof warn error]]
   [tick.alpha.api :as t] ; tick uses cljc.java-time
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tablecloth]
   [ta.data.date :as d]
   [ta.warehouse :as wh]
   [ta.dataset.helper :as h]
   [ta.data.helper :as data-helper]))

; init symbols - download complete timeseries once and save to disk.

(defn init-symbol [w fn-get-history-since-as-ds
                   frequency since symbol]
  (let [ds (fn-get-history-since-as-ds frequency since symbol)]
    (info "imported " symbol " - " (tablecloth/row-count ds) "bars.")
    ;(println (pr-str d))
    (wh/save-symbol w ds frequency symbol)))

(defn init-symbols [w fn-get-history-since-as-ds frequency since symbols]
  (doall (map
          (partial init-symbol w fn-get-history-since-as-ds frequency since)
          symbols)))

; append symbol - add missing bars at the end.

(defn append-symbol [w fn-get-history-since-as-ds frequency symbol]
  (let [ds-old (wh/load-symbol w frequency symbol)
        last-row (h/last-row ds-old)
        last-date (:date last-row)]
    (if last-date
      (let [s-new (fn-get-history-since-as-ds frequency last-date symbol)
            s-new-no-duplicate (data-helper/remove-first-bar-if-timestamp-equals s-new last-date)
            ds-new (tds/->>dataset s-new-no-duplicate)
            count-new (tablecloth/row-count ds-new)]
        (if (> count-new 0)
          (let [ds-combined (tablecloth/concat ds-old ds-new)]
            (info "adding " count-new "bars to " symbol frequency "since:" last-date " total: " (tablecloth/row-count ds-combined))
            (wh/save-symbol w ds-combined frequency symbol))
          (warn "no new bars for " symbol frequency "since" last-date)))
      (error "no existing series for " symbol frequency  "SKIPPING APPEND."))))

(defn append-symbols [w fn-get-history-since-as-ds frequency symbols]
  (doall (map
          (partial append-symbol w fn-get-history-since-as-ds frequency)
          symbols)))