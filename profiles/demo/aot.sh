#!/bin/sh

rm -rf classes
mkdir classes
clojure -e "(compile 'tech.v3.dataset)"