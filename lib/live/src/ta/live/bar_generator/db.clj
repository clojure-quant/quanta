(ns ta.live.bar-generator.db)

(defn create-db []
  (atom {}))

(defn- quote->quote-subscription [quote]
  (select-keys quote [:asset :feed]))

(defn- add-bar-asset [db bar-asset]
  (let [bar {:asset (:asset bar-asset) :epoch 1}
        quote-subscription (quote->quote-subscription bar-asset)
        calendar (:calendar bar-asset)]
    (assoc-in db [quote-subscription calendar] (atom bar))))

(defn add [db bar-asset]
  (swap! db add-bar-asset bar-asset))

(defn get-bar-atoms-for-quote [db quote]
  ; this function needs to be fast!
  ; the structure of the db is designed for this.
  (let [quote-subscription (quote->quote-subscription quote)]
    (-> @db
        (get quote-subscription)
        vals)))

(defn get-bar-atoms-for-calendar [db calendar]
  (->> (vals @db) ; seq of maps
       (map (fn [m]
              (map (fn [[cal bar]]
                     {:calendar cal :bar bar}) m)))
       flatten
       (filter #(= (:calendar %) calendar))
       (map :bar)))

(comment
  (def db (create-db))
  @db
  (add db {:asset "EUR/USD" :feed :fx :calendar [:us :d]})
  (add db {:asset "EUR/USD" :feed :fx :calendar [:us :m]})
  (add db {:asset "EUR/USD" :feed :fx2 :calendar [:us :m]})
;; => {{:asset "EUR/USD", :feed :fx} {[:us :d] {:asset "EUR/USD", :epoch 1}, [:us :m] {:asset "EUR/USD", :epoch 1}},
;;     {:asset "EUR/USD", :feed :fx2} {[:us :m] {:asset "EUR/USD", :epoch 1}}}

  (get-bar-atoms-for-calendar db [:us :m])
;; => (#<Atom@5bb45530: {:asset "EUR/USD", :epoch 1}>
;;     #<Atom@b9b23a8: {:asset "EUR/USD", :epoch 1}>)
  (get-bar-atoms-for-calendar db [:us :d])
;; => (#<Atom@2b170505: {:asset "EUR/USD", :epoch 1}>) 

  (get-bar-atoms-for-quote db {:asset "EUR/USD" :feed :fx})
;; => (#<Atom@2b170505: {:asset "EUR/USD", :epoch 1}> #<Atom@5bb45530: {:asset "EUR/USD", :epoch 1}>)

  (get-bar-atoms-for-quote db {:asset "EUR/USD" :feed :fx2})

 ;
  )
