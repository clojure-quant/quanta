(ns ta.algo.error-report
  (:require
   [clojure.string :as str]
   [clojure.stacktrace]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [tick.core :as t]))

(def location ".data/")

(def formatter (t/formatter "YYYY-MM-dd-HH-mm-ss"))

(defn sanitize-header [s]
   ; if header gets created from fully qualified symbols, then the path would contain a lot of /
   ; this is not good inside the filesystem.
  (str/replace s #"/" "_"))

(defn pr-ex [ex]
  (try
    (if ex
      (str
       "\r\n" "\r\n" "\r\n"
       "ex cause: " (ex-cause ex) "\r\n" "\r\n" "\r\n"
       "ex message: " (ex-message ex) "\r\n" "\r\n" "\r\n"
       "stacktrace: " (with-out-str (clojure.stacktrace/print-stack-trace ex)))
      "")
    (catch Exception ex
      "")))

(defn save-error-report [header data ex]
  (try
    (let [dt (-> (t/now)
                 (t/in "UTC"))
          sdt (t/format formatter  (t/zoned-date-time dt))
          header (sanitize-header header)
          filename (str location sdt "-" header ".txt")
          text (str "header: " header "\r\n"
                    "data: " (pr-str data)
                    (pr-ex ex))]
      (spit filename text)
      filename)
    (catch Exception ex
      ; error logging should be pain-free. 
      ; we dont want to cause more exceptions in the app if something goes wrong in saving
      ; the error report.
      (error "could not save error-report ex: " ex)
      "save-report-error")))
