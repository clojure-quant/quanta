(ns ta.tradingview.goldly.view.aggrid
  (:require
   [re-frame.core :as rf]
   ;[goldly.js]
   ;[tick.goldly]
   [ui.aggrid :refer [aggrid]]))

(defn round-number-digits
  [digits number] ; digits is first parameter, so it can easily be applied (data last)
  (if (nil? number) ""
      (str number) ; (goldly.js/to-fixed number digits)
      ))

(defn fmt-yyyymmdd [dt]
  (when dt
    ;(tick.goldly/dt-format "YYYYMMdd" dt)
    (str dt)))

; use fresh theme
;(re-frame.core/dispatch [:css/set-theme-component :aggrid "fresh"])

(defn has-trades? [data]
  (let [row1 (first data)
        cols (keys row1)]
    (some #(= % :trade) cols)))

(def time-cols
  [:index
   {:field :date} ;:format fmt-yyyymmdd
   ])

(def bar-cols
  [{:field :open :format (partial round-number-digits 2)}
   {:field :high :format (partial round-number-digits 2)}
   {:field :low :format (partial round-number-digits 2)}
   {:field :close :format (partial round-number-digits 2)}
   {:field :volume :format (partial round-number-digits 0)}])

(def trade-cols
  [:trade
   :trade-no
   :position
   :signal])

(def default-study-cols
  [:volume :date :low :open :close :high :symbol :signal :index :trade :trade-no :position])

(defn is-default-col? [c]
  (some #(= % c) default-study-cols))

(defn study-extra-cols [data]
  (let [row1 (first data)
        cols (keys row1)]
    (remove is-default-col? cols)))

(defn study-columns  [data]
  (let [extra-cols (or (study-extra-cols data) [])]
    (if (has-trades? data)
      (concat time-cols bar-cols trade-cols extra-cols)
      (concat time-cols bar-cols extra-cols))))

(defn study-table [_ data]
  (if data
    [:div.w-full.h-full
     ;[:div.bg-red-500 (pr-str data)]
     [:div {:style {:width "100%" ;"40cm"
                    :height "100%" ;"70vh" ;  
                    :background-color "blue"}}
      [aggrid {:data data
               :columns (study-columns data)
               :box :fl
               :pagination :true
               :paginationAutoPageSize true}]]]
    [:div "no data"]))

(defn bars-table [bars]
  (if bars
    [:div.w-full.h-full
     ;[:div.bg-red-500 (pr-str data)]
     [:div {:style {:width "100%" ;"40cm"
                    :height "100%" ;"70vh" ;  
                    :background-color "blue"}}
      [aggrid {:data bars
               :columns (concat [{:field "date"}]
                                bar-cols)
               :box :fl
               :pagination :true
               :paginationAutoPageSize true}]]]
    [:div "no bars available"]))

(defn table [data]
  [aggrid {:data data
           :box :lg
           :pagination :false
           :paginationAutoPageSize true}])