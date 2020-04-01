(ns ta.series.ta4j
  "convenience wrapper on the java library ta4j"
  (:import [org.ta4j.core BaseStrategy #_BaseTimeSeries$SeriesBuilder
            TimeSeriesManager]))

(defn ind-values
  ([ind] (ind-values (-> ind .getTimeSeries .getBarCount) ind))
  ([n ind]
   (->> (map #(->> % (.getValue ind) .doubleValue)
             (range n)))))

(defn constructor [pre-str post-str]
  (fn [class-key args]
    (let [kns       (when-let [x (namespace class-key)] (str x "."))
          class-str (str pre-str kns (name class-key) post-str)]
      (clojure.lang.Reflector/invokeConstructor
       (resolve (symbol class-str))
       (to-array args)))))

(defn ind [class-key & args]
  (let [ctor (constructor "org.ta4j.core.indicators." "Indicator")]
    (ctor class-key args)))

(defn rule [class-key & args]
  (let [ctor (constructor "org.ta4j.core.trading.rules." "Rule")]
    (ctor class-key args)))

(defn crit [class-key & args]
  (let [ctor (constructor "org.ta4j.core.analysis.criteria." "Criterion")]
    (ctor class-key args)))

;;Note: Doesn't work with parameterized crits.
(defn crit-values [crit-key series trades]
  (.doubleValue (.calculate (crit crit-key) series trades)))

(defn analysis [class-key & args]
  (let [ctor (constructor "org.ta4j.core.analysis." "")]
    (ctor class-key args)))

(defn ->series
  "Bars should be a sequnce of maps containing :date/:open/:high/:low/:close/:volume"
  [bars]
  (let [s (.build (org.ta4j.core.BaseTimeSeries$SeriesBuilder.))]
    (doseq [{:keys [date open high low close volume]} bars]
      (.addBar s date open high low close volume))
    s))

;;todo: other constructor signatures
(defn base-strategy [entry-rule exit-rule]
  (BaseStrategy. entry-rule exit-rule))

(defn run-strat [series strat]
  (let [mgr (TimeSeriesManager. series)]
    (.run mgr strat)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;ta4j->clj;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn record->clj [series rec]
  (->> (.getTrades rec)
       (map (fn [t] {:px-entry (-> t .getEntry .getPrice .doubleValue)
                     :px-exit  (-> t .getExit .getPrice .doubleValue)
                     :entry-time  (->> t .getEntry .getIndex (.getBar series) .getEndTime)
                     :exit-time   (->> t .getExit .getIndex (.getBar series) .getEndTime)
                     :idx-entry (-> t .getEntry .getIndex)
                     :idx-exit  (-> t .getExit .getIndex)}))))



