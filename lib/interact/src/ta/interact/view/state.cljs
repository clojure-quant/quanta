(ns ta.interact.view.state
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [ui.webly :refer [notify]]
   [goldly.service.core :refer [clj]]))

;; state management

(defn create-state []
  {:template (r/atom nil)
   :template-list (r/atom [])
   :subscription (r/atom nil)
   :options (r/atom {})
   :current (r/atom {})})

(defn set-state [state k v]
  (println "setting state k: " k "val: " v)
  (reset! (k state) v))

(defn get-view-a [state k]
  (get state k))

"executes fun in clj.
   on success sets k in state.
   on error notifies ui."

(defn clj-state-k [state k fun & args]
  (println "loading clj fun: " fun " args: " args)
  (let [rp (apply clj fun args)]
    (-> rp 
      (p/then (fn [r]
               (set-state state k r)))
      (p/catch (fn [_r]
                (notify :error (str "data load error:"  fun args)))))
    nil))

(defn get-available-templates [state]
  (clj-state-k state :template-list 'ta.interact.template/available-templates))

(defn get-template-options [state template-id]
  (clj-state-k state :options 'ta.interact.template/get-options template-id))

(defn start-algo [state]
  (clj-state-k state :subscription 'ta.interact.subscription/subscribe-live 
               @(:template state)
               @(:current state)
               ))
