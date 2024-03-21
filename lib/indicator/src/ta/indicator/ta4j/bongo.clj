(ns ta.indicator.ta4j.bongo
  (:require [ta.indicator.ta4j.ta4jcolumn])
  (:import [ta.indicator.ta4jcolumn Hello column]))

(compile 'ta.indicator.ta4jcolumn)

(ta.indicator.ta4jcolumn.Hello.)

(def b (ta.indicator.ta4jcolumn.column. "asdf"))

(.getValue b 5)

(Class/forName "java.lang.String")

(Class/forName "ta.indicator.ta4jcolumn.Hello")