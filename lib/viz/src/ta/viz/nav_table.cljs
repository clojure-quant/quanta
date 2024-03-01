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
  [aggrid {:box :fl
           :data nav
           :columns [{:field :date :format fmt-yyyymmdd}
                     {:field :open# :type "rightAligned"}
                     {:field :long$ :format #(round-number-digits 0 %) :type "rightAligned"}
                     {:field :short$ :format #(round-number-digits 0 %) :type "rightAligned"}
                     {:field :net$ :format #(round-number-digits 0 %) :type "rightAligned"}
                     {:field :pl-u :format #(round-number-digits 0 %) :type "rightAligned"}
                     {:field :pl-r :format #(round-number-digits 0 %) :type "rightAligned"}
                     {:field :pl-r-cum :format #(round-number-digits 0 %) :type "rightAligned"}]

           :pagination :false
           :paginationAutoPageSize false}])