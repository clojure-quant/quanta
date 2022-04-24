(ns notebook.gann.svg
  (:require
   [ta.helper.date :refer [parse-date now-datetime]]
   [ta.gann.box :as box]
   [ta.gann.svg-plot :refer [gann-svg]]
   [goldly.scratchpad :refer [show! show-as clear!]]))

(show!
 (gann-svg
  {:s "GLD"
   ;:dt-start "2021-01-01"
   ;:dt-end "2021-12-31"
   :height 500
   :width 500}))

(show!
 (gann-svg
  {:s "BTCUSD"
   :dt-start "2018-01-01"
   :dt-end "2021-12-31"}))

(def btc-box
  (box/make-root-box
   (box/convert-gann-dates
    {:symbol "BTCUSD"
     :ap 0.01
     :at "2010-07-18"
     :bp 11.0
     :bt "2014-04-13"})))

btc-box

(show!
 (gann-svg
  {:s "BTCUSD"
   :dt-start (parse-date "2021-01-01")
   :dt-end (parse-date "2021-12-31")
   :root-box (box/zoom-out btc-box)}))

(show!
 (gann-svg
  {:wh :crypto
   :s "BTCUSD"
   :dt-start (parse-date "2021-01-01")
   :dt-end (parse-date "2021-12-31")
   :root-box (box/zoom-in btc-box)}))

(show!
 (gann-svg
  {:wh :crypto
   :s "BTCUSD"
   :dt-start (parse-date "2021-01-01")
   :dt-end (parse-date "2021-12-31")
   :root-box (box/zoom-in (box/zoom-in (box/zoom-in (box/zoom-in btc-box))))}))

(show!
 (get-gann-spec
  {:wh :stocks
   :s  "SPY"
   :dt-start (parse-date "2021-01-01")
   :dt-end (parse-date "2021-12-31")}))

(def spy-box
  (box/make-root-box
   (box/convert-gann-dates
    {:symbol "SPY"
     :ap 0.01
     :at "2010-07-18"
     :bp 11.0
     :bt "2014-04-13"})))

(show!
 (gann-svg
  {:wh :stocks
   :symbol  "SPY"
   :dt-start (parse-date "2020-01-01")
   :dt-end (parse-date "2021-12-31")
   :root-box (box/zoom-in spy-box)}))

(show!
 (gann-svg
  {:wh :stocks
   :symbol  "GLD"
   :dt-start (parse-date "2005-01-01")
   :dt-end (parse-date "2021-12-31")
     ;:root-box (zoom-in (zoom-in boxes/gld-box))
   }))
