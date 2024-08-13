(ns ta.trade.roundtrip)

(defn- sign-switch [side v]
  (case side
    :long v
    :short (- 0.0 v)
    v))

(defn return-abs [{:keys [exit-price entry-price side] :as _roundtrip}]
  (sign-switch side (- exit-price entry-price)))

(defn return-prct [{:keys [entry-price] :as roundtrip}]
  (-> 100.0 (* (return-abs roundtrip) (/ entry-price))))

(defn return-log [{:keys [entry-price exit-price side] :as _roundtrip}]
  (sign-switch side (- (Math/log10 exit-price) (Math/log10 entry-price))))

(defn set-exit-price-percent [{:keys [entry-price side] :as roundtrip} percent]
  (let [m (+ 1.0 (/ (sign-switch side percent) 100.0))]
    (assoc roundtrip :exit-price (* m entry-price))))

(comment

  (return-abs {:entry-price 100.0 :exit-price 101.0 :side :long})
  (return-prct {:entry-price 100.0 :exit-price 101.0 :side :long})
  (return-log {:entry-price 100.0 :exit-price 101.0 :side :long})

  (return-abs {:entry-price 100.0 :exit-price 101.0 :side :short})
  (return-prct {:entry-price 100.0 :exit-price 101.0 :side :short})
  (return-log {:entry-price 100.0 :exit-price 101.0 :side :short})

  (set-exit-price-percent {:entry-price 100.0 :side :long} 5.0)
  (set-exit-price-percent {:entry-price 100.0 :side :short} 5.0)

;
  )