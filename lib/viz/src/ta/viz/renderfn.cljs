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
  (println "get-render-fn for : " render-fn)
  (let [render-fn-p (requiring-resolve render-fn)
        render-fn-a (r/atom show-loading)]
    (-> render-fn-p
        (p/then (fn [render-fn]
                  (.log js/console "success loading render-fn: " render-fn)
                  (reset! render-fn-a render-fn)))
        (p/catch (fn [error]
                   (.log js/console "could not get render-fn: " render-fn "error: " error)
                   (reset! render-fn-a show-failed))))
    render-fn-a))

(defn show-ui [render-fn-a render-fn spec data]
  [:div
   ;[:p "render-spec ui for render-fn: " (pr-str render-fn)]
   ;[:p "resolved render-fn: " (pr-str @render-fn-a)]
   [@render-fn-a spec data]])

(defn render-spec [{:keys [render-fn spec data]}]
  (let [render-fn-a (get-render-fn render-fn)]
    [show-ui render-fn-a render-fn spec data]))

(defn render [render-spec]
  (with-meta
    render-spec
    {:render-fn 'ta.viz.renderfn/render-spec}))



