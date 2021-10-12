(ns ta.dataset.date
  (:require
   [tick.alpha.api :as tick]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype.functional :as fun]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.datetime :as datetime]
   [tech.v3.dataset.print :refer [print-range]]
   [tablecloth.api :as tablecloth]
   [fastmath.core :as math]
   [fastmath.stats :as stats]
   [ta.data.date :as dt]))

(defn days-ago [n]
  (-> (tick/now)
      (tick/- (tick/new-duration n :days))
      (tick/date)))

(defn ds-epoch [ds]
  (dataset/column-map ds :epoch #(* 1000 (dt/->epoch %)) [:date]))

(defn select-rows-since [ds date]
  (-> ds
      (tablecloth/select-rows
       (fn [row]
         (-> row
             :date
             (tick/>= date))))))

(defn add-year-and-month [ds]
  (-> ds
      (tablecloth/add-columns
       {:year  #(->> %
                     :date
                     (datetime/long-temporal-field :years))
        :month #(->> %
                     :date
                     (datetime/long-temporal-field :months))})))

; (require '[clj-time.coerce])
; (clj-time.coerce/to-long dt)

