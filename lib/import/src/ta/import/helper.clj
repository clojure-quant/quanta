(ns ta.import.helper
  (:require
   [taoensso.timbre :refer [debug]]
   [tick.core :as t]
   [tech.v3.dataset :as tds]
   [clj-http.client :as http]
   [de.otto.nom.core :as nom]))

(defn http-get 
  "same as clj-http/get, but:
   - json only
   - socket timeout (we dont want to wait forever!)
   - no exceptions, but nom/fail."
  [url query-params]
  (try
    (http/get url
              {:accept :json
               :socket-timeout 5000
               :connection-timeout 5000
               :query-params query-params})
    (catch Exception ex
      (nom/fail ::http-get {:ex (ex-data ex)
                            :url url
                            :query-params query-params}))))

(defn p-or-fail [p opts range err-message]
  (if p
   p
    (nom/fail ::get-bars-req {:message err-message
                              :opts opts
                              :range range})))

(defn str->float [str]
  (if (nil? str)
    nil
    (Float/parseFloat str)))


(defn str->double [str]
  (if (nil? str)
    nil
    (Double/parseDouble str)))

(defn ensure-date-instant [bar-ds]
  (tds/column-map bar-ds :date #(t/instant %) [:date]))

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
  (-> "15.123" str->float type)
  (-> "15.123" str->double type)


  (remove-last-bar-if-timestamp-equals
   [{:date (t/instant "2000-12-31T00:00:00Z")}
    {:date (t/instant "1999-12-31T00:00:00Z")}]
   (t/instant "1999-12-31T00:00:00Z"))

;  
  )

