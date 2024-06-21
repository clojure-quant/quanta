(ns ta.viz.lib.loader
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [goldly.service.core :refer [clj]]))

(defn clj->p
  "makes a call to the server to execute a clj function.
   result is an atom containing a map {:status :data :error}
   :status has the loading-status
   :error gets set if there is some kind of error
   :data gets set once the clj-function is executed and the data is received."
  [fun & args]
  (println "loading clj fun: " fun " args: " args)
  (let [a (r/atom {:status :loading})
        rp (apply clj fun args)]
    (p/then rp (fn [r]
                 (println "result for " fun ": "  r)
                 (swap! a assoc :status :data :data r)))
    (p/catch rp (fn [r]
                  (println "error for " fun ": "  r)
                  (swap! a assoc :status :error :error r)))
    a))

(defn load-to-atom-once [a fun args]
  (println "loading clj fun: " fun " args: " args)
  (swap! a assoc :current [fun args] :status :loading)
  (let [rp (apply clj fun args)]
    (p/then rp (fn [r] (swap! a assoc :status :data :data r)))
    (p/catch rp (fn [r] (swap! a assoc :status :error :error r)))
    nil))

(defn clj->a [a fun & args]
  (let [current (:current @a)]
    (if (= current [fun args])
      nil
      (load-to-atom-once a fun args))))

