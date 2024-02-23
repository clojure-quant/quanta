(ns ta.env.algo.core
  (:require 
   [ta.calendar.core :refer [trailing-window get-bar-window]]
   [ta.env.core :as e]
   [ta.algo.spec :as s]))

(defn get-trailing-bars [env spec bar-close-date]
  (let [calendar (s/get-calendar spec)
        asset (s/get-asset spec)
        n (s/get-trailing-n spec)
        window (trailing-window calendar n bar-close-date)]
     (e/get-bars env {:asset asset 
                      :calendar calendar} window)))

(defn get-bars-lower-timeframe [env spec lower-timeframe]
  (let [calendar (s/get-calendar spec)
        market (first calendar)
        calendar-lower [market lower-timeframe]
        asset (s/get-asset spec)
        time (e/get-calendar-time env calendar)
        window (get-bar-window calendar time)]
    (e/get-bars env {:asset asset
                     :calendar calendar-lower} window)))


