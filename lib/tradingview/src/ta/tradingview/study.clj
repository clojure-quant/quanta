(ns ta.tradingview.study
  (:require
   [clojure.set :refer [rename-keys]]
   [clojure.walk]
   [taoensso.timbre :refer [trace debug info warnf error]]
   [tick.core :as t]
   [cljc.java-time.instant :as ti]
   [cljc.java-time.local-date-time :as ldt]
   [tablecloth.api :as tc]
   [modular.helper.id :refer [guuid-str]]
   [modular.config :refer [get-in-config]]
   [ta.helper.date :refer [now-datetime datetime->epoch-second epoch-second->datetime]]))

(defn has-col? [ds col-kw]
  (->> ds
       tc/columns
       (map meta)
       (filter #(= col-kw (:name %)))
       first))

#_{:volume 1.3607283E7,
   :signal :flat,
   :symbol "TLT",
   :trade-no 2,
   :date #time/date-time "2022-04-04T00:00",
   :index 4957,
   :trade :flat,
   :low 130.7100067138672,
   :open 131.92999267578125,
   :position :flat,
   :close 131.4600067138672,
   :high 131.97999572753906}

(defn get-trades [ds]
  (when (has-col? ds :trade)))