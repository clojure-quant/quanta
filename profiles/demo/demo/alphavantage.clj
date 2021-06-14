(ns demo.alphavantage)


(-> "creds.edn" slurp edn/read-string
    :alphavantage av/set-key!)

(av/search "S&P 500")

(defn g [s]
  (->> s
       (av/get-daily "compact")
       (map :close)))

(defn gf [s]
  (->> s
       (av/get-daily "full")
       (map :close)))

  (search "BA")

(def b (get-daily :compact "MSFT"))
(keys b)
(MetaData- b)
(keys (TimeSeriesDaily- b))
(vals (TimeSeriesDaily- b))

  ;(->> b
  ;     ;(convert-bars-)
  ;     (clojure.pprint/print-table [:date :open :high :low :close :volume]))

(def symbols ["BTC" "ETH" "LTC" "DASH" "NANO" "EOS" "XLM"])

(get-daily :compact "MSFT")
(get-daily-fx :compact "EURUSD")
(get-daily-crypto :compact "BTC")

(get-crypto-rating "BTC")

(set-key! "hhh")
@api-key

(map get-crypto-rating ["BTC" "ETH" "LTC" "DASH" "NANO" "EOS" "XLM"])

(def plust (throttler.core/throttle-fn + 5 :minute))

  ; this should be fast
(time
 (map #(plust 1 %) (range 2)))

(time
 (map #(plust 1 %) (range 7)))