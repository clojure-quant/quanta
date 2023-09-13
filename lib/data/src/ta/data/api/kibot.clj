(ns ta.data.api.kibot
  (:require
   [clojure.string :as str]
   [clojure.set]
   [taoensso.timbre :refer [info warn error]]
   [clojure.edn :as edn]
   [clj-http.client :as http]
   [cheshire.core :as cheshire] ; JSON Encoding
   [throttler.core]))

; available symbols:

; us etf:
; http://www.kibot.com/Files/2/All_ETFs_Intraday.txt

; us stocks:
; http://www.kibot.com/Files/2/All_Stocks_Intraday.txt

; us futures
; http://www.kibot.com/Files/2/Futures_tickbidask.txt

; forex
; http://www.kibot.com/Files/2/Forex_tickbidask.txt


; dividends/splits:
; Request URL
; http://api.kibot.com?action=adjustments&symbol=[symbol]&startdate=[startdate]&enddate=[enddate]&splitsonly=[splitsonly]&dividendsonly=[dividendsonly]&symbolsonly=[symbolsonly]
;
; Response
;The server returns TAB separated values with the first line defining the fields and their order. Here is an example:
; Date Symbol Company Action Description
; 2/16/2010 MSFT Microsoft Corp. 0.1300 Dividend
; 5/18/2010 MSFT Microsoft Corp. 0.1300 Dividend

; ftp://hoertlehner%40gmail.com:PWD@ftp.kibot.com/



;; ApiKey Management

(defonce api-key (atom {:user "guest" :password "guest"}))

(defn set-key!
  "to use api, call at least once set-key! api-key"
  [key]
  (warn "setting kibot key..")
  (reset! api-key key)
  nil ; Important not to return by chance the key, as this would be shown in the repl.
  )

; 

(defn extract-error [body]
  (if-let [match (re-matches #"^(\d\d\d)\s(.*)\r\n(.*)" body)]
    (let [[full error-code error-title error-message] match]
      {:code error-code
       :title error-title
       :message error-message})
    nil))

(comment 
  
  (extract-error "asdfasdfasdf")
  (extract-error "405 Data Not Found.\r\nNo data found for the specified period for EURUSD.")
  
  
 ; 
  )


(defn make-request [url query-params]
  (let [result (http/get url
                         {:accept :json
                          :query-params query-params
                          :socket-timeout 3000 
                          :connection-timeout 3000})
        body (:body result)
        error (extract-error body)]
    ;(info "status:" (:status result))  
    ;(info "headers: " (:headers result))
    (if error
        {:error error}
      body)
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
    ;(info "login user: " user "pwd: " password)
    (info "kibot history: " opts)
    (make-request base-url
                  (merge 
                     {:action "history"
                      :user user
                      :password password} 
                      opts)
                  )))




(defn snapshot [opts]
  (let [{:keys [user password]} @api-key]
    ;(info "login user: " user "pwd: " password)
    (info "kibot snapshot: " opts)
    (make-request base-url
                  (merge
                   {:action "snapshot"
                    :user user
                    :password password}
                   opts))))

; This example will work even if you do not have a subscription:
; http://api.kibot.com/?action=snapshot&symbol=$NDX,AAPL
; return format: Symbol,Date,Time,LastPrice,LastVolume,Open,High,Low,Close,Volume,ChangePercent,TimeZone.

(comment 
  
  (snapshot {:symbol ["$NDX" "AAPL"]})
  
  (snapshot {:type "future"
             :symbol "ESZ23"})

  (snapshot {:type "future"
             :symbol "JYZ23"})
  

  (snapshot {:symbol ["$NDX" 
                      "AAPL" 
                      "FCEL"
                      "MSFT" 
                      #_"BZ0"]})

  (snapshot {:symbol ["AAPL" "DAX0" "MSFT"]})

  ;
  )




(comment 
  
   (history {:symbol "AAPL"
             :interval "daily"
             :period 10})


   (history {:symbol "SIL" ; SIL - ETF
             :interval "daily"
             :period 1
             :type "ETF" ; Can be stocks, ETFs forex, futures.
             :timezone "UTC"
             :splitadjusted 1})

   ; futures
   ; http://www.kibot.com/historical_data/Futures_Historical_Tick_with_Bid_Ask_Data.aspx

   (history {:symbol "SIL" ; SIL - FUTURE
             :type "futures" ; Can be stocks, ETFs forex, futures.
             :interval "daily"
             :period 1
             :timezone "UTC"
             :splitadjusted 1})
   
(history {:symbol "SIL" ; SIL - FUTURE
          :type "futures" ; Can be stocks, ETFs forex, futures.
          :interval "daily"
          :period 1
          :timezone "UTC"
          :splitadjusted 1})

   
   (history {:symbol "SIL" ; SIL - FUTURE
             :type "futures" ; Can be stocks, ETFs forex, futures.
             :interval "daily"
             :startdate "2023-09-01"
             :timezone "UTC"
             :splitadjusted 1})

   (history {:type "forex", 
             :symbol "EURUSD", 
             :startdate "2023-09-01", 
             :interval "daily", 
             :timezone "UTC", 
             :splitadjusted 1})
   ;; => "405 Data Not Found.\r\nNo data found for the specified period for EURUSD."

   
   


   (-> (slurp "../resources/symbollist/futures-kibot.edn")
       (edn/read-string)
       count)
   ;; => 83
  



;  
  )


