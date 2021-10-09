(ns demo.warehouse.import
  (:require
   [taoensso.timbre :refer [trace debug info infof  error]]
   [tech.v3.dataset :as tds]
   [ta.data.alphavantage :as av]
   [ta.warehouse :as wh]
   [demo.env.warehouse :refer [w]]))

(defn gc [s]
  (av/get-daily "compact" s))

(gc "MSFT")

(defn gf [s]
  (->> s
       (av/get-daily "full")))

(defn import-symbol [s]
  (let [d (gf s)
        ds (tds/->dataset d)]
    (println "imported " s " - " (count d) "bars.")
    ;(println (pr-str d))
    (wh/save-ts w ds s)))

(defn import-ts [{:keys [symbol-list]}]
  (let [symbols (wh/load-list w symbol-list)]
    (infof "importing symbol-list: %s (%s) " symbol-list (count symbols))
    (doall (map import-symbol symbols))))

(defn show [s]
  (let [ds (wh/load-ts w s)
        r (tds/mapseq-reader ds)
        l (last r)]
    (println s " - last: " l)))

(defn show-ts [{:keys [symbol-list]}]
  (let [symbols (wh/load-list w symbol-list)]
    (infof "showing symbol-list: %s (%s) " symbol-list (count symbols))
    (doall (map show symbols))))

(comment

  (av/search "S&P 500")
  (print-table [:symbol :type :name] (av/search "BA"))

;; # stock series

  (av/get-daily :compact "MSFT")
  (print-table (->> (av/get-daily :compact "MSFT")
                    reverse
                    (take 5)))

;; # fx series

  (print-table (take 5 (reverse (av/get-daily-fx :compact "EURUSD"))))

;; # crypto series

  (print-table (take 5 (reverse (av/get-daily-crypto :compact "BTC"))))

  (av/get-crypto-rating "BTC")

; since we can only do 5 requests a minute, and we have 7 symbols, this
; will at least sleep for 1 minutes, after getting the first 5 symbols. However since before 
; we also execute requests, it might take 2 minutes
  (clojure.pprint/print-table
   (map av/get-crypto-rating ["BTC" "ETH" "LTC" "DASH"
                              "NANO" "EOS" "XLM"]))

;; # fidelity select search

  (clojure.pprint/print-table (av/search "Fidelity MSCI"))

  (clojure.pprint/print-table (load-edn-resource "ta/fidelity-select.edn"))

 ; 
  )