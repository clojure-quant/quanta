




2021-10-09  
bybit import to disk 

create this file: profiles/demo/creds.edn - then add your secret key in this format:
{:alphavantage "YOUR-SECRET-KEY"}

cd profiles/demo
once:
clj -X:bybit-import-initial

every 15 minutes / whenever you want to update. (missing a bar cannot happen)
clj -X:bybit-import-append
