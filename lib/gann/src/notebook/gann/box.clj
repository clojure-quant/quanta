(ns notebook.gann.box
  (:require
   [tick.core :as tick]
   [ta.helper.date :refer [parse-date now-datetime]]
   [ta.gann.box :as box]
   [goldly.scratchpad :refer [show! show-as clear!]]))

;; box
;; a: left point of square
;; b: right point of square
;; bt > at
;; bp > ap

(def btc
  (box/convert-gann-dates
   {:symbol "BTCUSD"
    :ap 0.01
    :at "2010-07-18"
    :bp 11.0
    :bt "2014-04-13"}))

btc

(def root (box/make-root-box btc))
root

;; QUADRANT

(box/get-quadrant root 0 0)
(box/get-quadrant root 1 0)
(box/get-quadrant root 2 0)
(box/get-quadrant root 3 0)

(box/get-quadrant root 0 1)
(box/get-quadrant root 0 2)

(box/get-quadrant root 1 0)
(box/get-quadrant root 0 1)
(box/get-quadrant root 1000 1)

(box/move-right root)
(box/move-up root)
(box/move-left root)
(box/move-down root)

(-> root box/move-left box/move-left)
(-> root box/move-down box/move-down)

(->> 5
     (iterate inc)
     (take 4))

(->> root
     (iterate box/move-right)
     (take-while #(tick/< (:at %) (now-datetime)))
     (clojure.pprint/print-table))

(->> root
     (iterate box/zoom-in)
     (take 4)
     (clojure.pprint/print-table))

;; ZOOM 

(box/zoom-level root 1)
(box/zoom-level root 2)
(box/zoom-level root 3)

;; MOVE

(->> (iterate box/move-left root)
     (take 5)
     last)

(->> (iterate box/move-down root)
     (take 5)
     last)

(->> (iterate box/move-down root)
     (take-while #(> (:bp %) -50))
     last)

(box/left-window-box root (parse-date "2020-01-01")) ; nil -> no adjustment
(box/left-window-box root (parse-date "1980-01-01")) ; moved root box

(box/bottom-window-box root 50.0)
(box/bottom-window-box root -10)

(box/root-box-bottom-left root (parse-date "1980-01-01") -10)

(->> (box/move-right-in-window root (parse-date "2021-01-01") (parse-date "2021-12-31"))
      ;(clojure.pprint/print-table)
     (map :idx-t))

(-> (box/move-up-in-window root 2.197252998145341 2.2621424532947794)
      ;(box/move-up-in-window root (Math/log10 1000) (Math/log10 70000))
    (clojure.pprint/print-table))

(-> (box/get-boxes-in-window root (parse-date "2021-01-01") (parse-date "2021-12-31")
                             (Math/log10 1000) (Math/log10 70000))
    (clojure.pprint/print-table))

(-> (box/get-boxes-in-window root (parse-date "1990-01-01") (parse-date "2021-12-31")
                             (Math/log10 0.00001) (Math/log10 70000))
    (clojure.pprint/print-table))

;; FIND QUADRANT
root
(ldt/is-after (:bt root) (parse-date "2021-01-01"))

(box/find-quadrant root (parse-date "2021-01-01") 240)

(box/find-quadrant box 80 240)
(box/find-quadrant box 20 340)
(box/find-quadrant box 20 440)
(box/find-quadrant box 80 440)


