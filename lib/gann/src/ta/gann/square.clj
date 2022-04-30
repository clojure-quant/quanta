(ns ta.gann.square)

(defn layer-size [layer-no]
  (* 8 layer-no))

(defn layer-end [layer-no]
  (if (= layer-no 0)
    1
    (-> layer-no dec layer-end (+ (layer-size layer-no)))))

(comment
  (layer-end 0)
  (layer-end 1)
  (layer-end 2)
  (layer-end 3)
  (layer-end 4)
  ;
  )

(defn nr->layer [nr]
  (loop [l 1]
    (if (<= nr (layer-end l))
      l
      (recur (inc l)))))

(comment
  (nr->layer 5)
  (nr->layer 11)
  (nr->layer 200)
  ;
  )






(defn layer-movement [layer-no]
  (* layer-no 2))

(defn layer-corners [layer-no]
  (let [end (layer-end layer-no)
        movment  (layer-movement layer-no)
        bottom-left end ; (dec end)
        bottom-right (- bottom-left  movment)
        top-right (- bottom-right movment)
        top-left (- top-right movment)]
    [top-left top-right bottom-right bottom-left]))

(comment
  (layer-corners 1)
  (layer-corners 2)
  (layer-corners 3)
  (layer-corners 4)
  (layer-corners 5)
;
  )


(defn coordinates-in-layer [layer-no nr]
  (let [s layer-no
        [tl tr br bl] (layer-corners layer-no)]

    (cond
      (>= nr br) {:y (- 0 s) ; horizontal bottom
                  :x (- s (- nr br))}
      (>= nr tr) {:x s ; vertical right
                  :y (- s (- nr tr))}
      (>= nr tl) {:y s ; horizontal top
                  :x (+ (- 0 s) (- nr tl))}
      :gre-bl   {:x (- 0 s) ; left
                 :y (- s (- tl nr))})))




(comment
  (coordinates-in-layer 1 9)
  (range 9 1 -1)

  (map #(coordinates-in-layer 1 %)
       (range 9 1 -1)
       ;(range 6 4 -1)
      ; (range 4 2 -1)
       )

  (map #(coordinates-in-layer 2 %)
       (range 25 9 -1)
       ;(range 6 4 -1)
      ; (range 4 2 -1)
       )
;
  )

(defn nr->coordinates [nr]
  (coordinates-in-layer (nr->layer nr) nr))

(defn square-vega-spec [data]
  {:$schema "https://vega.github.io/schema/vega-lite/v5.json"
   :data {:values data}

   :width 600
   :height 400
   :layer [{:mark "rect"
            :align "center"
            :width 0.5
            :height 0.5
            :encoding {:x {:field "x"
                           :type "ordinal"}
                       :y {:field "y"
                           :type "ordinal"}
                       :color {;:value "blue"
                               :field "layer"
                               :type "ordinal"}}}
           {:mark "text"
            :encoding {:x {:field "x"
                           :type "ordinal"}

                       :y {:field "y"
                           :type "ordinal"}

                       :text {:field "nr"
                              :type "quantitative"}
                       :color {:value "white"}}}]
   :config {:view {:stroke "transparent"}}})

(defn square-plot [data]
  [:p/vegalite {:box :sm
                :spec (square-vega-spec data)}])


(defn plot [max-nr]
  (let [f (fn [nr]
            (-> (nr->coordinates nr)
                (assoc :nr nr
                       :layer (nr->layer nr))))]
    (square-plot (map f (range 2 (inc max-nr))))))


(comment
  (nr->coordinates 3)
  (nr->coordinates 5)
  (require '[goldly.scratchpad :refer [show!]])

  (map nr->coordinates (range 5))

  (show! (plot 10))
  (show! (plot 25))
  (show! (plot 49))
  (show! (plot 81))




 ; 
  )












