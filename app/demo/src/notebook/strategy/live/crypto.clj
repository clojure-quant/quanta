(ns notebook.strategy.live.crypto
  (:require
   [taoensso.timbre :refer [info warn error]]
   [modular.system]
   [ta.algo.env.protocol :as algo]
   [ta.algo.permutate :as permutate]
   [ta.viz.publish :as p]
   [ta.viz.ds.highchart :refer [highstock-render-spec]]))

;; define algo

(defn nil-algo [_env opts bar-ds]
  (warn "nil algo: trailing-n: " (:trailing-n opts) " asset: " (:asset opts))
  bar-ds)

(def table-spec
  {:class "table-head-fixed padding-sm table-red table-striped table-hover"
   :style {:width "50vw"
           :height "40vh"
           :border "3px solid green"}
   :columns [{:path :date :format 'ta.viz.lib.format/dt-yyyymmdd}
             {:path :open}
             {:path :high}
             {:path :low}
             {:path :close}
             {:path :volume}]})

(def chart-spec
  {:chart {:box :fl}
   :charts [{:close :candlestick #_:ohlc}
            {:volume {:type :column :color "red"}}]})

(defn add-topic [spec asset type]
  (assoc spec :topic [:live :crypto asset type]))

(defn publish-result [env opts ds-bars]
  (let [table-spec (add-topic table-spec (:asset opts) :table)
        chart-spec (add-topic chart-spec (:asset opts) :chart)]
    (p/publish-ds->table nil table-spec ds-bars)
    (p/publish-ds->highstock nil chart-spec ds-bars)))


(def algo-spec
  {:type :trailing-bar
   :trailing-n 3000
   :calendar [:crypto :m]
   :asset "BTCUSDT"
   :feed :bybit
   :import :bybit
   :algo ['notebook.strategy.live.crypto/nil-algo
          'notebook.strategy.live.crypto/publish-result]})

(defn create-crypto [env & _args]
  (let [specs (permutate/->assets algo-spec ["BTCUSDT" "ETHUSDT"])
        result-a-seq (doall (map #(algo/add-algo env %) specs))]
    result-a-seq))


(defn calc-viz-highchart [bar-ds]
  (highstock-render-spec nil chart-spec bar-ds))


(comment
  (require '[ta.interact.subscription :refer [results-a]])

  @results-a

  (calc-viz-highchart @results-a)


 ;
  )


