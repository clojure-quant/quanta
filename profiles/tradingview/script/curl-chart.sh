#!/bin/sh


# without chart id it gets the list of charts
# echo "tradingview chart-list:"
# curl "https://saveload.tradingview.com/1.1/charts?client=77&user=77"

# echo "trateg chart-list:"
# curl "http://localhost:8000/api/tv/storage/1.1/charts?client=77&user=77"


# load chart
# echo "tradingview load-chart"
# curl "https://saveload.tradingview.com/1.1/charts?client=77&user=77&chart=722072"

# {"status": "ok", 
   "data": {"id": 722072, "name": "test", "timestamp": 1599055660.0, "content": 
\\"version\\\":2,\\\"timezone\\\":\\\"Etc/UTC\\\"}]}\",\"is_realtime\":\"1\"}"}}

echo "trateg load-chart"
curl "http://localhost:8000/api/tv/storage/1.1/charts?client=77&user=77&chart=1636558275"

# "status": "ok", "data": {"id": 722072, "name": "test", "timestamp": 1599055660.0, "content": 

{"status":"ok","data":{"description":"","content":"{\"layout\":\"s\",\"charts\
,"id":1636558275,
"exchange":"NasdaqNM",
"timestamp":1636566428,

"user":"77",
"symbol_type":"stock"

}}⏎