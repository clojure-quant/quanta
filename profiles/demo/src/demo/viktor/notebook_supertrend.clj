(ns demo.viktor.notebook-supertrend
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [tablecloth.api :as tablecloth]
   [ta.dataset.backtest :as backtest]
   [ta.dataset.helper :as helper]
   [ta.dataset.sma :as sma]
   [ta.dataset.supertrend :as supertrend]
   [demo.viktor.strategy-bollinger :as bs]
   [demo.env.warehouse :refer [w]]))

(def default-options
  {:atr-length 20
   :atr-mult 0.5})

(def r
  (backtest/run-study w "ETHUSD" "D"
                      supertrend/study-supertrend
                      default-options))

(defn print [ds-study]
  ; this function runs differently if outisde a function!
  (-> ds-study
      (tablecloth/select-columns [:index :date :close
                               ;:atr :atr-scaled
                               ;:upper :lower 
                               ;:upper-1 :lower-1
                                  :signal
                                  :position
                                  :trade
                             ;:chg
                                  :chg-p])
    ;(helper/print-all)
      (helper/print-overview)
      #_(tablecloth/random 50)))

(defn stats [ds-study]
  (-> ds-study
      (tablecloth/group-by :position)
      (tablecloth/aggregate {:bars (fn [ds]
                                     (->> ds
                                          :close
                                          count))
                             :trades (fn [ds]
                                       (->> ds
                                            :trade
                                            (remove nil?)
                                            count))
                             :points (fn [ds]

                                       (->> ds
                                            :chg
                                            (apply +)))
                             :prct (fn [ds]
                                     (->> ds
                                          :chg-p
                                          (apply +)))})
      (tablecloth/set-dataset-name (tablecloth/dataset-name ds-study))
      ))

(defn trades [ds-study]
  ; this function runs differently if outisde a function!
  (-> ds-study
      (tablecloth/select-rows (fn [{:keys [trade] :as row}]
                                (not (nil? trade))))
      (tablecloth/select-columns [:trade-no :index :date :close
                               ;:atr :atr-scaled
                               ;:upper :lower 
                               ;:upper-1 :lower-1
                                 ; :signal
                                  ; :position
                                  :trade
                             ;:chg
                                  :chg-p])
      (helper/print-all)
     ;(helper/print-overview)
      #_(tablecloth/random 50)))

(defn trade-details [ds-study]
  (-> ds-study
      (tablecloth/group-by :trade-no)
      (tablecloth/aggregate {:trade (fn [ds]
                                      (->> ds
                                           :trade
                                           first))
                             :index-open (fn [ds]
                                           (->> ds
                                                :index
                                                first))
                             :index-close (fn [ds]
                                            (->> ds
                                                 :index
                                                 last))
                             :date-open (fn [ds]
                                          (->> ds
                                               :date
                                               first))
                             :date-close (fn [ds]
                                           (->> ds
                                                :date
                                                last))
                             :price-open (fn [ds]
                                           (->> ds
                                                :close
                                                first))
                             :price-close (fn [ds]
                                            (->> ds
                                                 :close
                                                 last))

                             :bars (fn [ds]
                                     (->> ds
                                          :close
                                          count))
                             :trades (fn [ds]
                                       (->> ds
                                            :trade
                                            (remove nil?)
                                            count))
                             :points (fn [ds]

                                       (->> ds
                                            :chg
                                            (apply +)))
                             :prct (fn [ds]
                                     (->> ds
                                          :chg-p
                                          (apply +)))})

      (tablecloth/select-columns [:$group-name
                                  :index-open :index-close
                                  :bars
                                  :trade
                                   ; :date-open :date-close

; :price-open :price-close

                               ;:atr :atr-scaled
                               ;:upper :lower 
                               ;:upper-1 :lower-1
                                 ; :signal
                                  ; :position
                                   ;:trade
                             ;:chg
                                  :prct])
      (helper/print-all)))

(print r)
(stats r)
(trades r)
(trade-details r)


(-> (stats r)
    (tablecloth/group-by :$group-name :as-map)
    )


;; see if it works better for 15min bars

(def options-15
  {:atr-length 40
   :atr-mult 0.75})


(def r15
  (backtest/run-study w "ETHUSD" "15"
                      supertrend/study-supertrend
                      options-15))

(stats r15)

(trade-details r15)

;; run a couple different variations

(def default-options
  {:atr-length 20
   :atr-mult 0.5})

(for [m [0.5 0.75 1.0 1.25 1.5 1.75 2.0]]
  (let [options (assoc default-options 
                       :atr-mult m)
        _ (println "options: " options)
        r (backtest/run-study w "ETHUSD" "15"
                              supertrend/study-supertrend
                              options)
        r (tablecloth/set-dataset-name r m)
        ]
    (println r)
    (println "atr-mult: " m)
    (stats r)
    ))


(def next-options
  {:atr-length 20
   :atr-mult 0.75})

(for [m [10 15 20 25 30 35 40 45 50]]
  (let [options (assoc next-options
                       :atr-length m)
        _ (println "options: " options)
        r (backtest/run-study w "ETHUSD" "15"
                              supertrend/study-supertrend
                              options)
        r (tablecloth/set-dataset-name r m)
        ]
    (println r)    
    (println (stats r))
    ))
