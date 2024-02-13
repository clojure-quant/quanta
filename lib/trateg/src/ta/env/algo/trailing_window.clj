(ns ta.env.algo.trailing-window
  (:require
   [tick.core :as t]
   [ta.calendar.core :refer [trailing-window]]))

(defn create-trailing-bar-loader [{:keys [trailing-n asset bar-category] :as _spec}]
  ; fail once, when required parameters are missing
   (assert trailing-n)
   (assert asset)
   (assert bar-category)
   (fn [env _spec time]
     (when time 
       (let [{:keys [get-series]} env
             [calendar interval] bar-category
             time-seq (trailing-window calendar interval trailing-n time)
             dend  (first time-seq)
             dstart (last time-seq)
             dend-instant (t/instant dend)
             dstart-instant (t/instant dstart)
             ds-bars (get-series bar-category asset dstart-instant dend-instant)]
         ds-bars))))


