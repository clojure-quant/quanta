(ns ta.viz.chart-spec)

(defn chart-pane-spec? [spec]
  true)

(defn chart-count [panes]
  (count panes))

(defn get-chart [spec idx]
  (get spec idx))

(defn- ensure-type-keyword [{:keys [type] :as opts}]
  (if (string? type)
    (assoc opts :type (keyword type))
    opts))

(defn pane->series [idx pane]
  (map (fn [[col type]]
         (cond
           (map? type)
           (merge {:axis idx :column col} (ensure-type-keyword type))

           (string? type)
           {:axis idx :column col :type (keyword type)}

           (keyword type)
           {:axis idx :column col :type type})) pane))

(defn chart->series [chart]
  (->> (map-indexed pane->series chart)
       (reduce concat)))

(defn required-columns [chart]
  (let [series (chart->series chart)]
    (map :column series)))

(defn get-series [chart]
  (let [series (chart->series chart)]
    (map (juxt :column :type) series)))

(defn axes-count [chart]
  (count chart))

(defn chart-cols [chart]
  (->> (concat [:date :open :high :low :close]
               (required-columns chart))
       (into [])))

(comment

  (pane->series 2 {:volume "column"})

  (pane->series 3 {:sma-st "line"
                   :sma-lt "line"
                   :sma-diff {:type "line" :color "red"}})

  (chart->series
   [{;:trade "flags"
     :bb-lower {:type "line"
                :linewidth 2
             ;:color (color :blue-900)
                }
     :bb-upper {:type "line"
                :linewidth 4
             ;:color (color :red)
                }}
    {:volume {:type "line"
           ;:color (color :gold)
           ;:plottype (plot-type :columns)
              }}])

  (required-columns
   [{:sma-lt "line"
     :sma-st "line"
     :trade "flags"}
    {:volume "column"}])

  (chart-cols
   [{:sma-lt "line"
     :sma-st "line"
     :trade "flags"}
    {:volume "column"}])

  (series-input
   [{:sma-lt "line"
     :sma-st "line"
     :trade "flags"}
    {:volume "column"}])

  [nil ; nothing to add in price pane
   {:volume "column"}]

  [{:sma-st "line"
    :sma-lt "line"
    :sma-diff {:type "line" :color "red"}}]

  [{:trade "flags"}
   {:volume "column"}]

  [;{:trades "line"}
   #_{:volume {:type "line"
               :plottype (plot-type :columns)}}]

  [;nil ; {:trade "flags"}
           ;{:trade "chars" #_"arrows"}
   {:signal-text {:type "chars"
                  :char "!"
                  :textColor (color :steelblue)
                  :title "moon-phase-fullmoon" ; title should show up in pane settings
                  }}
   {:volume {:type "line" :plottype (plot-type :columns)}}]

  [{:sma-lt "line"
    :sma-st "line"
               ;:trade "flags"
    }
   {:volume "column"}]

  [{:sr-up-0 "line"
    :sr-up-1 "line"
    :sr-up-2 "line"
    :sr-down-0 {:type "line" :color "red"}
    :sr-down-1 {:type "line" :color "red"}
    :sr-down-2 {:type "line" :color "red"}}
   {:cross-up-close "column"
    :cross-down-close "column"}
   {:qp "column"
                         ;:qt "column"
    }
                       ;{:index "column"}
                       ; {:qt-jump-close "column"}
   ]
  {:type "candlestick"}

 ;
  )