(ns ta.import.provider.bybit.ds
  (:require
   [taoensso.timbre :refer [info]]
   [de.otto.nom.core :as nom]
   [tick.core :as t] ; tick uses cljc.java-time
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.import.provider.bybit.raw :as bybit]
   [ta.calendar.validate :as cal-type]
   [ta.calendar.core :refer [prior-close]]))

;; RESPONSE CONVERSION

(defn sort-ds [ds]
  (tc/order-by ds [:date] [:asc]))

(defn ensure-date-instant [bar-ds]
  (tds/column-map bar-ds :date #(t/instant %) [:date]))

(defn bybit-result->dataset [result]
  (-> result
      (tds/->dataset)
      (sort-ds) ; bybit returns last date in first row.
      (ensure-date-instant)
      (tc/select-columns [:date :open :high :low :close :volume])))

;; REQUEST CONVERSION

(defn symbol->provider [symbol]
  ; {:keys [category] :as instrument} (db/instrument-details symbol)
  symbol)

(def start-date-bybit (t/instant "2018-11-01T00:00:00Z"))

(def bybit-frequencies
  ; Kline interval. 1,3,5,15,30,60,120,240,360,720,D,M,W
  {:m "1"
   :h "60"
   :d "D"})

(defn bybit-frequency [frequency]
  (get bybit-frequencies frequency))

(defn ->epoch-millisecond [dt]
  (-> dt
      (t/long)
      (* 1000)))

(defn range->parameter [range]
  (assoc range
         :start (->epoch-millisecond (:start range))
         :end (->epoch-millisecond (:end range))))

(defn get-bars-req [{:keys [asset calendar] :as opts} range]
  (info "get-bars-req" opts range)
  (assert asset "bybit get-bars needs :asset")
  (assert asset "bybit get-bars needs range")
  (nom/let-nom>
   [f (if calendar
        (cal-type/interval calendar)
        (nom/fail ::get-bars-req {:message "bybit get-bars needs :calendar"}))
    frequency-bybit (bybit-frequency f)
    frequency-bybit (if frequency-bybit
                      frequency-bybit
                      (nom/fail ::get-bars-req {:message "unsupported bybit frequency!"
                                                :opts opts
                                                :range range}))
    symbol-bybit (symbol->provider asset)
    range-bybit (range->parameter range)]
   (-> (bybit/get-history-request (merge
                                   {:symbol symbol-bybit
                                    :interval frequency-bybit}
                                   range-bybit))
       (bybit-result->dataset))))

;; PAGING REQUESTS

(defn failed? [bar-ds]
  (if bar-ds false true))

(defn more? [start page-size bar-ds]
  ;(info "more start: " start " page-size " page-size "bars: " bar-ds)
  (cond
    (failed? bar-ds) false

    (and (= page-size (tc/row-count bar-ds))
         (t/> (-> bar-ds tc/first :date first)
              start))
    true

    :else
    false))

(defn next-request
  "returns the parameters for the next request.
   returns nil if last result is an anomaly, or
   if no more requests are needed."
  [calendar range bar-ds]
  (info "next-request range: " range)
  (when-not (nom/anomaly? bar-ds)
    (let [earliest-received-dt (-> bar-ds tc/first :date first)
          [calendar-kw interval-kw] calendar
          end (prior-close calendar-kw interval-kw earliest-received-dt)
          end-instant (t/instant end)
          {:keys [start limit]} range]
      (when (more? start limit bar-ds)
        (assoc range :end end-instant)))))


(defn all-ds-valid [datasets]
  (let [or-fn (fn [a b] (or a b))]
    (->> (map nom/anomaly? datasets)
         (reduce or-fn false)
         not)))

(defn consolidate-datasets [opts range datasets]
  (if (all-ds-valid datasets)
    (->> datasets
         (apply tc/concat)
         (sort-ds))
    (nom/fail ::consolidate-datasets {:message "paged request failed!"
                                      :opts opts
                                      :range range})))


(defn get-bars [{:keys [asset calendar] :as opts} {:keys [start end] :as range}]
  (info "get-bars: " opts range)
  (let [page-size 1000 ; 200
        ; dates need to be instant, because only instant can be converted to unix-epoch-ms
        start (if (t/instant? start) start (t/instant start))
        end (if (t/instant? end) end (t/instant end))
        range (assoc range :limit page-size :start start :end end)]
    (info "start: " start)
    (->> (iteration (fn [range]
                      (info "new page req: " range)
                      (get-bars-req opts range))
                    :initk range
                    :kf  (partial next-request calendar range))
         (consolidate-datasets opts range))))




(comment
  (bybit-frequency :d)
  (bybit-frequency :h)
  (bybit-frequency :m)
  (bybit-frequency :s)

  (def ds (tc/dataset [{:date (t/date-time)}
                       {:date (t/date-time)}]))

  (ensure-date-instant ds)

  (-> (t/instant) ->epoch-millisecond)

  (get-bars {:asset "BTCUSDT"
             :calendar [:crypto :d]}
            {:start (t/date-time "2024-02-26T00:00:00")})

; Execution error (DateTimeParseException) at java.time.format.DateTimeFormatter/parseResolved0 (DateTimeFormatter.java:2106).
; Text '2024-02-29T00:05:00' could not be parsed at index 19

  (-> (get-bars-req
       {:asset "BTCUSDT"
        :calendar [:crypto :m]}
       {:start (-> "2024-02-29T00:00:00Z" t/instant)
        :end (-> "2024-02-29T00:05:00Z" t/instant)})
      ;(ensure-date-instant)

      (tc/last)
      :date
      first

      ;count
      )

  (all-ds-valid [1 2 3 4 5])
  (all-ds-valid [1 2 3 (nom/fail ::asdf {}) 4 5])

  (get-bars
   {:asset "BTCUSDT"
    :calendar [:crypto :m]}
   {:start (-> "2024-02-29T00:00:00Z" t/instant)
    :end (-> "2024-02-29T00:07:00Z" t/instant)})

  
   (get-bars
   {:asset "BTCUSDT"
    :calendar [:crypto :m]}
   {:start (-> "2020-02-29T00:00:00Z" t/instant)
    :end (-> "2024-02-29T00:07:00Z" t/instant)})
  

; 
  )