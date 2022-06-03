(ns ta.backtest.trades
  (:require
   ;[clojure.set :refer [rename-keys]]
   ;[clojure.walk]
   [taoensso.timbre :refer [trace debug info warnf error]]
   ;[tick.core :as t]
   ;[cljc.java-time.instant :as ti]
   ;[cljc.java-time.local-date-time :as ldt]
   [tablecloth.api :as tc]
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

(defn select-trade-of [trade-type ds]
  (tc/select-rows
   ds
   (fn [{:keys [trade]}]
     (= trade-type trade))))

(defn shape-for-type [trade-type date]
  (let [epoch (datetime->epoch-second date)
        shape (case trade-type
                :buy "arrow_up"
                :sell "arrow_down"
                :flat "arrow_left")]
    {:points [{:time epoch}]
     :override {:text "🚀"
                :shape shape}}))

(defn trade-of-type [trade-type ds]
  (let [dates (-> (select-trade-of trade-type ds)
                  :date)]
    (map (partial shape-for-type trade-type) dates)))

(defn get-trades [ds]
  (when (has-col? ds :trade)
    (let [buy (trade-of-type :buy ds)
          sell (trade-of-type :sell ds)
          flat (trade-of-type :flat ds)]
      (into []
            (concat buy sell flat)))))

(comment
  (require '[ta.helper.date-ds  :refer [days-ago]])
  (-> {:date [(days-ago 1) (days-ago 2) (days-ago 3)
              (days-ago 5) (days-ago 6) (days-ago 6)]
       :trade [:buy :sell :flat nil nil nil]}
      tc/dataset
      get-trades
        ;(has-col? :iii)
      ))
