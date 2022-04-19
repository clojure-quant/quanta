(ns ta.tradingview.handler-datasource
  (:require
   [clojure.string :as str]
   [clojure.walk]
   [taoensso.timbre :refer [trace debug info warnf error]]
   [schema.core :as s]
   [tick.core :as tick]
   [ring.util.response :as res]
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [modular.webserver.middleware.api :refer [wrap-api-handler]]
   [modular.webserver.handler.registry :refer [add-ring-handler]]
   [ta.helper.date :refer [now-datetime datetime->epoch-second epoch-second->datetime]]
   [ta.warehouse :refer [symbols-available load-symbol search instrument-details]]
   [ta.warehouse.tml :refer [filter-date-range]]
   [ta.tradingview.db-ts :refer [save-chart-boxed delete-chart load-chart-boxed chart-list now-epoch]]
   [ta.tradingview.db-instrument :refer [inst-type inst-exchange inst-name
                                         category-name->category inst-crypto?]]
   [ta.tradingview.db-marks :refer [load-marks convert-marks]]))

(defn time-handler [_]
  (info "tv/time")
  (let [now-epoch (-> (now-datetime) datetime->epoch-second)]
    (res/response (str now-epoch))))

;; CONFIG - Tell TradingView which featurs are supported by server.

(def server-config
  {:supports_time true  ; we send our server-time
   :supports_search true ;search and individual symbol resolve logic.
   :supports_marks false ; true
   :supports_timescale_marks false
   :supports_group_request false
   :supported_resolutions ["15" "D"] ; ["1" "5" "15" "30" "60" "1D" "1W" "1M"]
   :symbols_types [{:value "" :name "All"}
                   {:value "Crypto" :name "Crypto"}
                   {:value "Equity" :name "Equities"}
                   {:value "Mutualfund" :name "Mutualfund"}
                   {:value "ETF" :name "ETF"}
                   ;{:value "Corp" :name "Bonds"}
                   ;{:value "Index" :name "Indices"}
                   ;{:value "Curncy" :name "Currencies"}
                   ]
   :exchanges [{:value "" :name "All Exchanges" :desc ""}
               {:value "BB" :name "Bybit" :desc ""}
               {:value "SG" :name "Stocks Global" :desc ""}
               ;{:value "US" :name "US (Nasdaq NYSE)" :desc ""}
               ;{:value "GR" :name "German (Xetra/Regional)" :desc ""}
               ;{:value "NO" :name "Norway" :desc ""}
               ;{:value "AV" :name "Austria" :desc ""}
               ;{:value "LN" :name "London" :desc ""}
               ]})

(defn config-handler [_]
  (info "tv/config")
  (res/response server-config))

;; symbol lookup

(defn symbol-info
  "Converts instrument [from db] to tradingview symbol-information
   Used in symbol and search"
  [s]
  (let [i (instrument-details s)]
    {:ticker s  ; OUR SYMBOL FORMAT. TV uses exchange:symbol
     :name  s ; for tv this is only the symbol
     :description (inst-name s i)
     :exchange (inst-exchange i)
     :exchange-listed (inst-exchange i)
     :type (inst-type i)
     :supported_resolutions ["15" "D"]
     :has_no_volume false
     ; FORMATTING OF DIGITS
     :minmov 1  ; is the amount of price precision steps for 1 tick. For example, since the tick size for U.S. equities is 0.01, minmov is 1. But the price of the E-mini S&P futures contract moves upward or downward by 0.25 increments, so the minmov is 25.
     :pricescale 100 ;  If a price is displayed as 1.01, pricescale is 100; If it is displayed as 1.005, pricescale is 1000.
     :minmov2 0  ;  for common prices is 0 or it can be skipped.
     :fractional 0  ; for common prices is false or it can be skipped.   ; Fractional prices are displayed 2 different forms: 1) xx'yy (for example, 133'21) 2) xx'yy'zz (for example, 133'21'5).
     :volume_precision 0 ;Integer showing typical volume value decimal places for a particular symbol. 0 means volume is always an integer. 1 means that there might be 1 numeric character after the comma.
     :pointvalue 1
     ; session
     :has_intraday true
     :timezone "Etc/UTC" ; "America/New_York"
     :session "0900-1600"  ;"0900-1630|0900-1400:2",
                           ;:session-regular "0900-1600"

     ; :expired true ; whether this symbol is an expired futures contract or not.
     ; :expiration_date  (to-epoch-no-ms- (-> 1 t/hours t/ago))
     }))
(comment
  ; stocks should have :exchange SG :type Stocks
  ; crypto should have :exchange BB :type Crypto
  (symbol-info "MSFT")
  (symbol-info "ETHUSD")
;
  )

(defn symbols-handler [{:keys [query-params] :as req}]
  (info "tv/symbols")
  (let [{:keys [symbol]} (clojure.walk/keywordize-keys query-params)
        si (symbol-info symbol)]
    (res/response si)))

;https://demo_feed.tradingview.com/search?query=B&type=stock&exchange=NYSE&limit=10
;[{"symbol":"BLK","full_name":"BLK","description":"BlackRock, Inc.","exchange":"NYSE","type":"stock"},
;  {"symbol":"BA","full_name":"BA","description":"The Boeing Company","exchange":"NYSE","type":"stock"}]

(defn filter-exchange [exchange list]
  (if (str/blank? exchange)
    list
    (filter #(= exchange (inst-exchange %)) list)))

(defn filter-category [type list]
  (if (str/blank? type)
    list
    (let [c (category-name->category type)]
      (filter #(= c (:category %)) list))))

(defn search-handler [{:keys [query-params] :as req}]
  (info "tv/search: " query-params)
  (let [{:keys [query type exchange limit]} (clojure.walk/keywordize-keys query-params)
        limit (Integer/parseInt limit)
        sr (->> (search query)
                (filter-exchange exchange)
                (filter-category type))
        sr-limit (take limit sr)
        sr-tv (map (fn [{:keys [symbol name] :as i}]
                     {:ticker symbol
                      :symbol symbol ; OUR SYMBOL FORMAT. TV uses exchange:symbol
                      :full_name symbol
                      :description  (inst-name symbol i)
                      :exchange (inst-exchange i)
                      :type (inst-type i)}) sr-limit)]
    (res/response sr-tv)))

(comment
  (-> (search-handler {:query-params {:query "E"
                                      :type ""
                                      :exchange "BB"
                                      :limit "10"}})
      :body
      ;count
      )

 ; 
  )
;; series

; https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=1567457308&to=1568321308
; {"s":"no_data",
; "nextTime":1522108800
;}

; https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=1487289600&to=1488499199
; {
;  "t":[1487289600,1487635200,1487721600,1487808000,1487894400,1488153600,1488240000,1488326400,1488412800],
;  "o":[135.1,136.23,136.43,137.38,135.91,137.14,137.08,137.89,140],
;  "h":[135.83,136.75,137.12,137.48,136.66,137.435,137.435,140.15,140.2786],
;  "l":[135.1,135.98,136.11,136.3,135.28,136.28,136.7,137.595,138.76],
;  "c":[135.72,136.7,137.11,136.53,136.66,136.93,136.99,139.79,138.96],
;  "v":[22198197,24507156,20836932,20788186,21776585,20257426,23482860,36414585,26210984],
; "s":"ok"}

(defn add-epoch-second
  "add epoch column to ds"
  [ds]
  (tds/column-map ds :epoch datetime->epoch-second [:date]))

(defn load-series [s resolution from to]
  (let [i (instrument-details s)
        w (if (inst-crypto? i) :crypto :stocks)
        frequency (case resolution
                    "1D" "D"  ; tradingview sometimes queries daily as 1D
                    resolution)
        from-dt (epoch-second->datetime from)
        to-dt (epoch-second->datetime to)
        series (try (-> (load-symbol w frequency s)
                        (filter-date-range from-dt to-dt)
                        add-epoch-second)
                    (catch Exception _
                      (error "Series not found: " s " " resolution)
                      nil))
        series (if (and series (= (tc/row-count series) 0))
                 nil
                 series)
        col (fn [k]
              (into [] (k series)))]
    (if series
      {:t (col :epoch)
       :o (col :open)
       :h (col :high)
       :l (col :low)
       :c (col :close)
       :v (col :volume)
       :s "ok"}
      {:s "no_data"
       :t []
       :o []
       :h []
       :l []
       :c []
       :v []})))

(defn history-handler [{:keys [query-params] :as req}]
  (info "tv/history: " query-params)
  (let [{:keys [symbol resolution from to]} (clojure.walk/keywordize-keys query-params)
        from (Integer/parseInt from)
        to (Integer/parseInt to)
        series (load-series symbol resolution from to)]
    (res/response series)))

(def demo-marks
  {:id [0 1 2 3 4 5]
   :time [1568246400 1567900800 1567641600 1567641600 1566950400 1565654400]
   :label ["A" "B" "CORE" "D" "EURO" "F"]
   :text ["Today"
          "4 days back"
          "7 days back + Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
          "7 days back once again"
          "15 days back"
          "30 days back"]
   :color ["red" "blue" "green" "red" "blue" "green"]
   :labelFontColor ["white" "white" "red" "#FFFFFF" "white" "#000"]
   :minSize [14 28 7 40 7 14]})

(defn marks-handler [{:keys [query-params] :as req}]
  ; https://demo_feed.tradingview.com/marks?symbol=AAPL&from=1488810600&to=1491226200&resolution=D
  (info "tv/marks: " query-params)
  (let [{:keys [symbol resolution from to]} (clojure.walk/keywordize-keys query-params)
        from (Integer/parseInt from)
        to (Integer/parseInt to)
        marks (load-marks symbol resolution from to)
        marks (convert-marks marks)
        ;_ (info "marks: " marks)
        ;marks demo-marks
        ]
    (res/response marks)))

(comment
  (datetime->epoch-second (tick/date-time "1980-04-01T00:00:00"))
  (datetime->epoch-second (tick/date-time "1980-05-01T00:00:00"))
  (datetime->epoch-second (tick/date-time "2019-04-01T00:00:00")) ; 1617235200
  (datetime->epoch-second (tick/date-time "2019-05-01T00:00:00")) ; 1619827200
  (datetime->epoch-second (tick/date-time "2020-05-01T00:00:00"))
  (datetime->epoch-second (tick/date-time "2021-05-01T00:00:00"))

  (-> (load-symbol :crypto "ETHUSD" "D")
      (filter-date-range
       (tick/date-time "2019-04-01T00:00:00")

       (tick/date-time "2021-05-01T00:00:00")))

  (load-series "ETHUSD" "D" 1617235200 1619827200)

; https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=1487289600&to=1488499199
   ; demo has data for 2017
  (epoch-second->datetime 1487289600)
  (epoch-second->datetime 1488499199)

   ;  https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=1554076800&to=1556668800
   ;  {"s":"no_data","nextTime":1522108800}
  ; no data found -> nextTime returns a PRIOR data
  (epoch-second->datetime 1554076800)
  (epoch-second->datetime 1556668800)
  (epoch-second->datetime 1522108800)

  ;  https://demo_feed.tradingview.com/history?symbol=AAPL&resolution=D&from=323395200&to=325987200
  ; {"t":[],"o":[],"h":[],"l":[],"c":[],"v":[],"s":"no_data"}

  ; test load known symbol
  (history-handler {:query-params {:symbol "ETHUSD"
                                   :from "1617235200"
                                   :to "1619827200"
                                   :resolution "D"}})

  ; test for unknown symbol
  (history-handler {:query-params {:symbol "XXXX"
                                   :from "1617235200"
                                   :to "1619827200"
                                   :resolution "D"}})

  (history-handler {:query-params {:symbol "Unknown-"
                                   :from "1617235200"
                                   :to "1619827200"
                                   :resolution "D"}})

  (history-handler {:query-params   {"symbol" "BTCUSD"
                                     "resolution" "D"
                                     "from" "1299075015"
                                     "to" "1303308614"}})
;  
  )

(add-ring-handler :tv/time (wrap-api-handler time-handler))
(add-ring-handler :tv/config (wrap-api-handler config-handler))
(add-ring-handler :tv/symbols (wrap-api-handler symbols-handler))
(add-ring-handler :tv/search (wrap-api-handler search-handler))
(add-ring-handler :tv/history (wrap-api-handler history-handler))
(add-ring-handler :tv/marks (wrap-api-handler marks-handler))



