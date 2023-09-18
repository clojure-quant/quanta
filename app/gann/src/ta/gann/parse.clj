(ns ta.gann.parse
  (:import
   [java.util Locale]  ; java.util.Locale
   [java.text NumberFormat ParsePosition] ; java.text.NumberFormat
   ))

#_(defn parse-double [number-string]
    (try
      (Double/parseDouble number-string)
      (catch Exception e
        (error "cannot parse: " number-string)
        nil)))

(defn parse-double [number-string]
  (let [locale-en-us (Locale. "en" "US")
        fmt (NumberFormat/getInstance locale-en-us) ;NumberFormat.getNumberInstance(Locale.getDefault());
        pp (ParsePosition. 0)]
    (.parse fmt number-string pp)))