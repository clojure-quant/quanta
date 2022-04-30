(ns demo.goldly.repl.vega-heatmap
  (:require
   [goldly.scratchpad :refer [show! show-as clear! eval-code!]]))

(def vega-spec-heatmap-url
  {:$schema "https://vega.github.io/schema/vega-lite/v5.json"
   :data {:url "/r/demo/movies.json"}
   :transform [{:filter {:and [{:field "IMDB Rating", :valid true}
                               {:field "Rotten Tomatoes Rating", :valid true}]}}]
   :mark "rect"
   :width 300
   :height 200
   :encoding {:x {:bin {:maxbins 60}
                  :field "IMDB Rating"
                  :type "quantitative"}
              :y {:bin {:maxbins 40}
                  :field "Rotten Tomatoes Rating"
                  :type "quantitative"}
              :color {:aggregate "count", :type "quantitative"}}
   :config {:view {:stroke "transparent"}}})

(defn make-vega-heatmap-spec [data]
  {:$schema "https://vega.github.io/schema/vega-lite/v5.json"
   :data {:values data}
   :mark "rect"
   :width 600
   :height 400
   :encoding {:x {:field "to"
                  :type "ordinal"}
              :y {:field "from"
                  :type "ordinal"}
              :color {;:value "blue"
                      :field "cor"
                      :type "quantitative"}}
   :config {:view {:stroke "transparent"}}})

(defn heatmap-corr-matrix [data]
  [:p/vegalite {:box :sm
                :spec (make-vega-heatmap-spec data)}])

(def demo-cor
  [{:from :a :to :b :cor 0.6}
   {:from :a :to :c :cor 0.2}
   {:from :a :to :d :cor -0.1}
   {:from :a :to :e :cor 0.0}

   {:from :b :to :c :cor 0.3}
   {:from :b :to :d :cor 0.7}
   {:from :b :to :e :cor 0.0}

   {:from :c :to :d :cor 0.4}
   {:from :c :to :e :cor 0.8}

   {:from :d :to :e :cor -0.2}])

(show! (heatmap-corr-matrix demo-cor))
