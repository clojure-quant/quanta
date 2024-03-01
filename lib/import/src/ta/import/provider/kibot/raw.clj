(ns ta.import.provider.kibot.raw
  (:require
   [clojure.string :as str]
   [clojure.set]
   [taoensso.timbre :refer [info warn error]]
   [clojure.edn :as edn]
   [clj-http.client :as http]
   [cheshire.core :as cheshire] ; JSON Encoding
   [throttler.core]))

;; KIBOT FTP:
;; ftp.kibot.com   (same user+password as for api)
;; guix install filezilla  (ftp client)
;; .exe files are rar files.
;; unrar e  /home/florian/20231120.rar
;; op<path>      Set the output path for extracted files
;;  unrar e -op./csv/stock ./stock/20230711.exe

;; guix install unrar
;; rar file contains a lot of txt files

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

api-key

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
                          :socket-timeout 30000
                          :connection-timeout 30000})
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
    (info "login user: " user)
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
                   opts))))

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

  (history {:symbol "AAPL"
            :interval 60
            :period 1})

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
            :enddate "2023-09-20"
            :timezone "UTC"
            :splitadjusted 1})

  (history {:type "forex"
            :symbol "EURUSD"
            :startdate "2023-09-01"
            :interval "daily"
            :timezone "UTC"})

  (history {:type "forex",
            :symbol "EURUSD",
               ;:startdate "2023-09-01",
            :period 1
            :interval "1" ; "daily"
            :timezone "UTC"})

  (history {:symbol "JY"
            :type "futures" ; Can be stocks, ETFs forex, futures.
            :interval "daily" ; 5 ; 5 minute bars
            :period 5 ; number of days going back
            :timezone "UTC"})
  ;; the following error happens if we want 1 day back, but 1 day back is a holiday.
  ;; => {:error {:code "405", :title "Data Not Found.", :message "No data found for the specified period for JY."}}
  ;; => "11/20/2023,0.0067125,0.006782,0.0066965,0.006772,245135\r\n11/21/2023,0.0067695,0.0068235,0.006757,0.0067695,234151\r\n11/22/2023,0.006769,0.0067805,0.0067015,0.006709,219589\r\n"

  (history {:symbol "JY"
            :type "futures" ; Can be stocks, ETFs forex, futures.
            :interval "1" ; 1 ; 5 ; 5 minute bars
            :period 10 ; number of days going back
            :timezone "UTC"})

  (-> (slurp "../resources/symbollist/futures-kibot.edn")
      (edn/read-string)
      count)
   ;; => 83

;  
  )

; url request only (used in symbol-list)
(defn make-request-url [url]
  (let [result (http/get url
                         {:socket-timeout 3000
                          :connection-timeout 3000})
        body (:body result)
        error (extract-error body)]
    ;(info "status:" (:status result))  
    ;(info "headers: " (:headers result))
    (if error
      {:error error}
      body)
    ;  (throw (ex-info (:retMsg result) result))
    )

;  
  )




