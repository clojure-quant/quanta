(ns ta.interact.view.state
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [ui.webly :refer [notify]]
   [goldly.service.core :refer [clj]]))

;; state management

(defn create-state []
  {:template-list (r/atom [])
   :template (r/atom nil)
   :options (r/atom {})
   :current (r/atom {})
   :subscription (r/atom nil)})

(defn set-state [state k v]
  (println "setting state k: " k "val: " v)
  (reset! (k state) v))

(defn get-view-a [state k]
  (get state k))

(defn clj-state-k
  "executes fun in clj.
     on success sets k in state.
     on error notifies ui."
  [state k fun & args]
  (println "loading clj fun: " fun " args: " args)
  (let [rp (apply clj fun args)]
    (-> rp
        (p/then (fn [r]
                  (set-state state k r)))
        (p/catch (fn [_r]
                   (notify :error (str "data load error:"  fun args)))))))

(defn get-available-templates [state]
  (clj-state-k state :template-list 'ta.interact.template/available-templates)
  nil)

(defn get-template-options [state template-id]
  (let [rp (clj-state-k state :options 'ta.interact.template/get-options template-id)]
    (p/then rp (fn [r]
                 (println "setting current state: " (:current r))
                 (set-state state :current (:current r))))
    nil))

(defn unsubscribe [state]
  (when-let [sub-id @(:subscription state)]
    (println "unsubscribing old subscription: " sub-id)
    (clj 'ta.interact.subscription/unsubscribe sub-id)))

(defn start-algo [state mode]
  (unsubscribe state)
  (clj-state-k state :subscription 'ta.interact.subscription/subscribe-live
               @(:template state)
               @(:current state)
               mode)
  nil)
