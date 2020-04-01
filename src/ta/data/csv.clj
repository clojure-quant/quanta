(ns ta.data.csv
  "reading and writing of CSV files that contain bar-series"
  (:require
   [clojure.data.csv :as csv]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clj-time.format :as fmt] ;wrapper to joda-date-time
   ;[tick.alpha.api :as t]
   ;[tick.timezone]
   ;[tick.locale-en-us]
   )
  (:import
   [java.time LocalDate LocalTime ZonedDateTime ZoneId]
   java.time.format.DateTimeFormatter))

;; csv format for bar-series comes in various flavors
;; - no header / with header
;; - separator , or ;
;; - decimal point . (english) or , (german)
;; - date format (dd/tt/yyy iso, ...) with and without time / time-zone

;; TODO: make this read first a resource, and if none exist, then
;; read the file

(defn resource->csv
  "reads csv from a resource 
   returns:
     - a sequence of vectors
     - (first) may be the header-row
     - nil if resource not available"
  [resource-name]
  (let [f (io/resource resource-name)]
    (if (nil? f)
      nil
      (csv/read-csv (io/reader f)))))

(defn file->csv
  "reads a csv from a  file
   returns:
     - a sequence of vectors
     - (first) may be the header-row
     - nil if file does not exist"
  [file-name]
  (let [f (io/file file-name)]
    (if (.exists f)
      (csv/read-csv (io/reader f))
      nil)))

; header

(defn extract-column-format
  "processes a csv header-row
   returns a map of keywords matching the column headers
   the values are indices of the position in the csv file

   the advantage of of this column-format definition is that we can 
   also process csv files that do not contain a header row

   example:
      input:
      date,PX_OPEN,PX_HIGH,PX_LOW,PX_LAST,PX_VOLUME

      output:
      {:date 0, :px_open 1, :px_high 2, :px_low 3, :px_last 4, :px_volume 5} "
  [header-row]
  (let [str->keyword (comp keyword str/lower-case)]
    (zipmap
     (map str->keyword header-row)
     (range (count header-row)))))

(defn- get-field
  "returns the value for the specified field in the given row,
   utilizing the csv column-format to determine the location"
  [column-format row field]
  (let [index (field column-format)]
    (if (nil? index)
      nil
      (nth row index))))

; parse doubles

(defn- non-empty-string?- [v]
  (if (instance? String v) (not (str/blank? v)) false))

(defn- double-or-nil [str]
  (if (non-empty-string?- str) (Double/parseDouble str) nil))

(defn- int-or-nil [str]
  (if (non-empty-string?- str)
    (try
      (Integer/parseInt str)
      (catch Exception _
        (int (double-or-nil str))  ; be easy about volume being a double instead of an int.
        ))
    nil))

; parse date/time

(defn- parse-date [options date time]
  (try
    ;(t/time date)
    (fmt/parse (:date options) date)
    (catch Exception _
      nil)))


(def date-fmt (DateTimeFormatter/ofPattern "MM/dd/yyyy"))
(def EST (ZoneId/of "America/New_York"))

(defn parse-zoned-date [options date time]
  (try
    (ZonedDateTime/of (LocalDate/parse date date-fmt)
                      (LocalTime/parse time)
                      EST)
    (catch Exception _
      nil)))

(defn- parse-row-data [options date time open high low close volume]
  {:date   ((:date-parser options) options date time) ; parse-date
   :open   (double-or-nil open)
   :high   (double-or-nil high)
   :low    (double-or-nil low)
   :close  (double-or-nil close)
   :volume (int-or-nil volume)})

; READ bar-series from csv

(def default-options
  {:date-parser parse-date
   :date (:date-time fmt/formatters) ; 2020-04-01T00:36:18.206Z
   ;:date (fmt/formatter "M/d/yyyy H:m:s a") ; 1/31/1990 12:00:00 AM
   })


(defn csv->bars
  "parses csv-data (a sequence of vectors)
   the first row (may) contain the column headers
   returns a bar-series"
  [options csv]
  (let [column-format (extract-column-format (first csv))]
    (vec (for [row (rest csv)]
           (let [field (partial get-field column-format row)]
             (parse-row-data options
                             (field :date)
                             (field :time)
                             (field :open)
                             (field :high)
                             (field :low)
                             (field :close)
                             (field :volume)))))))

(defn- load-bars
  "helper function
   reads csv content via load-csv function
   parses non-nil content"
  [load-csv options file]
  (let [csv (load-csv file)]
    (if (nil? csv)
      nil
      (csv->bars options csv))))


(defn load-bars-file
  "loads a bar-series from a file
   returns nil if file not existing"
  ([file]
   (load-bars file->csv default-options file))
  ([options file]
   (load-bars file->csv options file)))

(defn load-bars-resource
  "loads a bar-series from a resource
   returns nil if resource not available"
  ([file]
   (load-bars resource->csv default-options file))
  ([options file]
   (load-bars resource->csv options file)))

(def trateg-options
  {:date-parser parse-zoned-date
   :date (fmt/formatter "MM/dd/yyyy")})

(defn load-csv-bars-trateg
  "this function is here mainly for unit tests and compatibility"
  [file]
  (seq (load-bars-resource trateg-options file)))

; WRITE bar-series to csv

(defn save-bars-file
  "writes a bar-series to a csv file.
   the format is fixed, because we want to have our output standardized
   date format is iso-date with time and milliseconds."
  [file bar-series]
  (let [columns [:date :open :high :low :close :volume]
        headers (map name columns)
        rows (mapv #(mapv % columns) bar-series)]
    (with-open [writer (io/writer (io/file file))]
      (csv/write-csv writer (cons headers rows)))))


(comment

  (io/resource "ta/spx.cs") ; nil if not available
  (io/file "resources/test/csv-test.csv") ; always not nil

  (defn header-csv [file]
    (->> (file->csv file)
         first
         extract-column-format))
  (header-csv "resources/test/csv-test.csv")


  (:date fmt/formatters)
  (fmt/show-formatters)
  (:date-time fmt/formatters)

  ;(t/time "2017-01-01T00:00:00Z")
  ;(t/date-time "2017-01-01T00:00:00.000Z")

  (load-csv-bars-trateg "ta/spx.csv")
  (load-bars-file "resources/sector/FMCAX.csv")
  (load-bars-file "resources/sector/FIDSX.csv")






  ; comment end
  )





