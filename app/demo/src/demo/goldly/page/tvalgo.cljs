(ns tvalgo
  (:require 
    [r :refer :all]
    [user :refer [run-a link-href add-page]]))

(defonce algo-state
  (r/atom {:algos []
           :algo nil
           :algoinfo {:options {}
                      :charts []}
           :symbols ["TLT" "SPY" "QQQ" "EURUSD"]
           :symbol "SPY"
           :frequency "D"}))

(defonce window-state
  (r/atom {:data []}))


(run-a algo-state [:algos] :algo/names) ; get once the names of all available algos

(defn algo-info [algo]
  (let [algo-loaded (r/atom nil)]
    (when algo
      (when-not (= @algo-loaded algo)
        (run-a algo-state [:algoinfo] :algo/info algo)
        nil))))


(defn algo-dialog []
  [:div.bg-blue-300.p-5
    [:h1.text-blue-800.text-large "algo options"]
    [:p (pr-str (get-in @algo-state [:algoinfo :options]))]
    [:h1.text-blue-800.text-large "charts"]
   [:p (pr-str (get-in @algo-state [:algoinfo :charts]))]
    [:p "options are view only at the moment!"]
   ])


(defn get-window []
  (let [epoch-start 1642726800 ; jan 21 2022
        epoch-end 1650499200 ; april 21 2022
        algo (:algo @algo-state)
        symbol (:symbol @algo-state)
        frequency (:frequency @algo-state)
        options (get-in @algo-state [:algoinfo :options])]
     (run-a window-state [:data] :algo/run-window
            algo symbol frequency options epoch-start epoch-end)
  ))


(defn algo-menu []
  [:div.flex.flex-row.bg-blue-500
   [link-href "/" "main"]
   [input/select {:nav? false
                  :items (or (:algos @algo-state) [])}
    algo-state [:algo]]
   [input/select {:nav? false
                  :items (:symbols @algo-state)}
    algo-state [:symbol]]
   [input/button {:on-click #(rf/dispatch [:modal/open (algo-dialog)
                                          :medium])} "options"]
   
   [input/button {:on-click get-window} "get window"]
   
   ])

(defn algo-ui []
  (fn []
    (let [{:keys [algos algo]} @algo-state]
      [:div.flex.flex-col.h-full.w-full
       ;(do (run-algo algo opts data-loaded)
       ;    nil)
       [algo-menu]
       [algo-info algo]
       (when-let [wd (get-in @window-state [:data])]
          [:div (pr-str wd)]  
         )
       ;[page-renderer data page]
       ])))

(defn tvalgo-page [route]
  [:div.h-screen.w-screen.bg-red-500
   [algo-ui]])

(add-page tvalgo-page :algo/tv)
