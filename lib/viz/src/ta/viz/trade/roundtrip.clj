(ns ta.viz.trade.roundtrip
  (:require
   [ta.viz.ds.rtable :refer [rtable-render-spec]]))

(def default-spec
  {:class "table-head-fixed padding-sm table-blue table-striped table-hover"
   :style {:width "100%" ;"1000px"
           :height "100%" ;"400px"
           :border "1px solid blue"}
   :columns [{:path :entry-date :format 'ta.viz.trade.format/fmt-yyyymmdd :header "dt-e"}
             {:path :asset :header "asset"}
             {:path :side :header "side"}
             {:path :qty :header "qty" :attrs 'ta.viz.trade.format/align-right :format 'ta.viz.trade.format/round-digit-1}
             {:path :entry-price :header "px-e"  :attrs 'ta.viz.trade.format/align-right}
             {:path :entry-vol :format 'ta.viz.trade.format/round-digit-0 :header "vol-e" :attrs 'ta.viz.trade.format/align-right}
             {:path :exit-date :format 'ta.viz.trade.format/fmt-yyyymmdd :header "dt-x"}
             {:path :exit-price :header "px-x" :max-width 50 :attrs 'ta.viz.trade.format/align-right}
             {:path :pl :header "p/l" :format 'ta.viz.trade.format/round-digit-0 :attrs 'ta.viz.trade.format/align-right}
             {:path :win? :header "w?" :max-width 50}
             ; prct
             {:path :ret-prct :header "%" :max-width 50 :format 'ta.viz.trade.format/round-digit-0  :attrs 'ta.viz.trade.format/align-right}
             ;{:path :cum-ret-prct :header "c%" :max-width 50 :format 'ta.viz.trade.format/round-digit-0  :attrs 'ta.viz.trade.format/align-right}
             ; log
             {:path :ret-log :header "log" :max-width 50 :format 'ta.viz.trade.format/round-digit-2  :attrs 'ta.viz.trade.format/align-right}
             ;{:path :cum-ret-log :header "clog" :max-width 50 :format 'ta.viz.trade.format/round-digit-2  :attrs 'ta.viz.trade.format/align-right}
             ;{:path :cum-ret-abs :header "cp/l" :max-width 50 :format 'ta.viz.trade.format/round-digit-2  :attrs 'ta.viz.trade.format/align-right}
             {:path :cum-ret-volume :header "cum$" :max-width 50 :format 'ta.viz.trade.format/round-digit-0  :attrs 'ta.viz.trade.format/align-right}]})

(defn roundtrip-ui [{:keys [extra-columns]
                     :or {extra-columns []}
                     :as spec}
                    roundtrip-ds]
  (let [spec (assoc default-spec :columns (concat (:columns default-spec) extra-columns))]
    (rtable-render-spec spec roundtrip-ds)))