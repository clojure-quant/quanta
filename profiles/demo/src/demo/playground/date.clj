(ns demo.playground.date
  (:require
   [tick.alpha.api :as tick]
   [tech.v3.datatype.datetime :as datetime]
   [ta.dataset.date :refer [days-ago]]
   [java-time]))

(-> (tick/now) type)
; instant

(-> (days-ago 5) type)
; local-date

(->> (days-ago 5)
     (datetime/long-temporal-field :months))

(->> (tick/now)
     (tick/year)
     ;(.getLong 4)
       ;(datetime/long-temporal-field :hours)

       ;(datetime/long-temporal-field :months) ; this does not work.
     type)


