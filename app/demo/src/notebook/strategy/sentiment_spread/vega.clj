(ns notebook.strategy.sentiment-spread.vega
  (:require
   [tablecloth.api :as tc]
   [tech.v3.dataset :as tds]
   [ta.viz.publish :as p]))

(def spec
  {;:width "1000"
   :box :fl
   ;:width "500" ;"100%"
   :height "400" ;"100%"
   :description "Market-Sentiment"
   :layer [{:mark "line"
            :encoding {:x {:field :date :type "temporal"}
                       :y {:field "market", :type "quantitative" :color "blue"}}}
           {:mark "line"
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
