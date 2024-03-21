(ns ta.tradingview.goldly.indicator.bar-colorer)

(def study-bar-colorer
  {:name "Bar Colorer Demo"
   :metainfo {:_metainfoVersion 51
              :id "BarColoring6@tv-basicstudies-1"
              :name "BarColoring6"
              :description "Bar Colorer Demo"
              :shortDescription "BarColoring"
              "isCustomIndicator" true
              "is_price_study" true
              "isTVScript" false
              "isTVScriptStub" false
              "format" {"type" "price"
                        "precision" 4}
              "defaults" {"palettes" {"palette_0" {; palette colors
                                                ;  change it to the default colors that you prefer,
                                                ; but note that the user can change them in the Style tab
                                                ; of indicator properties
                                                   "colors" [{"color" "#FFFF00"}
                                                             {"color" "#0000FF"}]}}}
              "inputs" []
              "plots" [{"id" "plot_0"
                        "type" "bar_colorer" ; plot type should be set to 'bar_colorer'
                       ; this is the name of the palette that is defined
                       ; in 'palettes' and 'defaults.palettes' sections
                        "palette" "palette_0"}]
              "palettes" {"palette_0" {"colors" [{"name" "Color 0"}
                                                 {"name" "Color 1"}]
                                       ; the mapping between the values that
                                       ; are returned by the script and palette colors
                                       "valToIndex" {100 0
                                                     200 1}}}}
   :constructor (fn []
                  (clj->js
                   {:init (fn [context inputCallback]
                            (println "barcolorer.init"))
                    :main (fn [context _inputCallback]
                            (let [color (if (> 0.5 (js/Math.random))
                                          100
                                          200)]
                              ;(println "color: " color)
                              (clj->js [color])))}))})
