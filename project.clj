(defproject trateg "0.1.2-SNAPSHOT"
  :dependencies
  [;[org.clojure/clojure "1.10.1-RC1"]
   [org.clojure/clojure "1.10.1"]
  ;[org.clojure/core.async "0.4.490"]
   [org.clojure/core.async "1.0.567"]
   [com.taoensso/tufte "2.0.1"] ;performance tracking
   [medley "1.2.0"] ; lightweight, useful, mostly pure functions that are "missing" from clojure.core.
   [clj-time "0.14.3"] ; joda-time wrapper for clj (needed by bybit)
   ;[tick "0.4.17-alpha"] ; replacement for clj-time
   [cheshire "5.8.1"] ; JSON encoding
   [clj-http "3.10.0"]  ; http requests (bybit)                        
   [org.clojure/data.csv "1.0.0"] ; read/write csv
   [net.cgrand/xforms "0.18.2"] ; transducers for timeseries (ema sma)
   [org.ta4j/ta4j-core "0.12"] ; ta4j java technical indicator library
   [org.pinkgorilla/throttler "1.0.1"] ; throtteling (custom version, core.async upgrade)
   ; [com.stuartsierra/frequencies "0.1.0"]     ; percentile stats
   
   ]
  :plugins [[lein-ancient "0.6.15"]]
  :repl-options {:init-ns ta.model.single}
  :source-paths ["src" "dev"]
  :resource-paths ["resources"])
