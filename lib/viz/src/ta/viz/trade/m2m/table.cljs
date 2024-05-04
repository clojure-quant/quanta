(ns ta.viz.trade.m2m.table
  (:require
   [tick.core :as t]
   [ui.aggrid :refer [aggrid]]))

(defn to-fixed [n d]
  (.toFixed n d))

(defn fmt-yyyymmdd [dt]
  (if dt
    (t/format (t/formatter "YYYY-MM-dd") dt)
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

(def nav-cols
  [:year
   :month
   {:field :nav :format (partial round-number-digits 2)}
   {:field :drawdown :format (partial round-number-digits 5)}
   {:field :cum-pl-t :format (partial round-number-digits 5)}
   {:field :pl-log-cum :format (partial round-number-digits 5)}
   :trades])

(defn navs-view [_context navs]
  [:div.h-full.w-full.flex.flex-col
   [:h1 "navs " (count navs)]
   (when (> (count navs) 0)
     [:div {:style {:width "100%" ; "40cm"
                    :height "100%" ; "70vh"
                    :background-color "blue"}}
      [aggrid {:data navs
               :columns nav-cols
               :box :fl
               :pagination :true
               :paginationAutoPageSize true}]])])
