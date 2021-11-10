(ns ta.tradingview.handler-storage
  (:require
   [clojure.walk]
   [taoensso.timbre :refer [trace debug info warnf error]]
   [cheshire.core :refer [parse-string generate-string]]
   [schema.core :as s]
   [ring.util.response :as res]
     ;[ring.util.http-response :refer [ok]]
   [webly.web.middleware :refer [wrap-api-handler]]
   [webly.web.handler :refer [add-ring-handler]]
   [reval.persist.protocol :refer [save loadr]]
   [reval.helper.id :refer [guuid-str]]
   [ta.tradingview.config :refer [tv-config]]
   [tick.core :as tick]
   [cljc.java-time.instant :as ti]
   [clojure.java.io :as io]))

(defn now-epoch []
  (-> (tick/now)
      (ti/get-epoch-second)))

(defn content->edn [{:keys [name symbol resolution content] :as data}]
  (let [content-edn (parse-string content true)
        ; {:content :legs :id
        ;  :symbol :name :symbol_type :exchange :listed_exchange :short_name :is_realtime :resolution 
        ;  :publish_request_id :description}
        _ (info "first extract: " (keys content-edn))
        content-edn (if-let [content (:content content-edn)]
                      (assoc content-edn :content (parse-string content true))
                      content-edn)
        content-edn (if-let [legs (:legs content-edn)]
                      (assoc content-edn :legs (into [] (parse-string legs true)))
                      content-edn)]
    (debug "content->edn: "  content-edn)
    (merge (dissoc data :content)
           {:content content-edn})))


(defn chart->json [data]
  (let [{:keys [charts layout legs]} data
        content (select-keys data [:publish_request_id
                                   :name
                                   :description
                                   :resolution
                                   :symbol_type
                                   :exchange
                                   :listed_exchange
                                   :symbol
                                   :short_name
                                   :is_realtime])
        content (if legs
                  (assoc content :legs (generate-string legs))
                  content)
        content-inner {:content (generate-string
                                 {:layout layout
                                  :charts charts})}
        content (merge content content-inner)]
    {:timestamp (:timestamp data)
     :name (:name data)
     :id (:id data)
     :content (generate-string content)}))


;; chart

(defn filename-chart  [client-id user-id chart-id]
  (str (:db-path tv-config) "chart_" client-id "_" user-id "_" chart-id ".edn"))

(defn save-chart
  [client-id user-id chart-id data]
  (let [{:keys [name symbol resolution content]} data
        data-edn (content->edn data)
        data-edn (assoc data-edn :timestamp (now-epoch))]
    (save :edn (filename-chart client-id user-id chart-id) data-edn)
    (info "saved chart id: " chart-id)))

(defn delete-chart [client-id user-id chart-id]
  (info "deleting: " (filename-chart client-id user-id chart-id)))

(defn load-chart [client-id user-id chart-id]
  (loadr :edn (filename-chart client-id user-id chart-id)))

;; explore

(defn dir? [filename]
  (-> (io/file filename) .isDirectory))

(defn split-filename [filename]
  (let [m (re-matches #"(.*)_(.*)_(.*)_(.*).edn" filename)
        [_ type client user chart] m]
    (when m
      {:type type
       :client-id (Integer/parseInt client)
       :user-id (Integer/parseInt user)
       :chart-id (Integer/parseInt chart)})))

(defn explore-dir [dir purpose]
  (let [dir (io/file dir)
        files (if (.exists dir)
                (into [] (->> (.list dir)
                              (remove dir?)
                              (map split-filename)
                              (remove nil?)
                              doall))
                (do
                  (warnf "path for: %s not found: %s" purpose dir)
                  []))]
    (debug "explore-dir: " files)
    ;(warn "type file:" (type (first files)) "dir?: " (dir? (first files)))
    files))

(defn user-files
  [client-id user-id]
  (fn [i]
    (and (= client-id (:client-id i))
         (= user-id (:user-id i)))))

(defn chart-summary [{:keys [client-id user-id chart-id]}]
  (let [chart (load-chart client-id user-id chart-id)]
    (merge {:chart_id (:chart chart)}
           (select-keys chart [:name :symbol :resolution :chart :timestamp]))))


(defn chart-list [client-id user-id]
  (let [client-id (if (string? client-id)  (Integer/parseInt client-id) client-id)
        user-id (if (string? user-id)  (Integer/parseInt user-id) user-id)]
    (info "chart list for: client: " client-id " user: " user-id)
    (->> (explore-dir (:db-path tv-config) "chart")
         (filter (user-files client-id user-id))
         (map chart-summary)
         (into []))))





(comment

  (split-filename "chart_77_77_1636524198.edn")
  (split-filename ".placeholder")
  (explore-dir "tvdb" "d")
  (chart-list 77 77)
  (chart-list "77" "77")
;  
  )




;; chart handler

(defn save-chart-handler [{:keys [query-params params] :as req}]
  (info "saving tradingview chart: " (keys req))
  (let [{:keys [client user chart]} (clojure.walk/keywordize-keys query-params)
        chart (if chart chart (now-epoch)) ; post request can contain chart id, or not
        {:keys [name symbol resolution content]} params]
    (save-chart client user chart params)
    (res/response {:status "ok"
                   :id chart})))

(defn modify-chart-handler [{:keys [query-params body]}]
  (let [{:keys [client user chart]} query-params
        {:keys [chart-data Chart]} body]
    (save-chart client user chart chart-data)
    (res/response {:status "ok"})))

(defn delete-chart-handler [{:keys [query-params]}]
  (let [{:keys [client user chart]} query-params]
    ; [client :- s/Int user :- s/Int {chart :- s/Int 0}]
    (delete-chart client user chart)
    (res/response {:status "ok"})))


(defn load-chart-handler
  "returns eithe chart-summary-list or chart-file"
  [{:keys [query-params]}]
  (let [{:keys [client user chart]} (clojure.walk/keywordize-keys query-params);  ;(coerce/coercer CommentRequest coerce/json-coercion-matcher)
        ]
    (info "load chart :" query-params)
    (if chart
      (if-let [data (load-chart client user chart)]
        (res/response {:status "ok" :data (format-chart data)})
        (res/response {:status "error" :error "chart not found"}))
      (if-let [chart-list (chart-list client user)]
        (res/response {:status "ok" :data chart-list})
        (res/response {:status "error" :error "chart not found"})))))




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