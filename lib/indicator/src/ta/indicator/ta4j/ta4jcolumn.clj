(ns ta.indicator.ta4j.ta4jcolumn
  (:import
   [org.ta4j.core.num DoubleNum DecimalNum]
   [org.ta4j.core.indicators AbstractIndicator]))

(gen-class :name ta.indicator.ta4jcolumn.Hello)

(gen-class
 :name ta.indicator.ta4jcolumn.column
 :extends org.ta4j.core.indicators.AbstractIndicator
 :methods [[getValue [Long] Double]]
 :constructors {[String] []}
 :init init
   ;:exposes {baseField {:get getField :set setField}}
 )

(defn -init [s]
  [[] (ref {:s s})])

(defn -getValue [this index]
  (+ index 15.34))
