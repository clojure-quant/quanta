(in-ns 'ta.dataframe)

(defn align
  "aligns timeseries to a calendar

   ts-index: any type that is used for time indexing 
             (datetime / localdate / keyword / long)
   calendar: seq of ts-index
   indexed-series: map, keys=ts-index, vals=map containing 
                   all data of one 
   instruments: map, keys=symbols, vals=indexed series
  "
  [calendar instruments]
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

(defn series->indexed [series]
  (reduce (fn [r row]
            (assoc r (:date row) (dissoc row :date)))
          {} series))

(defn indexed->series [calendar indexed]
  (map (fn [date] (assoc (get indexed date) :date date)) calendar))

(defn load-aligned
  "use fn-load-bars to load series for all symbols
   each row is a map, with keys being the symbols and
   values the loaded series data for that time
   only those indices in the calendar are returned
   calendar ordering is obeyed"
  ([fn-load-bars symbols calendar]
   (let [load->indexed (comp series->indexed fn-load-bars)
         instruments (reduce (fn [r symbol]
                               (assoc r symbol (load->indexed symbol))) {} symbols)]
     (->> (align calendar instruments)
          (indexed->series calendar))))
  ([fn-load-bars symbols] ; take calendar from first symbol
   (load-aligned fn-load-bars symbols
                 (map :date (fn-load-bars (first symbols))))))

