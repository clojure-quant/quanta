#!/bin/bash

# this script is used in github-ci

cd lib/indicator && clojure -X:test
cd ../calendar && clojure -X:test
cd ../trade && clojure -X:test