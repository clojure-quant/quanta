#!/bin/bash

cd lib/indicator && clojure -X:test
cd ../calendar && clojure -X:test