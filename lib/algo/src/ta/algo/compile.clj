(ns ta.algo.compile
  (:require
   [de.otto.nom.core :as nom]
   [taoensso.timbre :refer [trace debug info warn error]]
   [ta.algo.error-report :refer [save-error-report]]))

(defn compile-symbol
  "returns the fn that corresponds to the symbol
   returns nom/anomaly on error."
  [fun]
  (if (symbol? fun)
    (try
      (info "requiring-resolve: " fun)
      (let [f (requiring-resolve fun)]
        (if f
          f
          (nom/fail ::compile-error {:fun fun
                                     :message "symbol is not defined."})))
      (catch Exception ex
        (let [filename   (save-error-report (str "compile-" fun) {:symbol fun} ex)]
          (error "could not compile function: " fun "error-log: " filename)
          (nom/fail ::compile-error {:fun fun
                                     :ex ex}))))
    fun))

(comment
  (requiring-resolve 'ta.algo.compile/superstar)

  (compile-symbol 'ta.algo.compile/superstar)
  (compile-symbol 'discover.bug.not/existing)

 ; 
  )