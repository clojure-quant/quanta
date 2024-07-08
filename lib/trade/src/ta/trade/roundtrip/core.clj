(ns ta.trade.roundtrip.core
  (:require
   [de.otto.nom.core :as nom]
   [taoensso.timbre :as timbre :refer [info warn error]]
   [tech.v3.dataset :as tds]
   [ta.trade.roundtrip.validation :refer [validate-roundtrips-ds]]
   [ta.trade.roundtrip.roundtrip :refer [add-performance]]
   [ta.trade.roundtrip.metrics :refer [calc-roundtrip-metrics]]
   [ta.trade.roundtrip.nav.metrics :refer [calc-nav-metrics]]
   [ta.trade.roundtrip.nav.grouped :refer [grouped-nav]]))

(defn- roundtrip-stats-impl [roundtrip-ds]
  (let [vr (validate-roundtrips-ds roundtrip-ds)]
    (if (nom/anomaly? vr)
      vr
      (let [_ (println "add performance")
            roundtrip-ds (add-performance roundtrip-ds)
            _ (println "calc rt metrics")
            rt-metrics (calc-roundtrip-metrics roundtrip-ds)
            _ (println "calc nav metrics")
            nav-metrics (calc-nav-metrics roundtrip-ds)
            _ (println "calc grouped nav metrics")
            nav-ds (grouped-nav roundtrip-ds)]
        {:roundtrip-ds roundtrip-ds
         :metrics {:roundtrip rt-metrics
                   :nav nav-metrics}
         :nav-ds nav-ds}))))

(defn roundtrip-stats [roundtrip-ds]
  (assert (tds/dataset? roundtrip-ds) "roundtrip stats needs a tml dataset!")
  (try
    (roundtrip-stats-impl roundtrip-ds)
    (catch Exception ex
      (error "roundtrip stat calc exception: " ex)
      (nom/fail ::viz-calc {:message "roundtrip-stats exception!"
                            :location :roundtrip-stats
                            :ex ex ; (ex-data ex)
                            }))))