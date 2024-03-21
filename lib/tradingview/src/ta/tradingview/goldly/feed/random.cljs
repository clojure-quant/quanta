(ns ta.tradingview.goldly.feed.random)

(defn days-ago [epoch days]
  (- epoch (* 24 60 60 1000 days)))

(defn bar [epoch last? days]
  {:time (days-ago epoch days)
   :open days
   :high days
   :low days
   :close days
   :volume days
   :isBarClosed true
   :isLastBar last?})

(def random-series
  (let [today-dt (js/Date.)
        ;today (.valueOf today-dt)
        today 1649030400000 ; april 4
        ;today 1649894400000 ; april 14.
        ;_ (println "TODAY: " today-dt "epoch: " today)
        bars (map #(bar today false %) (range 2000 1 -1))
        last-bar (bar today true 1)
        bars (concat bars [last-bar])
        bars (into [] bars)]
    bars))

;(println "BARS: " random-series)

(defn filter-random [from to]
  ;(println "filter-random " from to)
  (let [bars-filtered (->> (filter (fn [{:keys [time] :as _bar}]
                                     (and (> time from) (< time to))) random-series)
                           (into []))]
    ;(println "bars-filtered: " (count bars-filtered))
    bars-filtered))

(defn get-bars-random [symbolInfo _resolution period ok _err]
  (.log js/console (.keys js/Object period))
  (let [symbol (.-ticker symbolInfo)
        from (.-from period)
        to (.-to period)
        count-back (.-countBack period)
        first-request? (.-firstDataRequest period)
        from1000 (* 1000 from)
        to1000 (* 1000 to)
        bars (filter-random from1000 to1000)
        ;data {:bars bars
        ;      :meta {:noData false ; This flag should be set if there is no data in the requested period.
        ;            ;:nextTime nil
        ;             }}
        ;data-js (clj->js data)
        data-js (clj->js bars)]
    ;(println "GET-BARS-RANDOM" symbol from to count-back first-request?)
    ;(println "RANDOM-DATA: " data)
    (set! (.-tvd js/globalThis) data-js)
    (ok data-js)
    nil))

;(get-bars-random "SPY" "D" {:from 1000 :to 2000} (fn [data] (println "DEMO: " data)) nil false)
