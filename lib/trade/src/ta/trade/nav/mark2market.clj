(ns ta.trade.nav.mark2market
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.datatype.argops :as argops]
   [tech.v3.tensor :as dtt]
   [ta.calendar.core :as cal]
   [ta.db.bars.multi-asset :refer [load-aligned-assets is-valid? calendar-seq->date-ds]]))

(defn filter-range [bar-ds {:keys [start end]}]
  (tc/select-rows
   bar-ds
   (fn [row]
     (let [date (:date row)]
       (and
        (or (not start) (t/>= date start))
        (or (not end) (t/<= date end)))))))

(defn vec-const [size val]
  (vec (repeat size val)))

(defn no-effect [size]
  (tc/dataset
   {:open# (vec-const size 0)
    :long$ (vec-const size 0.0)
    :short$ (vec-const size 0.0)
    :net$ (vec-const size 0.0)
    :pl-u (vec-const size 0.0)
    :pl-r (vec-const size 0.0)}))

(defn trade-stats [ds-bars {:keys [date-entry date-exit qty]}]
  (let [ds-trade (filter-range ds-bars {:start date-entry :end date-exit})]
    (tc/add-columns ds-trade
                    {:position/trades-open
                     #(map inc (% :position/trades-open))})))

(defn set-col-win [ds win col val]
  ;(println "set-col val: " val)
  (dtt/mset! (dtt/select (col ds) win) val))

(defn trade-unrealized-effect [ds-eff idxs-win price-w w#
                               {:keys [qty side
                                       entry-price entry-date
                                       exit-price exit-date] :as trade}]
  ;(info "calculate trade-un-realized..")
  (let [long? (= :long side)
        qty2 (if long? (+ 0.0 qty) (- 0.0 qty))
        open# (vec-const w# 1.0)
        long$ (if long?
                (dfn/* price-w qty2)
                (vec-const w# 0.0))
        short$  (if long?
                  (vec-const w# 0.0)
                  (dfn/* price-w qty2))
        net$ (dfn/+ long$ short$)
        open$ (* qty2 entry-price)
        pl-u (dfn/- open$ net$)]
    ;(println "idxs-win: " idxs-win)
    ;(println "win-size: " w#)
    ;(println "open#: " open#)
    (set-col-win ds-eff idxs-win  :open# open#)
    (set-col-win ds-eff idxs-win  :long$ long$)
    (set-col-win ds-eff idxs-win  :short$ short$)
    (set-col-win ds-eff idxs-win  :net$ net$)
    (set-col-win ds-eff idxs-win  :pl-u pl-u)))

(defn trade-realized-effect [ds-eff idxs-exit
                             {:keys [qty side
                                     entry-price
                                     exit-price] :as trade}]
   ;(info "calculate trade-realized..")
  (let [; derived values of trade parameter
        long? (= :long side)
        qty2 (if long? qty (- 0 qty))
        pl-realized (* qty2 (- exit-price entry-price))
         ; columns [row-count window-size]
         ; columns [row-count 0]
        pl-r [pl-realized] ; scalar inside vector]
        ]
    (set-col-win ds-eff idxs-exit :pl-r pl-r)))

(defn trade-effect [ds-bars has-series?
                    {:keys [qty side
                            entry-date exit-date]
                     :as trade}]
  (assert entry-date "trade does not have entry-dt")
  ;(assert exit-date "trade does not have entry-dt")
  (assert side "trade does not have side")
  (assert qty "trade does not have qty")
  ;(info "calculating trade-effect " trade)
  (let [full# (tc/row-count ds-bars)
        ds-eff (no-effect full#)
        warnings (atom [])
        idxs-win (cond
                   ; open trade
                   (nil? exit-date)
                   (argops/argfilter #(t/<= entry-date %)
                                     (:date ds-bars))
                   ; closed trade with same date entry/exit
                   (t/= entry-date exit-date)
                   nil

                   ; closed trade over multiple bars
                   :else
                   (argops/argfilter #(and (t/<= entry-date %)
                                           (t/< % exit-date))
                                     (:date ds-bars)))]
    ; unrealized effect
    (when (and idxs-win has-series?)
      (let [ds-w (tc/select-rows ds-bars idxs-win)
            price-w (:close ds-w)
            w# (tc/row-count ds-w)]
        (if (= w# 0)
          (do (warn "cannot calculate unrealized effect for trade: " trade)
              (swap! warnings conj (assoc trade :warning :unrealized)))
          (trade-unrealized-effect ds-eff idxs-win price-w w# trade))))
    ; realized effect
    (when exit-date
      (let [idxs-exit (argops/argfilter #(t/= exit-date %) (:date ds-bars))
            ds-x (tc/select-rows ds-bars idxs-exit)
            x# (tc/row-count ds-x)]
        (if (= x# 0)
          (do (warn "cannot calculate REALIZED effect for trade: " trade)
              (swap! warnings conj (assoc trade :warning :realized)))
          (trade-realized-effect ds-eff idxs-exit trade))))
    ; return
    {:eff ds-eff
     :warnings @warnings}))

(defn effects+ [a b]
  (let [warnings (concat (:warnings a) (:warnings b))
        a (:eff a)
        b (:eff b)]
    {:warnings warnings
     :eff (tc/dataset
           {:open# (dfn/+ (:open# a) (:open# b))
            :long$ (dfn/+ (:long$ a) (:long$ b))
            :short$ (dfn/+ (:short$ a) (:short$ b))
            :net$ (dfn/+ (:net$ a) (:net$ b))
            :pl-u (dfn/+ (:pl-u a) (:pl-u b))
            :pl-r (dfn/+ (:pl-r a) (:pl-r b))})}))

(defn effects-sum [effects]
  (let [empty {:eff (no-effect (tc/row-count (:eff (first effects))))
               :warnings []}]
    (reduce effects+ empty effects)))

(defn effects-asset [bar-ds has-series? trades]
  (info "calculating " (if has-series? (tc/row-count bar-ds) "no-series"))
  (let [effects (map #(trade-effect bar-ds has-series? %) trades)]
    (effects-sum effects)))

(defn portfolio [bardb trades {:keys [calendar] :as opts}]
  (let [assets (->> trades
                    (map :asset)
                    (into #{})
                    (into []))
        start-dt (apply t/min (map :entry-date trades))
        end-dt (apply t/max (->> (map :exit-date trades)
                                 (remove nil?)))
        window {:start start-dt :end end-dt}
        cal-seq (cal/fixed-window calendar window)
        date-ds (calendar-seq->date-ds cal-seq)
        l (tc/row-count date-ds)
        bar-dict (load-aligned-assets bardb opts assets cal-seq)
        _ (info "bar-ds count: " (tc/row-count date-ds))
        trades-asset (fn [asset]
                       (filter #(= asset (:asset %)) trades))
        calc-asset (fn [asset]
                     (let [has-series? (is-valid? bar-dict asset)
                           bar-ds (if has-series?
                                    (get bar-dict asset)
                                    date-ds)]
                       (effects-asset bar-ds has-series? (trades-asset asset))))
        {:keys [eff warnings]} (reduce effects+
                                       {:eff (no-effect l)
                                        :warnings []}
                                       (map calc-asset assets))
        ; result
        pl-r-cum (dfn/cumsum (:pl-r eff))]
    {:warnings warnings
     :eff (tc/add-columns
           eff
           {:date (-> date-ds :date)
            :pl-r-cum pl-r-cum
            :pl-cum (dfn/+ (:pl-u eff) pl-r-cum)})}))

(comment

  (require '[ta.helper.date :refer [parse-date]])
  (t/min  (parse-date "2022-01-02")
          (parse-date "2022-01-01")
          (parse-date "2022-01-04"))

  (def ds1 (tc/dataset
            {:date [(parse-date "2022-01-01")
                    (parse-date "2022-01-02")
                    (parse-date "2022-01-04")
                    (parse-date "2022-01-05")
                    (parse-date "2022-01-06")
                    (parse-date "2022-01-07")]}))
  ds1

  (def entry-dt   (parse-date "2022-01-02"))
  (def exit-dt   (parse-date "2022-01-06"))

  (argops/argfilter #(t/<= entry-dt % exit-dt) (:date ds1))
  (argops/argfilter #(t/<= entry-dt % entry-dt) (:date ds1))

  (tc/select-rows ds1 [0 5])

  (defn in-range? [entry-date exit-date date]
    (t/<= entry-date date exit-date))

  (def entry-date (parse-date "2022-03-05"))
  (def exit-date (parse-date "2022-03-07"))

  (in-range? entry-date exit-date (parse-date "2022-03-06"))
  (in-range? entry-date exit-date (parse-date "2022-03-01"))
  (in-range? entry-date exit-date (parse-date "2022-03-08"))
  (in-range? entry-date exit-date (parse-date "2022-03-07"))

  (def trade1 {:asset "EUR/USD"
               :side :long
               :qty 1000.0
               :entry-date (t/instant "2023-03-09T17:00:00Z")
               :entry-price 94.78
               :exit-date (t/instant "2023-03-16T17:00:00Z")
               :exit-price 96.92})

  (def trades-test
    [trade1])

  (require '[modular.system])
  (def bardb (modular.system/system :duckdb ;:bardb-dynamic
                                    ))
  bardb

  (require '[ta.db.bars.protocol :as b])
  (def eurusd (b/get-bars bardb {:asset "EUR/USD"
                                 :calendar [:forex :d]}
                          {:start (t/instant "2023-03-06T20:30:00Z")
                           :end (t/instant "2023-03-16T20:30:00Z")}))

  (-> (effects-asset
       eurusd
       true
       trades-test)
      :eff)

  (trade-effect eurusd true trade1)

  (effects-sum [(trade-effect eurusd true trade1)
                (trade-effect eurusd true trade1)])

  (portfolio bardb trades-test {:calendar [:forex :d]
                                :import :kibot})

; 
  )

