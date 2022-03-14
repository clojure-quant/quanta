(ns astro
  (:require 
    [taoensso.timbre :refer [debug info warnf error]]
    [ephemeris.core :refer [calc]]
    [clojure.pprint :refer [print-table]]
    ))

(def sign-dict
{0 {:sign :aries :dstart 0 :sun-time "MAR 21 - APR 19"}
 1 {:sign :taurus :dstart 30 :sun-time "APR 20 - MAY 20"}
 2 {:sign :gemini :dstart 60 :sun-time "MAY 21 - JUN 20"}
 3 {:sign :cancer :dstart 90 :sun-time "JUN 21 - JUL 22"}
 4 {:sign :leo :dstart 120 :sun-time "JUL 23 - AUG 22"}
 5 {:sign :virgio :dstart 150 :sun-time "AUG 23 - SEP 22"}
 6 {:sign :libra :dstart 180 :sun-time "SEP 23 - OCT 22"}
 7 {:sign :scorpio :dstart 210 :sun-time "OCT 23 - NOV 21"}
 8 {:sign :sagittarius :dstart 240 :sun-time "NOV 22 - DEC 21"}
 9 {:sign :capricorn :dstart 270 :sun-time "DEC 22 - JAN 19"}
 10 {:sign :aquarius :dstart 300 :sun-time "JAN 20 - FEB 18"}
 11 {:sign :piscies :dstart 330 :sun-time "FEB 19 - MAR 20"}
})

(defn deg->sign [d]
  (let [q (quot d 30.0)
        q (int q)]
    (get sign-dict q)))


(defn deg->rad [d]
  ; Degrees x (π/180) = Radians
  (* d (/ Math/PI 180.0)))

(defn rad->deg [r]
  ; Radians  × (180/π) = Degrees
  (* r (/ 180.0 Math/PI)))


(def geo-req {:utc "2022-03-14T21:43:00Z"
                   ;"2009-02-03T21:43:00Z"
              :geo {:lat 40.58 :lon -74.48}
              :angles [:Asc :MC :Angle]
              :points [:Sun :Moon 
                       :Mercury :Venus :Mars 
                       :Jupiter :Saturn :Uranus :Neptune :Pluto 
                       ;:Body 
                       ;:Chiron :Pholus :Ceres :Pallas :Juno :Vesta 
                       :TrueNode]
              })




(defn point-table [res]
  (let [points (:points res)]
    (map (fn [[n v]] 
          (let [lon (:lon v)
                sign (deg->sign lon)]
            ;(info "sign: " sign)
            (merge v (assoc sign :name n))))
      points)
  )
)

(defn astro-test [& _]
   (let [res (calc geo-req)
         moon (get-in res [:points :Moon])
   ]
     (info "astro res: " res)
     (print-table (point-table res))


))

