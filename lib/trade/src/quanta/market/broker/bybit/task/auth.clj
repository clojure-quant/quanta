(ns quanta.market.broker.bybit.task.auth
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [missionary.core :as m]
   [buddy.core.codecs :as crypto.codecs] ; authentication
   [buddy.core.mac :as crypto.mac]
   [quanta.market.broker.bybit.connection :refer [send-msg! connection-start!]]
   [quanta.market.broker.util :refer [first-match]]))

(defn- sign
  [to-sign key-secret]
  (-> (crypto.mac/hash to-sign {:key key-secret :alg :hmac+sha256})
      (crypto.codecs/bytes->hex)))

(defn auth-msg [api-key api-secret]
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

(defn authenticate! [conn {:keys [account]}]
  (m/sp
     (let [{:keys [api-key api-secret]} account]
     (info "auth! api-key: " api-key)
     (send-msg! conn (auth-msg api-key api-secret))
     (let [auth-result (m/? (first-match auth-respose? (:msg-flow conn)))]
       (info "auth result: " auth-result)
       auth-result))))



(defn secure-connection
  [{:keys [account] :as opts}]
  (m/ap
   (info "creating secure account")
   (let [c (m/?> (connection-start! opts))]
     (if c
       (m/? (authenticate! c account))
       (error "secure-connection could not get stream!"))
     (info "returning c")
     c)))





