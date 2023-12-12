# kibot datafeed

- http://www.kibot.com/
- Kibot covers us-stocks, us-futures, us-etfs forex
- kibot has 3 ways of delivering data: 1. rest api 2. ftp 3. http
- [kibot rest api]: allows to download daily/1min historic bar download that is 
  delayed by 30 minutes.
- [kibot ftp] kibot entire market datafeed: provides daily update files for all
  etf, all stocks, all futures, all forex.
  the update files are downloaded with ftp, uncompressed with winrar, and are 1 csv file for each asset. Most assets just have a 1 line csv file for daily 
  updates; however stocks that had splits on that day will provide the entire
  history on the day of the split.
- [kibot http] kibot allows downloading single-asset entire history csv via http links. This works for daily/weekly/1min bars.








