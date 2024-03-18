(ns ta.trade.roundtrips
  (:require
   [ta.indicator.helper :refer [indicator]]))

(defn- new-signal [signal]
  (case signal
    :long :long
    :short :short
    :flat :flat
    nil))

(defn signal-action []
  (indicator
   [idx (volatile! 0)
    position (volatile! :flat)
    entry (volatile! {:side :flat
                      :entry-idx 0})]
   (fn [signal]
     (let [prior-position @position
           new-position (or (new-signal signal) prior-position)
           chg? (not (= new-position prior-position))
           entry? (and chg? (contains? #{:long :short} signal))
           exit? (and chg? (contains? #{:long :short} prior-position))
           result  {:signal signal
                    :entry? entry?
                    :exit? exit?
                    :position new-position
                    :roundtrip (when exit?
                                 (assoc @entry
                                        :exit-idx @idx))}]
       (when entry?
         (vreset! entry {:entry-idx @idx
                         :side new-position}))
       (vswap! idx inc)
       (vreset! position new-position)
       result))))

(defn signal->trades-and-position
  "input: a vector of signals.
     output: a vector (with same # rows as signals)
      containing a map of position/roundtrip data"
  [signal-vec]
  (into [] (signal-action) signal-vec))

(defn signal->roundtrips [signal-vec]
  (->> (signal->trades-and-position signal-vec)
       (map :roundtrip)
       concat
       (remove nil?)
       (into [])))

(comment

  (def s [:long :hold :long :flat
          :flat
          :short :flat :flat
          :long :short :flat])

  (require '[clojure.pprint :refer [print-table]])

  (->> (signal->trades-and-position s)
       (print-table))

; | :signal | :entry? | :exit? | :position |                                 :roundtrip |
; |---------+---------+--------+-----------+--------------------------------------------|
; |   :long |    true |  false |     :long |                                            |
; |   :hold |   false |  false |     :long |                                            |
; |   :long |   false |  false |     :long |                                            |
; |   :flat |   false |   true |     :flat |   {:entry-idx 0, :side :long, :exit-idx 3} |
; |   :flat |   false |  false |     :flat |                                            |
; |  :short |    true |  false |    :short |                                            |
; |   :flat |   false |   true |     :flat |  {:entry-idx 5, :side :short, :exit-idx 6} |
; |   :flat |   false |  false |     :flat |                                            |
; |   :long |    true |  false |     :long |                                            |
; |  :short |    true |   true |    :short |   {:entry-idx 8, :side :long, :exit-idx 9} |
; |   :flat |   false |   true |     :flat | {:entry-idx 9, :side :short, :exit-idx 10} |

  (signal->roundtrips s)
;; => [{:entry-idx 0, :side :long, :exit-idx 3}
;;     {:entry-idx 5, :side :short, :exit-idx 6}
;;     {:entry-idx 8, :side :long, :exit-idx 9}
;;     {:entry-idx 9, :side :short, :exit-idx 10}]

; 
  )