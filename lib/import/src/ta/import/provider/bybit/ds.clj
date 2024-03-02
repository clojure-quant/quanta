(ns ta.import.provider.bybit.ds
  (:require
   [taoensso.timbre :refer [info]]
   [tick.core :as t] ; tick uses cljc.java-time
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.import.provider.bybit.raw :as bybit]
   [ta.calendar.core :refer [prior-close]]))

(defn sort-ds [ds]
  (tc/order-by ds [:date] [:asc]))

(defn bybit-result->dataset [response]
  (-> response
      (tds/->dataset)
      (sort-ds) ; bybit returns last date in first row.
      ))
(defn symbol->provider [symbol]
  ; {:keys [category] :as instrument} (db/instrument-details symbol)
  symbol)

(def start-date-bybit (t/date-time "2018-11-01T00:00:00"))

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

(defn ensure-date-instant [bar-ds]
  (tds/column-map bar-ds :date #(t/instant %) [:date]))

(defn get-bars-req [{:keys [asset calendar] :as opts} range]
  (info "get-bars-req" opts range)
  (assert asset "bybit get-bars needs :asset")
  (assert asset "bybit get-bars needs :calendar")
  (assert asset "bybit get-bars needs range")
  (let [f (last calendar)
        frequency-bybit (bybit-frequency f)
        symbol-bybit (symbol->provider asset)
        range-bybit (range->parameter range)]
    (assert frequency-bybit (str "bybit does not support frequency: " f))
    (-> (bybit/get-history-request (merge
                                    {:symbol symbol-bybit
                                     :interval frequency-bybit}
                                    range-bybit))
        (bybit-result->dataset)
        (ensure-date-instant)
        (tc/select-columns [:date :open :high :low :close :volume]))))

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

(defn next-request [calendar range bar-ds]
  (info "next-request range: " range)
  (let [earliest-received-dt (-> bar-ds tc/first :date first)
        [calendar-kw interval-kw] calendar
        end (prior-close calendar-kw interval-kw earliest-received-dt)
        end-instant (t/instant end)
        {:keys [start limit]} range]
    (when (more? start limit bar-ds)
      (assoc range :end end-instant))))


(defn get-bars [{:keys [asset calendar] :as opts} {:keys [start end] :as range}]
  (info "get-bars: " opts range)
  (let [page-size 200
        start (if (t/instant? start) start (t/instant start))
        end (if (t/instant? end) end (t/instant end))
        range (assoc range :limit page-size :start start :end end)]
    (info "start: " start)
    (->> (iteration (fn [range]
                      (info "new page req: " range)
                      (get-bars-req opts range))
               ;(partial get-bars-req opts)
                    :initk range
                    ;:somef (partial more? start page-size)
                    :kf  (partial next-request calendar range))
         ;(into [])
         (apply tc/concat)
         (sort-ds)
         )))




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



  (get-bars
   {:asset "BTCUSDT"
    :calendar [:crypto :m]}
   {:start (-> "2024-02-29T00:00:00Z" t/instant)
    :end (-> "2024-02-29T00:07:00Z" t/instant)})



; 
  )