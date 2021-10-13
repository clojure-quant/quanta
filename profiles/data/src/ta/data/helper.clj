(ns ta.data.helper
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [clj-http.client :as http]
   [cheshire.core :as cheshire] ; JSON Encoding
   [cljc.java-time.instant :as ti]
   [tick.alpha.api :as t] ; tick uses cljc.java-time
   [ta.data.date :as d]))

(defn remove-first-bar-if-timestamp-equals
  "helper function. 
   when we request the next page, it might return the last bar of the last page 
   in the next page. If so, we do not want to have it included."
  [series last-dt]
  (let [date-first (-> series first :date)
        eq?    (and (not (nil? last-dt))
                    (ti/equals last-dt date-first))]
    (debug "first date: " date-first "last date:" last-dt " eq:" eq?)
    (if eq? (rest series) series)))