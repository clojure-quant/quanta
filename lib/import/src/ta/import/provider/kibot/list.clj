(ns ta.import.provider.kibot.list
  (:require
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.argops :as argops]
   [tablecloth.api :as tc]
   [ta.import.provider.kibot.raw :as kibot]
   [ta.import.provider.kibot.ds :refer [string->stream]]
   ))

;; symbollist 

(defn kibot-symbollist->dataset [tsv skip col-mapping]
  (->
   (tds/->dataset (string->stream tsv)
                  {:file-type :tsv
                   :header-row? true
                   :n-initial-skip-rows skip
                   :dataset-name "kibot-symbollist"})
   (tc/rename-columns col-mapping)))

(def list-mapping
  {:stocks {:url "http://www.kibot.com/Files/2/All_Stocks_Intraday.txt"
            :skip 5
            :cols {"column-0" :#
                   "column-1" :symbol
                   "column-2" :date-start
                   "column-3" :size-mb
                   "column-4" :desc
                   "column-5" :exchange
                   "column-6" :industry
                   "column-7" :sector}}
   :etf {:url  "http://www.kibot.com/Files/2/All_ETFs_Intraday.txt"
         :skip 5
         :cols {"column-0" :#
                "column-1" :symbol
                "column-2" :date-start
                "column-3" :size-mb
                "column-4" :desc
                "column-5" :exchange
                "column-6" :industry
                "column-7" :sector}}
   :futures {:url "http://www.kibot.com/Files/2/Futures_tickbidask.txt"
             :skip 4
             :cols {"column-0" :#
                    "column-1" :symbol
                    "column-2" :symbol-base
                    "column-3" :date-start
                    "column-4" :size-mb
                    "column-5" :desc
                    "column-6" :exchange}}
   :forex {:url "http://www.kibot.com/Files/2/Forex_tickbidask.txt"
           :skip 3
           :cols {"column-0" :#
                  "column-1" :symbol
                  "column-2" :date-start
                  "column-3" :size-mb
                  "column-4" :desc}}})

(defn download-symbollist [category]
  (kibot/make-request-url (-> list-mapping category :url)))

(defn row-delisted [ds-data]
  (-> (argops/argfilter #(= "Delisted:" %) (:# ds-data))
      first))

(defn filter-delisted-rows [ds-data]
  (let [idx-delisted (row-delisted ds-data)]
    (if idx-delisted
      (tc/select-rows ds-data (range idx-delisted))
      ds-data)))

(defn filter-empty-rows [ds-data]
  (tc/select-rows ds-data (comp #(not (nil? %)) :#)))

(defn parse-list [t tsv]
  (let [{:keys [skip cols]} (t list-mapping)
        ds-data (kibot-symbollist->dataset tsv skip cols)]
    (-> ds-data
        filter-delisted-rows
        filter-empty-rows)))

(defn symbol-list [t]
  (let [tsv (download-symbollist t)]
    (parse-list t tsv)))

(comment

  (def tsv-etf (download-symbollist :etf))
  (def ds-etf (parse-list :etf tsv-etf))
  ds-etf
  (:date-start ds-etf)

  (tc/row-count ds-etf)
  ; max #: 1667
  ; listed:    2889
  ; delisted:  1667
  ; all        4556
  ; row-count: 4562
  ; diff          6

  (symbol-list :etf)

;  
  )
