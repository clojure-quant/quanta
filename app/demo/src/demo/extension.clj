(ns demo.extension
  (:require 
     [extension.discover :as d]
   )
  )


(defn test-discovery [_]
  (let [state (d/discover {})]
    
    (println "see target/webly/public/*.edn")
    
    ; routes/handler
    (d/get-extensions-for state :cljs-routes merge {} {})
    (d/get-extensions-for state :clj-services concat [] {}) ;not working
    (d/get-extensions-for state :api-routes concat [] [])
    
    ; cljs-build
    (d/get-extensions-for state :cljs-namespace concat [] [])
    (d/get-extensions-for state :cljs-ns-bindings concat [] [])
    (d/get-extensions-for state :sci-cljs-ns concat [] [])
    
    ; runtime start
    (d/get-extensions-for state :ns-clj concat [] [])


    (d/get-extensions-for state :theme concat [] {})
    

    (d/get-extensions-for state :lazy concat [] {})
   
    
;    
    ))


