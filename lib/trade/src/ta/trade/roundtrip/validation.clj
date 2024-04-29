(ns ta.trade.roundtrip.validation
  (:require
   [de.otto.nom.core :as nom]
   [tick.core :as t]
   [malli.core :as m]
   [malli.registry :as mr]
   [malli.error :as me]
   [malli.experimental.time :as time])
  (:import
   (java.time Duration Period LocalDate LocalDateTime LocalTime Instant
              ZonedDateTime OffsetDateTime ZoneId OffsetTime)))

(def r
  (mr/composite-registry
   m/default-registry
   (mr/registry (time/schemas))))

(def above-zero 0.0000000000000001)

(def Roundtrip
  [:map
   [:asset :string]
   [:side [:enum :long :short]]
   [:qty [:double]]
   [:entry-price [:double {:min ta.trade.roundtrip.validation/above-zero}]]
   [:exit-price [:double {:min ta.trade.roundtrip.validation/above-zero}]]
   [:entry-date [:or :time/local-date :time/local-date-time :time/zoned-date-time :time/instant]]
   [:exit-date  [:or :time/local-date :time/local-date-time :time/zoned-date-time :time/instant]]
   [:entry-idx {:optional true} [:int]]
   [:exit-idx {:optional true} [:int]]])

(defn validate-roundtrip [rt]
  (m/validate Roundtrip rt {:registry r}))

(defn human-error-roundtrip [rt]
  (->> (m/explain Roundtrip rt {:registry r})
       (me/humanize)))

(defn validate-roundtrips [rts]
  (loop [rt (first rts)
         rts (rest rts)]
    ;(println "validationg rt: " rt)
    (if (validate-roundtrip rt)
      (if (empty? rts)
        true
        (recur (first rts) (rest rts)))
      (do
        (println "rt validation failed: rt:" rt "error: " (human-error-roundtrip rt))
        (nom/fail ::roundtrip-validation-errror {:message (human-error-roundtrip rt)})))))

(comment

  (validate-roundtrip {:asset "QQQ" :side :long
                       :entry-price 105.0
                       :exit-price 110.0
                       :entry-idx 15
                       :exit-date (t/zoned-date-time)
                       :entry-date (t/date)})

  (validate-roundtrip {:asset "QQQ" :side :long
                       :entry-price 105.0
                       :exit-price 110.0
                       :entry-idx 15
                       :exit-date (t/zoned-date-time)
                       :entry-date (t/instant)})

  (human-error-roundtrip
   {:asset "QQQ" :side :long
    :entry-price 105.0
    :exit-price 110.0
    :entry-date (t/instant)})

  (human-error-roundtrip
   {:asset "QQQ" :side :long
    :entry-price 105.0
    :exit-price 110.0
    :entry-date (t/instant)
    :exit-date 34})

  (human-error-roundtrip
   {:asset "QQQ" :side :long
    :entry-price 105.0
    :exit-price 110.0
    :entry-idx "asdf"})
  ;; => {:entry-idx ["should be an integer"] 
  ;;     :exit-date ["missing required key"] 
  ;;     :entry-date ["missing required key"]}

  (validate-roundtrips
   [{:asset "QQQ" :side :long
     :entry-price 105.0
     :exit-price 110.0
     :entry-idx 15
     :exit-date (t/zoned-date-time)
     :entry-date (t/date)}
    {:asset "QQQ" :side :long
     :entry-price 105.0
     :exit-price 110.0
     :entry-idx 15
     :exit-date (t/zoned-date-time)
     :entry-date (t/instant)}
    {:asset "QQQ" :side :long
     :entry-price 105.0
     :exit-price 110.0
     :entry-date (t/instant)}])

  (require '[malli.generator :as mg])
  (mg/generate Roundtrip {:registry r})
  ;; => {:asset "sN4tNQbispdW4sNton22fQW6hzs7oU", 
  ;;     :exit-price 19.7421875, 
  ;;     :entry-price 16.25, 
  ;;     :side :long}

  ;; => {:asset "2W0M85zVo14n7Sp95cU0XImGK5SC1",
  ;;     :exit-price 0.00445556640625,
  ;;     :entry-price 0.09006038308143616,
  ;;     :side :long,
  ;;     :entry-idx 1034499}

;; => nil

;  
  )
