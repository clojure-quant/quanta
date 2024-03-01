(ns ta.live.bar-generator.save-bars
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [clojure.pprint :refer [print-table]]
   [tablecloth.api :as tc]
   [ta.db.bars.protocol :as b]))

(defn select-valid-bars-ds [ds-bars]
  (when ds-bars
    (tc/select-rows ds-bars  #(:close %))))

(defn ds-has-rows [ds-bars]
  (if ds-bars
    (let [c (tc/row-count ds-bars)]
      (> c 0))
    false))

(defn print-finished-bars [calendar ds-bars]
  (let [bars (tc/rows ds-bars :as-maps)]
    (println "bars finished category: " calendar)
    (print-table bars)))

(defn save-finished-bars [bardb calendar bar-ds]
  (try
    (info "save finished bars " calendar " ... ")
    (let [ds-bars (select-valid-bars-ds bar-ds)]
      (if (ds-has-rows ds-bars)
        (do (info "bar-generator finish: calendar: " calendar   "# bars: " (tc/row-count bar-ds))
            (b/append-bars bardb {:calendar calendar} bar-ds))
        (warn "not saving finished bars - ds-bars is not valid!")))
    (catch Exception ex
      (error "generated bars save exception!")
      (print-finished-bars calendar bar-ds))))

(comment
  (def ds
    (-> {:close [10.0 20.0 nil]
         :asset ["MSFT" "QQQ" "SPX"]}
        tc/dataset))
  ds
  (select-valid-bars-ds ds)
  (select-valid-bars-ds nil)

  (ds-has-rows ds)
  (ds-has-rows nil)
;
  )
