(ns demo.env.warehouse
  (:require
   [clojure.pprint]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [trace debug info warn error]]
   [ta.warehouse :as wh]
   [ta.data.alphavantage :as av]))

(def w (wh/init {:series "../db/"
                 :list "../resources/etf/"}))

(let [secret (-> "creds.edn" slurp edn/read-string :alphavantage)]
  (warn "alphavantage secret: " secret)
  (av/set-key! secret))

; secrets should not be saved in a notebook
;
; secret loads a key from user defined secrets.
; the current implementation does just read the file test/creds.edn
; in the future the notebook will save creds only in webbrowser local storage
; (av/set-key! (secret :alphavantage))

