(ns notebook.strategy.sentiment-spread.vega
  (:require
   [tablecloth.api :as tc]
   [tech.v3.dataset :as tds]
   [ta.viz.publish :as p]))

(def spec
  {;:width "1000"
   :box :fl
   :width 1200 ;"100%"
   :height "400" ;"100%"
   :description "Market-Sentiment"
   :vconcat [{:title "Market"
              :mark "line"
              :height 500
              :width 1200
              :encoding {:x {:field :date :type "temporal"}
                         :y {:field "market", :type "quantitative" :color "blue"
                             :scale {:type "linear" :zero false}}}}
             {:title "Sentiment"
              :height 100
              :width 1200
              :mark "bar"
              :encoding {:x {:field :date :type "temporal"}
                         :y {:field "sentiment", :type "quantitative" :color "blue"}}}]})



(defn convert-row [row]
  {:date (:date row)
   :sentiment (:sentiment row)
   :market (:market row)
   ;:z 1000
   })

(defn convert-sentiment-ds-data [sentiment-ds]
  (let [r (tds/mapseq-reader sentiment-ds)]
    (->> (map convert-row r)
         (into []))))


(defn publish-vega [sentiment-ds topic]
  (p/publish nil {:topic topic}
             {:render-fn 'ta.viz.renderfn.vega/vega-lite
              :data (convert-sentiment-ds-data sentiment-ds)
              :spec spec}))
