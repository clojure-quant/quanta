(ns ta.db.bars.multi-asset
  (:require
   [de.otto.nom.core :as nom]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.calendar.align :as align]
   [ta.db.bars.protocol :as b]))

(defn calendar-seq->window [calendar-seq]
  {:start (-> (last calendar-seq) t/instant)
   :end (-> (first calendar-seq) t/instant)})

(defn calendar-seq->date-ds [calendar-seq]
  (tc/dataset {:date (reverse (map t/instant calendar-seq))}))

(defn- align-to-calendar2 [ds-bars ds-cal]
  (align/align-to-calendar ds-cal ds-bars))

(defn load-aligned [bardb {:keys [asset] :as opts} window ds-cal]
  (nom/let-nom> [ds-bars (b/get-bars bardb opts window)]
                (-> ds-bars
                    (tc/set-dataset-name asset)
                    (align-to-calendar2 ds-cal)
                    (align/fill-missing-close)
                    (tc/add-column :asset asset))))

(defn load-aligned-assets [bardb opts assets cal-seq]
  (let [window (calendar-seq->window cal-seq)
        date-ds (calendar-seq->date-ds cal-seq)
        load-asset (fn [asset]
                     [asset (load-aligned bardb (assoc opts :asset asset) window date-ds)])]
    (->> (map load-asset assets)
         (into {}))))

(defn is-valid? [result asset]
  (->  result (get asset) nom/anomaly? not))

(comment
  (require '[modular.system])
  (def bardb (modular.system/system :duckdb))
  bardb

  (require '[ta.calendar.core :as cal])
  (def cal-seq (cal/trailing-window [:forex :d] 100))
  cal-seq
  (calendar-seq->window cal-seq)
  (calendar-seq->date-ds cal-seq)

  (def cal-seq-fixed
    (cal/fixed-window [:forex :d] {:start (t/instant "2023-01-01T00:00:00Z")
                                   :end (t/instant)}))

  (b/get-bars bardb {:asset "EUR/USD"
                     :calendar [:forex :d]}
              (calendar-seq->window cal-seq))

  (b/get-bars bardb {:asset "EUR/USD"
                     :calendar [:forex :d]}
              (calendar-seq->window cal-seq-fixed))

  ;; test for one asset

  (load-aligned bardb {:asset "EUR/USD"
                       :calendar [:forex :d]}
                (calendar-seq->window cal-seq)
                (calendar-seq->date-ds cal-seq))

  (def r (load-aligned-assets
          bardb
          {:calendar [:forex :d]
           :import :kibot}
          ["EUR/USD"]
          cal-seq))

  (is-valid? r "EUR/USD")
  (get r "EUR/USD")

; 
  )
