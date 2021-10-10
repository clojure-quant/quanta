# 2021 10 - bollinger strategy backtest


## go to right directory

`cd profiles/demo`

## add credentials

create this file: profiles/demo/creds.edn - then add your secret key in this format:
{:alphavantage "YOUR-SECRET-KEY"}

## bybit import

- once:  `clj -X:bybit-import-initial`
- every 15 minutes / whenever you want to update. (missing a bar cannot happen)
 `clj -X:bybit-import-append`


## run strategy

`clj -X:bollinger-strategy`