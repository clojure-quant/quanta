(ns ta.viz.trade.metrics)

(defn metrics-ui [metrics]
  (with-meta
    metrics
    {:render-fn 'ta.viz.trade.metrics/metrics-view}))

