(ns juan.notebook.live.multiple
  (:require
   [ta.algo.env.protocol :as algo]
   [ta.viz.publish :as p]
   [juan.notebook.viz :refer [chart-spec table-spec combined-table-spec combined-chart-spec]]
   [juan.asset-pairs :refer [asset-pairs]]))

(defn add-topic [spec asset type]
  (assoc spec :topic [:live :juan asset type]))

(defn publish-result [env opts ds-bars]
  (let [table-spec (add-topic combined-table-spec (:asset opts) :table)
        chart-spec (add-topic combined-chart-spec (:asset opts) :chart)]
    (p/publish-ds->table nil table-spec ds-bars)
    (p/publish-ds->highstock nil chart-spec ds-bars)))

(def algo-spec
  [:day {:type :trailing-bar
         :algo   ['juan.algo.intraday/ensure-date-unique
                  'juan.algo.daily/daily]
         :calendar [:us :d]
         :asset "EUR/USD"
         :import :kibot
         :feed :fx
         :trailing-n 80
         :atr-n 10
         :step 0.0001
         :percentile 70}
   :minute {:calendar [:forex :m]
            :algo  ['juan.algo.intraday/ensure-date-unique
                    'juan.algo.doji/doji-signal]
            :type :trailing-bar
            :asset "EUR/USD"
            :feed :fx
            ; we just use the generated bars for intraday.
            ;:import :kibot-http
            :trailing-n 10000
            ; doji
            :max-open-close-over-low-high 0.3
            :volume-sma-n 30
            ; volume-pivots (currently not added)
            ;:step 10.0
            ;:percentile 70
            }
   :signal {:formula [:day :minute]
            :spike-atr-prct-min 0.5
            :pivot-max-diff 0.001
            :algo ['juan.algo.combined/daily-intraday-combined
                   'juan.notebook.live.multiple/publish-result]}])


(defn set-asset [algo-spec asset]
  (-> algo-spec
      (update 1 assoc :asset asset)
      (update 3 assoc :asset asset)))


(defn permutate-assets [algo]
  (map (fn [{:keys [fx] :as asset}]
         (set-asset algo fx))
       asset-pairs))

(defn create-juan [env _]
  (let [specs (permutate-assets algo-spec)
        result-a-seq (doall (map #(algo/add-algo env %) specs))]
    result-a-seq))

(comment 
  (require '[modular.system])
  (def env (modular.system/system :live))
  
  (create-juan env nil)

 ; 
  )