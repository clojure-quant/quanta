(ns ta.algo.core
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [tablecloth.api :as tc]
   [ta.viz.study-highchart :refer [study-highchart] :as hc]
   [ta.calendar.core :refer [trailing-window]]
   [ta.algo.ds :refer [has-col?]]
   [ta.env.core :as env]
   ))

(defn- get-symbol [algo-ns algo-symbol]
  (require [algo-ns])
  (let [s (symbol (name algo-ns) algo-symbol)]
    ;(println "resolve: " s)
    (resolve s)))

(defn get-algo [algo-ns]
  {:algo-calc (get-symbol algo-ns "bar-strategy")
   :algo-opts-default (var-get (get-symbol algo-ns "algo-opts-default"))
   :algo-charts (get-symbol algo-ns "algo-charts")})

(defn get-algo-calc [algo-ns]
  (:algo-calc (get-algo algo-ns)))

(defn run-algo [env
                {:keys [algo-ns algo-opts]
                :or {algo-opts {}}}]
  (let [{:keys [asset calendar]} algo-opts
        {:keys [algo-calc algo-opts-default algo-charts]} (get-algo algo-ns)
        opts (merge algo-opts-default algo-opts)
        ds-bars (env/get-bars env asset calendar)
        ds-algo (algo-calc ds-bars opts)]
    {:ds-algo ds-algo
     :algo-charts algo-charts
     :opts opts}))



(defn- default-algo-chart-spec [ds-algo]
  (if (has-col? ds-algo :trade)
    [{:trade "flags"}
     {:volume "column"}]
    [{:volume "column"}]))

(defn highchart [{:keys [ds-algo algo-charts]}]
  (let [axes-spec (if algo-charts
                    algo-charts
                    (default-algo-chart-spec ds-algo))]
    (println "highchart axes spec:" axes-spec)
    (-> (study-highchart ds-algo nil); axes-spec)
        second)))


(defn tap-highchart [data]
  (let [data (highchart data)
        ;data (assoc data :chart {:height 600 :width 1200})
        ]
    (tap> ^{:render-fn 'ui.highcharts/highstock}
     {:data data
      :box :lg
      
      })))

(defn calculate-algo-trailing-window 
  "returns ds-strategy for the current bar-close-time"
  [env
   {:keys [asset opts window] :as algo}
   time]
  (let [{:keys [calendar interval n]} window
        ds-time (trailing-window calendar interval n time)
        ds-algo (run-algo env algo)]
    ds-algo))


(comment
  (get-algo 'demo.algo.sma3)
  (get-algo-calc 'demo.algo.sma3)

  (require '[modular.system])
  (def session (:duckdb modular.system/system))
  (require '[ta.db.bars.duckdb :as duckdb])
  (def env {:bar-db session})

  (-> (run-algo env {:algo-ns 'demo.algo.sma3
                     :algo-opts {:asset "MSFT"
                                 :calendar "default"}})
      ;(highchart)

      (tap-highchart))

  (tap> ^{:render-fn 'ui.clock/clock} [])

  (tap>
   ^{:render-fn 'reval.goldly.viz.render-fn/reagent}
   [:p.bg-blue-500 "hello, world!"])


 ; 
  )