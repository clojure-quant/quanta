(ns ta.trade.backtest.rule)

(defn get-exit-rule [algo-opts rule-kw]
  (let [{:keys [exit]} algo-opts
        rule (->> exit
                  (partition 2)
                  (filter (fn [[rule rule-opts]]
                            (= rule rule-kw)))
                  first)]
    (when rule
      (into [] rule))))

(comment
  (get-exit-rule {:exit [:profit 2.0
                         :loss 0.3
                         :time 1]}
                 :time)
     ;; => [:time 1]

  (get-exit-rule {:exit [:profit 2.0
                         :loss 0.3]}
                 :time)
   ;; => nil

  (get-exit-rule {:exit [:profit 2.0
                         :loss 0.3]}
                 :profit)
   ;; => [:profit 2.0]

  (get-exit-rule {:exit [:profit 2.0
                         :loss 0.3]}
                 :loss)
  ;; => [:loss 0.3]

; 
  )

