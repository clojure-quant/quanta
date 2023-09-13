(ns joseph.lib.quote-table
  (:require
   [clojure.string :as str]
   [goldly.js :refer [to-fixed]]
   [tick.goldly]
   [tick.core :as tick]
   [ui.aggrid :refer [aggrid]]
   ))



(defn quote-table [quotes]
  [aggrid {:box :fl
           :data quotes
           :columns [{:field :symbol}
                     {:field :date}
                     {:field :time}
                     ;{:field :timezone}
                     {:field :open}
                     {:field :high}
                     {:field :low}
                     {:field :close}
                     {:field :volume}
                     {:field :changepercent}
                     ;{:field :lastprice}
                     ;{:field :lastvolume}
                    ]

           :pagination :false
           :paginationAutoPageSize false}])