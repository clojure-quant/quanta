(ns ta.persist.text
  (:require
   [taoensso.timbre :refer [info]]))

(defn save [file-name t]
  (info "saving text file: " file-name)
  (spit file-name (str t))
  t  ; important to be here, as save-study is used often in a threading macro
  )
(defn loadr [file-name]
  (info "loading text file: " file-name)
  (slurp file-name))

(comment
  (save "/tmp/test.edn" {:a 1 :b [1 3 4]})
  (loadr "/tmp/test.edn")

;  
  )