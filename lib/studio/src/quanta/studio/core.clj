(ns quanta.studio.core
  (:require
   [taoensso.timbre :refer [info warn error]]
   [extension :as ext]
   [clj-service.core :refer [expose-functions]]
   [quanta.studio.template :as template]))

(defn- requiring-resolve-safe [template-symbol]
  (try
    (requiring-resolve template-symbol)
    (catch Exception ex
      (error "could not resolve template symbol: " template-symbol " ex: " ex)
      nil)))


(defn add-template [this template-symbol]
  (if-let [template-var (requiring-resolve-safe template-symbol)]
    (let [template-val (var-get template-var)]
      (template/add this template-val))
      (throw (ex-info "quanta-template could not be resolved"
                     {:template template-symbol}))
    ))

(defn- add-templates
  "adds templates from extensions"
  [this exts]
  (let [template-symbols (ext/get-extensions-for exts :quanta/template concat [] [])]
    (info "adding templates: " template-symbols)
    (doall (map #(add-template this %) template-symbols))))

(defn start-studio [{:keys [exts clj role bar-db]}]
  (info "starting quanta-studio..")
  (let [this {:bar-db bar-db
              :templates (atom {})}]
    (add-templates this exts)
    (if clj
      (do
        (info "starting quanta-studio clj-services..")
        (expose-functions clj
                          {:name "quanta-studio"
                           :symbols ['quanta.studio.template/available-templates
                                     'quanta.studio.template/get-options
                                     'quanta.studio.subscription/subscribe-live
                                     'quanta.studio.subscription/unsubscribe]
                           :permission role
                           :fixed-args [this]}))
      (warn "quanta-studio starting without clj-services, perhaps you want to pass :clj key"))
      (info "quanta-studio running!")
     this))
