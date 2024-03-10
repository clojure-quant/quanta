(ns ta.db.bars.split-adjust
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as dfn]))

(defn has-col [ds col]
  (->> ds
       tc/columns
       (map meta)
       (filter #(= col (:name %)))
       empty?
       not
       ;(map :name)
       ))

(defn split-adjust [ds-bars]
  (if (has-col ds-bars :close-adj)
    (let [{:keys [open high low close close-adj]} ds-bars
          split-factor (dfn// close-adj close)]
      ;(println "split factor: " split-factor)
      (-> ds-bars
          (tc/drop-columns [:close-adj])
          (tc/add-columns
           {:split-factor split-factor
            :open (dfn/* open split-factor)
            :high (dfn/* high split-factor)
            :low (dfn/* low split-factor)
            :close (dfn/* close split-factor)})))
    ds-bars))

(comment

  (-> (tc/dataset {:open [100 200 300]
                   :high [100 200 300]
                   :low [100 200 300]
                   :close [100.1 100.2 200.3]
                   :close-adj [5.3 7.3 8.0]})
      ;(has-col :x)
   ;   (has-col :close)
      (split-adjust))

;  
  )