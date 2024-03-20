(ns ta.interact.studio
  (:require 
    [extension :as ext]
    [ta.interact.template :as template]))


(defn add-templates [exts]
  (let [template-symbols (ext/get-extensions-for exts :quanta/template concat [] [])
        template-vars (map requiring-resolve template-symbols)
        template-vals (map var-get template-vars)
        ]
    (doall (map template/add template-vals))))


(defn start-studio []
   (let [exts (ext/discover)]
     (add-templates exts)
     
     
     ))


