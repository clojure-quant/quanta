(ns dev.open-close-conversion
  (:require
    [quanta.calendar.core :refer [close->open-dt open->close-dt fixed-window fixed-window-open]]
    [dev.utils :refer [to-utc]]
    [tick.core :as t]))

;2024-02-08 => Thu
(close->open-dt [:us :m] (t/in (t/date-time "2024-02-09T12:34") "America/New_York"))
;=> #time/zoned-date-time"2024-02-09T12:33-05:00[America/New_York]"

(close->open-dt [:us :m] (t/in (t/date-time "2024-02-08T17:00") "America/New_York"))
;=> #time/zoned-date-time"2024-02-08T16:59-05:00[America/New_York]"

(close->open-dt [:us :d] (t/in (t/date-time "2024-02-08T17:00") "America/New_York"))
;=> #time/zoned-date-time"2024-02-08T09:00-05:00[America/New_York]"

(close->open-dt [:crypto :d] (t/in (t/date-time "2024-02-13T12:00:00") "UTC"))

; edge cases - day close crypto
(open->close-dt [:crypto :m] (to-utc "2024-02-08T23:58:00"))
;=> #time/zoned-date-time"2024-02-08T23:59Z[UTC]"

(open->close-dt [:crypto :m] (to-utc "2024-02-08T23:59:00"))
;=> #time/zoned-date-time"2024-02-09T00:00Z[UTC]"

(open->close-dt [:crypto :m] (to-utc "2024-02-09T00:00:00"))
;=> #time/zoned-date-time"2024-02-09T00:01Z[UTC]"

(close->open-dt [:crypto :m] (to-utc "2024-02-09T00:00:00"))
;=> #time/zoned-date-time"2024-02-08T23:59Z[UTC]"

(open->close-dt [:crypto :d] (to-utc "2024-02-08T12:00:00"))
;=> #time/zoned-date-time"2024-02-09T00:00Z[UTC]"

;;
;; 16:25 - 16:30
(def open-dt-window-crypto-m__16-25 (fixed-window [:crypto :m] {:start (to-utc "2024-02-09T16:25:00")
                                                                :end (to-utc "2024-02-09T16:29:00")}))
(vec open-dt-window-crypto-m__16-25)
;=>
;[#time/zoned-date-time"2024-02-09T16:29Z[UTC]"
; #time/zoned-date-time"2024-02-09T16:28Z[UTC]"
; #time/zoned-date-time"2024-02-09T16:27Z[UTC]"
; #time/zoned-date-time"2024-02-09T16:26Z[UTC]"
; #time/zoned-date-time"2024-02-09T16:25Z[UTC]"]

(map #(open->close-dt [:crypto :m] %) open-dt-window-crypto-m__16-25)
;=>
;(#time/zoned-date-time"2024-02-09T16:30Z[UTC]"
;  #time/zoned-date-time"2024-02-09T16:29Z[UTC]"
;  #time/zoned-date-time"2024-02-09T16:28Z[UTC]"
;  #time/zoned-date-time"2024-02-09T16:27Z[UTC]"
;  #time/zoned-date-time"2024-02-09T16:26Z[UTC]")


;;
;; 00:00 - 00:05
(def open-dt-window-crypto-m__00-00 (-> (fixed-window-open [:crypto :m] {:start (to-utc "2024-02-09T00:00:00")
                                                                    :end (to-utc "2024-02-09T00:04:00")})
                                        (concat [(to-utc "2024-02-09T00:00:00")])
                                        (vec)))
(vec (concat open-dt-window-crypto-m__00-00))
;=>
;[#time/zoned-date-time"2024-02-09T00:04Z[UTC]"
; #time/zoned-date-time"2024-02-09T00:03Z[UTC]"
; #time/zoned-date-time"2024-02-09T00:02Z[UTC]"
; #time/zoned-date-time"2024-02-09T00:01Z[UTC]"
; #time/zoned-date-time"2024-02-09T00:00Z[UTC]"]

(map #(open->close-dt [:crypto :m] %) open-dt-window-crypto-m__00-00)
;=>
;(#time/zoned-date-time"2024-02-09T00:05Z[UTC]"
;  #time/zoned-date-time"2024-02-09T00:04Z[UTC]"
;  #time/zoned-date-time"2024-02-09T00:03Z[UTC]"
;  #time/zoned-date-time"2024-02-09T00:02Z[UTC]"
;  #time/zoned-date-time"2024-02-09T00:01Z[UTC]")


;;
;; 23:55 - 00:05
(def open-dt-window-crypto-m__23-55-00-05 (fixed-window-open [:crypto :m] {:start (to-utc "2024-02-08T23:55:00")
                                                                           :end (to-utc "2024-02-09T00:04:00")}))
(vec open-dt-window-crypto-m__23-55-00-05)
;=>
;[#time/zoned-date-time"2024-02-09T00:04Z[UTC]"
; #time/zoned-date-time"2024-02-09T00:03Z[UTC]"
; #time/zoned-date-time"2024-02-09T00:02Z[UTC]"
; #time/zoned-date-time"2024-02-09T00:01Z[UTC]"
; #time/zoned-date-time"2024-02-09T00:00Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:59Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:58Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:57Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:56Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:55Z[UTC]"]

(map #(open->close-dt [:crypto :m] %) open-dt-window-crypto-m__23-55-00-05)
;=>
;(#time/zoned-date-time"2024-02-09T00:05Z[UTC]"
;  #time/zoned-date-time"2024-02-09T00:04Z[UTC]"
;  #time/zoned-date-time"2024-02-09T00:03Z[UTC]"
;  #time/zoned-date-time"2024-02-09T00:02Z[UTC]"
;  #time/zoned-date-time"2024-02-09T00:01Z[UTC]"
;  #time/zoned-date-time"2024-02-09T00:00Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:59Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:58Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:57Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:56Z[UTC]")

(def close-dt-window-crypto-m__23-55-00-05 (fixed-window [:crypto :m] {:start (to-utc "2024-02-08T23:55:00")
                                                                      :end (to-utc "2024-02-09T00:04:00")}))
(vec close-dt-window-crypto-m__23-55-00-05)
;=>
;[#time/zoned-date-time"2024-02-09T00:04Z[UTC]"
; #time/zoned-date-time"2024-02-09T00:03Z[UTC]"
; #time/zoned-date-time"2024-02-09T00:02Z[UTC]"
; #time/zoned-date-time"2024-02-09T00:01Z[UTC]"
; #time/zoned-date-time"2024-02-09T00:00Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:59Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:58Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:57Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:56Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:55Z[UTC]"]

(map #(close->open-dt [:crypto :m] %) close-dt-window-crypto-m__23-55-00-05)
;(#time/zoned-date-time"2024-02-09T00:03Z[UTC]"
;  #time/zoned-date-time"2024-02-09T00:02Z[UTC]"
;  #time/zoned-date-time"2024-02-09T00:01Z[UTC]"
;  #time/zoned-date-time"2024-02-09T00:00Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:59Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:58Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:57Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:56Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:55Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:54Z[UTC]")


(map #(open->close-dt [:crypto :m] %)
     (fixed-window [:crypto :m] {:start (t/in (t/date-time "2024-02-08T23:55:00") "UTC")
                                 :end (t/in (t/date-time "2024-02-08T23:59:00") "UTC")}))

(map #(open->close-dt [:crypto :m] %)
     (fixed-window [:crypto :m] {:start (t/in (t/date-time "2024-02-08T23:56:00") "UTC")
                                 :end (t/in (t/date-time "2024-02-09T00:05:00") "UTC")}))


(map #(open->close-dt [:crypto :m] %)
     (fixed-window-open [:crypto :m] {:start (t/in (t/date-time "2024-02-05T23:55:00") "UTC")
                                      :end (t/in (t/date-time "2024-02-05T23:59:00") "UTC")}))

