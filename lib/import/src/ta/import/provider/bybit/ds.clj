(ns ta.import.provider.bybit.ds
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer [info]]
   [de.otto.nom.core :as nom]
   [tick.core :as t] ; tick uses cljc.java-time
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.import.provider.bybit.raw :as bybit]
   [ta.calendar.validate :as cal-type]
   [ta.calendar.core :refer [prior-close]]
   [ta.db.bars.protocol :refer [barsource]]))

;; RESPONSE CONVERSION

(defn sort-ds [ds]
  (tc/order-by ds [:date] [:asc]))

(defn bybit-result->dataset [result]
  (-> result
      (tds/->dataset)
      (sort-ds) ; bybit returns last date in first row.
      (tc/select-columns [:date :open :high :low :close :volume])))

;; REQUEST CONVERSION

(defn symbol->provider
  "converts a quanta symbol to a bybit symbol

   spot or inverse:    => symbol equal
   perpetual:          => remove .P from 'symbol.P' pattern
   perpetual (USDC)    => additionally rename USDC to PERP"
  [symbol]
  ; {:keys [category] :as instrument} (db/instrument-details symbol)
  (cond
    ; USDT perp
    (str/ends-with? symbol "USDT.P")
    (str/replace symbol #"\.P$" "")

    ; USDC perp
    (str/ends-with? symbol "USDC.P")
    (str/replace symbol #"USDC\.P$" "PERP")

    :else symbol))

(defn symbol->provider-category
  "converts a quanta symbol to a bybit category"
  [symbol]
  (cond
    (or (str/ends-with? symbol "USDT.P")
        (str/ends-with? symbol "USDC.P"))
    "linear"

    (re-find #"USD$|USD[A-Z0-9]{1,2}\d\d$" symbol)
    "inverse"

    :else
    "spot"))

(def start-date-bybit (t/instant "2018-11-01T00:00:00Z"))

(def bybit-frequencies
  ; Kline interval. 1,3,5,15,30,60,120,240,360,720,D,M,W
  {:m "1"
   :h "60"
   :d "D"})

(defn bybit-frequency [frequency]
  (get bybit-frequencies frequency))

(defn instant->epoch-millisecond [dt]
  (-> dt
      (t/long)
      (* 1000)))

(defn range->parameter [range]
  (assoc range
         :start (instant->epoch-millisecond (:start range))
         :end (instant->epoch-millisecond (:end range))))

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
    category (symbol->provider-category asset)
    range-bybit (range->parameter range)]
   (-> (bybit/get-history-request (merge
                                   {:symbol symbol-bybit
                                    :interval frequency-bybit
                                    :category category}
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

(defn get-bars [{:keys [asset calendar] :as opts} {:keys [start end] :as window}]
  (info "get-bars: " opts range)
  (let [page-size 1000 ; 200
        ; dates need to be instant, because only instant can be converted to unix-epoch-ms
        start (if (t/instant? start) start (t/instant start))
        end (if (t/instant? end) end (t/instant end))
        window (assoc window :limit page-size :start start :end end)]
    (info "start: " start)
    (->> (iteration (fn [window]
                      (info "new page req: " window)
                      (get-bars-req opts window))
                    :initk window
                    :kf  (partial next-request calendar window))
         (consolidate-datasets opts range))))

(defrecord import-bybit []
  barsource
  (get-bars [this opts window]
    (get-bars opts window)))

(defn create-import-bybit []
  (import-bybit.))

(comment
  (bybit-frequency :d)
  (bybit-frequency :h)
  (bybit-frequency :m)
  (bybit-frequency :s)

  (def ds (tc/dataset [{:date (t/instant)}
                       {:date (t/instant)}]))

  (-> (t/instant) instant->epoch-millisecond)

  (get-bars {:asset "BTCUSDT"
             :calendar [:crypto :d]}
            {:start (t/instant "2024-02-26T00:00:00Z")})

  (-> (get-bars-req
       {:asset "BTCUSDT"
        :calendar [:crypto :m]}
       {:start (-> "2024-03-05T00:00:00Z" t/instant)
        :end (-> "2024-03-06T00:00:00Z" t/instant)})
      (tc/last)
      :date
      first
      ;count
      )
     ; 2024-03-05T21:26:00Z

  (all-ds-valid [1 2 3 4 5])
  (all-ds-valid [1 2 3 (nom/fail ::asdf {}) 4 5])

  (get-bars
   {:asset "BTCUSDT"
    :calendar [:crypto :m]}
   {:start (-> "2024-02-29T00:00:00Z" t/instant)
    :end (-> "2024-02-29T00:07:00Z" t/instant)})

  (-> (get-bars
        {:asset "BTCUSDT"
         :calendar [:crypto :d]}
        {:start (-> "2021-07-04T00:00:00Z" t/instant)
         :end (-> "2024-05-02T00:00:00Z" t/instant)})
      (tc/write-csv! "/clojure-quant/quanta/lib/indicator/test/ta/indicator/csv/BYBIT_SPOT_BTCUSDT_1D.csv"))
; 
  )