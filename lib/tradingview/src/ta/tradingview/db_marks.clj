(ns ta.tradingview.db-marks
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
   [ta.helper.date :refer [now-datetime datetime->epoch-second epoch-second->datetime]]
   ))


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


 (defn load-marks [symbol resolution from to]
   (let [all (load-edn)
         ;from (epoch-second->datetime from)
         ;to (epoch-second->datetime to)
         window (filter (inside-epoch-range from to) all)
         ]
     (info "aspect all:" (count all) " window: "(count window))
     window))

;{:type :thirty, :a :Moon, :b :Venus, :start "2020-01-01T00:00:00Z", :end "2020-01-01T00:00:00Z"}

(defn type->str [type]
  (case type
    :trine "T"
    :opposition "180"
    :conjunction "0"
    :square "90"
    :sextile "60"
    :thirty "30"
    "?"))


 (defn convert-mark [{:keys [type a b start end estart eend]}]
   {:id (str a b start)
    :time estart
    :label (type->str type)
    :text (str type "\r\n " a " " b "\r\n " start)
    :color "blue" 
    :labelFontColor "white" 
    :minSize 14
    })

(defn col [marks k]
  (into [] 
    (map k marks)))

(defn convert-marks [marks]
  ;(info "marks filtered: " (pr-str marks))
  (let [marks (map convert-mark marks)]
    ;(info "marks converted: " (pr-str marks))
    {:id (col marks :id)
     :time (col marks :time)
     :label (col marks :label)
     :text (col marks :text)
     :color (col marks :color)
     :labelFontColor (col marks :labelFontColor)
     :minSize (col marks :minSize)}))
