(defproject org.pinkgorilla/ta "0.1.14-SNAPSHOT"
  :license {:name "MIT"}
  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/release_username
                                     :password :env/release_password
                                     :sign-releases false}]]
  :release-tasks [["vcs" "assert-committed"]
                  ["bump-version" "release"]
                  ["vcs" "commit" "Release %s"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["deploy"]
                  ["bump-version"]
                  ["vcs" "commit" "Begin %s"]
                  ["vcs" "push"]]
  :dependencies
  [[org.clojure/clojure "1.10.1"]
   [org.clojure/core.async "1.1.582"]
   [com.taoensso/tufte "2.1.0"] ;performance tracking
   [medley "1.3.0"] ; lightweight, useful, mostly pure functions that are "missing" from clojure.core.
   [clj-time "0.15.2"] ; joda-time wrapper for clj (needed by bybit)
   [tick "0.4.17-alpha"] ; replacement for clj-time
   [cheshire "5.10.0"] ; JSON encoding
   [clj-http "3.10.0"]  ; http requests (bybit)                        
   [org.clojure/data.csv "1.0.0"] ; read/write csv
   [net.cgrand/xforms "0.19.2"] ; transducers for timeseries (ema sma)
   [org.ta4j/ta4j-core "0.12"] ; ta4j java technical indicator library
   [throttler "1.0.0" ; api rate-limits 
    :exclusions  [[org.clojure/clojure]
                  [org.clojure/core.async]]]; has very old core.async
   ;[dataset-tools "0.1.12"] ; not working - opened ticket

   ; [com.stuartsierra/frequencies "0.1.0"]     ; percentile stats
   ]
  
  :plugins [[lein-ancient "0.6.15"]]
  
  :source-paths ["src"]
  :resource-paths ["resources"]
  :repl-options {:init-ns ta.model.single}
  
  :profiles {:speed
             ; run performance tests
             {:source-paths ["profiles/speed/src"]
              :main ^:skip-aot speed.main}

             :notebook 
             ; run the pink-gorilla notebook (standalone, or in repl)
             ; important to keep this dependency in here only, as we do not want to
             ; bundle the notebook (big bundle) into our neat library       
             {:source-paths ["profiles/notebook/src"]
              :main ^:skip-aot notebook.main
              :dependencies [[org.pinkgorilla/gorilla-notebook "0.4.12-SNAPSHOT"]]
              :repl-options {:welcome (println "Profile: notebook.")
                             :init-ns notebook.main  ;; Specify the ns to start the REPL in (overrides :main in this case only)
                             :init (start) ;; This expression will run when first opening a REPL, in the namespace from :init-ns or :main if specified.
                             }}

             :dev   
             {:dependencies [[clj-kondo "2019.11.23"]]
              :plugins      [[lein-cljfmt "0.6.6"]
                             [lein-cloverage "1.1.2"]]
              :aliases      {"clj-kondo" ["run" "-m" "clj-kondo.main"]}
              :cloverage    {:codecov? true
                                  ;; In case we want to exclude stuff
                                  ;; :ns-exclude-regex [#".*util.instrument"]
                                  ;; :test-ns-regex [#"^((?!debug-integration-test).)*$$"]
                             }
                   ;; TODO : Make cljfmt really nice : https://devhub.io/repos/bbatsov-cljfmt
              :cljfmt       {:indents {as->                [[:inner 0]]
                                       with-debug-bindings [[:inner 0]]
                                       merge-meta          [[:inner 0]]
                                       try-if-let          [[:block 1]]}}}}

  :aliases {"bump-version"
            ["change" "version" "leiningen.release/bump-version"]

            "speed" ^{:doc "Runs performance tests"}
            ["with-profile" "+speed" "run" "-m" "speed.main"]

            "notebook" ^{:doc "Runs pink-gorilla notebook"}
            ["with-profile" "+notebook" "run" "-m" "notebook.main"]

            "lint" ^{:doc "Runs code linter"}
            ["clj-kondo" "--lint" "src"]})
