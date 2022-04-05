(ns ta.warehouse
  (:require
   [clojure.string :refer [includes? lower-case]]
   [clojure.java.io :as java-io]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [debug info warnf error]]
   [tech.v3.io :as io]
   ;[taoensso.nippy :as nippy]
   [tablecloth.api :as tc]
   [modular.config :refer [get-in-config] :as config]
   [ta.warehouse.split-adjust :refer [split-adjust]]))

(defn load-list-raw [name]
  (try
    (->> (str (get-in-config [:ta :warehouse :list]) name ".edn")
         slurp
         edn/read-string)
    (catch Exception _
      (error "Error loading List: " name)
      [])))

(defn process-item [symbols {:keys [list] :as item}]
  (if list 
    (concat symbols (load-list-raw list))
    (conj symbols item)))
            

;; lists
(defn load-list-full [name]
  (let [items (load-list-raw name)]
    (reduce process-item [] items)
    ;items
      ))

(defn load-lists-full [names]
  (->> (map load-list-full names)
       (apply concat)
       (into [])))


(defn load-list [name]
  (->> (load-list-full name)
       (map :symbol)))

(defn load-lists [names]
  (->> (map load-list names)
       (apply concat)))



(defn symbollist->dict [l]
  (let [s-name (juxt :symbol identity)
        dict (into {} (map s-name l))]
    dict))

(defn get-dict [names]
  (let [l (load-lists-full names)
        d (symbollist->dict l)]
    d))

(defn search [q]
  (let [l (load-lists-full (get-in-config [:ta :warehouse :lists]))
        q (lower-case q)]
    (filter (fn [{:keys [name symbol]}]
              (or (includes? (lower-case name) q)
                  (includes? (lower-case symbol) q)))

            l)))

(defn instrument-details [s]
  (let [d (get-dict (get-in-config [:ta :warehouse :lists]))]
    (get d s)))

(comment

   ;(search "P")
  (search "Bitc")
  (search "BT")
  (instrument-details "BTCUSD")

  (load-list-full "fidelity-select")
  (load-lists-full ["crypto"
                    "fidelity-select"
                    "bonds"
                    "commodity-industry"
                    "commodity-sector"
                    "currency-spot"
                    "equity-region"
                    "equity-region-country"
                    "equity-sector-industry"
                    "equity-style"
                    "test"])
  (->  (load-lists-full ["fidelity-select" "bonds"])
       (symbollist->dict))

; 
  )

; timeseries - name

(defn save-ts [wkw ds name]
  (let [p (get-in-config [:ta :warehouse :series wkw])
        s (io/gzip-output-stream! (str p name ".nippy.gz"))]
    (info "saving series " name " count: " (tc/row-count ds))
    (io/put-nippy! s ds)))

(defn load-ts [wkw name]
  (let [p (get-in-config [:ta :warehouse :series wkw])
        s (io/gzip-input-stream (str p name ".nippy.gz"))
        ds (io/get-nippy s)]
    (debug "loaded series " name " count: " (tc/row-count ds))
    ds))

; timeseries - symbol + frequency

(defn make-filename [frequency symbol]
  (str symbol "-" frequency))

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
                  (warnf "path for: %s not found: %s"  dir)
                  []))]
    (debug "explore-dir: " files)
    ;(warn "type file:" (type (first files)) "dir?: " (dir? (first files)))
    (->> (map filename->info files)
         (remove #(nil? (:symbol %)))
         (filter #(= frequency (:frequency %)))
         (map :symbol))))

(comment

  (config/set! :ta {:warehouse {:list "../resources/etf/"
                                :series  {:crypto "../db/crypto/"
                                          :stocks "../db/stocks/"
                                          :random "../db/random/"
                                          :shuffled  "../db/shuffled/"}}})

  (symbols-available :crypto "D")
  (load-symbol :crypto "D" "ETHUSD")

 ; 
  )