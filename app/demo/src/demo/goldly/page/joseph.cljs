(ns demo.goldly.page.joseph
  (:require
   [reagent.core :as r]
   [cljc.java-time.local-date-time :as ldt]
   [cljc.java-time.zone-offset :refer [utc]]
   [tick.goldly]
   [tick.core :as tick]
   [ui.aggrid :refer [aggrid]]
   [input]
   [goldly.page :as page]
   [demo.goldly.lib.loader :refer [clj->a]]
   [demo.goldly.lib.ui :refer [link-href]]
   ;[demo.goldly.view.tsymbol :refer [symbol-picker]]
   [ta.tradingview.goldly.algo.context :as c]
   [ta.tradingview.goldly.algo.tradingview :refer [tradingview-algo]]
   [ta.tradingview.goldly.page.tradingview-algo :refer []]
   ))

 
(defonce algo-ctx
  (c/create-algo-context "joseph" {:symbol "GOOGL" :frequency "D"}))



 (defn fmt-yyyymmdd [dt]
   (if dt
     (tick/format (tick/formatter "YYYY-MM-dd") dt)
     ""))
 
 (defn datetime->epoch-second [dt]
  (ldt/to-epoch-second dt utc))
 

(defn symbols [trades]
  (->> trades
       (map :symbol)
       (into #{})
       (into [])))

(defn on-click [args]
  (let [args-clj (js->clj args)
        data (get args-clj "data")
        entry-date (get data "entry-date")
        symbol (get data "symbol")
        input (:input algo-ctx)
        ]
    ;(println "on-click args: " args-clj)  
    (println "on-click data: " data)
    (println "symbol:" symbol "entry-epoch: " (datetime->epoch-second entry-date))

    (swap! input assoc-in [:opts :symbol]  symbol)


    ))

 


(defn trade-table [trades]
  [aggrid {:data trades
           :columns [{:field :symbol}
                     {:field :direction :onCellClicked on-click }
                     {:field :entry-date :format fmt-yyyymmdd}
                     {:field :exit-date :format fmt-yyyymmdd}
                     {:field :entry-price}
                     {:field :exit-price}
                     {:field :qty}
                     {:field :pl}]
           :box :fl
           :pagination :false
           :paginationAutoPageSize true}])

(defn filter-trades [trades symbol]
  (if (= symbol "*")
    trades
    (filter #(= symbol (:symbol %)) trades)))

(defn trade-ui [_trades]
  (let [state-internal (r/atom {:symbol "*"})]
    (fn [trades]
      (let [active-symbols (symbols trades)
            active-symbols (concat ["*"] active-symbols)
            trades-filtered (filter-trades trades (:symbol @state-internal))
            ]
        [:div.flex.flex-col.w-full.h-full
         [:div 
           ;main nav
           [link-href "/" "main"]
           ; symbol picker
           [:div {:style {:width "4cm"
                          :max-width "4cm"}}
             [input/select {:nav? false
                            :items active-symbols}
              state-internal [:symbol]] 
            ]
           
          ]
          
           [trade-table trades-filtered]   
        ]))))


(defn trades-view []
  (let [trades (r/atom {})]
    (fn []
      (clj->a trades 'demo.algo.joseph/load-trades)
      [:div.w-full.h-full
       ;[:h1.text-bold.bg-green-500 "Bars for symbol: " s " f: " f]
       (case (:status @trades)
         :loading [:p "loading"]
         :error [:p "error!"]
         :data [:div.w-full.h-full 
                [trade-ui (:data @trades)]
                ]
         [:p "unknown: status:" (pr-str @trades)])])))


(defn trades-page [_route]
  [:div.h-screen.w-screen.bg-red-500
      {;:class "h-full.w-full"
       :style {:display "flex"}} ;.flex.flex-row.
       ;[series-view  "BTCUSD" "D"]
       [:div.h-full.w-full  ; .flex-auto
        {:style {:flex "75%"
                 }}
        [tradingview-algo algo-ctx]] 
       [:div {;:class "flex-auto"
              :style {;:width "300"
                      ;:min-width "300"
                      :flex "25%"
                      }}
        [trades-view]]
       
     
     ])

(page/add trades-page :joseph/trades)
