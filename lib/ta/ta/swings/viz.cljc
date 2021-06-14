(ns ta.swings.viz)

(def swingchart-spec
  {;:$schema "http://localhost:8000/r/vega-lite/build/vega-lite-schema.json"  ;https://vega.github.io/schema/vega-lite/v5.json",
 ;:data {:url "/r/data/swings.json"}
   :data {:name "swings"}
   :layer [{;:mark "rect" ;
            :mark {:type "rect"
                   :tooltip {:content "data"}
                 ;:height 100 
                   :width 10}
            :encoding {"x"   {:field "idx"  
                              :type "quantitative"}
                       "width" 1 ; {:band 1 :type "quantitative"}
                     ;"x2"  {:field "idx2"  :type "quantitative"}
                       "y"   {:field "Low"  
                              :type "quantitative"
                              :scale {:nice true :zero false}
                              }
                       "y2"  {:field "High" 
                              :type "quantitative"
                              :scale {:nice true :zero false}
                              }
                            ;"color" {:value  "orange"}
                       "color" {:field "dir"
                                :type "nominal"
                                :scale {:domain ["up" "down"]
                                        :range ["green" "#c7c7c7"]}
                                :legend nil}}
            :scales [{:name "x"
                      :type "linear"
                      :domain {:data "swings"
                               :field "idx"}
                      :range "width"}
                     {:name "y"
                      :type "linear"
                      :domain {:data "swings" :field "Low"}
                      :nice true
                      :zero false
                      :range "height"}]
             :axes [{:orient "left"
                     :scale "y"
                     :grid true
                     :format "%"}]
            }
           {:mark {:type "point"
                   :height 10
                   :width 20}
            :encoding {"x"   {:field "idx", :type "quantitative"}
                       "y"   {:field "last", 
                              :type "quantitative"
                              :scale {:nice true :zero false}
                              }
                       "color" {:value  "red"}}}]})

(defn swing-chart2 [{:keys [data] :as opts}]
  (let [opts (assoc opts :spec swingchart-spec)]
    ^:R [:p/vegalite opts]))


(defn conv [sd]
  (assoc sd :swings
         (map-indexed (fn [i v]
                        (->
                         (assoc v
                                :idx i
                                :High (:high v)
                                :Low (:low v))
                         (dissoc :high :low)))
                      (:swings sd))))


(defn chart [swings]
  (let [swings (conv swings)]
    (swing-chart2 {:data swings})))



(defn conv2 [sd]
         (map-indexed (fn [i v]
                        (->
                         (assoc v
                                :idx i
                                :High (:high v)
                                :Low (:low v))
                         (dissoc :high :low)))
                      sd))

(defn chart2 [swings]
  (let [swings (conv2 swings)]
    (swing-chart2 {:data {:swings swings}})))


(comment
  
  (swing-chart2
   {:data {:swings [{:Low 18 :High 22 :dir "up" :idx 1 :idx2 2}
                    {:Low 12 :High 22 :dir "down" :idx 2  :idx2 3}
                    {:Low 12 :High 14 :dir "up" :idx 3  :idx2  4 :note "wow"}
                    {:Low 12 :High 22 :dir "down" :idx 4,  :idx2  5}
                    {:Low 12 :High 14 :dir "up" :idx 5  :idx2  6}]}})

  (conv  [{:low 18 :high 22 :dir "up" :idx 1 :idx2 2}
          {:low 12 :high 22 :dir "down" :idx 2  :idx2 3}])
  
  
  
 ; 
  )