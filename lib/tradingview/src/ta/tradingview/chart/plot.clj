(ns ta.tradingview.chart.plot)

(def plottypes
  {:line 0
   :histogram 1
    ; line 2 -no longer used 
   :cross 3
   :area 4
   :columns 5
   :circles 6
   :line-with-breaks 7
   :area-with-breaks 8
   :step-line 9})

(defn plot-type [plottype-kw]
  (or (get plottypes plottype-kw)
      (:line plottypes)))

(comment
  (plot-type :cross)
  (plot-type :columns)
  (plot-type 3) ; does not exist, returns :line = 0
 ;
  )

(def locations
  {:above-bar "AboveBar"
   :below-bar "BelowBar"
   :top "Top"
   :bottom "Bottom"
   :right "Right"
   :left "Left"
   :absolte "Absolute"
   :absolute-up "AbsoluteUp"
   :absolute-down "AbsoluteDown"})

(defn location [location-kw]
  (or (get locations location-kw)
      (:above-bar locations)))

(comment
  (location :top)
  (location :bottom)
  (location 3) ; does not exist, returns :line = 0
  ;
  )

(def linestyles
  {:solid 0
   :dotted 1
   :dashed 2
   :dashed-large 3})

(defn linestyle [linestyle-kw]
  (or (get linestyles linestyle-kw)
      (:solid linestyles)))

(def linewidths
  [1, 2, 3, 4])

(def shape-types
  {:arrow-down  "shape_arrow_down"
   :arrow-up "shape_arrow_up"
   :circle "shape_circle"
   :cross "shape_cross"
   :xcross "shape_xcross"
   :diamond "shape_diamond"
   :flag "shape_flag"
   :square "shape_square"
   :label-down "shape_label_down"
   :label-up "shape_label_up"
   :triangel-down "shape_triangle_down"
   :triangel-up "shape_triangle_up"})

(defn shape [shape-kw]
  (or (get shape-types shape-kw)
      (:cross shape-types)))

 ;; TEXT-SIZE

(def text-sizes
  {:auto "auto"
   :tiny "tiny"
   :small "small"
   :normal "normal"
   :large "large"
   :huge "huge"})

(defn text-size [size-kw]
  (or (get text-sizes size-kw)
      (:auto text-sizes)))