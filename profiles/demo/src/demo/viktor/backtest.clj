


; 1x
; fetch 15 min bars bybit
; save ts to disk

; fuer jede analyse
; load ts from disk
; bollinger band (moving average + std dev) upper-band lower-band
; event finden: close>upper oder close<lower => seqence of index.
; filter events, sodass nur alle 30 bars ein event stattfindet
; walk-forward window anlegen
; max/min innerhalb des walk-forward window finden
; max/min relativ zur range normalisieren
; ist der move ge-skewed (up fuer cross lower-channel, down fuer cross upper-channel)
; optimizer fuer beste parameter.

; parameter
; bollinger moving average length
; bollinger standard derivation multiplyer
; bollinger std dev lookback length
; forward window length


; event bollinger cross     ==> liste of event-bollinger-cross
; cross-type #{:up :down}
; up
; down
; up%
; down%
; diff   (up-down)
; diff%  (up% - down%)

; target funktion
; for cross-type-up: average diff% 
; for cross-type-down: (-average diff%)


(defn find-cross-up
  [ds]
  "returns a seq of vecs
   [idx date close bollinger-up bollinger-down]")

(api/dataset [[:A [1 2 3 4 5 6]] [:B "X"] [:C :a]])

(defn find-cross-down)

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

(defn events-goodness
  [df bollinger-events]
  "input: sequence of bollinger events
   output: value that can be put to the optimizer (average difference of range)")