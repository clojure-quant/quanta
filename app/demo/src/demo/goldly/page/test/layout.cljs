(ns demo.goldly.page.test.layout
  (:require
    [goldly.page :as page]
    [site :refer [ipsum]]
    [demo.goldly.lib.layout :as layout]))

(defn page-layout-1 [_route]
  (layout/left-right-top {:top "top" 
                          :left [:div.bg-blue-500.w-full.h-full "left"]
                          :right  [:div.bg-green-500.w-full.h-full "right"]}))

(defn page-layout-2 [_route]
  (layout/left-right-top {:top "menu" 
                          :left [:div.bg-blue-500.w-full.h-full.overflow-scroll (ipsum 5)]
                          :right [:div.bg-green-500.w-full.h-full (ipsum 100)]}))


(page/add page-layout-1 :test/layout1)
(page/add page-layout-2 :test/layout2)