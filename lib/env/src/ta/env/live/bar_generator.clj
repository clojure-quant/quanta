(ns ta.env.live.bar-generator
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [clojure.pprint :refer [print-table]]
   [chime.core :as chime]
   [tick.core :as tick]
   [tablecloth.api :as tc]
   [ta.calendar.core :refer [calendar-seq-instant]]
   [manifold.stream :as s]))

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


(defn print-finished-bars [{:keys [time category ds-bars]}]
  (let [bars (tc/rows ds-bars :as-maps)]
    (println "bars finished category: " category "time: " time)
    (print-table bars)))


(defn- make-on-bar-handler [db bar-category bar-close-stream]
  (fn [time]
    (try
      (let [state {:db db}
            bars (current-bars state)
            bars-with-data (remove empty-bar? bars)
            assets (map :asset bars)
          ; | :asset | :epoch |    :open |   :high |     :low |   :close | :volume | :ticks |
            time (tick/instant time)
            bar-seq (->> (current-bars state)
                         (map #(assoc % :date time)))
            bar-ds   (tc/dataset bar-seq)]
        (info "bar-generator finish: " bar-category " @ " time "# instruments: " (count bars) "# bars: " (count bars-with-data))
        (doall (map #(switch-bar (:db state) %) assets))
        (try
          (s/put! bar-close-stream  {:time time
                                     :category bar-category
                                     :ds-bars bar-ds})
          (catch Exception ex
            (error "Exception in putting data to on-bars stream!")
            (print-finished-bars bar-ds))))
      (catch Exception ex
        (error "Exception in on-bar-handler")
        (error "exception: " ex)))))

(defn- log-finished []
  (warn "bar-generator chime Schedule finished!"))

(defn- log-error [ex]
  (error "bar-generator chime exception: " ex)
  true)

(defn bargenerator-start [bar-category]
  (info "bargenerator-start : " bar-category)
  (let [[calendar-kw interval-kw] bar-category
        date-seq (calendar-seq-instant calendar-kw interval-kw)
        db (atom {})
        bar-close-stream (s/stream)]
    {:db db
     :bar-close-stream bar-close-stream
     :scheduler (chime/chime-at date-seq
                                (make-on-bar-handler db bar-category bar-close-stream)
                                {:on-finished log-finished :error-handler log-error})}))

(defn bar-close-stream [state]
  (:bar-close-stream state))

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
    
 
