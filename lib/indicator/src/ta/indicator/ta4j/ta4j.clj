(ns ta.indicator.ta4j.ta4j
  "convenience wrapper on the java library ta4j"
  (:require
   [tick.core :as t]
   [tech.v3.datatype :as dtype]
   [tech.v3.dataset :as tds])
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

;; ta4j class helpers

(defn- constructor [pre-str post-str]
  (fn [class-key args]
    (let [kns       (when-let [x (namespace class-key)] (str x "."))
          class-str (str pre-str kns (name class-key) post-str)]
      (println "class-str: " class-str)
      (clojure.lang.Reflector/invokeConstructor
       (resolve (symbol class-str))
       (to-array args)))))

(defn indicator [ind-kw args]
  (let [ctor (constructor "org.ta4j.core.indicators." "Indicator")]
    (ctor ind-kw args)))

(defn facade [ind-kw args]
  (let [ctor (constructor "org.ta4j.core.indicators." "Facade")]
    (ctor ind-kw args)))

(defn get-data [o m args]
  (clojure.lang.Reflector/invokeInstanceMethod o m (to-array args)))

(defn ind-values
  ([ind] (ind-values (-> ind .getBarSeries .getBarCount) ind))
  ([n ind]
   (->> (map #(->> % (.getValue ind) .doubleValue)
             (range n)))))

(defn ind-values-bool
  ([ind]
   (println "ind-values-bool!")
   (ind-values-bool (-> ind .getBarSeries .getBarCount) ind))
  ([n ind]
   (println "get-values-bool: " ind)
   (->> (map #(->> % (.getValue ind) .booleanValue)
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
        (.addBar series ldt open high low close volume)))
    series))

(defn ds->ta4j-close
  [ds]
  (let [ta4j-series (ds->ta4j-ohlcv ds)]
    (indicator :helpers/ClosePrice (list ta4j-series))))

(defn get-column
  [ds col]
  (let [series (org.ta4j.core.BaseBarSeries.)
        r  (dtype/->reader (ds col))]
    (doseq [data r]
      (.addPrice series data))
    series))

(comment
  (require '[ta.helper.date-ds  :refer [days-ago]])
  (require '[tablecloth.api :as tc])
  (def ds
    (-> {:open [10.0 10.6 10.7]
         :high [10.0 10.6 10.7]
         :low [10.0 10.6 10.7]
         :close [10.0 10.6 10.7]
         :volume [10.0 10.6 10.7]
         :date [(days-ago 3) (days-ago 2) (days-ago 1)]}
        tc/dataset))
  ds

  (def x (:close ds))
  x
  (get-column ds :close)

  (-> (ds->ta4j-close ds)
      (ind-values))

  (ds->ta4j-ohlcv ds)

  (:close ds)

  (def close  (ds->ta4j-close ds))

  (get-column ds :close)

  (indicator :SMA [close 2])

; 
  )

(def indicators [:AbstractEMA
                 :Abstract
                 :AccelerationDeceleration
                 :AccumulationDistribution
                 :ADX
                 :Amount
                 :AroonDown
                 :AroonOscillator
                 :AroonUp
                 :ATR
                 :AwesomeOscillator
                 :BearishEngulfing
                 :BearishHarami
                 :BollingerBandsLower
                 :BollingerBandsMiddle
                 :BollingerBandsUpper
                 :BollingerBandWidth
                 :BooleanTransform
                 :BullishEngulfing
                 :BullishHarami
                 :Cached
                 :CashFlow  ; misses Indicator
                 :CCI
                 :ChaikinMoneyFlow
                 :ChaikinOscillator
                 :ChandelierExitLong
                 :ChandelierExitShort
                 :Chop
                 :CloseLocationValue
                 :ClosePrice
                 :CMO
                 :Combine
                 :Constant
                 :ConvergenceDivergence
                 :CoppockCurve
                 :CorrelationCoefficienticator
                 :Covariance
                 :Cross
                 :DateTime
                 :DeMarkPivotPoint
                 :DeMarkReversal
                 :DifferencePercentage
                 :DistanceFromMA
                 :Doji
                 :DoubleEMA
                 :DPO
                 :DX
                 :EMA
                 :FibonacciReversal
                 :Fisher
                 :FixedBoolean
                 :FixedDecimal
                 :Fixed
                 :Gain
                 :HighestValue
                 :HighPrice
                 :HMA
                 :IchimokuChikouSpan
                 :IchimokuKijunSen
                 :IchimokuLine
                 :IchimokuSenkouSpanA
                 :IchimokuSenkouSpanB
                 :IchimokuTenkanSen
                 :III
                 :KAMA
                 :KeltnerChannelLower
                 :KeltnerChannelMiddle
                 :KeltnerChannelUpper
                 :KST
                 :Loss
                 :LowerShadow
                 :LowestValue
                 :LowPrice
                 :LWMA
                 :MACD
                 :MassIndex
                 :MeanDeviation
                 :MedianPrice
                 :MinusDI
                 :MinusDM
                 :MMA
                 :MVWAP
                 :Numeric
                 :NVII
                 :OnBalanceVolume
                 :OpenPrice
                 :ParabolicSar
                 :PearsonCorrelation
                 :PercentB
                 :PeriodicalGrowthRate
                 :PivotPoint
                 :PlusDI
                 :PlusDM
                 :PPO
                 :PreviousValue
                 :PriceVariation
                 :PVI
                 :PVO
                 :RAVI
                 :RealBody
                 :RecursiveCached
                 :Returns ; no Indicator
                 :ROC
                 :ROCV
                 :RSI
                 :RWIHigh
                 :RWILow
                 :Sigma
                 :SimpleLinearRegression
                 :SMA
                 :StandardDeviation
                 :StandardError
                 :StandardReversal
                 :StochasticOscillatorD
                 :StochasticOscillatorK
                 :StochasticRSI
                 :Sum
                 :ThreeBlackCrows
                 :ThreeWhiteSoldiers
                 :TradeCount
                 :Transform
                 :TR
                 :TripleEMA
                 :TypicalPrice
                 :UlcerIndex
                 :Unstable
                 :UpperShadow
                 :Variance
                 :Volume
                 :VWAP
                 :WilliamsR
                 :WMA
                 :ZLEMA])

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

(comment
  (require '[ta.helper.date-ds  :refer [days-ago]])
  (require '[tablecloth.api :as tc])
  (def ds
    (-> {:open [10.0 10.6 10.7]
         :high [10.0 10.6 10.7]
         :low [10.0 10.6 10.7]
         :close [10.0 10.6 10.7]
         :volume [10.0 10.6 10.7]
         :date [(days-ago 3) (days-ago 2) (days-ago 1)]}
        tc/dataset))
  ds
  (ds->ta4j-ohlcv ds)
  (ds->ta4j-close ds)
  (def close  (ds->ta4j-close ds))

  (ind :SMA close 2)

; 
  )
