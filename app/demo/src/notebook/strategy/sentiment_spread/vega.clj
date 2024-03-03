(ns notebook.strategy.sentiment-spread.vega
  (:require
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
   :vconcat [{:height 500
              :width w
              :title "Market"
              :layer [{:mark "rule"
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
                                      :scale {:type "linear" :zero false}}}}
                      ]}
             {:title "Sentiment"
              :height 100
              :width w
              :mark "bar"
              :encoding {:x {:field "date" :type "temporal"}
                         :y {:field "sentiment" :type "quantitative" :color "blue"}}}]})


;[{"filter": {"param": "index"}}]

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
              :data {:values (convert-sentiment-ds-data sentiment-ds)}
              #_[{:name "data"
                :values (convert-sentiment-ds-data sentiment-ds)}
               {:name "sentiment"
                :source "data"}]
              :spec spec}))
