(ns dev.utils
  (:require [tick.core :as t]))

(defn to-utc [dt-str]
  (t/in (t/date-time dt-str) "UTC"))
