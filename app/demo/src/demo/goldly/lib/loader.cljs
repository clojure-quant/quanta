(ns demo.goldly.lib.loader
  (:require
    [reagent.core :as r]
    [promesa.core :as p]
    [goldly.service.core :refer [clj]]
    ))


(defn clj->p [fun & args]
  (println "loading clj fun: " fun " args: " args)
  (let [a (r/atom {:status :loading})
        rp (apply clj fun args)]
    (p/then rp (fn [r] (swap! a assoc :status :data :data r)))
    (p/catch rp (fn [r] (swap! a assoc :status :error :error r)))
    a))


#_(defn clj->p [fun & args]
  (let [f (r/atom nil)
        a (r/atom {:status :loading})]
    (fn [fun & args]
      (when-not (= [fun args] @f)
        (swap! a assoc :status :loading)
        (let [rp (apply clj fun args)]
          (reset! f [fun args])
          (p/then rp (fn [r] (swap! a assoc :status :data :data r)))
           (p/catch rp (fn [r] (swap! a assoc :status :error :error r)))))
      a)))

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

