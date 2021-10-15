(ns demo.playground.indicator
  (:require
   [tablecloth.api :as tc]
   [ta.series.indicator :as ind]
   [ta.warehouse :as wh]
   [ta.series.ta4j :as ta4j]
   [demo.env.config :refer [w-crypto]]))

; test our indicators
(ind/sma 2 [1 1 2 2 3 3 4 4 5 5])

    ; test calculating simple indicators
(let [ds (wh/load-symbol w-crypto  "D" "ETHUSD")
      bars (ta4j/ds->ta4j-ohlcv ds)
      close (ta4j/ds->ta4j-close ds)]
  (-> (ta4j/ind :ATR bars 14) (ta4j/ind-values))
  (-> (ta4j/ind :SMA close 14) (ta4j/ind-values)))

(defn bad-add [a b]
  (println "bad add params:" a b)
  (+ a b 1000))

(defn bad-add2 [[a b]]
    ;(info "bad add params:" a b)
  (+ a b 1000))

(defn bad-add-vec [a b]
  (println "bad add-vec params:" a b)
  (map bad-add a b))

(->> (tc/dataset
      {:min [1.0 2 3 4 5 6 7 8 9 10 11 12]
       :max [10.0 12 13 41 5 6 7 8 9 10 11 12]})
     ((juxt :min :max))

     (apply bad-add-vec)
       ;(apply fun/>)
     )

((juxt + -) 1 2 3)

(tc/map-columns :test [:close :bb-upper] bad-add)
       ;(apply bad-add-vec)
       ;(apply fun/>)


