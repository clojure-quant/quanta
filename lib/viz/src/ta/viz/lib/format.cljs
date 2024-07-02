(ns ta.viz.lib.format
  (:require
   [tick.helper :refer [dt-format]]
   [pinkgorilla.goog.string :refer [format]]))

(defn fmt-nodigits [nr]
  (println "fmt-nodigits nr: " nr)
  ;;(js/isNaN nr) 
  ;(= nr ##NaN) 
  ;(to-fixed nr 1)
  (if (nil? nr)
    "-"
    (if (string? nr)
      nr
      (format "%.0f" nr))))

(defn dt-yyyymmdd [dt]
  ;(println "dt-yyyymmdd: " dt)
  (if (nil? dt)
    ""
    (if (string? dt)
      dt
      (dt-format "YYYY-MM-dd" dt)
      ;(str dt)
      )))

(defn dt-yyyymmdd-hhmm [dt]
  ;(println "dt-yyyymmdd: " dt)
  (if (nil? dt)
    ""
    (if (string? dt)
      dt
      (dt-format "YYYY-MM-dd HH:mm" dt)
      ;(str dt)
      )))

(defn align-right [_c]
  {:style {:float "right"}})