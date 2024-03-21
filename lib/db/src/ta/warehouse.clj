(ns ta.warehouse
  (:require
   [clojure.java.io :as java-io]
   [taoensso.timbre :refer [debug info warnf error]]
   [babashka.fs :refer [create-dirs]]
   [tech.v3.io :as io]
   ;[taoensso.nippy :as nippy]
   [tablecloth.api :as tc]
   [modular.config :refer [get-in-config] :as config]
   [ta.db.bars.split-adjust :refer [split-adjust]]))

; timeseries - name

(defn filename-ts [w symbol]
  (let [p (get-in-config [:ta :warehouse :series w])]
    (str p symbol ".nippy.gz")))

(defn save-ts [wkw ds name])

(defn load-ts [wkw name])

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

(def  ^:dynamic *default-warehouse* nil)

(defn wh [w symbol]
  (let [w (or w *default-warehouse*)]
    (if (fn? w)
      (w symbol)
      w)))

(defn load-series
  "warehouse can either be specified, 
   or can use default-warehouse
   or can be calculated by a fn that gets the symbol"
  [{:keys [warehouse symbol frequency]}]
  (let [w (wh warehouse symbol)]
    (load-symbol w frequency symbol)))

(defn save-series
  "warehouse can either be specified, 
   or can use default-warehouse
   or can be calculated by a fn that gets the symbol"
  [{:keys [warehouse symbol frequency]} ds]
  (let [w (wh warehouse symbol)]
    (save-symbol w ds frequency symbol)))

(defn exists-series?
  "warehouse can either be specified, 
   or can use default-warehouse
   or can be calculated by a fn that gets the symbol"
  [{:keys [warehouse symbol frequency]}]
  (let [w (wh warehouse symbol)]
    (exists-symbol? w frequency symbol)))

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

  (load-series {:symbol "MSFT" :frequency "D"})
  (exists-series? {:symbol "MSFT" :frequency "D"})

  (let [ds (load-series {:symbol "MSFT" :frequency "D"})]
    (save-series {:symbol "MSFT2" :frequency "D"} ds))

;
  )
