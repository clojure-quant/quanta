(ns quanta.notebook.interact-debug
  (:require
   [tablecloth.api :as tc]
   [tech.v3.dataset.print :refer [print-range]]
   [ta.interact.template :refer [get-options]]
   [ta.interact.subscription :as sub]
   [ta.interact.debug :refer [dump-dataset]]))

(defn get-sub []
  (let [active (-> @sub/subscriptions-a keys)
                 ;; => ("Z7FaM3" "huAQz9")
        ]
    (when (= 1 (count active))
      (first active))))

(-> @sub/subscriptions-a (get subscription-id))
(-> @sub/results-a (get subscription-id))
(-> @sub/visualizations-a (get subscription-id))

(defn get-result []
  (let [id (get-sub)]
    (when id
      (-> @sub/results-a (get id) deref))))

(get-result)

(defn dump-ds [ds]
  (dump-dataset "/tmp/test.txt" ds))

(-> (get-result)
    (tc/select-columns [:date :close :high :b2-upper
                        :b2h-crossed?  :b2h-cross-high
                        :carried-high  :carried-crossed?])
    (print-range :all)
    (dump-ds))

(defn get-viz []
  (let [id (get-sub)]
    (when id
      (-> @sub/visualizations-a (get id)))))

(get-viz)

(def v @(get-viz))

v

(-> v keys)

(:spec v)
  ;; => {:ohlc-height 600,
  ;;     :boost false,
  ;;     :series
  ;;     [{:color "blue",
  ;;       :allowOverlapX true,
  ;;       :dataGrouping {:enabled false},
  ;;       :name ":carried-crossed?",
  ;;       :type "flags",
  ;;       :yAxis 0,
  ;;       :id ":carried-crossed?",
  ;;       :shape "squarepin",
  ;;       :zIndex 9999,
  ;;       :fillColor "rgba(255, 255, 255, .4)",
  ;;       :onSeries ":close"}
  ;;      {:type "line",
  ;;       :id ":b1-upper",
  ;;       :name ":b1-upper",
  ;;       :yAxis 0,
  ;;       :zIndex 1000,
  ;;       :dataGrouping {:enabled false},
  ;;       :color "black"}
  ;;      {:color "blue",
  ;;       :allowOverlapX true,
  ;;       :dataGrouping {:enabled false},
  ;;       :name ":b2h-crossed?",
  ;;       :type "flags",
  ;;       :yAxis 0,
  ;;       :id ":b2h-crossed?",
  ;;       :shape "squarepin",
  ;;       :zIndex 9999,
  ;;       :fillColor "rgba(255, 255, 255, .4)",
  ;;       :onSeries ":close"}
  ;;      {:color "blue",
  ;;       :dataGrouping {:enabled false},
  ;;       :name ":b1-mid",
  ;;       :marker {:enabled true, :radius 2},
  ;;       :type "line",
  ;;       :yAxis 0,
  ;;       :id ":b1-mid",
  ;;       :lineWidth 0,
  ;;       :zIndex 1000}
  ;;      {:type "candlestick",
  ;;       :id ":close",
  ;;       :name ":close",
  ;;       :yAxis 0,
  ;;       :zIndex 1000,
  ;;       :dataGrouping {:enabled false},
  ;;       :color "blue"}
  ;;      {:type "line",
  ;;       :id ":b2-lower",
  ;;       :name ":b2-lower",
  ;;       :yAxis 0,
  ;;       :zIndex 1000,
  ;;       :dataGrouping {:enabled false},
  ;;       :color "red"}
  ;;      {:type "line",
  ;;       :id ":b2-upper",
  ;;       :name ":b2-upper",
  ;;       :yAxis 0,
  ;;       :zIndex 1000,
  ;;       :dataGrouping {:enabled false},
  ;;       :color "red"}
  ;;      {:type "line",
  ;;       :id ":b1-lower",
  ;;       :name ":b1-lower",
  ;;       :yAxis 0,
  ;;       :zIndex 1000,
  ;;       :dataGrouping {:enabled false},
  ;;       :color "black"}
  ;;      {:type "line",
  ;;       :id ":carried-high",
  ;;       :name ":carried-high",
  ;;       :yAxis 0,
  ;;       :zIndex 1000,
  ;;       :dataGrouping {:enabled false},
  ;;       :color "blue",
  ;;       :step true}
  ;;      {:type "column",
  ;;       :id ":bars-above-b1h",
  ;;       :name ":bars-above-b1h",
  ;;       :yAxis 1,
  ;;       :zIndex 1000,
  ;;       :dataGrouping {:enabled false},
  ;;       :color "blue"}
  ;;      {:type "column",
  ;;       :id ":volume",
  ;;       :name ":volume",
  ;;       :yAxis 2,
  ;;       :zIndex 1000,
  ;;       :dataGrouping {:enabled false},
  ;;       :color "blue"}],
  ;;     :other-height 100,
  ;;     :box :fl,
  ;;     :rangeSelector false,
  ;;     :plotOptions {:series {:animation 0}},
  ;;     :chart {:height 900},
  ;;     :yAxis
  ;;     [{:resize {:enabled true}, :lineWidth 2, :labels {:align "right", :x -3}, :height 600, :title {:text "OHLC"}}
  ;;      {:resize {:enabled true}, :lineWidth 2, :labels {:align "right", :x -3}, :top 600, :height 100}
  ;;      {:resize {:enabled true}, :lineWidth 2, :labels {:align "right", :x -3}, :top 700, :height 100}
  ;;      {:resize {:enabled true}, :lineWidth 2, :labels {:align "right", :x -3}, :top 800, :height 100}],
  ;;     :credits {:enabled false},
  ;;     :navigator false,
  ;;     :xAxis {:crosshair {:snap true}},
  ;;     :tooltip {:style {:width "200px"}, :valueDecimals 4, :shared true}}

(:data v)

;{:x 1711227540000, :title "true", :shape "url(/r/arrow-down.svg)", :text "col: :carried-crossed?val: true"}
  ;{:x 1711240080000, :title "false", :shape nil, :text "col: :carried-crossed?val: false"}
  
  