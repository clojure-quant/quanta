(ns series.csvlib
  (:require [clojure.java.io :as io])
  (:import (com.csvreader CsvReader CsvWriter)
           java.nio.charset.Charset)
  (:use [clojure.set :only (subset?)]))

; Default delimiter
(def ^{:dynamic true} *delimiter* \,)
; Default charset
(def ^{:dynamic true} *charset* "UTF-8")
; Flush every record write?
(def ^{:dynamic true} *flush?* nil)

(defn- make-converter
  "Make a converter function from a conversion table."
  [converison-map]
  (let [convert (fn [key value] ((get converison-map key identity) value))]
    (fn [record]
      (zipmap (keys record) (map #(convert % (record %)) (keys record))))))

(defn- record-seq
  "Reutrn a lazy sequence of records from a CSV file"
  [stream-or-filename delimiter charset]
  (let [csv (CsvReader. stream-or-filename delimiter (Charset/forName charset))
        read-record (fn []
                      (when (.readRecord csv)
                        (into [] (.getValues csv))))]
    (take-while (complement nil?) (repeatedly read-record))))

(defn read-csv
  "Return a lazy sequence of records (maps) from CSV file or input stream.
  With headers? map will be header->value, otherwise it'll be position->value.
  Options keyword arguments:
    headers?        - Use first line as headers
    convert         - A conversion map (field -> conversion function)
    convert-headers - A function used to conver (map) the header values. (e.g. #'keyword)
    charset         - Charset to use (defaults to *charset*)
    delimiter       - Record delimiter (defaults to *delimiter*)"
  [stream-or-filename & {:keys [headers? convert charset delimiter convert-headers]
                         :or {charset *charset* delimiter *delimiter*
                              convert-headers identity}}]
  (let [records (record-seq stream-or-filename delimiter charset)
        convert (make-converter convert)
        headers (map convert-headers (if headers? (first records) (range (count (first records)))))]
    (map convert
         (map #(zipmap headers %) (if headers? (rest records) records)))))
