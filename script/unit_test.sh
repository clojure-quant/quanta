#!/bin/sh

echo "unit test ta/warehouse"
cd profiles/warehouse && clj -M:test && cd ../..

echo "unit test ta/tradingview"
cd profiles/tradingview && clj -M:test && cd ../..

echo "unit test ta/backtest"
clj -M:test