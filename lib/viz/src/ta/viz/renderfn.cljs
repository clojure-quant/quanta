(ns ta.viz.renderfn
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [goldly.sci :refer [requiring-resolve]]))

(defn show-loading [& args]
  [:p "loading renderer.."])

(defn show-failed [& args]
  [:p "error : could not load renderer!"])

(defn get-render-fn [render-fn]
  (let [render-fn-p (requiring-resolve render-fn)
        render-fn-a (r/atom show-loading)]
    (-> render-fn-p
        (p/then (fn [render-fn]
                  (reset! render-fn-a render-fn)))
        (p/catch (fn [error]
                   (reset! render-fn-a show-failed))))
    render-fn-a))


(defn render-spec [{:keys [render-fn spec data]}]
  (let [render-fn-a (get-render-fn render-fn)]
    (fn [{:keys [render-fn spec data]}]
     [@render-fn-a spec data])))

    



