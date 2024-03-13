(ns astro.algo
  (:require
   [tick.core :as t]
   [ephemeris.core :refer [calc]]))

(def geo-req {:utc "2022-03-15T00:13:00Z"
                   ;"2009-02-03T21:43:00Z"
              :geo {:lat 40.58 :lon -74.48}
              :angles [:Asc :MC :Angle]
              :points [:Sun :Moon
                       :Mercury :Venus :Mars
                       :Jupiter :Saturn :Uranus :Neptune :Pluto
                       ;:Body 
                       ;:Chiron :Pholus :Ceres :Pallas :Juno :Vesta 
                       :TrueNode]})

(defn calc-date [dt]
  (let [sdt (str dt)]
    (calc (assoc geo-req :utc sdt))))

(defn extract-planets [result]
  (map (fn [[planet {:keys [lon]}]]
         {:planet planet :degree lon}) (:points result)))


(defn astro-algo [env opts dt]
  (if dt
    {:date dt
     :planets (-> (calc-date dt)
                  extract-planets)}
    {}))


(comment

  (-> (calc-date (t/instant))
      extract-planets)

  (astro-algo nil nil (t/instant))
 ;; => {:date #inst "2024-03-12T14:39:41.432817061-00:00",
 ;;     :planets
 ;;     ({:planet :TrueNode, :degree 15.705733886619516}
 ;;      {:planet :Neptune, :degree 357.1749663425126}
 ;;      {:planet :Saturn, :degree 341.32594054372174}
 ;;      {:planet :Mars, :degree 321.9272594479421}
 ;;      {:planet :Mercury, :degree 4.648438122953529}
 ;;      {:planet :Pluto, :degree 301.50119987157836}
 ;;      {:planet :Sun, :degree 352.5127626101504}
 ;;      {:planet :Moon, :degree 23.991116870430357}
 ;;      {:planet :Venus, :degree 330.867660074795}
 ;;      {:planet :Uranus, :degree 49.9589078417395}
 ;;      {:planet :Jupiter, :degree 43.41381669923683})}


 ; 
  )