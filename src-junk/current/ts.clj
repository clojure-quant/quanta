(ns ta.series.ts
  (:require
   ;[clojure.string     :as str]
   [clojure.data.avl :as avl]
   [tick.core :as tick]
   ;[tick.interval :as ti]
   ))
;; the key is that these tick.interval functions are really powerful:
;; https://github.com/juxt/tick/blob/master/src/tick/interval.cljc#L439-L748

(def zdt tick/zoned-date-time)
(def dt tick/date-time)

(def beg tick/beginning)
(def end tick/end)
(defn ival [beg end] (tick/new-interval beg end))

(defn ordered-ivals [& ivals]
  (apply
   avl/sorted-set-by #(compare (beg %1) (beg %2))
   ivals))

(defn ->ordered-ivals [ivals]
  (into (ordered-ivals) ivals))

(defn needed [coverage-ivals ival-wanted]
  (not-empty (tick/difference [ival-wanted] coverage-ivals)))

;;ok an issue is that hte tick interval fns return a lazy seq when we want the
;;sorted set.

(defn conj-unite [acc x]
  (->ordered-ivals (tick/unite (conj acc x))))

(defn into-unite [to from]
  (->ordered-ivals (tick/unite (into to from))))

(defn new-db [] {})

(defn save [db series-id ivals coverage]
  (-> db
      (update-in [series-id :series]
                 (fnil into-unite (ordered-ivals))
                 ivals)
      (update-in [series-id :coverage] (fnil conj-unite (ordered-ivals)) coverage)))

(defn db-needed [db series-id ival-wanted]
  (-> db (get-in [series-id :coverage]) (needed ival-wanted)))

;;assumes none needed
(defn get-slice [db series-id ival-wanted]
  (->> (get-in db [series-id :series])
       (drop-while #(#{:precedes :meets} (tick/relation % ival-wanted)))
       (take-while #(#{:overlaps :starts :during :finishes :overlapped-by}
                     (tick/relation % ival-wanted)))
       ->ordered-ivals))
