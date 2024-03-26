(ns ta.interact.debug)

(defn dump-dataset [filename ds]
  (let [content (with-out-str
                  (println ds))]
    (spit filename content)))

; TODO: how to trigger this in a bood way?
; it needs to be added just ad-hoc.
; and it needs to dump only tml-datasets (which can be checked)

;(defonce dump-path (atom nil))

(comment
  (require '[tablecloth.api :as tc])
  (def ds (tc/dataset {:a [1 2 3]}))

  (dump-dataset "/tmp/a.txt" ds)

; 
  )