(ns astro.marks
  (:require
   [clojure.set :refer [rename-keys]]
   [clojure.edn :as edn]
   [clojure.walk]
   [taoensso.timbre :refer [trace debug info warnf error]]
   [cheshire.core :refer [parse-string generate-string]]
   [schema.core :as s]
   [tick.core :as t]
   [cljc.java-time.instant :as ti]
   [cljc.java-time.local-date-time :as ldt]
   [clojure.java.io :as io]
   [modular.persist.protocol :refer [save loadr]]
   [modular.helper.id :refer [guuid-str]]
   [modular.config :refer [get-in-config]]
   [ta.helper.date :refer [now-datetime datetime->epoch-second epoch-second->datetime]]))

;{:type :thirty, :a :Moon, :b :Venus, :start "2020-01-01T00:00:00Z", :end "2020-01-01T00:00:00Z"}

(defn filename-mark  []
  (str (get-in-config [:ta :tradingview :marks-path]) "aspects.edn"))

(defn parse-mark [{:keys [start end] :as mark}]
  (let [dstart (t/instant start) ;(t/instance start)
        dend (t/instant end) ;(t/instance end)
        estart (ti/get-epoch-second  dstart)
        eend (ti/get-epoch-second  dend)]
    (assoc mark :start dstart :dend end :estart estart :eend eend)))

(defn load-edn []
  (let [marks (-> (slurp (filename-mark)) edn/read-string)
        marks (map parse-mark marks)]
    marks))

(defn inside-epoch-range [from to]
  (fn [{:keys [estart eend]}]
    (and (>= estart from)
         (<= estart to))))

(defn find-aspect [{:keys [type a b] :as mark}]
  (and (= type :thirty)
       (or (= a :Moon) (= b :Moon))
       (or (= b :Uranus) (= b :Uranus))))

(defn load-aspects [symbol resolution options from to]
  (let [all (load-edn)
         ;from (epoch-second->datetime from)
         ;to (epoch-second->datetime to)
        window (filter (inside-epoch-range from to) all)
         ;window (filter find-aspect window)
        ]
    (info "aspect all:" (count all) " window: " (count window))
    window))

;{:type :thirty, :a :Moon, :b :Venus, :start "2020-01-01T00:00:00Z", :end "2020-01-01T00:00:00Z"}

(defn type->str [type]
  (case type
    :conjunction "0"
    :square "90"
    :opposition "180"

    :trine "2"
    :sextile "60"
    :sesquiquadrate "5"
    :thirty "30"

    :biquentile "B" ; bullish
    :quintile "Q" ; 72" quintile 

    :semi-square "ss" ; 45" semi-square very bearish
    :quincunx "qc"
    :septile "sl"
    :triseptile "ts"

    "?"))

(defn type->color [type]
  (case type
    :conjunction "black" ; 180" bullish/bearish/neutral black
    :square "black" ; 90" "bullish/bearish/neutral black
    :opposition "black" ; 180" green-black

    :trine "green" ; bullish
    :sextile "green" ; bullish
    :sesquiquadrate "green" ; bullish
    :thirty "green" ; semi-sextile bullish
    :biquentile "green" ; bullish
    :quintile "green" ; 72" quintile bullish
    :novile "green"
    :binovile "green"

    :semi-square "red" ; 45" semi-square very bearish
    :quincunx "red"
    :septile "red" ; 51.43" 
    :triseptile "red"

    "blue"))

(defn aspect->mark [{:keys [type a b start end estart eend]}]
  {:id (str a b start)
   :time estart
   :label (type->str type)
   :text (str type "\r\n " a " " b "\r\n " start)
   :color (type->color type)
   :labelFontColor "white"
   :minSize 28 ; 14
   })

(defn markcol [marks k]
  (into []
        (map k marks)))

(defn convert-marks [marks]
  ;(info "marks filtered: " (pr-str marks))
  (let [marks (map aspect->mark marks)]
    ;(info "marks converted: " (pr-str marks))
    {:id (markcol marks :id)
     :time (markcol marks :time)
     :label (markcol marks :label)
     :text (markcol marks :text)
     :color (markcol marks :color)
     :labelFontColor (markcol marks :labelFontColor)
     :minSize (markcol marks :minSize)}))

(defn moon-aspect? [{:keys [a b] :as aspect}]
  (or (= a :Moon) (= b :Moon)))

(defn astro-marks [options from to]
  (let [{:keys [symbol frequency]} options
        aspects (load-aspects symbol frequency options from to)
        aspects (if (:show-moon options)
                  aspects
                  (remove moon-aspect? aspects))]
    (->> aspects
         (map aspect->mark)
       ;(convert-marks)
         )))
