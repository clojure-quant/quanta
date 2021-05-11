(defproject org.pinkgorilla/ta "0.1.17-SNAPSHOT"
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
  [; conflict resolution: 
   [org.slf4j/slf4j-api "2.0.0-alpha1"]
   [org.clojure/tools.cli "1.0.206"]
   [borkdude/sci "0.2.5"]
   [org.ow2.asm/asm "9.1"]
   [org.clojure/tools.reader "1.3.5"]
   [org.apache.httpcomponents/httpcore "4.4.14"]
   [commons-codec "1.15"]
   [net.cgrand/macrovich "0.2.1"]
   [com.fasterxml.jackson.core/jackson-core "2.12.3"]
   [org.pinkgorilla/notebook-bundel "0.5.4"]
   ;

   
   [org.clojure/clojure "1.10.3"]
   [org.clojure/core.async "1.3.618"]

   [medley "1.3.0"] ; lightweight, useful, mostly pure functions that are "missing" from clojure.core.
   [clj-time "0.15.2"] ; joda-time wrapper for clj (needed by bybit)
   [tick "0.4.17-alpha"] ; replacement for clj-time
   [cheshire "5.10.0"] ; JSON encoding
   [clj-http "3.12.1"]  ; http requests (bybit)                        
   [org.clojure/data.csv "1.0.0"] ; read/write csv
   [net.cgrand/xforms "0.19.2"] ; transducers for timeseries (ema sma)
   [org.ta4j/ta4j-core "0.14"] ; ta4j java technical indicator library
   [throttler "1.0.0" ; api rate-limits 
    :exclusions  [[org.clojure/clojure]
                  [org.clojure/core.async]]]; has very old core.async
   ;[dataset-tools "0.1.12"] ; not working - opened ticket
   ; [com.stuartsierra/frequencies "0.1.0"]     ; percentile stats
   ;[clj-python/libpython-clj "1.38"]

   ; tech.ml.dataset cannot be loaded via pomegranate
   ;[techascent/tech.datatype "5.0-beta-2"]
   ;[techascent/tech.resource "4.6"]
   ;[techascent/tech.ml.dataset "2.0-beta-22"]
   ]

  :plugins [[lein-ancient "0.6.15"]
            ;[org.pinkgorilla/lein-pinkgorilla "0.0.17"]
            ]

  :source-paths ["src"]
  :resource-paths ["resources"]
  :repl-options {:init-ns ta.model.single}

  :jvm-opts ["-Djdk.attach.allowAttachSelf"
             "-XX:+UnlockDiagnosticVMOptions"
             "-XX:+DebugNonSafepoints"]

  ;:pinkgorilla {:runtime-config "./profiles/notebook/config.edn"}

  :profiles {:speed {; run performance tests
                     :dependencies [[com.taoensso/tufte "2.2.0"] ;performance tracking         
                                    ]
                     :source-paths ["profiles/speed/src"]
                     :main ^:skip-aot speed.main}

             :notebook {:source-paths ["profiles/notebook/src"]
                        :main ^:skip-aot notebook.main
                        :dependencies [[org.pinkgorilla/notebook-bundel "0.5.4"]]
                        :repl-options {:init-ns notebook.main  ;; Specify the ns to start the REPL in (overrides :main in this case only)
                                       :init (start) ;; This expression will run when first opening a REPL, in the namespace from :init-ns or :main if specified.
                                       }}

             :dev {:dependencies [[clj-kondo "2021.04.23"]]
                   :plugins      [[lein-cljfmt "0.6.6"]
                                  [lein-cloverage "1.1.2"]]
                   :aliases      {"clj-kondo" ["run" "-m" "clj-kondo.main"]}
                   :cloverage    {:codecov? true}
                   :cljfmt       {:indents {as->                [[:inner 0]]
                                            with-debug-bindings [[:inner 0]]
                                            merge-meta          [[:inner 0]]
                                            try-if-let          [[:block 1]]}}}}

  :aliases {"bump-version"
            ["change" "version" "leiningen.release/bump-version"]

            "speed" ^{:doc "Runs performance tests"}
            ["with-profile" "+speed" "run" "-m" "speed.main"]

            "notebook" ^{:doc "Runs pink-gorilla notebook"}
            ["with-profile" "+notebook"
             "run" "-m" "notebook.main"]

            "lint" ^{:doc "Runs code linter"}
            ["clj-kondo" "--lint" "src"]})
