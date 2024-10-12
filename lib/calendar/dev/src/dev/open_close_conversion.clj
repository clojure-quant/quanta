(ns dev.open-close-conversion
  (:require
    [ta.calendar.core :refer [close->open-dt open->close-dt]]
    [dev.utils :refer [to-utc]]
    [tick.core :as t]
    [ta.calendar.core :refer [fixed-window]]))

;2024-02-08 => Thu
(close->open-dt [:us :m] (t/in (t/date-time "2024-02-09T12:34") "America/New_York"))
;=> #time/zoned-date-time"2024-02-09T12:33-05:00[America/New_York]"

(close->open-dt [:us :m] (t/in (t/date-time "2024-02-08T17:00") "America/New_York"))
;=> #time/zoned-date-time"2024-02-08T16:59-05:00[America/New_York]"

(close->open-dt [:us :d] (t/in (t/date-time "2024-02-08T17:00") "America/New_York"))
;=> #time/zoned-date-time"2024-02-08T09:00-05:00[America/New_York]"

; edge cases - day close crypto
(open->close-dt [:crypto :m] (to-utc "2024-02-08T23:58:00"))
;=> #time/zoned-date-time"2024-02-08T23:59Z[UTC]"

(open->close-dt [:crypto :m] (to-utc "2024-02-08T23:59:00"))
;=> #time/zoned-date-time"2024-02-08T23:59:59.999999999Z[UTC]"

(open->close-dt [:crypto :m] (to-utc "2024-02-09T00:00:00"))
;=> #time/zoned-date-time"2024-02-09T00:01Z[UTC]"

(close->open-dt [:crypto :m] (to-utc "2024-02-08T23:59:59.999999999"))
;=> #time/zoned-date-time"2024-02-08T23:59Z[UTC]"

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
(def open-dt-window-crypto-m__00-00 (-> (fixed-window [:crypto :m] {:start (to-utc "2024-02-09T00:00:00")
                                                                    :end (to-utc "2024-02-09T00:04:00")})
                                        (concat [(to-utc "2024-02-09T00:00:00")])
                                        (vec)))
; NOTE: we are generating the date-times with close iterator functions. 00:00 is no close time, this is why its missing and we have to add it explicit
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
;; 23:55 - 23:59:59.999
(def open-dt-window-crypto-m__23-55 (fixed-window [:crypto :m] {:start (to-utc "2024-02-08T23:55:00")
                                                                :end (to-utc "2024-02-08T23:59:00")}))
(vec open-dt-window-crypto-m__23-55)
;=>
;[#time/zoned-date-time"2024-02-08T23:59Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:58Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:57Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:56Z[UTC]"
; #time/zoned-date-time"2024-02-08T23:55Z[UTC]"]

(map #(open->close-dt [:crypto :m] %) open-dt-window-crypto-m__23-55)
;=>
;(#time/zoned-date-time"2024-02-08T23:59:59.999999999Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:59Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:58Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:57Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:56Z[UTC]")


;;
;; 23:55 - 00:05
; NOTE: we are generating the date-times with close iterator functions. 00:00 is no close time, this is why its missing and we have to add it explicit
(def open-dt-window-crypto-m__23-55-00-05 (concat (fixed-window [:crypto :m] {:start (to-utc "2024-02-09T00:00:00")
                                                                              :end (to-utc "2024-02-09T00:04:00")})
                                                  [(to-utc "2024-02-09T00:00:00")]
                                                  (fixed-window [:crypto :m] {:start (to-utc "2024-02-08T23:55:00")
                                                                              :end (to-utc "2024-02-08T23:59:00")})))
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
;  #time/zoned-date-time"2024-02-08T23:59:59.999999999Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:59Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:58Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:57Z[UTC]"
;  #time/zoned-date-time"2024-02-08T23:56Z[UTC]")