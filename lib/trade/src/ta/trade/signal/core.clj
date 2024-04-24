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
  "takes :entry from bar-entry-ds and iterates over all rows to create roundtrips 
   and adds :position column to var-signal-ds. This has a double purpose: 
   1. Modify ds so that position column can be displayed in a chart. 
   2. Returns roundtrips so it is not required to run backtest a second time. 
   Since it returns a map template viz functions have to use :roundtrips and :ds 
   to get the data they need."
  [{:keys [entry exit] :as opts} bar-entry-ds]
  (assert (:entry bar-entry-ds) "to create roundtrips :entry column needs to be present!")
  (assert (:date  bar-entry-ds) "to create roundtrips :date column needs to be present!")
  (assert (:close  bar-entry-ds) "to create roundtrips :close column needs to be present!")
  (assert (:low  bar-entry-ds) "to create roundtrips :low column needs to be present!")
  (assert (:high  bar-entry-ds) "to create roundtrips :high column needs to be present!")
  (let [roundtrips (volatile! [])
        record-roundtrip (fn [p]
                           (->> (assoc p :id (inc (count @roundtrips)))
                                (vswap! roundtrips conj)))
        fun (manage-position-row opts record-roundtrip)
        bar-signal-idx-ds (tc/add-column bar-entry-ds :idx (range (tc/row-count bar-entry-ds)))
        exit-signal (into [] fun (tds/mapseq-reader bar-signal-idx-ds))
        bar-entry-exit-ds (tc/add-columns bar-entry-ds {:exit exit-signal})]
    {:roundtrips (tc/dataset @roundtrips)
     :exit exit-signal
     :ds bar-entry-exit-ds}))
