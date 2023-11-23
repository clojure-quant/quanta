(ns juan.ui
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [goog.string]
   [spaces]
   [rtable.rtable :refer [rtable]]))

(defonce juan-table (r/atom []))

(rf/reg-event-fx
 :juan/table
 (fn [_ [_ msg]]
   (reset! juan-table msg)
   nil))

(defn left-right-top [{:keys [top left right]}]
  [spaces/viewport
   [spaces/top-resizeable {:size 50} top]
   [spaces/fill
    [spaces/left-resizeable {:size "50%" :scrollable false} left]
    [spaces/fill {:scrollable false} right]]])


(defn fmt-digits [nr]
  (if (nil? nr)
    ""
    (if (string? nr)
      nr
      (goog.string/format "%.5f" nr))))

(defn fmt-0digits [nr]
  (if (nil? nr)
    ""
    (if (string? nr)
      nr
      (goog.string/format "%.1f" nr))))

(defn format-signal [s]
  (println "format-signal: " (pr-str s))
  (cond
    (= s :long)  "▲"  ; U+25B2 Black up-pointing triangle
    (= s :short) "▼"  ; U+25BC Black down-pointing triangle
    (= s false)  "○"  ; U+25CB White circle
    (= s "false")  "○"  ; U+25CB White circle
    (nil? s) "♿" ; wheelchair
    :else "?"))

(defonce detail-symbol (r/atom nil))

(defn format-symbol [s]
  [:a {:on-click #(reset! detail-symbol s)} s])

(defn sanitize-signal [s]
  (println "sanitizing signal: " s)
  (if (= s false)
    "false"
    s))

(defn juan-table-ui [data]
  (let [data (map (fn [d]
                    (println "D: " d)
                    (assoc d :spike-signal (sanitize-signal (:spike-signal d))
                           :sentiment-signal (sanitize-signal (:sentiment-signal d))
                           :setup-signal (sanitize-signal (:setup-signal d))
                           :pivot-signal (sanitize-signal (:pivot-signal d))
                           )) data)]
    [rtable {:class "table-head-fixed padding-sm table-blue table-striped table-hover"
             :style {:width "100%" ; "100vw" ;"50vw"
                     :height "100%" ; "100vh" ;"40vh"
                     :border "3px solid green"}}
     [{:path :symbol :header "Symbol" :format format-symbol}
      {:path :close :header "PClose"}
      {:path :price :header "PCurrent"
     ;:format icon
       }
      {:path :change :header "chg" :format fmt-digits}
      {:path :change-atr-prct :header "chg-atr%" :format fmt-0digits}
      {:path :sentiment-long-prct :header "SentLong%"}
    ;{:path :age
    ; :attrs (fn [age] {:class (age-color age)
    ;                   :style {:text-align "right"
    ;                           :display "block"
    ;                           :width "2cm"
    ;                           :max-width "2cm"
    ;                           :overflow "hidden"
    ;                           :text-overflow "ellipsis"
    ;                           :white-space "nowrap"}})}
    ;{:path :sex :format format-sex}
      {:path :spike-signal :format format-signal :header "Spike"}
      {:path :sentiment-signal  :format format-signal :header "Sentiment"}
      {:path :setup-signal :format format-signal :header "Setup"}
      {:path :pivot-signal :format format-signal :header "Pivot"}]
     data]))




(defn pivot-table-ui [data]
  [rtable {:class "table-head-fixed padding-sm table-blue table-striped table-hover"
           :style {:width "6cm" ; "100vw" ;"50vw"
                   :height "10cm" ; "100vh" ;"40vh"
                   :border "3px solid green"}}
   [{:path :name :header "Pivot Name"}
    {:path :price :header "Pivot Price"}] data])

(defn pivot-map->table [pivots]
  (map (fn [[k v]] {:name k :price v}) pivots))


(defn detail-view []
  (let [s @detail-symbol
        data (when s
               (->> (filter #(= s (:symbol %)) @juan-table)
                    first))
        pivots (when data
                 (-> data :pivots pivot-map->table))
        data-no-pivots (when data (dissoc data :pivots))]
    [:div
     (if s
       [:div
        [:p.m-5 "Details for: symbol: " s " data: " (pr-str data-no-pivots)]
        [pivot-table-ui pivots]
        ;[:p "Pivot data: " (pr-str pivots)]
        ]
       [:p "Click on a symbol to show details!"])
     ;[:p (pr-str @juan-table)]
     ]))

(defn juan-page [{:keys [_route-params _query-params _handler] :as _route}]
  [left-right-top
   {:top [:div.text-blue.text-xl.bg-yellow-300 "juan fx spike reversal strategy"]
    :left [juan-table-ui @juan-table]
    :right [detail-view]}])


;{:setup-signal false, 
; :symbol "EURUSD", 
; :pivot-signal nil, 
; :close 1.08882, 
; :close-dt #time/date-time "2023-11-22T00:00", 
; :sentiment-long-prct 20, 
; :close1-dt #time/date-time "2023-11-21T00:00", 
; :close1 1.09108, 
; :change-atr-prct 21.903870945479895, 
; :atr 0.007487000000000044, 
; :spike-signal false, 
; :change 0.0016399428176880892, 
; :pivots {:p0-high 1.09229, :p0-low 1.08522, :p1-high 1.09652, :p1-low 1.08999, :pweek-high 1.09652, :pweek-low 1.08246},
; :price 1.09046,
; :pivot-nearby nil, 
; :sentiment-signal :long}