(ns demo.playground.arrow
  (:require
   [tech.v3.dataset :as dataset]
   [ta.viz.arrow :as a]))

(def ds1
  (dataset/->dataset
   {:score [11 2 3 99 3 61 32 5 32 54]
    :player ["A" "B" "C" "D" "E" "F" "G" "H" "I" "J"]}))

(a/save-ds-as-arrow-file ds1 "/tmp/data.arrow")

(a/load-ds-from-arrow-file "/tmp/data.arrow")

(a/publish-ds! ds1 :test)

^:R
[:p/vegalite
 {:width 800
  :height 600
  :spec {:data {:url "/api/arrow"
                :format {:type "arrow"}}
         :mark {:type "bar"
               ;:tooltip true
                :tooltip {:content "data"}}
         :encoding {:x {:field "player" :type "ordinal"}
                    :y {:field "score" :type "quantitative"}}}}]

(a/publish-ds! ds1 :test)

^:R
[:p/vegalite
 {:width 800
  :height 600
  :spec {:data {:url "/api/arrow"
                :format {:type "arrow"}}
         :mark {:type "bar"
               ;:tooltip true
                :tooltip {:content "data"}}
         :encoding {:x {:field "date" :type "quantitative"}
                    :y {:field "close" :type "quantitative"}}}}]

^:R
[:p/vegalite
 {:width 800
  :height 300
  :spec {:data {:url "/api/arrow"
                :format {:type "arrow"}}
         :mark {:type "bar"
               ;:tooltip true
               ; :tooltip {:content "data"}
                }
         :encoding {:x {;:field "epoch" 
                        :timeUnit "month"
                        :field "date"

                        ;:type "temporal"
                        ;:type "quantitative"
                        }
                    :y {:field "close" :type "quantitative"}
                    :row {:field "symbol"}}}}]