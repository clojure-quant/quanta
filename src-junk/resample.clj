

(defn resample-5 [{:keys [datetime count]}]
  (letfn [(add-tick [result dt c]
            (if dt
              (-> result
                  (update-in [:datetime] conj dt)
                  (update-in [:count] conj c))
              result))]

    (loop [datetimes datetime
           counts count
           rounded-last nil
           count-last 0
           result {:datetime [] :count []}]
      (if (empty? datetimes)
        (add-tick result rounded-last count-last)
        (let [dt (first datetimes)
              c (first counts)
              rounded (- dt (mod dt (* 5 60 1000)))]
          (if (= rounded-last rounded)
            (recur (rest datetimes) (rest counts) rounded (+ count-last c) result)
            (recur (rest datetimes) (rest counts) rounded c (add-tick result rounded-last count-last))))))))