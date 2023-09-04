(ns demo.goldly.page.joseph
  (:require
   [reagent.core :as r]
   [goldly.page :as page]
   [demo.goldly.lib.loader :refer [clj->a]]
   [demo.goldly.lib.ui :refer [link-href]]
   [ui.aggrid :refer [aggrid]]
   [tick.goldly]
   [tick.core :as tick]
     [input]
   ;[demo.goldly.view.tsymbol :refer [symbol-picker]]
   ))


 (defn fmt-yyyymmdd [dt]
  (println "date: " dt " type: " (type dt))
  (set! (.-mydate js/window) dt)
  dt
  #_(when dt
    (tick/format (tick/formatter "YYYYMMdd") dt)
    "")
    )

(defn symbols [trades]
  (->> trades
       (map :symbol)
       (into #{})
       (into [])))

(defn on-click [args]
  (let [args-clj (js->clj args)
        data (get args-clj "data")]
    ;(println "on-click args: " args-clj)  
    (println "on-click data: " data)))

(defn trade-table [trades]
  [aggrid {:data trades
           :columns [{:field :symbol}
                     {:field :direction :onCellClicked on-click }
                     {:field :entry-date :format fmt-yyyymmdd}
                     {:field :exit-date}
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
   [:div.flex.flex-col.h-full.w-full
      ;[:div.flex.flex-row.bg-blue-500
    [link-href "/" "main"]
    ;[series-view  "BTCUSD" "D"]
    [trades-view]
   ;    ]
    ]])

(page/add trades-page :joseph/trades)
