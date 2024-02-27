(ns ta.viz.renderfn.rtable
  (:require 
     [ui.rtable]))

(defn rtable [spec data]
  ; rtable needs 3 parameters: opts, cols, data
  ; our spec format only uses two parameters; we moved the 
  ; columns definition to a :columns key in opts
  (let [opts spec ; we could dissoc :columns here, but why?
        cols (:columns spec)]
    (if (empty? data)
      [:div.h-full.w-full.p-10 "No Rows in this table. "]
      [ui.rtable/rtable opts cols data])))