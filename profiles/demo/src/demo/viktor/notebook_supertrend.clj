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
                                          (apply +)))})))

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



