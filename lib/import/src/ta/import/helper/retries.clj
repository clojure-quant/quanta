(ns ta.import.helper.retries
  (:require
   [de.otto.nom.core :as nom]))

(defn get-delay-ms [attempt]
  (let [random-ms (rand-int 250)
        max-delay-ms (* 30 1000)]
    (long (min (+ (* 2 (Math/pow 2 (dec attempt))) random-ms) max-delay-ms))))

(defn wait-exponential-time [attempt]
  (Thread/sleep (get-delay-ms attempt)))

(defn run-safe [fun args]
  (try
    (apply fun args)
    (catch Exception ex
      (nom/fail ::exception {:ex ex
                             :fun fun
                             :args args}))))

(defn with-retries [nr-attempts fun & args]
  (loop [i 1]
    (let [r (run-safe fun args)]
      (if (and (nom/anomaly? r)
               (<= i nr-attempts))
        (do (wait-exponential-time i)
            (recur (inc i)))
        r))))

(comment
  (get-delay-ms 1)
  (get-delay-ms 2)
  (get-delay-ms 3)

  (Thread/sleep 1000)
  (wait-exponential-time 1)

  (defonce s (atom 0))
  (defn unstable-fn [label]
    (swap! s inc)
    (when (< @s 5)
      (throw (Exception. "my exception message")))
    27)

  (reset! s 0)
  (with-retries 3 unstable-fn :test)

  (reset! s 0)
  (with-retries 5 unstable-fn :test)

  (reset! s 0)
  (with-retries 10 unstable-fn :test)

; 
  )
