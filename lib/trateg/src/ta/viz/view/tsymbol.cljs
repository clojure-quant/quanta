(ns ta.viz.view.tsymbol
  (:require
   [reagent.core :as r]
   [layout]
   [site]
   [input]
   [goldly.service.core :refer [run-a]]))

(defn run-on-change [input-atom input-path f]
  (let [loaded (r/atom nil)]
    (fn []
      (let [current (get-in @input-atom input-path)]
        [:div
         (when-not (= current @loaded)
           (reset! loaded current)
           (f current)
           nil)]))))

(defn symbol-picker [_state-atom _path-symbol]
  (let [state-internal (r/atom {:lists []
                                :symbols-in-list ["BTCUSD" "SPY" "QQQ" "GLD" "SLV" "EURUSD"]
                                :list "currency-etf"})]
    (run-a state-internal [:lists]
           'ta.warehouse.symbollist/get-lists)
    (fn [state-atom path-symbol]
      [:div
       [run-on-change state-internal [:list]
        (fn [list]
          ;(println "getting symbols for list " list)
          (run-a state-internal [:symbols-in-list]
                 'ta.warehouse.symbollist/load-list list))]
       [input/select
        {:nav? false
         :items (:lists @state-internal)}
        state-internal [:list]]
       [input/select
        {:nav? false
         :items (:symbols-in-list @state-internal)}
        state-atom path-symbol]])))
