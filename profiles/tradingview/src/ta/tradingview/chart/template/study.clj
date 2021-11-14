(ns ta.tradingview.chart.template.study)

(def template-study
  {:type "Study"
   :zorder 3
   :state {:name "Volume@tv-basicstudies"
           :id "Volume@tv-basicstudies"
           :fullId "Volume@tv-basicstudies-1"
           :productId "tv-basicstudies"
           :is_hidden_study false
           :description "Volume"
           :shortDescription "Volume"
           :paneSize "large"
           :isTVScript false
           :description_localized "Volume"
           :showInDataWindow true
           :shortId "Volume"
           :isTVScriptStub false
           :version "1"
           :visible true
           :is_price_study false
           :packageId "tv-basicstudies"
           :palettes {:volumePalette {:colors {:0 {:color "#eb4d5c", :width 1, :style 0, :name "Falling"}
                                               :1 {:color "#53b987", :width 1, :style 0, :name "Growing"}}}}
           :precision "default", :showStudyArguments true
           :inputs {:showMA false, :maLength 20}
           :graphics {}
           :styles {:vol {:linewidth 1, :color "#000080", :trackPrice false, :joinPoints false, :plottype 5, :title "Volume"
                          :linestyle 0, :visible true, :histogramBase 0, :transparency 65}
                    :vol_ma {:linewidth 1, :color "#0496FF", :trackPrice false, :joinPoints false
                             :plottype 4, :title "Volume MA", :linestyle 0, :visible true, :histogramBase 0, :transparency 65}}
           :area {}
           :plots {:0 {:id "vol", :type "line"}
                   :1 {:id "volumePalette", :palette "volumePalette", :target "vol", :type "colorer"}
                   :2 {:id "vol_ma", :type "line"}}
           :bands {}
           :_metainfoVersion 15
           :transparency 65}
   :metaInfo {:description "Volume"
              :isTVScript false
              :description_localized "Volume"
              :name "Volume@tv-basicstudies"
              :shortId "Volume"
              :id "Volume@tv-basicstudies-1"
              :fullId "Volume@tv-basicstudies-1"
              :packageId "tv-basicstudies"
              :productId "tv-basicstudies"
              :is_price_study false
              :palettes {:volumePalette {:colors {:0 {:name "Falling"}
                                                  :1 {:name "Growing"}}}}
              :isTVScriptStub false
              :defaults {:styles {:vol {:linestyle 0, :linewidth 1, :plottype 5, :trackPrice false, :transparency 65, :visible true, :color "#000080"}
                                  :vol_ma {:linestyle 0, :linewidth 1, :plottype 4, :trackPrice false, :transparency 65, :visible true, :color "#0496FF"}}, :precision 0, :palettes {:volumePalette {:colors {:0 {:color "#eb4d5c", :width 1, :style 0}, :1 {:color "#53b987", :width 1, :style 0}}}}, :inputs {:showMA false, :maLength 20}}, :shortDescription "Volume", :inputs [{:id "showMA", :name "show MA", :defval false, :type "bool"} {:id "maLength", :name "MA Length", :defval 20, :type "integer", :min 1, :max 2000}]
              :is_hidden_study false
              :graphics {}
              :styles {:vol {:title "Volume", :histogramBase 0}
                       :vol_ma {:title "Volume MA", :histogramBase 0}}
              :plots [{:id "vol", :type "line"}
                      {:id "volumePalette", :palette "volumePalette", :target "vol", :type "colorer"}
                      {:id "vol_ma", :type "line"}]
              :version "1",  :_metainfoVersion 15, :transparency 65}})

