(ns quanta.notebook.studio-template
  (:require 
    [modular.system]
    [quanta.template :refer [load-template get-options]]))


(def s (modular.system/system :studio))


(load-template s :juan-fx)
(get-options s :juan-fx)