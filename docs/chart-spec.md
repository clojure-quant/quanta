# chart-spec

# one chart

One chart is represented as a map.
The keys describes the columns used, 
the value is either a keyword with the type of the series-plot.
Or it is a map that needs to have :type key and other keys of this
map being custom options to the plot type.

{:close :line
 [:open :close] :range
 :signal :flags}

# series types 
- :line
- :point
- :column
- :ohlc
- :candlestick
- :hollowcandlestick
- :flags
- :step


