

(defn make-window
  “Takes a look-forward window out of a dataframe.
  Returns nil if full window not possible
  Start is after the event”
  [df idx forward-size]
  (assert (and (df? df) (int? Idx) (int? forward-size))
          (when (< (size df) (+ idx forward-size))
            (select-cols df idx (+ idx forward-size)))))

(defn calc-channel-turningpoint-event
  [df idx dir-label forward-size]
  (assert (df-has-cols df #{:close :high :low :chan-up :chan-down})
          (when-let [w (window df idx forward-size)] ; at end of series window might be nil. 
            (let [;[p band-up band-down]  (cols df idx [:close :chan-up :chan-down])
                  r (- band-up band-down)
                  h (high (:high w))
                  l (low (:low w))]))))

; (let [df  :close

