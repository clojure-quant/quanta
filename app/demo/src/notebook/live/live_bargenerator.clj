(ns notebook.live.live-bargenerator
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [manifold.stream :as s]
   [modular.system]
   [ta.env.live-bargenerator :as env]))

; alternative way to generate a live-bargenerator
;(def duckdb (modular.system/system :duckdb))
;(def feed (modular.system/system :feed))
;duckdb
;feed
;(def live (env/create-live-environment feed duckdb))

(def live (modular.system/system :live))

live

(def bar-category [:crypto :m])

; log all results

(defn print-result [result]
  (info "result: " result))

(s/consume print-result (env/get-result-stream live))


; test if the result gets logged
(s/put! (env/get-result-stream live) "hello")


(env/quote-snapshot live)
(require '[clojure.pprint :refer [print-table]])

(-> live env/quote-snapshot print-table)

(env/unfinished-bar-snapshot live bar-category)
(-> live (env/unfinished-bar-snapshot bar-category) print-table)



(def time-stream (env/category-bar-time-stream live bar-category))

(s/consume #(info "*** bargen bar-close time: " %) 
           time-stream)


#_(add-to-scheduler print-time {:window {:calendar :us
                                       :interval :m}
                              :algo-ns 'algo2})

#_(add-to-scheduler print-time {:window {:calendar :us
                                       :interval :h}
                              :algo-ns 'algo2})