(ns ta.viz.lib.format-number
  (:require
   [goog.string]))

(defn format-number [sformat nr]
  (println "fmt-nodigits nr: " nr)
  ;;(js/isNaN nr) 
  ;(= nr ##NaN) 
  ;(to-fixed nr 1)
  (if (nil? nr)
    "-"
    (if (string? nr)
      nr
      (goog.string/format "%.0f" nr))))

(defn fmt-nodigits [nr]
  (format-number "%.0f" nr))

