(ns notebook.strategy.sentiment-spread.vega
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [tablecloth.api :as tc]
   [tech.v3.dataset :as tds]
   [ta.viz.publish :as p]))

(def w 1600)

(def spec
  {;:width "1000"
   :box :fl
   :width w ;"100%"
   :height "400" ;"100%"
   :description "Market-Sentiment"
   :params [{:name "currentyear"
             :value 2024
             :bind {:name "Year"
                    :input "range"
                    :min 2009 :max 2024 :step 1}}]
   :transform [{:calculate "year(datum.date)" :as "year"}
               {:filter "datum.year == currentyear"}]
   :vconcat [{:height 500
              :width w
              :title "Market"
              :layer [{:mark {:type "text"
                              :fontSize 100
                              :x 420
                              :y 600
                              :opacity 0.06},
                       :encoding {:text {:field "year"}}}
                      {:mark "rule"
                       :transform [{:filter "datum.sentiment > 4"}]
                       :encoding {:color {:value "blue"}
                                  :size {:value 5}
                                  :x {:field :date :type "temporal"}}}
                      {:mark "rule"
                       :transform [{:filter "datum.sentiment < -4"}]
                       :encoding {:color {:value "red"}
                                  :size {:value 5}
                                  :x {:field :date :type "temporal"}}}
                      {:mark "line"
                       :encoding {:x {:field "date" :type "temporal"}
                                  :y {:field "market" :type "quantitative" :color "blue"
                                      :scale {:type "linear" :zero false}}}}]}
             {:title "Sentiment"
              :height 100
              :width w
              :mark "bar"
              :encoding {:x {:field "date" :type "temporal"}
                         :y {:field "sentiment" :type "quantitative" :color "blue"}}}]})


(defn convert-row [row]
  {:date (:date row)
   :sentiment (:sentiment row)
   :market (:market row)})

(defn convert-sentiment-ds-data [sentiment-ds]
  (let [r (tds/mapseq-reader sentiment-ds)]
    (->> (map convert-row r)
         (into []))))

(defn calc-viz-vega [sentiment-ds]
  (when sentiment-ds 
    (info "calculating sentiment-spread viz for: " (tc/row-count sentiment-ds))
    {:render-fn 'ta.viz.renderfn.vega/vega-lite
     :data {:values (convert-sentiment-ds-data sentiment-ds)}
     :spec spec}))

(defn publish-vega [sentiment-ds topic]
  (p/publish nil {:topic topic}
             (calc-viz-vega sentiment-ds)))
