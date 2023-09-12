(ns ta.multi.nav-trades
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [tick.core :as t] 
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.datatype.argops :as argops]
   [tech.v3.tensor :as dtt]
   [ta.warehouse :as wh]
   [ta.data.settings :refer [determine-wh]]
   [ta.multi.aligned :refer [load-aligned-filled]]
   [ta.multi.calendar :refer [daily-calendar daily-calendar-sunday-included]]
   ))

(defn filter-range [ds-bars {:keys [start end]}]
  (tc/select-rows
   ds-bars
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
                     #(map inc (% :position/trades-open))})
  ))


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
        pl-u (dfn/- open$ net$) ]
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
     (set-col-win ds-eff idxs-exit :pl-r pl-r)
     ))

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
     :warnings @warnings}
    ))

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


(defn empty-series [calendar]
  (:calendar calendar))

(defn effects-symbol [symbol calendar trades]
  ;(info "calculating " symbol)
  (let [ds-bars (load-aligned-filled symbol calendar)
        has-series? ds-bars
        ds-bars (or ds-bars (empty-series calendar))
        effects (map #(trade-effect ds-bars has-series? %) trades)]
    (effects-sum effects)))

(defn exists-series? [s ]
  (let [w (determine-wh s)]
     (wh/exists-symbol? w "D" s)))

(defn portfolio [trades]
  (let [start-dt (apply t/min (map :entry-date trades))
        end-dt (apply t/max (->> (map :exit-date trades)
                                 (remove nil?)))
        ;calendar (daily-calendar start-dt end-dt)
        calendar (daily-calendar-sunday-included start-dt end-dt)
        trades-symbol (fn [symbol]
                        (filter #(= symbol (:symbol %)) trades))
        calc-symbol (fn [symbol]
                      (effects-symbol symbol calendar (trades-symbol symbol)))
        symbols (->> trades 
                     (map :symbol) 
                     ;(filter exists-series?)
                     (into #{})
                     (into []))
        {:keys [eff warnings]} (reduce effects+
                                 {:eff (no-effect (tc/row-count (:calendar calendar)))
                                  :warnings []}
                                  (map calc-symbol symbols))
        ; result
        pl-r-cum (dfn/cumsum (:pl-r eff))] 
    {:warnings warnings 
     :eff (tc/add-columns 
           eff
           {:date (-> calendar :calendar :date)
            :pl-r-cum pl-r-cum
            :pl-cum (dfn/+ (:pl-u eff) pl-r-cum)})}))


(comment 
  
  (require '[ta.helper.date :refer [parse-date]])
  (require '[tech.v3.dataset.print :refer [print-range]])
  (require '[clojure.pprint :refer [print-table]])
  
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

  (require '[joseph.trades :refer [load-trades-valid]])
  
  (def trades (load-trades-valid))
  (count trades)

  (def trades-googl 
    (filter #(= "GOOGL" (:symbol %)) trades))
  (count trades-googl)
  
  (print-table [:entry-date :exit-date :symbol ] trades)
  (print-table [:entry-date :exit-date :symbol] trades-googl)

  (def trade (last trades))
  trade

  (defn in-range? [entry-date exit-date date]
    (t/<= entry-date date exit-date) 
    )
  
  (def entry-date (parse-date "2022-03-05"))
  (def exit-date (parse-date "2022-03-07"))

  (in-range? entry-date exit-date (parse-date "2022-03-06"))
  (in-range? entry-date exit-date (parse-date "2022-03-01"))
  (in-range? entry-date exit-date (parse-date "2022-03-08"))
  (in-range? entry-date exit-date (parse-date "2022-03-07"))



  (def ds-bars 
    (load-symbol "GOOGL" "D" (parse-date "2023-03-01")
                 (parse-date "2023-03-20")))
  ds-bars
  (trade-effect ds-bars trade)
  
  (effects-sum [(trade-effect ds-bars trade)
                (trade-effect ds-bars trade)])
  
  (-> (effects-symbol 
       "GOOGL" 
       (daily-calendar (parse-date "2022-10-01")
                       (parse-date "2023-04-01"))
        trades-googl)
      (print-range :all))
  
  (exists-series "MSFT")
  (exists-series "DAX0")

  (def trades-test 
    [{:symbol "GOOGL" 
      :side :long
      :qty 1000.0
      :entry-date #time/date-time "2023-03-06T00:00"
      :entry-price 94.78
      :exit-date #time/date-time "2023-03-16T00:00"
      :exit-price 96.92}])

  (portfolio trades-googl)

  (portfolio [{:symbol "BZ0"
               :side :long
               :qty 1000.0
               :entry-date #time/date-time "2022-11-27T00:00"
               :entry-price 80.96
               :exit-date #time/date-time "2022-11-27T00:00"
               :exit-price 82.18}])
  
  (def trades-dax
    (filter #(= "DAX0" (:symbol %)) trades))
  
  (count trades-dax)

  (portfolio trades-dax)

  (portfolio (take 1 trades))
  (portfolio (take 5 trades))

  (portfolio (take 10 trades))
  (portfolio (take 20 trades))
  (portfolio (take 50 trades))
  (portfolio (take 100 trades))
  (portfolio (take 200 trades))
  (portfolio (take 300 trades))
  (portfolio (take 400 trades))
  (portfolio trades)

  
  (->> 
   (portfolio trades)
   :warnings
   ;(print-table [:warning :symbol :side :exit-date])
   )
  
  
  (require '[ta.multi.aligned :refer [load-symbol-window]])
  
  (-> 
  (load-symbol-window "BZ0" "D" 
                      #time/date-time "2022-08-01T00:00"
                      #time/date-time "2023-08-01T00:00")
  
   (print-range :all))
; |   :realized |     BZ0 |     :short | 2023-07-16T00:00 |
; | :unrealized |     BZ0 |     :short | 2022-10-03T00:00 |
; |   :realized |     BZ0 |      :long | 2022-11-27T00:00 |
; |   :realized |     BZ0 |     :short | 2023-07-16T00:00 |
; | :unrealized |     NG0 |     :short | 2022-08-08T00:00 |


 ; 
  )
  
