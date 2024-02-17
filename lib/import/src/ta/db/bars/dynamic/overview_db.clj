(ns ta.db.bars.dynamic.overview-db
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [tick.core :as t]
   [clojure.java.io :as io]
   [datahike.api :as d]))

(def overview-schema
  [{:db/ident :asset
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :market
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one}
   {:db/ident :interval
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one}
   {:db/ident :start
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one}
   {:db/ident :end
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one}])

(defn- cfg [path]
  {:store {:backend :file ; backends: in-memory, file-based, LevelDB, PostgreSQL
           :path path}
   :keep-history? false
   :schema-flexibility :write  ;default - strict value types need to be defined in advance. 
   ;:schema-flexibility :read ; transact any  kind of data into the database you can set :schema-flexibility to read
   :initial-tx overview-schema ; commit a schema
  })

(defn- create! [cfg]
  (warn "creating datahike db..")
  (d/delete-database cfg)
  (d/create-database cfg)
  (d/connect cfg))

(defn start-overview-db [path] 
  (let [cfg (cfg path)
        db-filename (get-in cfg [:store :path])]
    (info "starting datahike-overview-db at path: " db-filename)
    (if (.exists (io/file db-filename))
      (d/connect cfg)
      (create! cfg))))

(defn stop-overview-db [conn] 
  (when conn
    (info "disconnecting from datahike..")
    (d/release conn)))

(defn available-range 
  "returns a map {:start :end} or nil."
  [conn {:keys [asset calendar]}]
  (let [[market interval] calendar]
  (d/q '[:find (pull ?i [:start :end]) .
         :in $ asset market interval
         :where [?i :asset asset]
                [?i :market market]
                [?i :interval interval]]
        @conn asset market interval)))

(defn- find-id
  "returns the id of an existing entry or nil."
  [conn {:keys [asset calendar]}]
  (let [[market interval] calendar]
    (d/q '[:find  ?i .
           :in $ asset market interval
           :where [?i :asset asset]
                  [?i :market market]
                  [?i :interval interval]]
         @conn asset market interval)))

(defn- key-as-inst [m key]
  (if-let [d (key m)] 
    (assoc m key (t/inst d))
    m))

(defn- ensure-inst [range]
  (-> range
      (key-as-inst :start)
      (key-as-inst :end)))


(defn update-range [conn {:keys [asset calendar] :as opts} range]
  (let [id (find-id conn opts)
        range (ensure-inst range)
        tx (if id 
             (assoc range :db/id id)
             (let [[market interval] calendar]
               (merge range {:asset asset
                             :market market 
                             :interval interval})))]
    (info "overview tx: " tx)
    (d/transact conn [tx])))
  
  
(comment 
  (def conn (start-overview-db "/tmp/datahike-overview"))
  conn

  (require '[tick.core :as t])

  (d/transact conn [{:asset "QQQ"
                     :market :us
                     :interval :d
                     :start (t/inst)
                     :end (t/inst)
                    }])

  (available-range conn {:asset "QQQ"
                         :calendar [:us :d]})
  (available-range conn {:asset "ORCL"
                         :calendar [:us :d]})

  (find-id conn {:asset "QQQ"
                 :calendar [:us :d]})
   
   (find-id conn {:asset "ORCL"
                  :calendar [:us :d]})
   

  (ensure-inst {})
  (ensure-inst {:start (t/date-time)})
  (ensure-inst {:start (t/date-time)
                :end (t/instant)})
  

   (update-range conn {:asset "ORCL"
                       :calendar [:us :d]} {:start (t/inst)})
  
   (update-range conn {:asset "ORCL"
                      :calendar [:us :d]} {:end (t/inst)})

  
 ; 
  )
