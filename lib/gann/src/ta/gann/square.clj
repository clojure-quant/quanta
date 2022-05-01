(ns ta.gann.square
 )

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
  (layer-end 5)
  (layer-end 6)
  (layer-end 7)
  (layer-end 8)
  (layer-end 9)
  (layer-end 10)
  (layer-end 11)
  (layer-end 12)
  (layer-end 13)
  (layer-end 14)
  (layer-end 15)
  (layer-end 16)




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

   :width 1000
   :height 600
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
                               :type "ordinal"
                               :legend nil}}}
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

  (show! (plot 9))

  (show! (plot 25))
  (show! (plot 49))
  (show! (plot 81))
  (show! (plot 121))
  (show! (plot 1089))
;
  )


(defn polar [x y]
  ; https://de.wikipedia.org/wiki/Rechtwinkliges_Dreieck
  (let [r (Math/sqrt (+ (Math/pow x 2.0) (Math/pow y 2.0)))
        a (if (or (= x 0.0) (= x 0))
            (if (> y 0.0) 
              (/ Math/PI 2.0)
              (- 0.0 (/ Math/PI 2.0)))
            (Math/atan (/ y x)))
        a (if (< x 0.0) (+ a Math/PI) a)
        a (if (< a 0.0) (+ a Math/PI Math/PI) a) ; negative angle => add 360 deg = 2* PI
        ]
    {:r r
     :a a}))

(defn ->deg [a]
  (/ (* a 180.0) Math/PI))

(defn polar-deg [x y]
  (let [p (polar x y)]
    (assoc p :a (-> (:a p) ->deg))))


(comment
  (->deg 0.785398)
  (->deg 1.5707)
  (->deg 2.5)
  (->deg 3.1)
  (->deg 3.14)

  (polar-deg 1.0 0.0)
  (polar-deg -1.0 0.0)

  (polar-deg 1.0 1.0)
  (polar-deg 1.0 -1.0)
  (polar-deg -1.0 1.0)
  (polar-deg -1.0 -1.0)
  (polar-deg -1.0 -1000.0)
  (polar-deg 0.001 1.0)
  (polar-deg 0.0 1.0)
  (polar-deg 0 1)

  

  (polar-deg 1.0 0.001)
  (polar-deg 1.0 0.577)
  (polar-deg 1.0 1.0)
  (polar-deg 1.0 2.0)
  (polar-deg 1.0 1000.0)
  (polar-deg 1.0 -1000.0)

  (polar-deg 2.0 1.0)
  (polar-deg 3.0 1.0)
    ;
  )


(defn nr->polar [nr]
  (let [{:keys [x y]} (nr->coordinates nr)] 
    (-> (polar-deg x y)
        (assoc :nr nr))))

(comment
  (nr->polar 1)
  (nr->polar 2)
  (nr->polar 3)
  (nr->coordinates 4)
  (polar-deg 0.0 1.0)
  (nr->polar 4)
  (nr->polar 5)

  (nr->polar 311)
 ; 
  )



(defn square-phase-spec [x-field y-field data ]
  {:$schema "https://vega.github.io/schema/vega-lite/v5.json"
   :data {:values data}
   :width 1000
   :height 600
   :layer [{:mark "line" ;"point"
            :encoding {:x {:field x-field ; "a"
                           :type "quantitative"}
                       :y {:field y-field ; "r"
                           :type "quantitative"}
                       :color {;:value "blue"
                               :field "layer"
                               :type "ordinal"
                               :legend nil}}}
           #_{:mark "text"
            :encoding {:x {:field "a"
                           :type "quantitative"}
                       :y {:field "r"
                           :type "quantitative"}
                       :text {:field "nr"
                              :type "quantitative"}
                       :color {:value "black"}}}]
   :config {:view {:stroke "transparent"}}})


(defn phase-plot [x-field y-field max-nr]
  (let [f (fn [nr]
            (-> (nr->polar nr)
                (assoc :nr nr
                       :layer (nr->layer nr))))
        spec (square-phase-spec x-field y-field (map f (range 2 (inc max-nr))))]
     [:p/vegalite {:box :sm
                   :spec spec}]))



(comment
  (nr->coordinates 3)
  (nr->coordinates 5)
  (require '[goldly.scratchpad :refer [show!]])

  (map nr->polar (range 2 5))
  
  (show! (plot 25))
  (show! (plot 49))
  (show! (plot 121))
  
  (show! (phase-plot "a" "r" 1089))
  (show! (phase-plot "a" "nr" 1089))
  (show! (phase-plot "layer" "r" 1000))


  (show! (phase-plot "a" "nr" 10890))

  (show! (phase-plot "a" "nr" 108900))



;
  )











