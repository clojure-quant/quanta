(ns ta.swings.viz
  ;(:require 
  ; [reagent.core :as r])
  ;)
  )

(def swingchart-spec
  {;:$schema "http://localhost:8000/r/vega-lite/build/vega-lite-schema.json"  ;https://vega.github.io/schema/vega-lite/v5.json",
 ;:data {:url "/r/data/swings.json"}
   :width 2400 ;{:step 40
          ; :type "container"
           ;} ;2400
   :height 1000
   :data {:name "swings"}
   :layer [{;:mark "rect" ;
            :mark {:type "rect"
                   :tooltip {:content "data"}
                 ;:height 100 
                   ;:width 10
                   }
            :encoding {"x"   {:field "idx"
                              :type "ordinal"}
                       "width" 1 ; {:band 1 :type "quantitative"}
                     ;"x2"  {:field "idx2"  :type "quantitative"}
                       "y"   {:field "low"
                              :type "quantitative"
                              :scale {:nice true :zero false}}
                       "y2"  {:field "high"
                              :type "quantitative"
                              :scale {:nice true :zero false}}
                            ;"color" {:value  "orange"}
                       "color" {:field "dir"
                                :type "nominal"
                                :scale {:domain ["up" "down"]
                                        :range ["green" "red"]}
                                :legend nil}}
            :scales [{:name "x"
                      :type "band"
                      :bandSize  "fit"
                      :step 20
                      :minExtent 20
                      :offset 10
                      :domain {:data "swings"
                               :field "idx"}
                      :range "width"}
                     {:name "y"
                      :type "log"
                      :domain {:data "swings" :field "low"}
                      :nice true
                      :zero false
                      :range "height"}]
            :axes [{:orient "left"
                    :scale "y"
                    :grid true
                    :format "%"
                    :type "log"}]}
           #_{:mark {:type "point"
                     :height 10
                     :width 20}
              :encoding {"x"   {:field "idx", :type "quantitative"}
                         "y"   {:field "last"
                                :type "quantitative"
                                :scale {:nice true :zero false}}
                         "color" {:value  "red"}}}]})

(defn pages [swings]
  (let [c (count swings)
        per_page (/ 2000 30)
        pages (inc (/ c per_page))]
    pages))

(defn add-idx [swings]
  (map-indexed (fn [i v]
                 (assoc v :idx i))
               swings))

(defn swing-chart [swings]
  (let [swings (add-idx swings)
        max_p (pages swings)
        ;page (r/atom max_p)
        opts {}
        opts (assoc opts :width 2400
                    :height 1000
                    :spec swingchart-spec
                    :data {:swings swings})]
    ^:R [:div {:style {:width 2400
                       :height 1000
                       :overflow "hidden"
                       :background-color "green"}}
         ;[:p/select {:items (range max_p)} page []]
         ;[:p "state: " (pr-str page)]
         [:p/vegalite opts]]))

(comment
  (add-idx  [{:low 18 :high 22 :dir "up" :idx 1 :idx2 2}
             {:low 12 :high 22 :dir "down" :idx 2  :idx2 3}])

  (swing-chart
   [{:Low 18 :High 22 :dir "up" :idx 1 :idx2 2}
    {:Low 12 :High 22 :dir "down" :idx 2  :idx2 3}
    {:Low 12 :High 14 :dir "up" :idx 3  :idx2  4 :note "wow"}
    {:Low 12 :High 22 :dir "down" :idx 4,  :idx2  5}
    {:Low 12 :High 14 :dir "up" :idx 5  :idx2  6}])

; 
  )