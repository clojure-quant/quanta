(ns ta.env.tools.multi-calendar-executor
  ""
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [manifold.stream :as s]
   [manifold.bus :as mbus]))


calendar-executor
Create (on-add on-remove) default nothing.
Add [state calendar fn]
Remove [state id]
Get-result-stream [state id]
Process-calendar-event [state {:keys [calendar time]}]
Get-calendars


(defn create-calendar-executor []
  (atom {}))

 (defn get-bar-categories [state]
   (keys @state))

(defn get-algos-for-category [state calendar]
  (get @state calendar))

(defn add-algo-to-bar-category [state bar-category algo]
  (swap! state update-in [bar-category] conj algo))



(defn category-algos [state bar-category]
  (get-in @(:bar-categories state) [bar-category :algos]))



(defn calc-algo [env {:keys [algo algo-opts]} {:keys [calendar time]}]
  (try
    (debug "calculating algo: " algo " time: " time)
    (let [result (algo env algo-opts time)]
      (debug "calculating algo: " algo " time: " time " result: " result)
      {:time time
       :category category
       :algo algo
       :algo-opts algo-opts
       :result result})
    (catch Exception ex
      (error "algo-calc exception " algo time ex)
      nil)))

(defn calculate-on-bar-close
  "input: time-msg 
   output: bar-done-msg
   side effect: calculates algos and puts results to result steam"
  [{:keys [env] :as state}
   bar-category
   {:keys [time category ds-bars] :as msg}]
  (info "calculate-on-bar-close " bar-category time)
  (try
    (let [result-stream (category-result-stream state bar-category)
          algos (category-algos state bar-category)]
      ; (debug "algos: " algos)
      (doall (map (fn [algo]
                    (let [result (calc-algo env algo time category)]
                      (when result
                        (debug "putting result to result stream: " result)
                        (s/put! result-stream result))))
                  algos)))
    (catch Exception ex
      (error "exception calculate-on-bar-close " bar-category)
      (error "ex: " ex)))
  msg)

(defn create-bar-category [{:keys [duckdb feed] :as state}
                           bar-category]
  (info "add new bar category: " bar-category)
  (let [bargen (bg/bargenerator-start bar-category)
        bar-close-stream (bg/bar-close-stream bargen)
        bars-finished-stream (s/map (save-finished-bars duckdb) bar-close-stream)
        ;results-stream (s/map (partial calculate-on-bar-close env bar-category)
        ;                      bars-finished-stream)
        results-stream (s/stream)
        data {:bargenerator bargen
              :calc-fns []
              :bar-category bar-category
              :bar-close-stream bar-close-stream
              :bars-finished-stream bars-finished-stream
              :results-stream results-stream}
        live-results-stream (get-result-stream state)]
    (swap! (:bar-categories state) assoc bar-category data)
    ;(info "connecting streams...")
    (s/consume (partial calculate-on-bar-close state bar-category) bars-finished-stream)
    ;(connect-feed-with-bargenerator bargen feed)
    (doall
     (map (partial connect-feed-with-bargenerator bargen) feed))
    (s/connect results-stream live-results-stream)
    ;(info "connecting streams... done!")
    data))

(defn get-bar-category [state bar-category]
  (or (get-existing-bar-category state bar-category)
      (create-bar-category state bar-category)))

(defn add-one [state algo-wrapped]
  (let [id (nano-id 6)
        {:keys [algo-opts _algo]} algo-wrapped
        {:keys [bar-category asset feed]} algo-opts
        algo-wrapped-with-id (assoc algo-wrapped :algo-opts
                                    (assoc algo-opts :id id))]
    (get-bar-category state bar-category)
    (add-algo-to-bar-category state bar-category algo-wrapped-with-id)
    (swap! (:algos state) assoc id algo-wrapped-with-id)
    (if (and asset feed)
      (let [f (get-feed state feed)]
        (info "added algo with asset [" asset "] .. subscribing with feed " feed " ..")
        (subscribe f asset))
      (warn "added algo without asset .. not subscribing!"))
    id))



(defn add
  "adds an algo to the live environment.
   if algo is a map, it will add one algo
   otherwise it will add multiple algos"
  [state algo-wrapped]
  (if (map? algo-wrapped)
    (add-one state algo-wrapped)
    (doall (map #(add-one state %) algo-wrapped))))