(ns ta.live.bar-generator.db)

(defn create-db []
  (atom {:subscriptions {} ; feed+asset -> subscription
         :calendars {}}))

; 
; feed+asset+calendar -> bar
; calendar -> 


(defn create-bar! [db asset]
  (let [bar {:asset asset :epoch 1}]
    (swap! db assoc asset bar)
    bar))

(defn key-fn [{:keys [feed calendar]}]
  [feed calendar])

(defn get-bars-calendar [calendar]
  [])

(defn get-bar [state {:keys [feed calendar] :as opts}]
  (get @(:bars state) (key-fn opts)))
