(ns ta.trade.signal
  (:require
   [tech.v3.datatype :as dtype]
   [tablecloth.api :as tc]))

(defn filter-signal [{:keys [signal of]
                      :or {of :signal}}
                     ds]
  (tc/select-rows ds
                  (fn [cols]
                    (let [cur-signal (of cols)]
                      (= cur-signal signal)))))

(defn select-signal-contains [ds signal-col v]
  (tc/select-rows ds
                  (fn [row]
                    (contains? v (signal-col row)))))

(defn select-signal-is [ds signal-col v]
  (tc/select-rows ds
                  (fn [row]
                    (= (signal-col row) v))))

(defn select-signal-has [ds signal-col]
  (tc/select-rows ds
                  (fn [row]
                    (signal-col row))))

(defn signal-keyword->signal-double [signal]
  (let [n (count signal)]
    (dtype/make-reader
     :float64 n
     (let [s (signal idx)]
       (cond
         (= :buy s) 1.0
         (= :long s) 1.0
         (= :sell s) -1.0
         (= :short s) -1.0
         :else 0.0)))))


(defn signal-bool->keyword-long [long-bool-signal-col]
  (let [n (tc/row-count long-bool-signal-col)]
    (dtype/make-reader
     :keyword n
     (let [s (long-bool-signal-col idx)]
       (case s
         true :long
         nil)))))

(defn signal-bool->keyword-short [short-bool-signal-col]
  (let [n (tc/row-count short-bool-signal-col)]
    (dtype/make-reader
     :keyword n
     (let [s (short-bool-signal-col idx)]
       (case s
         true :short
         nil)))))


(comment

  (def ds
    (tc/dataset [{:idx 1 :signal false :doji :buy}
                 {:idx 2 :signal false :doji :flat}
                 {:idx 3 :signal true :doji :sell}
                 {:idx 4 :signal false :doji :long}]))

  (select-signal-is ds :signal true)

  (select-signal-contains ds :doji #{:buy :sell})
  (select-signal-contains ds :doji #{:buy :sell :flat})
  (select-signal-contains ds :doji #{:buy :long})

  (:doji ds)
  (signal-keyword->signal-double (:doji ds))

; 
  )
