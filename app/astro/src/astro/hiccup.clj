(ns astro.hiccup
  (:require
   [astro :refer [get-text]]
   [ta.viz.ds.hiccup :refer [hiccup-render-spec]]))

(defn line [t0 p0 t1 p1]
  [:line {:color "blue"} [t0 p0] [t1 p1]])

(defn angle-marker [d w h]
  [:rect {:x 250 :y 400 :width w :height h :fill "blue"
          :transform (str "rotate(" d ",250,250)")}])


(defn text-marker [d t]
  [:text {:x 250 :y 428
          :transform (str "rotate(" (- d 15) ",250,250)")}
   t
   ])

(defn sign-marker [i]
  (let [d (* 30 i)]
    [:g 
      (text-marker d (get-text i))
      (angle-marker d 4 50)]
  ))

(defn scale-marker [d]
  (angle-marker d 2 10))


(defn degrees-marker []
  (into [:g {:stroke "green"}]
        (map #(scale-marker (* 10 %))
             (range 36))))

(defn zodiac-marker []
  (into [:g {:stroke "green"}]
        (map sign-marker (range 12))))

(defn planet-marker [d]
    [:circle {:cx "150" :cy "250" :r 5 :fill "blue"
              :transform (str "rotate(" d ",250,250)")
              }])

(defn planet-all  [planets]
  (into [:g {:stroke "green"}]
        (map (fn [{:keys [planet degree]}]
               (planet-marker degree))
             planets)))


(defn astro-hiccup [{:keys [date planets]}]
  (hiccup-render-spec
   [:svg
    {:width 500
     :height 500}
    [:path {:d "M 200 200" :stroke "green" :stroke-width 5}]
    [:circle {:cx "250" :cy "250" :r 200 :fill "yellow"
              :stroke "black" :stroke-width 2}] ;outer sign
    [:circle {:cx "250" :cy "250" :r 150 :fill "white"
              :stroke "black" :stroke-width 2}] ; inner sign
    [:circle {:cx "250" :cy "250" :r 20 :fill "blue"
              :stroke "black" :stroke-width 2}] ; earth
    #_[:rect {:x 250 :y 400 :width 10 :height 50 :fill "blue"}]
    #_[:rect {:x 250 :y 400 :width 10 :height 50 :fill "blue"
            :transform "rotate(90,250,250)"}]
    (degrees-marker)
    (zodiac-marker)
    ;(planet-marker 5)
    ;(planet-marker 35)
    ;(planet-marker 180)
    (planet-all planets)

    ;(line 100 100 1000 1000)
    ;(line 0 1000 1000 1000) 
    ;(line 1000 0 1000 1000)
    ]))




(comment
  (astro-hiccup {:date #inst "2024-03-12T14:39:41.432817061-00:00",
                 :planets
                  '({:planet :TrueNode, :degree 15.705733886619516}
                    {:planet :Neptune, :degree 357.1749663425126}
                    {:planet :Saturn, :degree 341.32594054372174}
                    {:planet :Mars, :degree 321.9272594479421}
                    {:planet :Mercury, :degree 4.648438122953529}
                    {:planet :Pluto, :degree 301.50119987157836}
                    {:planet :Sun, :degree 352.5127626101504}
                    {:planet :Moon, :degree 23.991116870430357}
                    {:planet :Venus, :degree 330.867660074795}
                    {:planet :Uranus, :degree 49.9589078417395}
                    {:planet :Jupiter, :degree 43.41381669923683})})
  (zodiac-marker)
  
 ; 
  )

