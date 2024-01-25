(ns ta.tickerplant.bar-generator
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [clojure.pprint :refer [print-table]]
   [chime.core :as chime]
   [tick.core :as tick]
   [tablecloth.api :as tc]
   [ta.calendar.core :refer [calendar-seq-instant]]
   ))

(defn- create-bar! [db asset]
  (let [bar {:asset asset :epoch 1}]
    (swap! db assoc asset bar)
    bar))

(defn- empty-bar? [{:keys [open] :as bar}]
  (not open))

(defn- get-bar [db asset]
  (get @db asset))

(defn- update-bar [db {:keys [asset] :as bar}]
  (swap! db assoc asset bar))

(defn- empty-bar [db {:keys [asset] :as bar}]
  (swap! db update-in [asset] dissoc :open :high :low :close :volume :ticks)  
  (swap! db update-in [asset :epoch] inc))

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


(defn process-tick [{:keys [db] :as state} {:keys [asset] :as tick}]
  ;(info "process tick...")
  (let [bar (or (get-bar db asset)
                (create-bar! db asset))
        bar-new (aggregate-tick bar tick)]
    ;(info "bar: " bar)
    ;(info "bar-new: " bar-new)
    (update-bar db bar-new)
    bar-new))

(defn active-instruments [state]
  (keys @(:db state)))

(defn current-bars [state]
  (vals @(:db state)))

(defn- switch-bar [db asset]
  (let [bar (get-bar db asset)]
    (when-not (empty-bar? bar)
      (empty-bar db bar))
    bar))


(defn print-finished-bars [ds-bars]
  (let [bars (tc/rows ds-bars :as-maps)]
    (print-table bars)))


(defn- make-on-bar-handler [db calendar on-bars-finished]
  (fn [time]
    (let [state {:db db}
          bars (current-bars state)
          bars-with-data (remove empty-bar? bars)
          assets (map :asset bars)
          ; | :asset | :epoch |    :open |   :high |     :low |   :close | :volume | :ticks |
          time (tick/instant time)
          bar-seq (->> (current-bars state)
                       (map #(assoc % :date time)))
          bar-ds   (tc/dataset bar-seq)
         ]
    (info "bar-generator finish bar: " time "# instruments: " (count bars) "# bars: " (count bars-with-data))
    (try 
      (on-bars-finished {:time time :ds-bars bar-ds})  
      (catch Exception ex
         (error "Exception in saving new finished bars to duckdb!")
         (print-finished-bars bar-ds)))
    (doall (map #(switch-bar (:db state) %) assets)))))

(defn- log-finished []
  (warn "bar-generator chime Schedule finished!"))

(defn- log-error [ex]
  (error "bar-generator chime exception: " ex)
  true)

(defn bargenerator-start [[calendar-kw interval-kw] on-bars-finished]
  (info "bargenerator-start calendar: " calendar-kw "interval: " interval-kw)
  (let [date-seq (calendar-seq-instant calendar-kw interval-kw)
        db (atom {})]
    {:db db
     :scheduler (chime/chime-at date-seq
                                (make-on-bar-handler db calendar-kw on-bars-finished)
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


;; in demo see notebook.live.bar-generator

(comment 
  
  
   ;(get-bar (:db state) "MSFT")
  ;(get-bar (:db state) "EURUSD")
  ;(get-bar (:db state) "IBM")
  ;(create-bar! (:db state) "QQQ")
  ;(create-bar! (:db state) "IBM") 
  
;  
  )
    
 
