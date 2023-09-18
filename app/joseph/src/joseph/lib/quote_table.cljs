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
           :columns [{:field :symbol :width 80}
                     {:field :date :width 100}
                     {:field :time :width 80}
                     ;{:field :timezone}
                     {:field :open :width 70 :type "rightAligned"}
                     {:field :high :width 70 :type "rightAligned"}
                     {:field :low :width 70 :type "rightAligned"}
                     {:field :close :width 70 :type "rightAligned"}
                     {:field :volume :width 80 :type "rightAligned"}
                     {:field :changepercent :width 50 :type "rightAligned"}
                     ;{:field :lastprice}
                     ;{:field :lastvolume}
                    ]
           :autosize-columns false
           :pagination :false
           :paginationAutoPageSize false}])