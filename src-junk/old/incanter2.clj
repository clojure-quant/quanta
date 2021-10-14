(ns junk.incanter2)

(ns learning.math.incanter
  (:require [clojure.string :as str]
            [incanter.core :as ic]
            [incanter.stats :as stats]))

(comment

  ; Incanter, Clojure's primary statistical computing library used throughout the book. Similar to R.
  ; Incanter 2.0 lib for statistics

  (use '(incanter core stats charts io))

  (view (histogram (sample-normal 1000)))

  (def my-plot (function-plot sin -10 10))
  (view my-plot)

  (stats/correlation [1 2 3 3] [4 -5 -6 -7]))
