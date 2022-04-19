(ns notebook.studies.astro
  (:require
   [clojure.edn :as edn]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]
   [modular.config :refer [get-in-config]]
   [ta.helper.print :refer [print-all]]
   [ta.warehouse :refer [load-symbol]]
   [ta.helper.date-ds :refer [select-rows-interval days-ago days-ago-instant now]]
   [ta.helper.returns :refer [log-return]]
   [astro.windowstats :refer [window-stats]]))

;; load aspects

(defn filename-mark  []
  (str (get-in-config [:ta :tradingview :marks-path]) "aspects.edn"))

(defn parse-mark [{:keys [start end] :as mark}]
  (let [dstart (-> (t/instant start) (t/date-time)) ; (t/instance start) ; ;
        dend (-> (t/instant end) (t/date-time)) ; (t/instance end) ; (t/instant end) ;
        ]
    (assoc mark :start dstart :end dend)))

(defn load-aspects []
  (let [marks (-> (slurp (filename-mark)) edn/read-string)
        marks (map parse-mark marks)]
    marks))

(defn p-before-now [n]
  (fn [{:keys [end] :as a}]
    (t/<= end n)))

(defn p-select-has-window [{:keys [start end] :as a}]
  (t/< start end))

(defn select-aspects-until-now [aspects]
  (->> aspects
       (filter p-select-has-window)
       (filter (p-before-now (now)))))

(defn load-aspects-until-now []
  (-> (load-aspects) (select-aspects-until-now)))

;; aspect return

(defn assoc-aspect-return [ds {:keys [start end] :as aspect}]
  (merge aspect (window-stats ds start end)))

(defn calc-aspect-return []
  (let [aspects (load-aspects-until-now)
        ds (load-symbol :crypto "15" "BTCUSD")]
    (map (partial assoc-aspect-return ds) aspects)))

(defn aspect-group-stats [group-by ds-aspect]
  (-> ds-aspect
      (tc/group-by group-by)
      (tc/aggregate
       {:count (fn [ds]
                 (->> ds
                      :chg
                      count))
        :bars (fn [ds]
                (->> ds
                     :bars
                     fun/mean
                     int))
        :mean (fn [ds]
                (->> ds
                     :chg
                     fun/mean
                     int))
        :med (fn [ds]
               (->> ds
                    :chg
                    fun/median
                    int))
        :min (fn [ds]
               (-> (apply min (:chg ds)) int))
        :max (fn [ds]
               (-> (apply max (:chg ds)) int))
        :trend (fn [ds]
                 (->> ds
                      :trend
                      fun/mean
                      (* 100.0)
                      int))})))

(defn calc-aspect-stats [group-by]
  (let [ds-all (->> (calc-aspect-return)
                    tc/dataset)
        ds-groups (-> (->> ds-all
                           (aspect-group-stats group-by))
                      (tc/order-by [:type :a :b]))]
    {:groups ds-groups
     :all ds-all}))

(defn select-moon-aspects [ds]
  (tc/select-rows
   ds
   (fn [{:keys [a b]}]
     (or (= a :Moon) (= b :Moon)))))

(defn remove-moon-aspects [ds]
  (tc/select-rows
   ds
   (fn [{:keys [a b]}]
     (not (or (= a :Moon) (= b :Moon))))))

(defn select-aspect-type [ds T A B]
  (tc/select-rows
   ds
   (fn [{:keys [type a b]}]
     (and (= type T) (= a A) (= b B)))))

(defn print-stats [ds]
  (let [ds (tc/select-columns ds [:type :a :b :bars :count :trend :mean :med :min :max])]
    (print-all ds)))

(defn print-aspect-type [data T A B]
  (println "ASPECT LIST: "  T A B)
  (let [ds (select-aspect-type (:all data) T A B)
        ds (tc/select-columns ds [:start :chg-l :chg-r :chg :trend :bars]) ; :end
        ]
    (print-all ds)))

(comment
  ; date
  (now)
  (t/<= (now) (now))
  ; aspects
  (load-aspects)
  (load-aspects-until-now)
  (-> (load-aspects-until-now) first)
  (-> (load-aspects-until-now) last)

  (clojure.pprint/print-table (load-aspects-until-now))

  ; aspect return
  (let [ds (load-symbol :crypto "15" "BTCUSD")
        a (-> (load-aspects-until-now) first)]
    (println ds)
    (assoc-aspect-return ds a)
    ;a
    )
  (clojure.pprint/print-table (calc-aspect-return))
  ; tml ds
  (tc/dataset [{:a 1 :b 2 :c 3} {:a 4 :b 5 :c 6}])

  (->> (calc-aspect-return)
       tc/dataset
       aspect-mean)

  (def data (calc-aspect-stats [:type]))

  (def data (calc-aspect-stats [:type :a :b]))

  (do
    (println "MOON ASPECT STATS:")
    (print-stats (select-moon-aspects (:groups data))))

  (do
    (println "ASPECT STATS (MOON EXCLUDED):")
    (print-stats (remove-moon-aspects (:groups data))))

  (let [type :trine
        a  :Moon
        b :Jupiter]
    (print-aspect-type data type a b))

  (demo)

;  
  )