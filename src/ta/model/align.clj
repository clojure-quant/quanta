(ns ta.model.align)

(defn align [calendar instruments]
  ;(let [trigger? (fn [_] (> (rand) 0.5))]
  (reduce
   (fn [acc date]
     (assoc acc date (into {}
                           (for [[ticker bars] instruments
                                 :let [bar (get bars date)]
                                 :when bar]
                             [ticker bar ; (assoc bar :trigger? (trigger? (:close bar)))
                              ]))))
   {}
   calendar));)

(comment

  (def instruments {:spy {"2020-01-01" {:close 10}
                          "2020-01-02" {:close 11}
                          "2020-01-03" {:close 11}}
                    :iwm {"2020-01-02" {:close 11}
                          "2020-01-03" {:close 11}}})

  (def calendar ["2020-01-01" "2020-01-02" "2020-01-03"])


  (align calendar instruments)

  ; comment end
  )
