# ta [![GitHub Actions status |pink-gorilla/trateg](https://github.com/pink-gorilla/trateg/workflows/CI/badge.svg)](https://github.com/pink-gorilla/trateg/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/ta.svg)](https://clojars.org/org.pinkgorilla/ta)

## What is TA?

TA is a technical analysis datascience platform written in Clojure.

- Ease of Use: TA tries to get out of your way so that you can focus on algorithm development. See below for a code example.

- "Batteries Included": many common statistics like moving average and linear regression can be readily accessed from within a user-written algorithm.

- TechML Dataset Integration: Input of historical data and output of performance statistics are based on TechML DataSet.

- Statistics and Machine Learning Libraries: 
You can use libraries similar to matplotlib, scipy, statsmodels, and sklearn to support development, analysis, and visualization of state-of-the-art trading systems.

- Extensible: UI is based on [goldy-docs](https://github.com/pink-gorilla/goldly) , so you can create websites / dashboards / notebooks quickly.


##  data import 

First, lets get some data. Goto demo directory: `cd app/demo`

*Bybit Feed*
Bybit feed does not need credentials. It has data since 2018-11 for BTC and ETH.

*Alphavantage Feed*
Alphavantage needs an api key (you can get it free and fast on their website)
The creds file in `profiles/demo/creds.edn` has to contain your alphavantage api key:
`{:alphavantage "your-alphavantage-api-key"}`
Alphavantage can download 5 symbols a minute. We have 40 demo symbols, so this will take a while.

*symbol lists*
`app/resources/symbollist`contains edn files that contain symbols.
The name (without .edn) can be specified in the commandline.

Timeseries data is stored as gzipped nippy files in db directory. The path is
stored in the config in [:ta :warehouse :series]. :crypto and :stocks are two different
warehouses. Bybit goes to :crypto. Alphavantage goes to :stocks.

*import*
- alphavantage: `clj -X:run :task :alphavantage-import :symbol "test"`
- bybit:  `clj -X:run :task :bybit-import :symbol "crypto"`

*append*
- bybit: `clj -X:run :task :bybit-append :symbol "crypto"`

*warehouse summary* `clj -X:run :task :warehouse`

*shuffle warehouse* `clj -X:run :task :shuffle` 
This reads the :crypto warehouse, shuffles the returns and creates the :random warehouse.

*gann maker** `clj -X:run :task :gann` 
This reads profiles/resources/gann.edn and creates tradingview charts for each symbol in it.
The charts can be loaded via the tradingview page.



## GoldlyDocs Web app

In `profiles/demo`
 - run: `clj -X:goldly-docs`
 - in webbrowser go to localhost:8000 


## Tradingview Chart Study maker

`cd profiles/tradingview && clj -X:make-demo-charts`

Generated charts are stored in profiles/resources/tradingview-charts
and can be seen in goldlydocs web app in developer tools / pages / tradingview

## build your app with goldly docs

When you start goldly-docs it starts an nrepl server on port 9100.
You could jack in, and start developing.

Have a look at [demo-goldy](https://github.com/pink-gorilla/demo-goldly) 
 

## for TA developers

*code linter*  `clj -M:lint`

*code formatter `clj -M:cljfmt-fix`

*unit tests* `./script/unit_test.sh`

*speed test* `./script/performance_test.sh`

*outdated dependencies* `./script/outdated.sh`




