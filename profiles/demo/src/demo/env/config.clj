(ns demo.env.config
  (:require
   [clojure.pprint]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [warn]]
  
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

(wh/init {:list "../resources/etf/"
          :series  {:crypto "../db/crypto/"
                    :stocks "../db/stocks/"
                    :random "../db/random/"
                    :shuffled  "../db/shuffled/"}})


