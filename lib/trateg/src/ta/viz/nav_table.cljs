(ns ta.viz.nav-table
  (:require
   [goldly.js :refer [to-fixed]] 
   [tick.goldly]
   [tick.core :as tick]
   [ui.aggrid :refer [aggrid]]))
 
 (defn fmt-yyyymmdd [dt]
  (if dt
    (tick/format (tick/formatter "YYYY-MM-dd") dt)
    ""))
 
(defn round-number-digits
  [digits number] ; digits is first parameter, so it can easily be applied (data last)
  (if (nil? number) "" (to-fixed number digits)))

(defn nav-table [nav]
  [aggrid {:data nav
           :columns [{:field :date :format fmt-yyyymmdd}
                     {:field :open# }
                     {:field :long$ :format #(round-number-digits 0 %)}
                     {:field :short$ :format #(round-number-digits 0 %)}
                     {:field :net$ :format #(round-number-digits 0 %)}
                     {:field :pl-u :format #(round-number-digits 0 %)}
                     {:field :pl-r :format #(round-number-digits 0 %)}
                     {:field :pl-r-cum :format #(round-number-digits 0 %)}]
           :box :fl
           :pagination :false
           :paginationAutoPageSize false}])