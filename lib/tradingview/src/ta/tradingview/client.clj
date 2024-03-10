(ns ta.tradingview.client
  (:require
   [taoensso.timbre :refer [info warn error]]
   [clj-http.client :as client]
   [cheshire.core] ; JSON Encoding
   [ta.tradingview.db.chart :refer [save-chart-boxed delete-chart load-chart-boxed chart-list now-epoch chart-box chart-unbox]]))

(def roots {:demo "https://saveload.tradingview.com/1.1/charts"
            :ta "http://localhost:8000/api/tv/storage/1.1/charts"})

(defn get-raw [k chart-id process-fn]
  (let [qp {:client 77
            :user 77}]
    (-> (client/get (k roots)
                    {:accept :json
                     :query-params (if chart-id
                                     (assoc qp :chart chart-id)
                                     qp)})
        (:body)
        (cheshire.core/parse-string true) ; {:status :data}
        :data
        process-fn)))

(defn get-chart [k chart-id process-fn]
  (-> (client/get (k roots)
                  {:accept :json
                   :query-params {:client 77
                                  :user 77
                                  :chart chart-id}})
      (:body)
      (cheshire.core/parse-string true) ; {:status :data}
      :data ; (:id :name :timestamp :content)
      ;(select-keys [:id :name :timestamp])
      :content
      (cheshire.core/parse-string true)
      process-fn))

(defn show-keys-1 [data]
  (-> data
      keys
      sort))

(defn print-keys-1 [data]
  (-> data
      (dissoc :content)))

(defn print-chart [data]
  (-> data
      :content
      (cheshire.core/parse-string true)))

(defn print-legs [data]
  (-> data
      :content
      (cheshire.core/parse-string true)))

(get-chart :demo 722072 show-keys-1)
(get-chart :ta 1636578239 show-keys-1)

(= (get-chart :demo 722072 show-keys-1)
   (get-chart :ta 1636578239 show-keys-1))

; save demo chart from api from tradingview to local storage
(->> (get-chart :demo 722072 identity)
     (save-chart-boxed 77 77 722072))

(get-chart :demo 722072 print-legs)

(get-chart :demo 722072 print-keys-1)
(get-chart :ta 1636578239 print-keys-1)

(get-chart :demo 722072 print-chart)
(get-chart :ta 1636578239 print-chart)

;; how the load request compares
(-> (load-chart-boxed 77 77 1636578239)
    chart-unbox
    (dissoc :charts)
   ; keys
   ; sort
    )

(-> (get-raw :demo 722072 identity)
    chart-unbox
    (dissoc :charts)
     ;keys
     ;sort
    )

;; test if chart list is identical

(get-raw :demo nil identity)

(get-raw :ta nil identity)

