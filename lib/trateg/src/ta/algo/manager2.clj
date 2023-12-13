(ns ta.algo.manager2
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [tablecloth.api :as tc]
   [ta.viz.study-highchart :refer [study-highchart] :as hc]))

(defn- get-symbol [algo-ns algo-symbol]
  (require [algo-ns])
  (let [s (symbol (name algo-ns) algo-symbol)]
    ;(println "resolve: " s)
    (resolve s)))

(defn- get-algo [algo-ns]
  {:algo-calc (get-symbol algo-ns "algo-calc")
   :algo-opts-default (var-get (get-symbol algo-ns "algo-opts-default"))
   :algo-charts (get-symbol algo-ns "algo-charts")})

(defn run-algo [{:keys [get-series] :as env} {:keys [algo-ns algo-opts]
                                              :or {algo-opts {}}}]
  (let [{:keys [asset calendar]} algo-opts
        {:keys [algo-calc algo-opts-default algo-charts]} (get-algo algo-ns)
        opts (merge algo-opts-default algo-opts)
        ds-bars (get-series asset calendar)
        ds-algo (algo-calc ds-bars opts)]
    {:ds-algo ds-algo
     :algo-charts algo-charts
     :opts opts}))

(defn- has-col? [ds col-kw]
  (->> ds
       tc/columns
       (map meta)
       (filter #(= col-kw (:name %)))
       first))

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



(comment
  (get-algo 'demo.algo.sma3)

  (require '[modular.system])
  (def session (:duckdb modular.system/system))
  (require '[ta.warehouse.duckdb :as duckdb])
  (def env {:get-series (fn [asset cal]
                          (duckdb/get-bars session asset))})

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