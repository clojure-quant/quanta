(ns demo.lib.gann-data
  (:require
   [ta.data.date :refer [parse-date now-datetime]]
   [demo.lib.gann :refer [make-root-box zoom-out zoom-in move-down]]))

(def btc
  {:ap 0.01
   :at (parse-date "2010-07-18")
   :bp 11.0
   :bt (parse-date "2014-04-13")})

(def btc-option2
  {:ap 0.01
   :at (parse-date "2010-07-18")
   :bp 0.04
   :bt (parse-date "2011-04-17")})

(def sup
  {:ap 77.24 ; a and b price are actually changed. 
   :at (parse-date "2000-08-18")
   :bp  153.48
   :bt (parse-date "2002-10-10")})

(def gld
  {:ap 254.0
   :at (parse-date "2001-02-27")
   :bp  329.0
   :bt (parse-date "2003-02-12")})

(def slv
  {:ap 1.281
   :at (parse-date "1971-12-01")
   :bp  28.945
   :bt (parse-date "1986-12-01")})

(def qqq
  {:ap 21.80
   :at (parse-date "2000-02-01")
   :bp  110.92
   :bt (parse-date "2013-11-01")})

(def btc-box (make-root-box btc))
(def sup-box (make-root-box sup))
(def gld-box (move-down (move-down (make-root-box gld))))

(def root-dict {"BTCUSD" (make-root-box btc)
                "SPY" (make-root-box sup)
                "QQQ" (zoom-in  (make-root-box qqq))
                "GLD" (move-down (move-down (make-root-box gld)))
                "SLV" (zoom-in (zoom-in (make-root-box slv)))})

(defn load-root-box [symbol]
  (get root-dict symbol))

(comment
  (load-root-box "BTCUSD")
  (load-root-box "GLD")
  (load-root-box "BAD")

  ;
  )



