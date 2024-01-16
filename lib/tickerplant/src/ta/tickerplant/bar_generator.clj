(ns ta.tickerplant.bar-generator
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [clojure.pprint :refer [print-table]]
   [chime.core :as chime]
   [tick.core :as tick]
   [tablecloth.api :as tc]
   [ta.tickerplant.calendar :as c]))

(defn- create-bar! [db symbol]
  (let [bar {:symbol symbol :epoch 1}]
    (swap! db assoc symbol bar)
    bar))

(defn- empty-bar? [{:keys [open] :as bar}]
  (not open))

(defn- get-bar [db symbol]
  (get @db symbol))

(defn- update-bar [db {:keys [symbol] :as bar}]
  (swap! db assoc symbol bar))

(defn- empty-bar [db {:keys [symbol] :as bar}]
  (swap! db update-in [symbol] dissoc :open :high :low :close :volume :ticks)  
  (swap! db update-in [symbol :epoch] inc))

(defn- aggregate-tick [{:keys [open high low _close volume ticks epoch] :as bar} {:keys [price size]}]
  (merge bar
         (if (empty-bar? bar)
           {:open price
            :high price
            :low price
            :close price
            :volume size
            :ticks 1}
           {:open open
            :high (max high price)
            :low (min low price)
            :close price
            :volume (+ volume size)
            :ticks (inc ticks)})))


(defn process-tick [{:keys [db] :as state} {:keys [symbol] :as tick}]
  ;(info "process tick...")
  (let [bar (or (get-bar db symbol)
                (create-bar! db symbol))
        bar-new (aggregate-tick bar tick)]
    ;(info "bar: " bar)
    ;(info "bar-new: " bar-new)
    (update-bar db bar-new)
    bar-new))

(defn active-instruments [db]
  (keys @db))

(defn current-bars [db]
  (vals @db))

(defn- switch-bar [db symbol]
  (let [bar (get-bar db symbol)]
    (when-not (empty-bar? bar)
      (empty-bar db bar))
    bar))



(defn- make-on-bar-handler [db calendar on-bars-finished]
  (fn [time]
    (let [bars (current-bars db)
          bars-with-data (remove empty-bar? bars)
          symbols (map :symbol bars)
          ; | :symbol | :epoch |    :open |   :high |     :low |   :close | :volume | :ticks |
          time (tick/instant time)
          bar-seq (->> (current-bars db)
                       (map #(assoc % :date time)))
          bar-ds   (tc/dataset bar-seq)
         ]
    (info "bar-generator finish bar: " time "# instruments: " (count bars) "# bars: " (count bars-with-data))
    (on-bars-finished bar-ds)
    (doall (map #(switch-bar db %) symbols)))))

(defn- log-finished []
  (warn "bar-generator chime Schedule finished!"))

(defn- log-error [ex]
  (error "bar-generator chime exception: " ex)
  true)

(defn bargenerator-start [calendar on-bars-finished]
  (info "bargenerator-start calendar: " calendar)
  (let [date-seq (c/date-seq calendar)
        db (atom {})]
    {:db db
     :scheduler (chime/chime-at date-seq
                                (make-on-bar-handler db calendar on-bars-finished)
                                {:on-finished log-finished :error-handler log-error})
     }))

(defn- stop-chime [c]
  (try
    (.close c)
    (catch Exception ex
      (error "Exception in stopping chime-fn!" ex))))

(defn bargenerator-stop [{:keys [scheduler] :as state}]
  (info "bargenerator-stop! ")
  (stop-chime scheduler))



  
(defn print-finished-bars [ds-bars]
  (println "bars finished!")
  (let [bars (tc/rows ds-bars :as-maps)]
    (print-table bars)))

(comment 

  (def calendar {})
  
 (def state 
   (bargenerator-start calendar print-finished-bars))

  state

  (get-bar (:db state) "MSFT")
  (get-bar (:db state) "EURUSD")
  (get-bar (:db state) "IBM")
  (create-bar! (:db state) "QQQ")
  (create-bar! (:db state) "IBM")
  (current-bars (:db state))
  (print-finished-bars (current-bars (:db state)))

  (process-tick state {:symbol "MSFT" :price 98.20 :size 100})
  (process-tick state {:symbol "EURUSD" :price 1.0910 :size 100})
  (process-tick state {:symbol "EURUSD" :price 1.0920 :size 100})

  (bargenerator-stop state)
 ; 
  )



    
 
