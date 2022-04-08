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
   [ta.helper.returns :refer [log-return]]))


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
    (t/< start end ))

(defn select-aspects-until-now [aspects]
  (->> aspects 
       (filter p-select-has-window)
       (filter (p-before-now (now)))))     


(defn load-aspects-until-now []
  (-> (load-aspects) (select-aspects-until-now)))


;; interval return

(defn select-first-last [ds]
  (let [index-start 0
        index-end (tc/row-count ds)
        index-end (if (= 0 index-end) 0 (dec index-end))]
    ;(println "indice: " [index-start index-end])
    (tc/select-rows ds [index-start index-end])))


(defn interval-change [ds dt-start dt-end]
  (let [ds-interval (select-rows-interval ds dt-start dt-end)
        rc (tc/row-count ds-interval)]
    (if (> rc 0)
      (let [ds-first-last (select-first-last ds-interval)
            vec-first-last (:close ds-first-last)
            [first last] vec-first-last
            change (-> (/ last first) (- 1.0) (* 100.0))]
        change
        {:chg change :bars rc}
        )
      {:chg 0.0 :bars 0})))

;; aspect return

(defn assoc-aspect-return [ds {:keys [start end] :as aspect}]
  (merge aspect (interval-change ds start end)))

 (defn calc-aspect-return []
  (let [aspects (load-aspects-until-now)
        ds (load-symbol :crypto "15" "BTCUSD")
        ]
    (map (partial assoc-aspect-return ds) aspects)))

(defn aspect-mean [ds-aspect]
  (-> ds-aspect
      (tc/group-by [:type :a :b])
      (tc/aggregate 
        {:count (fn [ds]
                 (->> ds
                      :chg
                      count))
         :avg-bars (fn [ds]
                     (->> ds
                      :bars
                      fun/mean))
         :mean (fn [ds]
                 (->> ds
                 :chg
                 fun/mean))})))

(defn calc-aspect-stats []
  (-> (->> (calc-aspect-return)
           tc/dataset
           aspect-mean)
      (tc/order-by [:type :a :b])))

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



(defn demo []
  (let [ds (load-symbol :crypto "15" "BTCUSD")
        dt-start (days-ago 50)
        dt-end (days-ago 20)
        ]
    (count ds)
    (println ds)
    (println "dt-start " dt-start "dt-end" dt-end)
    (println (select-rows-interval ds dt-start dt-end))
    (println "interval change: " (interval-change ds dt-start dt-end))
    ))


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
  ; bars
  (let [ds (load-symbol :crypto "15" "BTCUSD")
        dt-start (days-ago 50)
        dt-end (days-ago 20)]
    (println ds)
    (println "interval " dt-start "-" dt-end " change: " (interval-change ds dt-start dt-end))
    (println (select-first-last ds)))
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

  (def ds-stats (calc-aspect-stats))

  (do
    (println "MOON ASPECT STATS:")
    (print-all (select-moon-aspects ds-stats)))
  
  (do
    (println "ASPECT STATS (MOON EXCLUDED):")
    (print-all (remove-moon-aspects ds-stats)))
  

  (demo)


;  
  )