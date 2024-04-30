(ns ta.interact.studio
  (:require
   [extension :as ext]
   [ta.interact.template :as template]))

(defn ok-symbol? [s v]
  (when (nil? v)
    (throw (ex-info (str "quanta studio start: could not resolve template: " s)
                    {:template s}))))

(defn add-templates [exts]
  (let [template-symbols (ext/get-extensions-for exts :quanta/template concat [] [])
        template-vars (map requiring-resolve template-symbols)
        _ (doall (map ok-symbol? template-symbols template-vars))
        template-vals (map var-get template-vars)]
    (doall (map template/add template-vals))))

(defn start-studio []
  (let [exts (ext/discover)]
    (add-templates exts)))


