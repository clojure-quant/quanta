(ns ta.trade.signal
  (:require
   [tablecloth.api :as tc]
   [ta.helper.ago :refer [xf-ago-pair]]))


(defn buy-above [p o]
  (if (and p o)
    (cond
      (> p o) :buy
      (< p o) :flat
      :else :hold)
    :hold))

(defn filter-signal [{:keys [signal of]
                      :or {of :signal}}
                     ds]
  (tc/select-rows ds
                  (fn [cols]
                    (let [cur-signal (of cols)]
                      (= cur-signal signal)))))

(comment

  (into [] xf-signal->position
        [:none
         :buy :buy :buy :none nil nil :buy :none :none
         :sell :none])

  (into [] (comp xf-ago-pair
                 (map position-change->trade))
        [:none :long :long :long :long :long :long :long :long :long :short :short])

  (into [] (comp
            xf-signal->position
            xf-ago-pair
            (map position-change->trade))
        [:none
         :buy :buy :buy :none nil nil :buy :none :none
         :sell :none])

  (signal->position [:none
                     :buy :buy :buy :none :flat nil :buy :none :none
                     :sell :none])

  (let [s [:none
           :buy :buy  :none nil
           :flat :none
           :buy :none
           :flat :none]]
    (tc/dataset {:signal s
                 :position (signal->position s)
                 :trade  (signal->trade s)}))

  (-> [:none
       :buy :buy :buy :none nil nil :buy :none :none
       :sell :none]
      signal->trade
      trade->trade-no)

  (-> (signal->trade [:none
                      :buy :buy :buy :none nil nil :buy :none :none
                      :sell :none])
      trade->trade-no)

  (signal->trade [:neutral :long :long])

;  
  )