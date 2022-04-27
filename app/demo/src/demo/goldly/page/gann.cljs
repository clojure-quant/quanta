(ns gann.svg
  (:require
   [user :refer [println run-a run-cb link-href add-page aggrid to-fixed]]))

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
                        {:field :ap :format (partial round-number-digits 4)}
                        {:field :bp :format (partial round-number-digits 4)}]
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
        p-with-size (assoc p :height height :width width)
        p-with-size-rootbox (assoc p-with-size :root-box (:root @*state))
        ]
    (println "getting gann-svg with params: " p-with-size-rootbox)
    (run-a *state [:data] :gann/svg p-with-size-rootbox)
    (run-a *state [:boxes] :gann/boxes p-with-size-rootbox)
    ))


(defn save-rootbox []
  (let [root-box (:root @*state)]
    (println "saving root-box: " root-box)
    (run-cb {:fun :gann/save 
             :args [root-box]
             :timeout 1000
             :cb #(js/alert "rootbox saved!")
             })
                 
    ))


(defn rootbox-ui [{:keys [at ap bt bp] :as box}]
  ;[:p (pr-str (:root @*state))]
  [:div.grid 
    {:style {:grid-template-columns "60px 1fr"}}
   
    [:span.bg-blue-500 "at"]
    [input/textbox {:class "w-full"
                    :placeholder "Start Date"}
       *state [:root :at]]
    
    [:p "bt"]
    [input/textbox {:class "w-full"
                    :placeholder "End Date"}
         *state [:root :bt]]
   
    [:p "ap"]
    [input/textbox {:class "w-full"
                    :placeholder "Start Price"}
       *state [:root :ap]]
  
     [:p "bp"]
     [input/textbox {:class "w-full"
                     :placeholder "End Price"}
        *state [:root :bp]]
    
     [:span ""]
     [input/button {:on-click save-rootbox} "save rootbox"]
   
   ])

(defn rootbox []
  (let [symbol-loaded (r/atom nil)]
    (fn []
      (let [symbol (get-in @*state [:params :s])]
        [:div
         (when-not (= symbol @symbol-loaded)
           (reset! symbol-loaded symbol)
           (run-a *state [:root] :gann/load symbol)
           nil)
         [:p.text-blue-500.text-bold "root box"]
         [rootbox-ui (:root @*state)]
         ]))))



(defn menu []
  [:div.bg-gray-300.w-full ;.flex.flex-cols
   [:div.w-full
    [input/select
     {:nav? false
      :items ["BTCUSD" "SPY" "QQQ" "GLD" "SLV" "EURUSD"]}
     *state [:params :s]]]
   [rootbox]
   [:p.text-blue-500.text-bold "show"]
   [:div.grid {:style {:grid-template-columns "60px 1fr"}}
      [:span "start"]
      [input/textbox {:class "bg-red-500 text-black-500 w-full"
                      :placeholder "Start Date"}
       *state [:params :dt-start]]
      [:span "end"]
      [input/textbox {:class "bg-red-500 text-black-500 w-full"
                      :placeholder "End Date"}
       *state [:params :dt-end]]
    
      [:span ""]
      [input/button {:on-click get-data} "show gann"] 
    
      [:span "table?"]
      [input/checkbox {} *state [:table?]]

    ]
   
   
   [:p "boxes: " (count (:boxes @*state))]
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
                  :default {:width 200
                            :height 400
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