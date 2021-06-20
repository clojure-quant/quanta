#!/bin/sh

rm -rf classes
mkdir classes
# clojure -e "(compile 'tech.v3.dataset)"

 clojure -e "(compile 'demo.aot)"
# clojure -e "(compile 'ta.backtest.core)"

