(ns ta.series.ta4j
  "convenience wrapper on the java library ta4j"
  (:require
   [tick.alpha.api :as t]
  ; [tech.v3.dataset.print :as print]
   [tech.v3.dataset :as tds]
  ; [tech.v3.datatype.datetime :as datetime]
  ; [tablecloth.api :as tablecloth]
   )
  (:import [org.ta4j.core.num DoubleNum DecimalNum])
  (:import [org.ta4j.core BaseStrategy #_BaseTimeSeries$SeriesBuilder
            ;TimeSeriesManager
            ]))

; https://github.com/ta4j/ta4j/
; https://ta4j.github.io/ta4j-wiki/
; https://oss.sonatype.org/service/local/repositories/releases/archive/org/ta4j/ta4j-core/0.14/ta4j-core-0.14-javadoc.jar/!/org/ta4j/core/num/DoubleNum.html#valueOf(float)

; More than 130 technical indicators (Aroon, ATR, moving averages, parabolic SAR, RSI, etc.)
; A powerful engine for building custom trading strategies
; Utilities to run and compare strategies
; Minimal 3rd party dependencies
;
; the original ta4j wrapper used 0.12 - we upgraded to 0.14
; some of the function names have changed.

;; ta4j class helpers

(defn constructor [pre-str post-str]
  (fn [class-key args]
    (let [kns       (when-let [x (namespace class-key)] (str x "."))
          class-str (str pre-str kns (name class-key) post-str)]
      ;(println "TA4J constructor name: " class-str)
      (clojure.lang.Reflector/invokeConstructor
       (resolve (symbol class-str))
       (to-array args)))))

(defn ind [class-key & args]
  (let [ctor (constructor "org.ta4j.core.indicators." "Indicator")]
    (ctor class-key args)))

(defn ind-values
  ([ind] (ind-values (-> ind .getBarSeries .getBarCount) ind))
  ([n ind]
   (->> (map #(->> % (.getValue ind) .doubleValue)
             (range n)))))

(defn num-double [d]
  (DoubleNum/valueOf d))

(defn num-decimal [d]
  (DecimalNum/valueOf d))

; tml-dataset -> ta4j data conversion

(defn ds->ta4j-ohlcv
  [ds]
  (let [series (org.ta4j.core.BaseBarSeries.)
        r (tds/mapseq-reader ds)]
    (doseq [{:keys [date open high low close volume]} r]
      (let [ldt (t/in date "UTC")] ; convert time instance to (zoned)localdate
        ;(info "adding: " date open ldt)
        (.addBar series ldt open high low close volume)))
    series))

(defn ds->ta4j-close
  [ds]
  (let [ta4j-series (ds->ta4j-ohlcv ds)]
    (ind :helpers/ClosePrice ta4j-series)))

; trading rules

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

;;todo: other constructor signatures
(defn base-strategy [entry-rule exit-rule]
  (BaseStrategy. entry-rule exit-rule))

#_(defn run-strat [series strat]
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
                     :idx-exit  (-> t .getExit

                                    .getIndex)}))))



