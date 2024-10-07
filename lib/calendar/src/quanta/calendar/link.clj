(ns quanta.calendar.link)

; the current link-fn returns COLUMN-VALUES.

; we typically want to link multiple columns in an algo.
; therefore it is more efficient to just return INDEX-VALUES.

; then we can have a second function to copy values

(defn create-row-links
  "returns a column of size (ds) 
   containing row-indices of ds-faster
   or nil. it is to be used in conjunction
   with copy-add-column"
  [ds-faster ds])

(defn copy-add-column
  "adds column col to ds.
   col is copied from col-faster using faster-indices"

  [ds ds-faster faster-indices col-faster col])