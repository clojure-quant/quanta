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
   [ta.multi.calendar :refer [daily-calendar]]
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

(defn idxs-last [idxs]
  (let [v (last idxs)]
    [v]))

(defn set-col-win [ds win col val]
  ;(println "set-col val: " val)
  (dtt/mset! (dtt/select (col ds) win) val))

(defn trade-unrealized-effect [ds-eff idxs-win price-w w# 
                               {:keys [qty direction
                                       entry-price entry-date
                                       exit-price exit-date] :as trade}]
  ;(info "calculate trade-un-realized..")
  (let [long? (= :long direction)
        qty2 (if long? qty (- 0 qty))
        open# (vec-const w# 1.0)
        long$ (if long?
                (dfn/* price-w qty)
                (vec-const w# 0.0))
        short$  (if long?
                  (vec-const w# 0.0)
                  (dfn/* price-w qty))
        net$ (dfn/- long$ short$)
        open$ (* qty entry-price)
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
                            {:keys [qty direction
                                    entry-price entry-date
                                    exit-price exit-date] :as trade}]
   ;(info "calculate trade-realized..")
   (let [; derived values of trade parameter
         long? (= :long direction) 
         qty2 (if long? qty (- 0 qty))
         pl-realized (* qty2 (- exit-price entry-price))
         ; columns [row-count window-size]
         ; columns [row-count 0]
         pl-r [pl-realized] ; scalar inside vector]
       ]
     (set-col-win ds-eff idxs-exit :pl-r pl-r)
     ))

(defn trade-effect [ds-bars {:keys [qty direction 
                                    entry-price entry-date
                                    exit-price exit-date] :as trade}]
  (assert entry-date "trade does not have entry-dt")
  (assert exit-date "trade does not have entry-dt")
  (assert direction "trade does not have side")
  (assert qty "trade does not have qty")
  ;(info "calculating trade-effect " trade)
  (let [full# (tc/row-count ds-bars)
        ds-eff (no-effect full#)
        idxs-win (if (t/= entry-date exit-date)
                    (argops/argfilter #(t/= entry-date %) (:date ds-bars))
                    (argops/argfilter #(t/<= entry-date % exit-date) (:date ds-bars)))
        idxs-exit (idxs-last idxs-win)
        ds-w (tc/select-rows ds-bars idxs-win)
        price-w (:close ds-w)
        w# (tc/row-count ds-w)]
    (if (= w# 0)
      (do (warn "cannot calculate effect for trade: " trade)
          ds-eff)
      (do (trade-unrealized-effect ds-eff idxs-win price-w w# trade)
          (trade-realized-effect ds-eff idxs-exit trade)
          ds-eff))))

(defn effects+ [a b]
  (tc/dataset
    {:open# (dfn/+ (:open# a) (:open# b))
     :long$ (dfn/+ (:long$ a) (:long$ b))
     :short$ (dfn/+ (:short$ a) (:short$ b))
     :net$ (dfn/+ (:net$ a) (:net$ b))
     :pl-u (dfn/+ (:pl-u a) (:pl-u b))
     :pl-r (dfn/+ (:pl-r a) (:pl-r b))}))

(defn effects-sum [effects]
   (let [empty (no-effect (tc/row-count (first effects)))]
     (reduce effects+ empty effects)))

(defn effects-symbol [symbol calendar trades]
  ;(info "calculating " symbol)
  (let [ds-bars (load-aligned-filled symbol calendar)
        effects (map #(trade-effect ds-bars %) trades)]
    (effects-sum effects)))

(defn exists-series? [s ]
  (let [w (determine-wh s)]
     (wh/exists-symbol? w "D" s)))

(defn portfolio [trades]
  (let [start-dt (apply t/min (map :entry-date trades))
        end-dt (apply t/max (map :exit-date trades))
        calendar (daily-calendar start-dt end-dt)
        trades-symbol (fn [symbol]
                        (filter #(= symbol (:symbol %)) trades))
        calc-symbol (fn [symbol]
                      (effects-symbol symbol calendar (trades-symbol symbol)))
        symbols (->> trades 
                     (map :symbol) 
                     (filter exists-series?)
                     (into #{})
                     (into []))
        ds  (reduce effects+
                    (no-effect (tc/row-count (:calendar calendar)))
                    (map calc-symbol symbols))
        
        ]  
      (tc/add-columns ds {:date (-> calendar :calendar :date)
                          :pl-r-cum (dfn/cumsum (:pl-r ds))
                          })
     ))




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

  (require '[joseph.trades :refer [load-trades]])
  (def cutoff-date (parse-date "2022-01-01"))
  (defn invalid-date [{:keys [entry-date exit-date]}]
    (or (t/<= entry-date cutoff-date)
        (t/<= exit-date cutoff-date)))
  (def trades 
    (->> (load-trades)
         (remove invalid-date)))
  (count trades)

  (def trades-googl 
    (filter #(= "GOOGL" (:symbol %)) trades))
  
  trades
  
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
        trades)
      (print-range :all))
  
  (exists-series "MSFT")
  (exists-series "DAX0")

  (def trades-test 
    [{:symbol "GOOGL" 
      :direction :long
      :qty 1000.0
      :entry-date #time/date-time "2023-03-06T00:00"
      :entry-price 94.78
      :exit-date #time/date-time "2023-03-16T00:00"
      :exit-price 96.92}])

  (portfolio trades-googl)

  (portfolio [{:symbol "BZ0"
               :direction :long
               :qty 1000.0
               :entry-date #time/date-time "2022-11-27T00:00"
               :entry-price 80.96
               :exit-date #time/date-time "2022-11-27T00:00"
               :exit-price 82.18}])
  

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


 ; 
  )
  
