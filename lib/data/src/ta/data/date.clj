(ns ta.data.date
  (:require
   [tick.alpha.api :as t]
   [cljc.java-time.instant :as ti]
   [cljc.java-time.local-date :as ld]
   ))

(t/date "2000-01-01")
(defn dt-now []
  (t/date (t/now)))

(def d (dt-now))
d
(type d) ; java.time.LocalDate

(type (t/now)) ; java.time.Instant

(ld/to-epoch-day d) ; 18797
(ld/of-epoch-day 18797)

;; instant

(-> (ti/now) ti/get-epoch-second) ; 1624140369
(ti/of-epoch-second 1624140369 )

(-> (ti/now) (ti/to-epoch-milli)) ; 1624141864204 
(ti/of-epoch-milli 1624141864204)