(ns ta.notebook.resource-mapper
  (:require
   [tablecloth.api :as tc]
   [ta.notebook.persist :refer [save loadr]]))

(def default-mappings
  {java.lang.String :text
   clojure.lang.PersistentArrayMap :edn
   clojure.lang.PersistentVector :edn
   java.awt.image.BufferedImage :png
   tech.v3.dataset.impl.dataset.Dataset :nippy})

(defn map-to [mapping-table item]
  (->> item
       type
       (get mapping-table)))

(comment
  (type "adf")
  (type {})
  (type [])
  (map-to default-mappings 1)
  (map-to default-mappings {:a 1})
;  
  )
(defn map-item [{:keys [ns-nb resources mapping-table]} item]
  (assert mapping-table)
  (assert ns-nb)
  (assert resources)
  (if-let [format (map-to mapping-table item)]
    (let [name (-> (count @resources) inc str)
          res-vec [name format]]
      (save item ns-nb name format)
      (swap! resources conj res-vec)
      res-vec)
    item))

(comment

  ; check type of tml-ds
  (def ds1 (-> {:close [1 2 3 4 5]
                :open [1 2 3 4 5]}
               (tc/dataset)))

  (type ds1)

  ; prepare context
  (def res (atom []))
  (def ctx {:ns-nb "demo.mapping-test"
            :resources res
            :mapping-table default-mappings})

  ; map item (save to notebook storage)
  (map-item ctx 4)
  (map-item ctx {:a 1}) ; "1" :edn
  (map-item ctx [1 2 3 4 5 6]) ; "2" :edn
  @res ; this gives back all the resources

  ; load back resources
  (loadr "demo.mapping-test" "1" :edn)
  (-> (loadr "demo.mapping-test" "2" :edn)
      last)

; 
  )

