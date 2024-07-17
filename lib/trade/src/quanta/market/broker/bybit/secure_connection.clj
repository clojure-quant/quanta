(ns quanta.market.broker.bybit.secure-connection
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [missionary.core :as m]
   [buddy.core.codecs :as crypto.codecs] ; authentication
   [buddy.core.mac :as crypto.mac]
   [quanta.market.broker.bybit.connection :refer [connection3 send-msg! get-and-keep-connection]]
   [quanta.market.broker.util :refer [first-match next-value always]]
   [nano-id.core :refer [nano-id]]))

(defn- sign
  [to-sign key-secret]
  (-> (crypto.mac/hash to-sign {:key key-secret :alg :hmac+sha256})
      (crypto.codecs/bytes->hex)))

(defn auth-msg [{:keys [api-key api-secret]}]
  (let [millis (System/currentTimeMillis)
        expires (+ millis (* 1000 60 5)) ; 5 min
        to-sign (str "GET/realtime" expires)
        signature (sign to-sign api-secret)]
    {"op" "auth"
     "args" [api-key
             expires
             signature]}))

(def test-msg-auth-success
  {:retCode 0
   :retMsg "OK"
   :connId "cpv85t788smd5eps8ncg-2tfk"
   :op "auth"
   :reqId "0MczApsc" ; optional
   })

(defn auth-respose? [{:keys [op]}]
  (= op "auth"))

(defn authenticate! [conn account]
  (m/sp
     (info "sending auth message! ")
     (send-msg! conn (auth-msg account))
     (let [auth-result (m/? (first-match auth-respose? (:msg-flow conn)))]
       (info "auth result: " auth-result)
       auth-result)))

(defn secure-connection
  [{:keys [account] :as opts}]
  (m/ap
   (info "creating secure account")
   (let [c (m/?> (connection3 opts))]
     (if c
       (m/? (authenticate! c account))
       (error "secure-connection could not get stream!"))
     (info "returning c")
     c)))




(defn req [conn msg]
  (m/sp
   (let [kconn conn ; (get-and-keep-connection conn 15000)
         _ (info "waiting for req connection.")
         c (m/? (next-value kconn))
         id (nano-id 8)
         p-reqId (fn [{:keys [reqId]}]
                   (= id reqId))
         result (first-match p-reqId (:msg-flow c))
         msg (assoc msg :reqId id)]
     (info "making request")
     (send-msg! c msg)
     (info "waiting for result")
     (m/? result))))



