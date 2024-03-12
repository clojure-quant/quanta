(ns astro
  (:require
   [clojure.data :refer [diff]]
   [taoensso.timbre :refer [debug info warn error]]
   [ephemeris.core :refer [calc]]
   [clojure.pprint :refer [print-table]]
   [tick.core :as t]))


(def planet-dict 
  {:Moon  "☾"
   :Mercury "☿"
   :Venus "♀︎"
   :Sun "☉"
   :Mars "♂︎"
   :Jupiter "♃"
   :Saturn "♄"
   :Neptune "♆"
   :TrueNode "☊"
   :Pluto "♇"
   :Uranus "♅"
   })

(defn get-body-text [b]
  ;(warn "body: " b)
  (or (get planet-dict b) "?"))


; https://github.com/astrolin/ephemeris/blob/develop/src/clj/ephemeris/points.clj

(def sign-dict
  {0 {:sign :aries :dstart 0 :sun-time "MAR 21 - APR 19" :text "♈︎"} ; 	Aries (Ram)	U+2648
   1 {:sign :taurus :dstart 30 :sun-time "APR 20 - MAY 20" :text "♉︎"}  ; 	Taurus (Bull)	U+2649
   2 {:sign :gemini :dstart 60 :sun-time "MAY 21 - JUN 20" :text "♊︎"} ; 	Gemini (Twins)	U+264A
   3 {:sign :cancer :dstart 90 :sun-time "JUN 21 - JUL 22" :text "♋︎"} ; 	Cancer (Crab)	U+264B
   4 {:sign :leo :dstart 120 :sun-time "JUL 23 - AUG 22" :text "♌︎"} ; 	Leo (Lion)	U+264C
   5 {:sign :virgio :dstart 150 :sun-time "AUG 23 - SEP 22" :text "♍︎"} ; 	Virgo (Virgin)	U+264D
   6 {:sign :libra :dstart 180 :sun-time "SEP 23 - OCT 22" :text "♎︎"} ; 	Libra (Scale)	U+264E
   7 {:sign :scorpio :dstart 210 :sun-time "OCT 23 - NOV 21" :text "♏︎"} ; Scorpio (Scorpion)	U+264F
   8 {:sign :sagittarius :dstart 240 :sun-time "NOV 22 - DEC 21" :text "♐︎"} ; 	Sagittarius (Archer)	U+2650
   9 {:sign :capricorn :dstart 270 :sun-time "DEC 22 - JAN 19" :text "♑︎"}  ;(Sea-Goat)	U+2651
   10 {:sign :aquarius :dstart 300 :sun-time "JAN 20 - FEB 18" :text  "♒︎"} ; (Waterbearer)	U+2652
   11 {:sign :piscies :dstart 330 :sun-time "FEB 19 - MAR 20" :text "♓︎"} ; (Fish) U+2653
   })

(defn get-text [i]
  (-> (get sign-dict i)
      :text))

;(get-text 3)

(defn deg->sign [d]
  (let [q (quot d 30.0)
        q (int q)]
    (get sign-dict q)))

(defn aspect [angel]
  (let [angel (Math/abs angel)]
    (cond
      (and (< angel 3)  (> angel -1)) :conjunction
      (and (< angel 32) (> angel 28)) :thirty
      (and (< angel 62) (> angel 58)) :sextile
      (and (< angel 92) (> angel 88)) :square
      (and (< angel 122) (> angel 118)) :trine
      (and (< angel 182) (> angel 178)) :opposition
      (and (< angel 47) (> angel 43)) :semi-square ; :45 degree : semi-square
      (and (< angel 74) (> angel 70)) :quintile ; :72 degree : quintile
      (and (< angel 137) (> angel 133)) :sesquiquadrate ; 135" 
      (and (< angel 146) (> angel 142)) :biquentile ; 144" 
      (and (< angel 152) (> angel 148)) :quincunx ; 150" 
      (and (< angel 53) (> angel 49)) :septile ; 51.43" 
      (and (< angel 156) (> angel 152)) :triseptile ; 154.29" 
      (and (< angel 42) (> angel 38)) :novile ; 40" 
      (and (< angel 82) (> angel 78)) :binovile ; 80" 
      ;(and (< angel 148) (> angel 152)) :parallel ; : degree 
      )))

(defn subsets [n items]
  (cond
    (= n 0) '(())
    (empty? items) '()
    :else (concat (map
                   #(cons (first items) %)
                   (subsets (dec n) (rest items)))
                  (subsets n (rest items)))))

#_(defn find-aspects-duplicates [res]
    (let [points (:points res)
          planets (keys points)]
      (for [a planets
            b planets]
        (when-not (= a b)
          (let [adeg (get-in points [a :lon])
                bdeg (get-in points [b :lon])
                angel (- adeg bdeg)
                c (aspect angel)]
            (when c
              {:type c :a a :b b}))))))

(defn aspect-for [points]
  (fn [[a b]]
    (let [adeg (get-in points [a :lon])
          bdeg (get-in points [b :lon])
          angel (- adeg bdeg)
          c (aspect angel)]
      (when c
        {:type c :a a :b b}))))

(defn find-aspects [res]
  (let [points (:points res)
        planets (keys points)
        combinations (subsets 2 planets)
        calc-aspect (aspect-for points)]
    (map calc-aspect combinations)))

(defn find-current-aspects [res]
  (->> (find-aspects res)
       (remove nil?)))

(defn deg->rad [d]
  ; Degrees x (π/180) = Radians
  (* d (/ Math/PI 180.0)))

(defn rad->deg [r]
  ; Radians  × (180/π) = Degrees
  (* r (/ 180.0 Math/PI)))

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

(defn calc-date [sdt]
  (calc (assoc geo-req :utc sdt)))

(defn aspects-for-date [sdt]
  (->> (calc-date sdt)
       find-current-aspects
       (assoc {:date sdt} :aspects)))

(defn print-aspects [{:keys [date aspects]}]
  (print "\r\nAspects @ " date)
  (print-table aspects))

(defn point-table [res]
  (let [points (:points res)]
    (map (fn [[n v]]
           (let [lon (:lon v)
                 sign (deg->sign lon)]
            ;(info "sign: " sign)
             (merge v (assoc sign :name n))))
         points)))

; (diff (set [1 2 3]) (set [5 9 3 2 3 7]))

(defn add-aspect [date new-aspect]
  [new-aspect {:start date :end date}])

(defn update-aspect [date existing-aspect]
  [existing-aspect {:end date}])

(defn map->vec [old-map]
  ;(println "old-map: " old-map)
  (map (fn [[k v]]
         ; (println "processing: k: " k " v: " v)
         (merge k v))
       old-map))

(defn deep-merge [a & maps]
  (if (map? a)
    (apply merge-with deep-merge a maps)
    (apply merge-with deep-merge maps)))

(defn add-date [v-aspect-durations {:keys [date aspects]}]
  (let [aspect-duration-map-old @v-aspect-durations
        ;_ (println "aspect-duration-map-old: " aspect-duration-map-old)
        aspect-duration-keys (keys aspect-duration-map-old)
        aspect-duration-keys (if aspect-duration-keys aspect-duration-keys '())
        ;_ (println "aspect-old: " aspect-duration-keys)
        ;_ (println "aspect-new: " aspects)
        [old new same] (diff (into #{} aspect-duration-keys) (into #{} aspects))
        ;; NEW         
        ;_ (println "new: " new)
        new-data (if new
                   (into {} (map (partial add-aspect date) new))
                   {})
        ;_ (println "new data: " new-data)
        ; SAME
        ;_ (println "same: " same)
        same-old (select-keys aspect-duration-map-old same)
        ;_ (println "same-old: " same-old)
        same-data (if same
                    (into {} (map (partial update-aspect date) same))
                    {})
        ;_ (println "same data: " same-data)
        aspect-duration-map (merge new-data (deep-merge same-old same-data))
        ;_  (println "current aspect duration map: " aspect-duration-map)
        ; OLD 
        ;_ (println "old: " old)
        old-map (select-keys aspect-duration-map-old old)
        old-vec (map->vec old-map)
        ;_ (println "old vec: " old-vec)
        ]
    ; old -> add event
    ; new -> add to accumulator with start date
    ; same -> add end date
    (vreset! v-aspect-durations aspect-duration-map)
    old-vec))

(defn xf-aspect-duration []
  (fn [xf]
    (let [v-aspect-durations (volatile! {})]
      (fn
        ([] (xf))
        ([result]
         (println "finished result: " @v-aspect-durations)
         (xf result))
        ([result {:keys [date aspects] :as input}]
         (let [finished-aspects (add-date v-aspect-durations input)]
            ;(println "finished aspects: " finished-aspects)
           (xf result finished-aspects)
           #_(doall
              (for [a finished-aspects]
                (do
                ;(println "finished aspect: " a "result: " result)
                  (xf result a))))))))))

(defn calc-aspect-durations [dates]
  (flatten (transduce (xf-aspect-duration) conj (map aspects-for-date dates))))

(defn dt-format [dt]
  (let [dtz (t/zoned-date-time dt)]
    (t/format (t/formatter :iso-instant) dtz))) ; :iso-date

(defn date-range [date-begin date-end]
  (let [date-begin (t/inst date-begin)
        date-end (t/inst date-end)
        d (t/new-duration 1 :hours)]
    (loop [dt date-begin
           r [(dt-format dt)]]
      (let [dt-next (t/>> dt d)]
        (if (t/< dt-next date-end)
          (recur dt-next (conj r (dt-format dt-next)))
          r)))))

(defn astro-test [& _]
   ;(println "subsets: " (subsets 2 [:a :b :c :d]))
  (println "diffs: "
           (diff (set [1 2 3]) (set [5 9 3 2 3 7])))
      ;[#{1} #{7 9 5} #{3 2}]
  (let [res (calc geo-req)]
    (info "astro res: " res)
    (print-table (point-table res))

    (print-table (find-current-aspects  res))

    (print-aspects (aspects-for-date "2022-02-15T00:13:00Z"))
    (print-aspects (aspects-for-date "2022-03-15T00:13:00Z"))
    (print-aspects (aspects-for-date "2022-04-15T00:13:00Z"))

    (println "durations: " (print-table  (calc-aspect-durations ["2022-02-15T00:13:00Z"
                                                                 "2022-02-16T00:13:00Z"
                                                                 "2022-02-17T00:13:00Z"
                                                                 "2022-02-18T00:13:00Z"
                                                                 "2022-02-19T00:13:00Z"
                                                                 "2022-02-20T00:13:00Z"
                                                                 "2022-02-21T00:13:00Z"])))

    #_(println "date range: " (date-range "2022-02-15T00:13:00Z"
                                          "2022-03-15T00:13:00Z"))

    (let [aspects (calc-aspect-durations
                   (date-range "2020-01-01T00:00:00Z"
                               "2023-01-01T00:00:00Z"))
          t (with-out-str
              (print-table [:type :a :b :start :end] aspects))]
      (println "durations: " t)
      (spit "../../data/aspects.txt" t)
      (spit "../../data/aspects.edn" (pr-str aspects)))
;
    ))

