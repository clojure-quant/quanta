(ns ta.data.kibot
  (:require
   [clojure.string :as str]
   [clojure.set]
   [taoensso.timbre :refer [info warn error]]
   [charred.api :as charred]
   [clojure.edn :as edn]
   [clj-http.client :as http]
   [cheshire.core :as cheshire] ; JSON Encoding
   [throttler.core]
   [ta.helper.date :refer [parse-date]]
   [ta.data.helper :refer [str->float]]))


;; ApiKey Management

(defonce api-key (atom {:user "guest" :password "guest"}))

(defn set-key!
  "to use api, call at least once set-key! api-key"
  [key]
  (info "setting kibot key..")
  (reset! api-key key)
  nil ; Important not to return by chance the key, as this would be shown in the repl.
  )


(defn make-request [url query-params]
  (let [result (-> (http/get url
                             {:accept :json
                              :query-params query-params
                              :socket-timeout 3000 
                              :connection-timeout 3000
                              })
                   (:body)
                   ;(cheshire/parse-string true)
                   )]
    result
    ;  (throw (ex-info (:retMsg result) result))
    ))

; http://api.kibot.com?action=login&user=guest&password=guest
(set-key! {:user "guest" :password "guest"})

(def base-url "http://api.kibot.com")

(defn login []
  (let [{:keys [user password]} @api-key]
     (info "loing user: " user "pwd: " password)
     (make-request base-url 
                 {:action "login"
                 :user user
                 :password password})))
  
(defn status []
    (make-request base-url
                  {:action "status"}))

(comment 
  (login)
  (status) 
  ;
  )

(defn history [opts]
  (let [{:keys [user password]} @api-key]
    (info "login user: " user "pwd: " password)
    (make-request base-url
                  (merge 
                     {:action "history"
                      :user user
                      :password password} 
                      opts)
                  )))

(history {:symbol "AAPL"
          :interval "daily"
          :period 10
          })


(history {:symbol "SIL" ; SIL - ETF
          :interval "daily"
          :period 1
          :type "ETF" ; Can be stocks, ETFs forex, futures.
          :timezone "UTC"
          :splitadjusted 1
          })


; futures
; http://www.kibot.com/historical_data/Futures_Historical_Tick_with_Bid_Ask_Data.aspx

(history {:symbol "SIL" ; SIL - FUTURE
          :interval "daily"
          :period 1
          :type "futures" ; Can be stocks, ETFs forex, futures.
          :timezone "UTC"
          :splitadjusted 1})



(comment 
   (-> (slurp "../resources/symbollist/futures-kibot.edn")
       (edn/read-string)
       count)
   ;; => 83

;  
  )


