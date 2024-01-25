(ns ta.env.live-bargenerator
  "the live-bargenerate is an environment in which algos can be run.
   An environment currently only provides get-series fn., but more
   can be added later.
   We can have multiple live environments, and multiple backtest environments.
   You can schedule fns that are run at the close of a bar-category.
   Outputs a stream of results-fn
   
   Manages subscriptiosn to a quote feed. 

   Inspection:
   (quote-snapshot state) - gets last quote of all currently subscribed quotes
   (unfinished-bar-snapshot state [:us :m]) - gets unfinished bars snapshot.
   Todo:
   - unsubscribe-quote
   - remove algo
   - stop live environment
   - 

   "
  (:require 
   [taoensso.timbre :refer [trace debug info warn error]]
   [manifold.stream :as s]
   [tablecloth.api :as tc]
   [ta.warehouse.duckdb :as duck]
   [ta.tickerplant.bar-generator :as bg]
   [ta.quote.core :refer [subscribe quote-stream]]
   [ta.env.last-msg-summary :as summary]
   ))

(defn create-live-environment [feed duckdb]
  {:feed feed
   :duckdb duckdb
   :bar-categories (atom {})
   :env {:get-series (fn [asset bar-category]
                 (duck/get-bars duckdb asset))}
   :live-results-stream (s/stream)
   :summary-quote (summary/create-last-summary (quote-stream feed) :asset)
   })

(defn get-feed [state]
  (:feed state))

(defn get-result-stream [state]
  (:live-results-stream state))

(defn get-bar-categories [state]
  (keys @(:bar-categories state)))

(defn quote-snapshot [state]
  (summary/current-summary (:summary-quote state)))

;; bar-generator for bar-category
(defn get-existing-bar-category [{:keys [bar-categories] :as state} bar-category]
  (get @bar-categories bar-category))


(defn unfinished-bar-snapshot [state bar-category]
  (let [{:keys [bargenerator]} (get-existing-bar-category state bar-category)]
    (bg/current-bars bargenerator)))

        ;  [bar-category :results-stream]))

(defn category-result-stream [state bar-category]
  (get-in @(:bar-categories state) [bar-category :results-stream]))

(defn category-onbar-stream [state bar-category]
  (get-in @(:bar-categories state) [bar-category :bars-finished-stream]))

(defn category-algos [state bar-category]
  (get-in @(:bar-categories state) [bar-category :algos]))

(defn add-algo-to-bar-category [state bar-category algo]
  (swap! (:bar-categories state) update-in [bar-category :algos] conj algo))

(defn valid-ds [ds-bars]
  (let [c (tc/row-count ds-bars)]
    (> c 0)))

(defn save-finished-bars [duckdb bars-finished-stream]
  (fn [{:keys [time ds-bars]}]
    (try
      (if (valid-ds ds-bars)
         (duck/append-bars duckdb ds-bars)    
         (warn "not saving finished bars - ds-bars is not valid!"))
      (catch Exception ex
        (error "generated bars save exception!")
        (error "bars that could not be saved: " ds-bars)
        (bg/print-finished-bars ds-bars)
        (s/put! bars-finished-stream time)))))

(defn calc-algo [env {:keys [algo algo-opts]} time]
  (try 
    (info "calculating algo: " algo " time: " time)
    (let [result (algo env algo-opts time)]
       (info "calculating algo: " algo " time: " time " result: " result)
       result)
    (catch Exception ex
       (error "algo-calc exception " algo time ex)  
       nil)))


(defn calculate-on-bar-close [{:keys [env] :as state} 
                              bar-category
                              time]
  (info "calculate-on-bar-close " bar-category time)
  (let [result-stream (category-result-stream state bar-category)
        algos (category-algos state bar-category)] 
    (info "algos: " algos)
    (doall (map (fn [algo] 
                  (let [result (calc-algo env algo time)]
                    (when result 
                       (s/put! result-stream result))))
                algos))))

(defn connect-feed-with-bargenerator [bargen feed]
  (info "connecting feed with bargenerator")
  (let [stream (quote-stream feed)
        process-tick (fn [tick]
                       ;(info "bargen is processing tick: " tick)
                       (bg/process-tick bargen tick)
                       ;(info "bargen is processing tick: " tick " FINISHED!")
                       )]
    (s/consume process-tick stream)))

(defn add-new-bar-category [{:keys [duckdb feed env] :as state} bar-category]
  (info "add new bar category: " bar-category)
  (let [bars-finished-stream (s/stream) 
        results-stream (s/stream)
        bargen (bg/bargenerator-start bar-category
                                     (save-finished-bars duckdb bars-finished-stream))
        data {:bargenerator bargen
              :calc-fns []
              :bar-category bar-category
              :bars-finished-stream bars-finished-stream
              :results-stream results-stream}
        live-results-stream (get-result-stream state)]
    (swap! (:bar-categories state) assoc bar-category data)
    (info "connecting streams...")
    (s/consume (partial calculate-on-bar-close env bar-category) bars-finished-stream)
    ;  (s/map (partial calculate-on-bar-close env bar-category)
    ;                          bars-finished-stream)
    (connect-feed-with-bargenerator bargen feed)
    ;(s/connect bars-finished-stream results-stream)
    (s/connect results-stream live-results-stream)
    (info "connecting streams... done!")
    data))

(defn get-bar-category [state bar-category]
  (or (get-existing-bar-category state bar-category)
      (add-new-bar-category state bar-category)))

(defn add [state bar-category algo]
  (get-bar-category state bar-category)
  (add-algo-to-bar-category state bar-category algo)
  (if-let [asset (get-in algo [:algo-opts :asset])]
    (let [feed (get-feed state)] 
      (info "added algo with asset [" asset "] .. subscribing..")
      (subscribe feed asset))
    (warn "added algo without asset .. not subscribing!")))

;; see in demo notebook.live.live-bargenerator


(comment 
  (require '[modular.system])
  (def live (modular.system/system :live)) 
  live  

  ;(def bar-category [:forex :m])
  ;(def bar-category [:us :m])
  (def bar-category [:eu :m])

  ; add (create) a calculation category.
  (get-bar-category live bar-category)

  (category-algos live bar-category)

  ; force calculation at the end of a simulated bar
  (def onbar-stream (category-onbar-stream live bar-category))
  onbar-stream
  (s/put! onbar-stream :right-now)


  (defn algo-const [_env opts time]
    {:time time
     :v 42})
  
  (add live bar-category {:algo algo-const
                          :algo-opts {:asset "EUR/USD"}})

  
  (calculate-on-bar-close live bar-category :now)


  (get-result-stream live)
  
  (get-bar-categories live)
  
  (get-feed live)

   (let [feed (get-feed live)]
  (subscribe feed "USD/JPY"))

  
(-> live env/quote-snapshot print-table)


@(:bar-categories live)

(get-in @(:bar-categories live) [[:eu :m] :results-stream])

(get-in @(:bar-categories live) [[:eu :m] :bars-finished-stream])

(def eu-m-finished-bars-stream
  (get-in @(:bar-categories live) [[:eu :m] :bars-finished-stream]))


(s/put! eu-m-finished-bars-stream :right-now)

eu-m-finished-bars-stream

(s/take! eu-m-finished-bars-stream)


(def forex-result-stream (category-result-stream  live [:forex :m]))

forex-result-stream

(s/take! forex-result-stream)

(s/put! forex-result-stream {:r 123})



(env/add-calc-fn-to-bar-category live [:forex :m] algo-const)

(calculate-on-bar-close live [:us :m] :now)


(get-bar-category live [:us :m])

(get-bar-category live [:us :m])

(get-bar-category live [:eu :m])


 ; 
  )