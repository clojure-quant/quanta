(ns notebook.svg-demo
  (:require
   [tick.core :as tick :refer [>>]]
   [ta.helper.date :refer [parse-date now-datetime]]
   [ta.gann.svg-view :refer [svg! svg-view point line ellipse series]]
   [goldly.scratchpad :refer [show! show-as clear!]]))

(show!
 (svg! 500 500
       #_[:path {:d "M 50 50 H 290 V 90 H 50 L 50 50"
                 :stroke-width 5
                 :stroke "white"
                 :fill "none"}]
       ;[:path {:d "M 200 200" :stroke "green" :stroke-width 5}]
       ;[:circle {:cx "0" :cy "0" :r 150 :fill "red"}]
       [:rect {:width 400 :height 100 :style "fill:rgb(0,0,255);stroke-width:10;stroke:rgb(0,0,0)"}]
       [:polygon {:points "100,10 40,198 190,78 10,78 160,198"
                  :style "fill:lime;stroke:purple;stroke-width:5;fill-rule:evenodd;"}]
         ;[:ellipse {:cx "250" :cy "250" :rx "250" :ry "50"}]
       (ellipse {:rx 250 :ry 50})
       (point {:color "red"} [400 400])
       (point {:color "blue"}  [300 300])
       (line {:color "green"} [200 200] [500 700])
       (series {:color "black"} [[10 10] [20 40] [120 60] [220 200] [320 100] [420 400]])))

(show!
 (svg-view {:min-px 500
            :max-px 1000
            :min-dt (parse-date "2021-01-01")
            :max-dt (parse-date "2021-12-31")
            :svg-width 500
            :svg-height 500}
           [[:point {:color "red"}   [(parse-date "2021-04-01") 600]]
            [:point {:color "blue"}  [(parse-date "2021-05-01") 700]]
            [:point {:color "green"} [(parse-date "2021-09-01") 900]]
            [:point {:color "blue"}  [(parse-date "2021-03-01") 600]]
            [:line {:color "green"}
             [(parse-date "2021-03-01") 500]
             [(parse-date "2021-12-01") 900]]
            [:series {:color "black"}
             [[(parse-date "2021-01-01") 600]
              [(parse-date "2021-02-01") 500]
              [(parse-date "2021-03-01") 400]
              [(parse-date "2021-04-01") 300]
              [(parse-date "2021-05-01") 600]
              [(parse-date "2021-06-01") 700]
              [(parse-date "2021-07-01") 800]
              [(parse-date "2021-08-01") 900]
              [(parse-date "2021-09-01") 700]
              [(parse-date "2021-10-01") 600]
              [(parse-date "2021-11-01") 500]
              [(parse-date "2021-12-01") 600]]]
            [:ellipse {:style {:stroke "red"}
                       :cy 750
                       :cx (parse-date "2021-05-01")
                       :ry 250
                       :rx (tick/new-duration 100 :days)}]]))
