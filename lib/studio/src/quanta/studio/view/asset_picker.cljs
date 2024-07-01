(ns quanta.studio.view.asset-picker
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [promesa.core :as p]
   [goldly.service.core :refer [clj]]))

(defn asset-table [assets set-fn]
  (into [:table
         [:tr
          [:td "Symbol"]
          [:td "Name"]
          [:td "Category"]
          [:td "Exchange"]]]
        (map (fn [asset]
               [:tr {:class "hover:bg-blue-700"
                     :on-click (fn [& _]
                                 (set-fn (:symbol asset))
                                 (rf/dispatch [:modal/close]))}
                [:td (:symbol asset)]
                [:td (:name asset)]
                [:td (str (:category  asset))]
                [:td (str (:exchange  asset))]]) assets)))

(defn asset-dialog [asset-initial set-fn]
  (let [asset-initial (or asset-initial "")
        asset (r/atom asset-initial)
        assets (r/atom [])
        search-fn (fn []
                    (let [rp (clj 'ta.db.asset.db/search @asset)]
                      (p/then rp (fn [r] (reset! assets r)))
                      ;(p/catch rp (fn [r] (swap! a assoc :status :error :error r)))
                      ))]
    (fn [asset-initial set-fn]
      [:div.bg-blue-300.p-5 {:style {"z-index" 9999}}
       [:h1.text-blue-800.text-large "asset search"]
       [:input {:type "text"
                :value @asset
                :placeholder "Search Asset .."
                :on-change (fn [e]
                             (let [v (-> e .-target .-value)]
                               (println "search value:  " v)
                               (reset! asset v)
                               (search-fn)))}]
       [:h1.text-blue-800.text-large "assets:"]
       [:div.w-full.h-64.overflow-scroll
        [asset-table @assets set-fn]]])))

(defn asset-dialog-wrapped [asset-initial set-fn]
  [asset-dialog asset-initial set-fn])

(defn show-algo-dialog [asset-initial set-fn]
  (rf/dispatch [:modal/open (asset-dialog-wrapped asset-initial set-fn)
                :medium]))

(defn editor-asset-picker [{:keys [set-fn options]} current-val]
  (let [{:keys [class name] :or {class "" name ""}} options]
    [:div.flex.flex-row.w-100
     [:input {:class "w-32" ;class
              :type "text"
              :value current-val
              :placeholder (str "Asset: " name)
              :on-change (fn [e]
                           (let [v (-> e .-target .-value)]
                             ;(show-algo-dialog current-val set-fn)
                            ;(println "setting checkbox to: " v)
                             (set-fn v)))}]
     [:button {:class "bg-blue-500 p-1"
               :on-click (fn [& _]
                           (show-algo-dialog current-val set-fn))}
      " .. "]]))