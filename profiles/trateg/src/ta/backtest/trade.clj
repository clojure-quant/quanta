(ns ta.backtest.trade)

(defn pf [initial-equity]
  {:cash initial-equity
   :long []
   :roundtrips []})

(defn buy [{:keys [cash long roundtrips] :as pf} {:keys [symbol qty price date] :as t}]
  (let [v (* qty price)]
    {:cash (- cash v)
     :long (conj long t)
     :roundtrips roundtrips}))

(defn sell [{:keys [cash long roundtrips] :as pf} {:keys [symbol price qty date] :as t}]
  (let [v (* qty price)
        o (first (filter #(= symbol (:symbol %)) long))]
    {:cash (+ cash v)
     :long (remove #(= symbol (:symbol %)) long)
     :roundtrips (conj roundtrips {:symbol symbol
                                   :qty (:qty o) :px-open (:price o) :dt-open (:date o)
                                   :px-close price :dt-close date
                                   :pl (* (:qty o) (- price (:price o)))
                                   :data (:data o)})}))

(defn holdings [pf]
  (into #{} (map :symbol (:long pf))))

(defn trade [pf long-symbol-data  f-price dt]
  (let [long-symbols (into #{} (map :symbol long-symbol-data))
        holding (holdings pf)
        sells (filter #(not (contains? long-symbols %)) holding)
        buys (filter #(not (contains? holding (:symbol %))) long-symbol-data)
        buys (take (max 0 (- 3 (count holding))) buys)
        sell-s (fn [pf s] (sell pf {:symbol s
                                    :qty 100
                                    :date dt
                                    :price (or (f-price s) 111.33)}))
        buy-s (fn [pf {:keys [symbol data]}] (buy pf   {:symbol symbol
                                                        :qty 100
                                                        :date dt
                                                        :price (or (f-price symbol) 111.33)
                                                        :data data}))]

;(println dt "hold: " holding "buys:" buys " sells:" sells)
    (as-> pf x
      (reduce sell-s x sells)
      (reduce buy-s x buys))))

(comment

  (take 1 [9 8])

  (def p (atom 100))
  (defn getp [s]
    (swap! p inc))

  (-> (pf 100000)
      (trade [{:symbol "A" :data {:bang 7}} {:symbol "B"} {:symbol "C"}] getp "0")
      (trade [{:symbol "A"} {:symbol "B"}] getp "1")
      (trade [{:symbol "A"} {:symbol "B"} {:symbol "D"}] getp "2")
      (trade [] getp "3")))