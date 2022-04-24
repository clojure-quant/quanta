(ns gann.svg
  (:require
   [user :refer [println run-a link-href add-page aggrid to-fixed]]))

(defn round-number-digits
  [digits number] ; digits is first parameter, so it can easily be applied (data last)
  (if (nil? number) "" (to-fixed number digits)))

(defn box [{:keys [at bt zoom dp ap bp idx-p idx-t]}]
  [:tr
   [:td zoom]
   [:td idx-p]
   [:td idx-t]
   [:td ap]
   [:td bp]
   [:td (str at)]
   [:td (str bt)]])

(defn box-table [boxes]
  (when boxes
    (into [:table.bg-yellow-300
           [:tr
            [:td "zoom"]
            [:td "idx-p"]
            [:td "idx-t"]
            [:td "ap"]
            [:td "bp"]
            [:td "at"]
            [:td "bt"]]]
          (map box boxes))))

(defn box-table [boxes]
  (when boxes
    [:div.p-2.bg-blue-300 
     {:style {:width "100%" ;"40cm"
      :height "100%" ;"70vh" ;  
      ;:background-color "blue"
              }}
      [aggrid {:data boxes
                ;  dp   idx-p idx-t
               :columns [{:field :at ;:format (partial round-number-digits 2)
                          }
                           {:field :bt ;:format (partial round-number-digits 2)
                            }
                           {:field :zoom ;:format (partial round-number-digits 2)
                            }
                           {:field :ap :format (partial round-number-digits 4)
                            }
                           {:field :bp :format (partial round-number-digits 4)
                            }]
               :box :fl
               :pagination false
               :paginationAutoPageSize true}]]))


(defonce *state (r/atom {:table false
                     :params {:s "BTCUSD"
                              :dt-start "2021-01-01"
                              :dt-end  "2022-12-31"}
                     :root {}
                     :data [:div "data not yet loaded."]}))


(defn get-data [& args]
  (let [p (:params @*state)
        height (.-innerHeight js/window)
        width (.-innerWidth js/window)
        p2 (assoc p :height height :width width)]
    (println "params: " p)
    (println "params: " p2)
    (run-a *state [:data] :gann/svg p2)
    (run-a *state [:boxes] :gann/boxes p2)))

(defn rootbox []
  (let [symbol-loaded (r/atom nil)]
    (fn []
      (let [symbol (get-in @*state [:params :s])]
        [:div
           (when-not (= symbol @symbol-loaded)
              (reset! symbol-loaded symbol)
              (run-a *state [:root] :gann/load symbol)
              nil)
         [:p "root box:"]
         [:p (pr-str (:root @*state))]
        ]
         ))))




(defn menu []
  [:div.bg-gray-300.w-full ;.flex.flex-cols
    [:div.w-full
      [input/select
        {:nav? false
         :items ["BTCUSD" "SPY" "QQQ" "GLD" "SLV" "EURUSD"]}
         *state [:params :s]]]
    [rootbox]
    [input/textbox {:placeholder "Start Date"}
       *state [:params :dt-start]]
    [input/textbox {:placeholder "End Date"}
       *state [:params :dt-end]]
    [input/button {:on-click get-data} "show gann"]
    [input/checkbox {} *state [:table?]]
    [:p "boxes: " (count (:boxes @*state) )]
    [link-href "/" "main"]])

(defn float-table []
  [container/rnd {:bounds "window"
                  :default {:width 600
                            :height 300
                            :x 400
                            :y 60}
                  :style {:display "flex"
                          ;:alignItems "center"
                          :justifyContent "center"
                          :border "solid 1px #ddd"
                          :background "#f0f0f0"}}
   [box-table (:boxes @*state)]])

(defn float-menu []
  [container/rnd {:bounds "window"
                  :default {:width 140
                            :height 270
                            :x 50
                            :y 60}
                  :style {:display "flex"
                          ;:alignItems "center"
                          :justifyContent "center"
                          :border "solid 1px #ddd"
                          :background "#f0f0f0"}}
   [menu]])

(defn gann-page [{:keys [route-params query-params handler] :as route}]
  (fn [{:keys [route-params query-params handler] :as route}]
    [:div.w-screen.h-screen.p-0.m-0
     [float-menu]
     (when (:table? @*state)
       [float-table])
     (:data @*state)
   ;[:div (pr-str (:boxes @*state))]
   ;[:div.bg-gray-500.mt-12 "params:" (pr-str (:params @*state))]
     ]))
(add-page gann-page :gann)