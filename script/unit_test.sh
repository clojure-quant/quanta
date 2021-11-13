#!/bin/sh

cd profiles/warehouse && clj -M:test

# echo "PWD: " $PWD
cd ../..

cd profiles/tradingview && clj -M:test
