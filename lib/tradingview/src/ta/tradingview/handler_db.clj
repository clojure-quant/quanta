(ns ta.tradingview.handler-storage
  (:require
   [clojure.walk]
   [taoensso.timbre :refer [trace debug info warnf error]]
   [schema.core :as s]
   [ring.util.response :as res]
   [modular.webserver.middleware.api :refer [wrap-api-handler]]
   [modular.webserver.handler.registry :refer [add-ring-handler]]
   [ta.tradingview.db-ts :refer [save-chart-boxed delete-chart load-chart-boxed chart-list now-epoch]]))

;; chart handler

(defn save-chart-handler [{:keys [query-params params] :as req}]
  (info "saving tradingview chart: " (keys req))
  (let [{:keys [client user chart]} (clojure.walk/keywordize-keys query-params)
        ; post request can contain chart id, or not
        chart (if chart chart (now-epoch))]
    (save-chart-boxed client user chart params)
    (res/response {:status "ok"
                   :id chart})))

(defn modify-chart-handler [{:keys [query-params body]}]
  (let [{:keys [client user chart]} query-params
        {:keys [chart-data Chart]} body]
    (info "modifying tradingview chart: " client user chart)
    (save-chart-boxed client user chart chart-data)
    (res/response {:status "ok"})))

(defn delete-chart-handler [{:keys [query-params]}]
  (let [{:keys [client user chart]} query-params]
    ; [client :- s/Int user :- s/Int {chart :- s/Int 0}]
    (info "deleting tradingview chart: " client user chart)
    (delete-chart client user chart)
    (res/response {:status "ok"})))

(defn load-chart-handler
  "returns eithe chart-summary-list or chart-file"
  [{:keys [query-params]}]
  (let [{:keys [client user chart]} (clojure.walk/keywordize-keys query-params);  ;(coerce/coercer CommentRequest coerce/json-coercion-matcher)
        ]
    (info "load chart :" query-params)
    (if chart
      (if-let [data (load-chart-boxed client user chart)]
        (res/response {:status "ok" :data data})
        (res/response {:status "error" :error "chart for user not found."}))
      (if-let [chart-list (chart-list client user)]
        (res/response {:status "ok" :data chart-list})
        (res/response {:status "error" :error "chart-list for user failed."})))))

(comment
  {"status" "ok"
   "data" [{"id" 888
            "name" "autogen gann1"
            "symbol" "MCD"
            "resolution" "D"
            "timestamp" 1636565215}]}
  ;
  )
;; template

#_(defn load-template
    ([db client-id user-id] ; LIST
     (-> (mc/find-maps db "tvtemplate"
                       {:client_id client-id :user_id user-id}
                       {:_id 0 :name 1})))
    ([db client-id user-id chart-id] ; ONE
     (mc/find-one-as-map db "tvtemplate"
                         {:client_id client-id :user_id user-id :_id chart-id}
                         {:_id 0 :name 1 :content 1})))

; POST REQUEST: charts_storage_url/charts_storage_api_version/charts?client=client_id&user=user_id&chart=chart_id

#_(defn save-template
    [db client_id user_id data]
    (let [query {:client_id client_id :user_id user_id :name (:name data)}
          doc (merge data query)]
      (mc/update db "tvtemplate" query doc {:upsert true})
      nil))

#_(defn modify-template--unused
    [db client_id user_id chart_id data]
    (let [query {:client_id client_id :user_id user_id :chart_id chart_id}
          doc (merge data query)
          doc (merge doc {:timestamp (t/now)})]
      (mc/update db "tvtemplate" query doc {:upsert false})))

#_(defn delete-template
    [db client_id user_id name]
    (mc/remove db "tvtemplate"
               {:client_id client_id :user_id user_id :name name}))

(add-ring-handler :tv-db/save-chart (wrap-api-handler save-chart-handler))
(add-ring-handler :tv-db/modify-chart (wrap-api-handler modify-chart-handler))
(add-ring-handler :tv-db/delete-chart (wrap-api-handler delete-chart-handler))
(add-ring-handler :tv-db/load-chart (wrap-api-handler load-chart-handler))

(defn load-template-handler
  "returns eithe chart-template-list or chart-template"
  [{:keys [query-params]}]
  (let [{:keys [client user chart]} (clojure.walk/keywordize-keys query-params);  ;(coerce/coercer CommentRequest coerce/json-coercion-matcher)
        ]
    (info "load template :" query-params)
    (if chart
      (if-let [template-data [] ;(load-chart-boxed client user chart)
               ]
        (res/response {:status "ok" :data template-data})
        (res/response {:status "error" :error "template not found."}))
      (if-let [template-list [] ; (chart-list client user)
               ]
        (res/response {:status "ok" :data template-list})
        (res/response {:status "error" :error "template-list not found."})))))

(add-ring-handler :tv-db/load-template (wrap-api-handler load-template-handler))


  