(ns demo.env.config
  (:require
   [clojure.pprint]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [trace debug info warn error]]
   [webly.log]
   [ta.warehouse :as wh]
   [ta.data.alphavantage :as av]))

(let [secret (-> "creds.edn" slurp edn/read-string :alphavantage)]
  (warn "alphavantage secret: " secret)
  (av/set-key! secret))

; secrets should not be saved in a notebook
;
; secret loads a key from user defined secrets.
; the current implementation does just read the file test/creds.edn
; in the future the notebook will save creds only in webbrowser local storage
; (av/set-key! (secret :alphavantage))

(def w-crypto (wh/init {:series "../db/crypto/"
                        :list "../resources/etf/"}))

(def w-stocks (wh/init {:series "../db/stocks/"
                        :list "../resources/etf/"}))

(def w-random (wh/init {:series "../db/random/"
                        :list "../resources/etf/"}))

(def w-shuffled (wh/init {:series "../db/shuffled/"
                        :list "../resources/etf/"}))


(defn log-config! []
  (webly.log/timbre-config!
   {:timbre-loglevel
    [[#{"pinkgorilla.nrepl.client.connection"} :info]
     [#{"org.eclipse.jetty.*"} :info]
     [#{"webly.*"} :info]
     [#{"*"} :info]]}))
