(ns ta.viz.lib.format-number
  (:require
   [pinkgorilla.goog.string :refer [format]]))

(defn format-number [sformat nr]
  (println "fmt-nodigits nr: " nr)
  ;;(js/isNaN nr) 
  ;(= nr ##NaN) 
  ;(to-fixed nr 1)
  (if (nil? nr)
    "-"
    (if (string? nr)
      nr
      (format "%.0f" nr))))

(defn fmt-nodigits [nr]
  (format-number "%.0f" nr))

