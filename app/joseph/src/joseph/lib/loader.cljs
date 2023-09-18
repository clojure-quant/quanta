(ns joseph.lib.loader
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [goldly.service.core :as service]
   [ui.webly :refer [notify]]))

(defn clj [init-value fun & args]
  (let [a (r/atom init-value)
        rp (apply service/clj fun args)]
    (p/then rp (fn [r] (reset! a r)))
    (p/catch rp (fn [err] 
                  (println "exception in loading data " fun " error: " err)
                  (notify :error (str "data load error:" (if (map? fun) (first args) fun)))))
    a))

