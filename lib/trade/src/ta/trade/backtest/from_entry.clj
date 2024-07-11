(ns ta.trade.backtest.from-entry
  (:require
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.indicator.helper :refer [indicator]]
   [ta.trade.backtest.exit :refer [eventually-exit-position]]
   [ta.trade.backtest.entry :refer [eventually-entry-position]]))

(defn position? [p]
  (not (= :flat (:side p))))

(defn existing-position [p]
  (when (position? p)
    p))

(defn- manage-position-row
  [{:keys [asset entry exit] :as _opts} record-roundtrip]
  (indicator
   [position (volatile! {:side :flat})
    open-position! (fn [p] (vreset! position p))
    close-position! (fn [p]
                      (record-roundtrip p)
                      (vreset! position {:side :flat}))
    exit-signal (volatile! nil)
    set-exit-signal! (fn [s] (vreset! exit-signal s))]
   (fn [row]
     ;(println "processing row: " row)
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

(defn entry-signal->roundtrips
  "takes :entry from bar-entry-ds and iterates over all rows to create roundtrips 
   and adds :position column to var-signal-ds. This has a double purpose: 
   1. Modify ds so that position column can be displayed in a chart. 
   2. Returns roundtrips so it is not required to run backtest a second time. 
   Since it returns a map template viz functions have to use :roundtrips and :ds 
   to get the data they need."
  [{:keys [asset entry exit] :as opts} bar-entry-ds]
  (assert asset  "roundtrip-creation needs :asset in opts!")
  (assert entry  "roundtrip-creation needs :entry in opts!")
  (assert exit "roundtrip-creation needs :exit in opts!")
  (assert (:entry bar-entry-ds) "roundtrip-creation needs :entry column in bar-entry-ds!")
  (assert (:date  bar-entry-ds) "roundtrip-creation needs :date column in bar-entry-ds!")
  (assert (:close  bar-entry-ds) "roundtrip-creation needs :close column in bar-entry-ds!")
  (assert (:low  bar-entry-ds) "roundtrip-creation needs :low column in bar-entry-ds!")
  (assert (:high  bar-entry-ds) "roundtrip-creation needs :high column in bar-entry-ds!")
  (let [roundtrips (volatile! [])
        record-roundtrip (fn [p]
                           (->> (assoc p :id (inc (count @roundtrips)))
                                (vswap! roundtrips conj)))
        fun (manage-position-row opts record-roundtrip)
        bar-signal-idx-ds (tc/add-column bar-entry-ds :idx (range (tc/row-count bar-entry-ds)))
        exit-signal (into [] fun (tds/mapseq-reader bar-signal-idx-ds))
        bar-entry-exit-ds (tc/add-columns bar-entry-ds {:exit exit-signal})]
    {:opts {:entry entry
            :exit exit}
     :roundtrips (tc/dataset @roundtrips)
     :exit exit-signal
     :ds bar-entry-exit-ds}))
