(ns ta.trade.signal.core
  (:require
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.indicator.helper :refer [indicator]]
   [ta.trade.signal.exit :refer [eventually-exit-position]]
   [ta.trade.signal.entry :refer [eventually-entry-position]]))

(defn position? [p]
  (not (= :flat (:side p))))

(defn existing-position [p]
  (when (position? p)
    p))

(defn- manage-position-row
  [{:keys [asset entry exit] :as opts} record-roundtrip]
  (indicator
   [position (volatile! {:side :flat})
    open-position! (fn [p] (vreset! position p))
    close-position! (fn [p]
                      (record-roundtrip p)
                      (vreset! position {:side :flat}))
    exit-signal (volatile! nil)
    set-exit-signal! (fn [s] (vreset! exit-signal s))]
   (fn [row]
     (println "processing row: " row)
     (set-exit-signal! :none)
     ; exit
     (when-let [p (existing-position @position)]
       (when-let [p (eventually-exit-position exit p row)]
         (set-exit-signal! :close)
         (close-position! p)))
     ;entry
     (when-not (position? @position)
       (when-let [p (eventually-entry-position asset entry row)]
         (open-position! p)))
     ; signal
     @exit-signal)))

(defn create-positions
  "takes :entry from bar-signal-ds and iterates over all rows to create roundtrips 
   and adds :position column to var-signal-ds. This has a double purpose: 
   1. Modify ds so that position column can be displayed in a chart. 
   2. Returns roundtrips so it is not required to run backtest a second time. 
   Since it returns a map template viz functions have to use :roundtrips and :ds 
   to get the data they need."
  [{:keys [entry exit] :as opts} bar-signal-ds]
  (assert (:signal bar-signal-ds) "to create roundtrips :signal column needs to be present!")
  (assert (:date  bar-signal-ds) "to create roundtrips :date column needs to be present!")
  (assert (:close  bar-signal-ds) "to create roundtrips :close column needs to be present!")
  (let [roundtrips (volatile! [])
        record-roundtrip (fn [p] (vswap! roundtrips conj p))
        _ (println "creating transducer..")
        fun (manage-position-row opts record-roundtrip)
        _ (println "creating exit-signal ..")
        exit-signal (into [] fun (tds/mapseq-reader bar-signal-ds))]
    (println "returning result..")
    {:roundtrips @roundtrips
     :exit exit-signal
     ;:ds (tc/add-columns bar-signal-ds {:exit exit-signal})
     }))
(comment
  (require '[tick.core :as t])

  (def ds (tc/dataset {:idx [1 2 3 4 5 6]
                       :date (repeatedly 6 #(t/instant))
                       :close [100.0 104.0 106.0 103.0 102.0 108.0]
                       :high [100.0 104.0 106.0 103.0 102.0 108.0]
                       :low [100.0 104.0 106.0 103.0 102.0 108.0]
                       ;:open [100.0 101.0 102.0 103.0]
                       :signal [:long :nil :nil :short :nil :nil]}))

  ds

  (create-positions {:asset "QQQ"
                     :entry [:fixed-qty 3.1]
                     :exit [:time 15
                            :loss-percent 2.5
                            :profit-percent 5.0]}
                    ds)

; 
  )