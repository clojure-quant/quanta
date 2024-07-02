(ns quanta.studio.view.state
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [re-frame.core :as rf]
   [nano-id.core :refer [nano-id]]
   [frontend.notification :refer [show-notification]]
   [goldly.service.core :refer [clj]]))

(defn get-result-a [state]
  (get state :result))

(defn set-result [state result]
  (reset! (get state :result) result))

(defn start-listening! [state]
  (rf/reg-event-db
   :interact/subscription
   (fn [db [_ {:keys [task-id result]}]]
     (let [target-task-id @(:task-id state)]
       ; only store if we currently want to see it.
       (if (= target-task-id task-id)
         (do (println "received subscription result for task: " task-id "result :" result)
             (set-result state result))
         (println
          (println "received task result that is not subscribed task " task-id
                   " target-task-id " target-task-id " result: " result)))
       db))))

;; state management

(defn create-state []
  {:template-list (r/atom [])
   :template (r/atom nil)
   :options (r/atom {})
   :current (r/atom {})
   :task-id (r/atom nil)
   :result (r/atom nil)})

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
                   (show-notification :error (str "data load error:"  fun args)))))))

(defn get-available-templates [state]
  (clj-state-k state :template-list 'quanta.studio.template/available-templates)
  nil)

(defn get-template-options [state template-id]
  (let [rp (clj-state-k state :options 'quanta.studio.template/get-options template-id)]
    (p/then rp (fn [r]
                 (println "setting current state: " (:current r))
                 (set-state state :current (:current r))))
    nil))

(defn unsubscribe [state]
  (when-let [task-id @(:task-id state)]
    (println "unsubscribing old task-id: " task-id)
    (clj 'quanta.studio/unsubscribe task-id)))

(defn get-mode [state]
  (or (get @(:current state) :mode)
      :chart))

(defn subscribe [state]
  (unsubscribe state)
  (let [task-id (nano-id 6)]
    (set-state state :task-id task-id)
    (start-listening! state) ; result gets pushed, so it might be that the subscription request
    ; comes back after the request comes back.
    (let [rp (clj 'quanta.studio/subscribe
                  @(:template state)
                  @(:current state)
                  (get-mode state)
                  task-id)]
      (-> rp
          (p/then (fn [task-id]
                    (println "successfully subscribed to task: " task-id)
                    ))
          (p/catch (fn [err]
                     (println "subscription error: " err)
                     (show-notification :error  "subscription failed!"))))
      nil)))

(defn backtest [state]
  (unsubscribe state)
  (clj-state-k state :result 'quanta.studio/backtest
               @(:template state)
               @(:current state)
               (get-mode state))
  nil)

