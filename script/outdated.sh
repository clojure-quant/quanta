#!/bin/sh

echo "outdated for ta/backtest"
clj -M:outdated

echo "outdated for ta/tradingview"
cd profiles/tradingview && clj -M:outdated 
cd ../..

echo "outdated for ta/helper"
cd profiles/helper && clj -M:outdated 
cd ../..

echo "outdated for ta/warehouse"
cd profiles/warehouse && clj -M:outdated 
cd ../..

echo "outdated for ta/data"
cd profiles/data && clj -M:outdated 
cd ../..





