(ns ta.data.print
  (:require
   [medley.core :as m]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Repl utils;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;https://github.com/semperos/rankle/blob/master/src/com/semperos/rankle/util.clj
(defn print-table-neat
  "Visually calmer version of clojure.pprint/print-table"
  ([ks rows]
   (when (seq rows)
     (let [widths  (map
                    (fn [k]
                      (apply max (count (str k)) (map #(count (str (get % k))) rows)))
                    ks)
           spacers (map #(apply str (repeat % "-")) widths)
           fmts    (map #(str "%" % "s") widths)
           fmt-row (fn [leader divider trailer row]
                     (str leader
                          (apply str (interpose divider
                                                (for [[col fmt] (map vector (map #(get row %) ks) fmts)]
                                                  (format fmt (str col)))))
                          trailer))]
       (println)
       #_(println (fmt-row "| " " | " " |" (zipmap ks ks)))
       (println (fmt-row "  " "   " "  " (zipmap ks ks)))
       #_(println (fmt-row "|-" "-+-" "-|" (zipmap ks spacers)))
       (println (fmt-row "--" "- -" "--" (zipmap ks spacers)))
       (doseq [row rows]
         #_(println (fmt-row "| " " | " " |" row))
         (println (fmt-row "  " "   " "  " row))))))
  ([rows]
   (print-table-neat (keys (first rows)) rows)))

(defn format-row [row]
  (m/map-vals #(cond->> % (float? %) (format "%.3f")) row))