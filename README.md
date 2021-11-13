# ta [![GitHub Actions status |pink-gorilla/trateg](https://github.com/pink-gorilla/trateg/workflows/CI/badge.svg)](https://github.com/pink-gorilla/trateg/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/ta.svg)](https://clojars.org/org.pinkgorilla/ta)

## What is TA?

TA is a technical analysis datascience platform written in Clojure.

- Ease of Use: TA tries to get out of your way so that you can focus on algorithm development. See below for a code example.

- "Batteries Included": many common statistics like moving average and linear regression can be readily accessed from within a user-written algorithm.

- TechML Dataset Integration: Input of historical data and output of performance statistics are based on TechML DataSet.

- Statistics and Machine Learning Libraries: 
You can use libraries like matplotlib, scipy, statsmodels, and sklearn to support development, analysis, and visualization of state-of-the-art trading systems.

- Extensible: UI is based on [goldy-docs](https://github.com/pink-gorilla/goldly) , so you can create websites / dashboards / notebooks quickly.


## bybit data import

First, lets get some data. Goto demo directory: `cd profiles/demo`

- once:  `clj -X:bybit-import-initial`
- every 15 minutes / whenever you want to update. (missing a bar cannot happen)
 `clj -X:bybit-import-append`

 Data is stored as gzipped nippy files in db directory.
 

## Alphavantage data import

Create the file `profiles/demo/creds.edn`
It has to contain your alphavantage api key (you can get it free and fast on their website)
`{:alphavantage "your-alphavantage-api-key"}`

- Import once: `clj -X:alphavantage-import`

Alphavantage can download 5 symbols a minute. We have 40 demo symbols, so this will take a while.

## GoldlyDocs Web app

In `profiles/demo`
 - run: `clj -X:goldly-docs`
 - in webbrowser go to localhost:8000 


## Tradingview Chart Study maker

`cd profiles/tradingview && clj -X:make-demo-charts`

Generated charts are stored in profiles/resources/tradingview-charts
and can be seen in goldlydocs web app in developer tools / pages / tradingview

## Develop with goldly docs

When you start goldly-docs it starts an nrepl server on port 9100.
You could jack in, and start developing.

Have a look at [demo-goldy](https://github.com/pink-gorilla/demo-goldly) 
 


## run unit tests / speed tests

*unit tests* `./script/unit_test.sh`

*speed test* `./script/performance_test.sh`





