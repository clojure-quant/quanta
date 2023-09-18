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
           :columns [{:field :symbol :width 50}
                     {:field :date :width 50}
                     {:field :time :width 50}
                     ;{:field :timezone}
                     {:field :open :width 50}
                     {:field :high :width 50}
                     {:field :low :width 50}
                     {:field :close :width 50}
                     {:field :volume :width 50}
                     {:field :changepercent :width 50}
                     ;{:field :lastprice}
                     ;{:field :lastvolume}
                    ]

           :pagination :false
           :paginationAutoPageSize false}])