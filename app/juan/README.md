# juan's forex reversal strategy


A Mean Reversion System to be traded on the main seven forex currency pairs, all set to 5 minute candlestick charts


Main indicators/tools required:

1. Manually drawn in support and resistance (horizontal) lines based upon the previous day's high and low and also the week's high and low prices. These are coloured gold.

2. 'Volume lines' manually drawn in, but based upon market/volume profile and derived from the equivalent CME futures contracts. Purple horizontal lines for previous day's max level and grey for this week's and previous week's levels. 

See here: https://clusterdelta.com/marketprofile

3. Volume histogram. Based upon actual traded volume from equivalent CME futures contracts. 

See here: https://clusterdelta.com/volume

4. Average Daily Range indicator (ADR). 


Secondary indicator:

5. Trader sentiment. Based upon data derived from MyFXbook website and used as a contrarian gauge.

See here: https://www.myfxbook.com/community/outlook
-------------------------------------------------------

System premises:

A) Price can only do one of two things, go up or go down. It does both, but not at the same time. 
B) Price does not continue indefinitely in one direction. What goes up must come down. This system is contrarian in nature and tries to capture counter trend moves at specific points.
C) Price moves due to buy or sell orders. There are really only two fundamental types of order in existence:
(i) Market orders - these orders CONSUME liquidity.
(ii) Limit orders - these orders PROVIDE liquidity.
-------------------------------------------------------

The system:

The first thing we wish to do is to look at the ADR indicator for each individual currency pair being monitored. It is set to a period of 20 days (i.e. one full month in trading days) and therefore gives us an idea of the size of the daily price fluctuations for this given time period. 

We then want to find a currency pair whose price move for the current day has thus far covered at LEAST 75% of it's ADR and preferably 100% or more. If such a pair is found then the second thing we wish to observe is a 'clean' move, i.e. while as noted previously, price never moves unilaterally, ideally we still want to find a consistenly trending pair that hasn't had wild swings up and down.

If our hypothetical currency pair has met the aforementioned criteria then the specific conditions that we want to focus on are these:

1. We want to see price reaching either our S/R lines or (better) our volume profile lines - these are the prices where there has previously been a lot of activity and will hopefully act as a wall to prevent further trending movement.

2. When a line has been 'hit', we want to see that there has been a greater than average spike in the volume histogram - volume indicates actual market orders being placed.

3. We want to see this candle* CLOSE AS A DOJI - indicating that the volume we have witnessed is counter trend in nature.

4. If all of the preceding criteria have been met then upon the opening of the very next candle a relevant buy or sell order should be entered into the market with simultaneous take profit and stop loss orders of 10 pips.

Secondary consideration:

5. While we don't use this as much anymore, in addition to everything thus far mentioned, we like to make sure that the retail sentiment is in OPPOSITION to the trade we are wishing to take. The idea being that the wholesale market players consume retail liquidity and act against what is expected by them. The Myfxbook website publishes and updates the retail sentiment for many financial instruments throughout the day and we take the data published at 5pm Eastern time as a marker for the following day. Here is a hypothetical example:

For EUR/USD the retail traders are 82% short in their positions, meaning that only 18% of them are long. We therefore favour long trades. The more committed the retail crowd are in one direction, the better for us. Here is the gauge I use to determine the strength of the sentiment:

50-69	Neutral
70-79	Weak
80-89	Strong
90-100	Very strong

In the preceding example 82% of retail traders were short, therefore any buy trades we take will be considered STRONG.



*Ideally we want the first candle that hits an S/R or volume line to be the doji candle, but so long as price does not continued unabated in the direction of the established trend and we have a 'stalling' at the level then we can enter on the next candle or, at maximum, the third candle, so long as one of them is a doji and there is a corresponding increase in volume.

Fx calendar.
- Holidays: us (+ uk.)
- Open 5pm EST. 
- Close 5pm EST or 430pm est.
- Close on satursday. Week sunday-friday.

