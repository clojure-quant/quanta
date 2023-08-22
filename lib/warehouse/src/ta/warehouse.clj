(ns ta.warehouse
  (:require
   [clojure.java.io :as java-io]
   [taoensso.timbre :refer [debug info warnf error]]
   [tech.v3.io :as io]
   ;[taoensso.nippy :as nippy]
   [tablecloth.api :as tc]
   [modular.config :refer [get-in-config] :as config]
   [ta.warehouse.split-adjust :refer [split-adjust]]))

; timeseries - name

(defn filename-ts [w symbol]
  (let [p (get-in-config [:ta :warehouse :series w])]
    (str p symbol ".nippy.gz")))

(defn save-ts [wkw ds name]
  (let [s (io/gzip-output-stream! (filename-ts wkw name))]
    (info "saving series " name " count: " (tc/row-count ds))
    (io/put-nippy! s ds)))

(defn load-ts [wkw name]
  (let [s (io/gzip-input-stream (filename-ts wkw name))
        ds (io/get-nippy s)]
    (debug "loaded series " name " count: " (tc/row-count ds))
    ds))

; timeseries - symbol + frequency

(defn make-filename [frequency symbol]
  (str symbol "-" frequency))

(defn exists-symbol? [w frequency s]
  (let [filename (filename-ts w (make-filename frequency s))]
    (.exists (java-io/file filename))))

(defn load-symbol [w frequency s]
  (-> (load-ts w (make-filename frequency s))
      split-adjust
      (tc/set-dataset-name (str s))
      (tc/add-column :symbol s)))

(defn save-symbol [w ds frequency symbol]
  (let [n (make-filename frequency symbol)]
    ;(info "saving: " n)
    (save-ts w ds n)))

; series in warehouse

(defn- filename->info [filename]
  (let [m (re-matches #"(.*)-(.*)\.nippy\.gz" filename)
        [_ symbol frequency] m]
    ;(errorf "regex name: %s cljs?: [%s]" name cljs?)
    {:symbol symbol
     :frequency frequency}))

(comment
  (filename->info "BTCUSD-15.nippy.gz")
 ;
  )
(defn- dir? [filename]
  (-> (java-io/file filename) .isDirectory))

(defn symbols-available [w frequency]
  (let [dir (java-io/file (get-in-config [:ta :warehouse :series w]))
        files (if (.exists dir)
                (into [] (->> (.list dir)
                              (remove dir?)
                              doall))
                (do
                  (warnf "path for: %s not found: %s" w dir)
                  []))]
    (debug "explore-dir: " files)
    ;(warn "type file:" (type (first files)) "dir?: " (dir? (first files)))
    (->> (map filename->info files)
         (remove #(nil? (:symbol %)))
         (filter #(= frequency (:frequency %)))
         (map :symbol))))

(comment

  (get-in-config [:ta])
  (exists-symbol? :crypto "D" "BTCUSD")
  (exists-symbol? :stocks "D" "SPY")
  (exists-symbol? :stocks "D" "BAD")

  (config/set! :ta {:warehouse {:list "../resources/etf/"
                                :series  {:crypto "../db/crypto/"
                                          :stocks "../db/stocks/"
                                          :random "../db/random/"
                                          :shuffled  "../db/shuffled/"}}})

  (symbols-available :crypto "D")
  (load-symbol :crypto "D" "ETHUSD")

 ;
  )
