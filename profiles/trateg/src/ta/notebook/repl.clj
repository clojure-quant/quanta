(ns ta.notebook.repl
  (:require
   [taoensso.timbre :refer [info]]
   [webly.ws.core :refer [send-all! send-response connected-uids]]
   [ta.notebook.clj :refer [walk-forms]]
   [ta.notebook.resource-mapper :refer [default-mappings]]
   [ta.notebook.persist :as p]   
   
   ))


(defn save [data name format]
  (p/save data *ns* name format))

(defonce mapping-table
  (atom default-mappings))

(defn make-ctx []
  {:ns-nb *ns*
   :mapping-table @mapping-table
   :make-resources true})

(defn send! [r]
  (info "sending to scratchpad: " r)
  (send-all! [:viewer/update {:ns-nb (str *ns*)
                              :op :plot
                              :data (dissoc r :ns-nb)
                              }])
  r
  )

(defmacro show [form]
  (send! 
   (walk-forms
    (eval (make-ctx))
    form)))

(defn clear []
  (info "clearing output directory for " *ns*)
  (send-all! [:viewer/update {:ns-nb (str *ns*)
                              :op :clear}])
  )

(comment

  (show
   (line-plot [1 2 3]))

  (clear)

 ; 
  )



