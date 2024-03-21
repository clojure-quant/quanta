(ns ta.tradingview.goldly.dialog
  (:require
   [re-frame.core :as rf]
   [ta.tradingview.goldly.algo.context :as c]
   [ta.tradingview.goldly.view.aggrid :refer [study-table]]))

(defn algo-dialog [algo-ctx]
  [:div.bg-blue-300.p-5
   [:h1.text-blue-800.text-large "algo options"]
   [:p (pr-str (c/get-algo-input algo-ctx))]

   [:h1.text-blue-800.text-large "charts"]
   [:p (pr-str (c/get-algo-input algo-ctx))]])

(defn show-algo-dialog [algo-ctx]
  (rf/dispatch [:modal/open (algo-dialog algo-ctx)
                :medium]))

(defn table-dialog-table [algo-ctx]
  (fn []
    (let [data (c/get-data algo-ctx)
          data (update data :ds-study (fn [rows] (take-last 100 rows)))]
      (cond
        (nil? data)
        [:div "No Data available!"]
        (empty? data)
        [:div "Data is empty"]
        :else
        #_[:div
           [:div (pr-str (keys data))]
           [:div (pr-str (:ds-study data))]]
        [study-table nil (:ds-study data)]))))

(defn table-dialog [algo-ctx]
  [:div.bg-blue-300.p-5.w-full;.h-64
   {:style {:height "10cm"}}
   [table-dialog-table algo-ctx]])

(defn show-table-dialog [algo-ctx]
  (rf/dispatch [:modal/open (table-dialog algo-ctx)
                :large]))
