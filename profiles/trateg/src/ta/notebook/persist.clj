(ns ta.notebook.persist
  (:require
   [taoensso.timbre :refer [debug info warnf error]]
   [clojure.java.io :as io]
   ; persister
   [ta.persist.text :as text]
   [ta.persist.edn :as edn]
   [ta.persist.image :as image]
   [ta.persist.tds :as tds-persist]))

(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

(defonce notebook-root-dir (atom "/tmp/studies/"))

(defn- ensure-directory-notebook []
  (ensure-directory @notebook-root-dir))

(defn get-notebook-list []
  (let [nb-root-dir (io/file @notebook-root-dir)]
    (if (and (.exists nb-root-dir)
             (.isDirectory nb-root-dir))
      (->> nb-root-dir
           (.listFiles)
           (map #(.getName %)))
      [])))

(defn get-resource-list [nb-ns]
  (let [nb-dir (io/file (str @notebook-root-dir (str nb-ns)))]
    (if (and (.exists nb-dir)
             (.isDirectory nb-dir))
      (->> nb-dir
           (.listFiles)
           (map #(.getName %)))
      [])))

(defn- make-filename [ns-nb name ext]
  (let [;nss *ns*
        study-dir (str @notebook-root-dir ns-nb)
        file-name (str study-dir "/" name "." ext)]
    (ensure-directory-notebook)
    (ensure-directory study-dir)
    file-name))

(defn get-filename-ns [ns-nb name]
  (str @notebook-root-dir (str ns-nb) "/" name))

(def formats
  {; browser can handle those:
   :text {:ext "txt" :save text/save :load text/loadr}
   :edn {:ext "edn" :save edn/save :load edn/loadr}
   :csv {:ext "csv" :save tds-persist/save-csv}
   :png {:ext "png" :save image/save-png}
   :arrow {:ext "arrow" :save tds-persist/save-arrow :loadr tds-persist/load-arrow}
   ; browser cannot handle this
   :nippy {:ext "nippy.gz" :save tds-persist/save-nippy :loadr tds-persist/load-nippy}})

(defn known-formats []
  (map first formats))

(comment
  (known-formats)
  ;
  )

(defn save [data ns-nb name format]
  (if-let [f (format formats)]
    (let [{:keys [ext save]} f
          file-name (make-filename ns-nb name ext)]
      (save file-name data))
    (error "save with unknown format: " format "name: " name "known formats: " (pr-str (known-formats))))
  data ; usable for threading macros
  )

(defn loadr [ns-nb name format]
  (if-let [f (format formats)]
    (let [{:keys [ext load]} f
          file-name (make-filename ns-nb name ext)]
      (if loadr
        (load file-name)
        (do (error "no load fn for format: " format)
            nil)))
    (error "load with unknown format: " format "name: " name)))

(defn filename->extension [filename]
  (let [[_ path _ ext] (re-matches #"([\w\/\.]*)\/+([\w-]+)\.([\w\.]+)$" filename) ; with path
        [_ _ ext2] (re-matches #"([\w-]+)\.([\w\.]+)$" filename)] ; no path
    (if path
      ext
      ext2)))

(defn filename->format [filename]
  (let [ext (filename->extension filename)]
    (->>
     (map (fn [[k v]] [k v]) formats)
     (filter #(= (-> % second :ext) ext))
     first
     first)))

(comment

  ; just filename
  (filename->extension "bongo.txt")
  (filename->extension "bongo.csv")
  (filename->extension "ds1.nippy.gz")
  (filename->extension "item-plot.png")
  ; should fail
  (filename->extension "bongo")
  (filename->extension ".bongo")

  ; filename with path
  (filename->extension "/tmp/notebooks/demo.studies.a/bongo.csv")
  (filename->extension "/tmp/notebooks/demo.studies.a/ds1.nippy.gz")

  (filename->format "bongo.edn")
  (filename->format "/tmp/notebooks/demo.studies.a/ds1.nippy.gz")
  (filename->format "bongo.png")

;
  )
(comment

  (ensure-directory-notebook)

  (save {:a 1 :b "bongotrott" :c [1 2 3]}  "demo.3" "bongotrott" :edn)
  (save  {:a 1 :b "bongotrott" :c [1 2 3]} "demo.3" "bongotrott-1" :edn)
  (save  {:a 1 :b "bongotrott" :c [1 2 3]} "demo.3" "bongotrott-2" :edn)
  (save  {:a 1 :b "bongotrott" :c [1 2 3]} "demo.3" "bongotrott" :bad-format-3)

  ; should fail, needs ds
  (save  {:a 1 :b "bongotrott" :c [1 2 3]} "demo.3" "bongotrott" :arrow)

  (get-notebook-list)

  (get-resource-list 'demo.3)

  (require '[tablecloth.api :as tc])
  (save (tc/dataset {:a [1.4 2.5]}) "demo.3" "bongotrott" :nippy)

  ; nippy only works on java11
  (save  (tc/dataset {:a [1 2] :b  [1 2] :c [2 3]}) "demo.3" "bongotrott" :arrow)
  (save (tc/dataset {:a [1.4 2.5]}) "demo.3" "bongotrott5" :arrow)

  (let [ns-nb "demo.3"
        n "ds-daniel"]
    (-> (tc/dataset {:a [1 2 3]
                     :b [4 4 4]
                     ;:c [true false true]
                     })
        (save ns-nb n :nippy)
        (save ns-nb n :csv)
        ;(save ns-nb n :arrow)
        ))

  (-> (loadr "demo.3" "bongotrott-2" :edn)
      :c)

  (loadr "demo.studies.asset-allocation-dynamic" "2" :text)
  (loadr "demo.studies.asset-allocation-dynamic" "ds2" :bad-format-5)
  (loadr "demo.studies.asset-allocation-dynamic" "ds-777" :nippy)
  (loadr "demo.studies.asset-allocation-dynamic" "ds2" :arrow)

; 
  )