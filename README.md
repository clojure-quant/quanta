# ta [![GitHub Actions status |pink-gorilla/trateg](https://github.com/pink-gorilla/trateg/workflows/CI/badge.svg)](https://github.com/pink-gorilla/trateg/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/ta.svg)](https://clojars.org/org.pinkgorilla/ta)

Trateg is a technical analysis platform written in Clojure.
You can see charts with indicators and run backtests.
You can also do complex datamining with the timeseries.

Trateg relies on techml dataset and goldly-docs.

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

Goto tradingview directory: `cd profiles/tradingview`

`clj -X:make-demo-charts`

Charts are in profiles/tradingview/tvdb

To see demo charts start `clj -X:goldly-docs` (in tradingview directory).
Charts are in developer tools / pages / tradingview



## Develop with goldly docs

When you start goldly-docs it starts an nrepl server on port 9100.
You could jack in, and start developing.

Have a look at 
 


## run unit tests / speed tests

```
clojure -M:test
lein speed
```




clj -X:test


clj -X:performance-test

