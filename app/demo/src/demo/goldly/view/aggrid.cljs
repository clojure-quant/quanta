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

(defn study-columns  [context data]
  (let [extra-cols (or context [])]
  ;(println "first row: " data)
    (println "context: " context)
    (if (has-trades? data)
      (concat time-cols bar-cols trade-cols extra-cols)
      (concat time-cols bar-cols extra-cols))))

(defn table [data]
  [aggrid {:data data
           :box :lg
           :pagination :false
           :paginationAutoPageSize true}])