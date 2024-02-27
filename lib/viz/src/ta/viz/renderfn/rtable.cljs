(ns ta.viz.renderfn.rtable
  (:require 
     [rtable.rtable]))

(defn rtable [spec data]
  ; rtable needs 3 parameters: opts, cols, data
  ; our spec format only uses two parameters; we moved the 
  ; columns definition to a :columns key in opts
  (let [opts spec ; we could dissoc :columns here, but why?
        cols (:columns spec)]
    (with-meta 
      (if (empty? data)
        [:div.h-full.w-full.p-10 "No Rows in this table. "]
        [rtable.rtable/rtable opts cols data])
        {:R true})))