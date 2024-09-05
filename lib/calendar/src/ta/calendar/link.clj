(ns ta.calendar.link
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   ;[tech.v3.datatype-api :as dtype-api]
   ;[ta.data.import.sort :refer [is-ds-sorted?]]
   ))

(defn make-aligner [ds col v]
  (let [idx-max (dec (tc/row-count ds))
        idx (atom 0)
        move-next (fn [] (swap! idx inc))
        date-current (fn [] (-> ds :date (get (min idx-max @idx))))
        date-next    (fn [] (-> ds :date (get (min idx-max (inc @idx)))))
        get-val (fn [] (-> ds col (get (min idx-max @idx))))]
    (fn [date]
      (if (t/> (date-current) date)
        v
        (do
          (while (and (< @idx idx-max)
                      (t/<= (date-next)  date))
            (move-next))
          (get-val))))))

(defn col-type [ds col]
  (-> ds col meta :datatype))

#_(defn link-bars
    "returns timeseries values form remote-col, aligned to a size of bar-ds.
   both bar-ds and remote-ds need to be datasets with :date column.
   alignment rule is: last remote value is shown, except when remote value,
   does not have a value yet, in case nil-val is returned.
   Useful to link to remote time-series that are of lower frequency.
   TODO: MAKE IT WORK. FOR BIG DATASETS IT FAILS. EMAP IS LAZY??"
    [bar-ds remote-ds remote-col nil-val]
    (let [align (make-aligner remote-ds remote-col nil-val)
          t (col-type remote-ds remote-col)]
    ;(info "link-bars type: " t)
    ;(info "bar-ds sorted: " (is-ds-sorted? bar-ds))
    ;(info "remote-ds sorted: " (is-ds-sorted? remote-ds))
    ; dtype/clone is essential. otherwise on large datasets, the mapping will not
    ; be done in sequence, which means that the stateful mapping function will fail.
      (dtype/clone (dtype/emap align t (:date bar-ds)))))

(defn link-bars2
  "returns timeseries values form remote-col, aligned to a size of bar-ds.
   both bar-ds and remote-ds need to be datasets with :date column.
   alignment rule is: last remote value is shown, except when remote value,
   does not have a value yet, in case nil-val is returned.
   Useful to link to remote time-series that are of lower frequency.
   EQUAL TO link-bars, BUT THIS VERSION WORKS WITH BIG DATASETS."
  [bar-ds remote-ds remote-col nil-val]
  (let [align (make-aligner remote-ds remote-col nil-val)
        ;t (col-type remote-ds remote-col)
        ;local-date (:date bar-ds)
        ]
      ;(println "link-bars type: " t)
    ;(info "remote type: " t)
    ;(info "bar-ds sorted: " (is-ds-sorted? bar-ds))
    ;(info "remote-ds sorted: " (is-ds-sorted? remote-ds))
    (map align (:date bar-ds))))

#_(defn link-bars3
    "ALTERNATIVE IMPLEMETATION WITH LOOP.
   NOT YET READY.
   IS TECHNICALLY BETTER THAN LINK-BARS and LINK-BARS2"
    [local-ds remote-ds nil-val]
    (loop [local-idx 0
           remote-idx 0]
      (let [idx-max 0
            local-idx-max 0
            col-name nil
            col nil
            mget (fn [ds col idx] :r)
            mset! (fn [ds col v] :r)
            dt-local (mget local-ds :date local-idx)
            dt-remote (mget remote-ds :date remote-idx)
            dt-remote-next (mget remote-ds col-name (min idx-max  (inc remote-idx)))]
        (if (t/> dt-local dt-remote)
          (mset! col local-idx nil-val)
          (do (when (t/>= dt-remote-next dt-local)
              ; recur: inc remote-idx
                (recur local-idx (inc remote-idx)))
              (mset! col local-idx (mget remote-ds col-name remote-idx))))
      ;recur: inc local-idx
        (when (< local-idx local-idx-max)
          (recur (inc local-idx) remote-idx)))))

(comment
  (def daily-ds (tc/dataset [{:date (t/date-time "2024-01-01T17:00:00") :a 1}
                             {:date (t/date-time "2024-01-02T17:00:00") :a 2}
                             {:date (t/date-time "2024-01-03T17:00:00") :a 3}]))
  (col-type daily-ds :a)
  (-> daily-ds :a (get 2))
  (def hour-ds (tc/dataset [{:date (t/date-time "2024-01-01T15:00:00")} ; 0 = no daily
                            {:date (t/date-time "2024-01-01T16:00:00")} ; 0 = no daily
                            {:date (t/date-time "2024-01-01T17:00:00")} ; 1
                            {:date (t/date-time "2024-01-02T09:00:00")} ; 1
                            {:date (t/date-time "2024-01-02T16:00:00")} ; 1
                            {:date (t/date-time "2024-01-02T17:00:00")} ; 2
                            {:date (t/date-time "2024-01-02T18:00:00")} ; 2
                            {:date (t/date-time "2024-01-03T17:00:00")} ; 3
                            {:date (t/date-time "2024-01-03T18:00:00")} ; 3
                            ]))
  (:date daily-ds)

  (get (:date hour-ds) 0)

  (dtype/make-reader (col-type daily-ds :date)
                     (tc/row-count hour-ds)
                     (get (:date hour-ds) idx))

  (def align (make-aligner daily-ds :a 11))
  (dtype/make-reader (col-type daily-ds :a)
                     (tc/row-count hour-ds)
                     (align (get (:date hour-ds) idx)))

  (map align (:date hour-ds))

  (def t2 :local-date-time)
  (def t2 (col-type daily-ds :date))
  (dtype/make-reader t2 ; :local-date-time 
                     (tc/row-count daily-ds)
                     ((:date daily-ds) idx))

  (link-bars2 hour-ds daily-ds :a 0)
  ;;    (0 0 1 1 1 2)
  ;; => [0 0 1 1 1 2]

  (def early-daily-ds (tc/dataset [{:date (t/date-time "2021-01-01T17:00:00") :a 1}
                                   {:date (t/date-time "2022-01-01T17:00:00") :a 2}
                                   {:date (t/date-time "2023-01-01T17:00:00") :a 3}
                                   {:date (t/date-time "2024-01-01T17:00:00") :a 4}
                                   {:date (t/date-time "2024-01-02T17:00:00") :a 5}
                                   {:date (t/date-time "2024-01-03T17:00:00") :a 6}]))

  (link-bars2 hour-ds early-daily-ds :a 0)
    ;; => 2024-03-08T23:38:39.054Z nuc12 INFO [ta.calendar.link:29] - move next..
    ;;    2024-03-08T23:38:39.055Z nuc12 INFO [ta.calendar.link:29] - move next..
    ;;    2024-03-08T23:38:39.055Z nuc12 INFO [ta.calendar.link:29] - move next..
    ;;    2024-03-08T23:38:39.055Z nuc12 INFO [ta.calendar.link:29] - move next..
    ;;    (3 3 4 4 4 5)

  (link-bars2 hour-ds daily-ds :date (t/date-time "2000-01-01T15:00:00"))

  (-> daily-ds tc/info)

  (def hour2-ds (tc/dataset [{:date (t/date-time "2024-01-02T09:00:00")} ; 1
                             {:date (t/date-time "2024-01-02T16:00:00")} ; 1
                             {:date (t/date-time "2024-01-02T17:00:00")} ; 2
                             ]))

  (link-bars2 hour2-ds daily-ds :date (t/date-time "2000-01-01T15:00:00"))

  (def daily-inst-ds (tc/dataset [{:date (-> (t/date-time "2024-01-01T17:00:00") t/inst) :a 1}
                                  {:date (-> (t/date-time "2024-01-02T17:00:00") t/inst) :a 2}
                                  {:date (-> (t/date-time "2024-01-03T17:00:00") t/inst)  :a 3}]))

  (def hour-inst-ds (tc/dataset [{:date (-> (t/date-time "2024-01-01T15:00:00") t/inst)} ; 0
                                 {:date (-> (t/date-time "2024-01-01T16:00:00") t/inst)} ; 0
                                 {:date (-> (t/date-time "2024-01-01T17:00:00") t/inst)} ; 1
                                 {:date (-> (t/date-time "2024-01-02T09:00:00") t/inst)} ; 1
                                 {:date (-> (t/date-time "2024-01-02T16:00:00") t/inst)} ; 1
                                 {:date (-> (t/date-time "2024-01-02T17:00:00") t/inst)} ; 2
                                 ]))

  (link-bars2 hour-inst-ds daily-inst-ds :date (t/date-time "2000-01-01T15:00:00"))

  ; 
  )

