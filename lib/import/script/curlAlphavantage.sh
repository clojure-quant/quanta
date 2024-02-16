#!/bin/bash

apikey="demo"
#apikey="my-api-key"

getdata () {
   echo -e "Getting Data.."
   curl -XGET  \
      "https://www.alphavantage.co/query?function=CRYPTO_RATING&symbol=BTC&apikey=$apikey" 
}

getdata
getdata
getdata
getdata
getdata
getdata
getdata
getdata
getdata
getdata
getdata



#-F content=@'flo.gz'