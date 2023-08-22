(ns demo.goldly.lib.ui
  (:require
   [re-frame.core :as rf]
   [layout]
   [site]
   [goldly.page :as page]
   [goldly.js] ; to-fixed
   [ui.rnd] ;  rnd
   ))

;; links

(defn link-fn [fun text]
  [:a.bg-blue-600.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-600.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

;; site layout

(defn site-header []
  [site/header-menu
   {:brand "DemoGoldly"
    :brand-link "/"
    :items [{:text "fortune-cookie" :dispatch [:bidi/goto :user/fortune :query-params {}]}
            {:text "bodymass-indicator"  :dispatch [:bidi/goto :user/bmi]}
            {:text "time"  :dispatch [:bidi/goto :user/time]}
            {:text "space-station"  :dispatch  [:bidi/goto :user/iss]}
            {:text "jon-grid"  :dispatch [:bidi/goto  :user/aggrid]}
            {:text "vega"  :dispatch [:bidi/goto :user/vega]}
           ; {:text "feedback" :link "https://github.com/pink-gorilla/goldly/issues" :special? true}
            ]}])

(defn add-page-site [fn-page name]
  (let [wrapped-page (fn [route]
                       [layout/header-main  ; .w-screen.h-screen
                        [site-header]
                        [fn-page route]])]
    (page/add wrapped-page name)))

(def to-fixed goldly.js/to-fixed)

(def rnd ui.rnd/rnd)
