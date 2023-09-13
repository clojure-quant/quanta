(ns joseph.lib.live-pl
  (:require
   [goldly.js :refer [to-fixed]]
   ))


(defn round-number-digits
  [digits number] ; digits is first parameter, so it can easily be applied (data last)
  (if (nil? number) "" (to-fixed number digits)))


(defn current-pl [{:keys [qty side entry-price]} current-price]
  (let [change (- current-price entry-price)
        qty2 (if (= side :long)
               qty
               (- 0.0 qty))
        pl (* qty2 change)]
    (round-number-digits 0 pl)))


(defn trades-with-pl [trades quotes]
  (let [quote-dict (->> quotes
                        (map (juxt :symbol identity))
                        (into {}))]
    (println "quote-dict: " quote-dict)
    (map (fn [{:keys [symbol] :as trade}]
           (if-let [q (get quote-dict symbol)]
             (let [current-price (:close q)]
               (assoc trade :current-price current-price
                      :current-pl (current-pl trade current-price)))
             (assoc trade :current-price ""
                    :current-pl "")))
         trades)))