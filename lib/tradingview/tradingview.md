

period request:
from - unix timestamp, leftmost required bar time (inclusive end)
to: unix timestamp, rightmost required bar time (not inclusive)
countBack - the exact amount of bars to load, should be considered a higher priority than from if your datafeed supports it (see below). It may not be specified if the user requests a specific time period.
firstDataRequest: boolean to identify the first call of this method. When it is set to true you can ignore to (which depends on browser's Date.now()) and return bars up to the latest bar.



                {:from 1595381213, :to 1650504413, :count-back 456, :first-request? true}
{:from 1592184413, :to 1595381213, :count-back 27, :first-request? false}

request 1: {:from 1607563451, :to 1614216251, :count-back 55, :first-request? false}


Meta information is an object with the following fields:

noData: boolean. This flag should be set if there is no data in the requested period.
nextTime: unix timestamp (UTC). Time of the next bar in the history. It should be set if the requested period represents a gap in the data. Hence there is available data prior to the requested period.