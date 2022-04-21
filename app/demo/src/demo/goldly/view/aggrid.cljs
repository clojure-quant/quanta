(defn round-number-digits
  [digits number] ; digits is first parameter, so it can easily be applied (data last)
  (if (nil? number) "" (to-fixed number digits)))

(defn fmt-yyyymmdd [dt]
  (when dt
    (dt-format "YYYYMMdd" dt)))

; use fresh theme
(rf/dispatch [:css/set-theme-component :aggrid "fresh"])

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
    [:div "no data "]))


(defn table [data]
  [aggrid {:data data
           :box :lg
           :pagination :false
           :paginationAutoPageSize true}])