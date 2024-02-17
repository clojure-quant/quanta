(ns ta.import.helper
  (:require
   [taoensso.timbre :refer [debug]]))

(defn str->float [str]
  (if (nil? str)
    nil
    (Float/parseFloat str)))



(defn remove-last-bar-if-timestamp-equals
  "helper function. 
   when we request the next page, it might return the last bar of the last page 
   in the next page. If so, we do not want to have it included."
  [series last-dt]
  (let [date-first (-> series last :date)
        eq?    (and (not (nil? last-dt))
                    (= last-dt date-first))]
    (debug "first date: " date-first "last date:" last-dt " eq:" eq?)
    (if eq? (take (-> series count dec) series) series)))


(comment

 
  ; removes
 
  
   (remove-last-bar-if-timestamp-equals
   [{:date (t/instant "2000-12-31T00:00:00Z")}
    {:date (t/instant "1999-12-31T00:00:00Z")}
    ]
   (t/instant "1999-12-31T00:00:00Z"))

;  
  )

