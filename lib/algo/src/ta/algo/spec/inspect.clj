(ns ta.algo.spec.inspect)

(defn- specs [spec]
  (if (map? spec)
    [(assoc spec :id :0)]
    (->> (map (fn [[id spec]]
                (assoc spec :id id))
              (partition 2 spec))
         (into []))))

(defn- subscription? [{:keys [asset feed] :as spec}]
  (and asset feed))

(defn subscriptions [spec]
  (let [spec-vec (specs spec)]
    (->> (filter subscription? spec-vec)
         (map #(select-keys % [:asset :feed])))))


(defn- calendar-subscription? [{:keys [asset feed calendar] :as spec}]
  (and asset feed calendar))

(defn calendar-subscriptions [spec]
  (let [spec-vec (specs spec)]
    (->> (filter calendar-subscription? spec-vec)
         (map #(select-keys % [:asset :feed :calendar]))
         )))


(comment
  (def algo-spec
    [:day {:type :trailing-bar
           :algo   ['juan.algo.intraday/ensure-date-unique
                    'juan.algo.daily/daily]
           :calendar [:us :d]
           :asset "EURUSD"
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
              :asset "EURUSD"
              :feed :fx
              :import :kibot-http
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
              :algo 'juan.algo.combined/daily-intraday-combined}])


  (subscriptions algo-spec)
  (calendar-subscriptions algo-spec)

 ; 
  )



