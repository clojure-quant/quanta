(ns ta.viz.renderfn.highstock
  (:require 
     [ui.highcharts]))


(defn highstock [spec data]
  ; highcharts has data inside the spec; we need to merge it
  ; into the spec
  (let [opts spec]
    (with-meta 
      [ui.highcharts/highstock opts]
      {:R true})))