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

(defn load-csv
  "reads a resource file
   returns a sequence of vectors
   the first item in the sequence may be the header-row"
  [file]
  (csv/read-csv (io/reader (io/resource file))))

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
  (if (non-empty-string?- str) (Integer/parseInt str) nil))

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


(defn load-csv-bars
  ([file]
   (load-csv-bars default-options file))
  ([options file]
   (let [csv (load-csv file)
         column-format (extract-column-format (first csv))]
     (vec (for [row (rest csv)]
            (let [field (partial get-field column-format row)]
              (parse-row-data options
                              (field :date)
                              (field :time)
                              (field :open)
                              (field :high)
                              (field :low)
                              (field :close)
                              (field :volume))))))))


(defn load-csv-bars-trateg
  "this function is here mainly for unit tests and compatibility"
  [file]
  (let [options {:date-parser parse-zoned-date
                 :date (fmt/formatter "MM/dd/yyyy")}]
    (seq (load-csv-bars options file))))

; WRITE bar-series to csv


(defn write-csv-bars
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

  (defn header-csv [file]
    (->> (load-csv file)
         first
         extract-column-format))

  (header-csv "test/csv-test.csv")

  (load-csv-bars-trateg "ta/spx.csv")

  (:date fmt/formatters)
  (fmt/show-formatters)
  (:date-time fmt/formatters)

  ;(t/time "2017-01-01T00:00:00Z")
  ;(t/date-time "2017-01-01T00:00:00.000Z")


  ; comment end
  )





