(ns ta.csv-test
  (:require
   [clojure.test :refer :all]
   [ta.data.csv :refer [load-csv-bars write-csv-bars load-csv-bars-trateg]]
   [clj-time.core :as t]))

(def csv-test-filename "test/csv-test.csv")

(def test-series
  [#_{:date (t/date-time 2011 04 14) 
    :open 19 :high 19 :low 19 :close 19 :volume nil}
   #_{:date (t/date-time 2011 04 15) 
    :open 19.1 :high 21.7 :low 19.1 :close 21.7 :volume 4124893}
   {:date (t/date-time 2011 04 18) 
    :open 21.28 :high 21.9 :low 20.755 :close 21.0 :volume 469876}])

(defn- write-read-back [series]
   (write-csv-bars (str "resources/" csv-test-filename) series)
   (load-csv-bars csv-test-filename))

(deftest csv-read-write-equal
  (is (= test-series (write-read-back test-series))))



(comment
  
  ; date,PX_OPEN,PX_HIGH,PX_LOW,PX_LAST,PX_VOLUME
  ; 4/14/2011 12:00:00 AM,19,19,19,19,
  ; 4/15/2011 12:00:00 AM,19.1,21.7,19.1,21.7,4124893
  ; 4/18/2011 12:00:00 AM,21.28,21.9,20.755,21,469876
  
  (write-csv-bars csv-test-filename test-series)
  (load-csv-bars csv-test-filename)
  (write-read-back test-series)
  
;comment
  )
