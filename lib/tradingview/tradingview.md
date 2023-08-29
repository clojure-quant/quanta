



The library creates an instance of a custom study by applying operator new to the constructor.

Arguments for both functions are context and inputCallback.

this.init = function(context, inputCallback) {
    var symbol = '#EQUITY';
    var period = PineJS.Std.period(this._context);
    context.new_sym(symbol, period);
};
this.main = function(ctx, inputCallback) {
    this._context = ctx;
    this._input = inputCallback;
    var inputValue1 = this._input(0);
    var inputValue2 = this._input(1);
    var zigzag1 = PineJS.Std.zigzag(inputValue1 / 100, inputValue2 / 2, this._context);
    var zigzag2 = PineJS.Std.zigzagbars(inputValue1 / 100, inputValue2 / 2, this._context);
    return [zigzag1, zigzag2];
};

(deftype Foo [a b c]
  Object
  (bar [this x] (+ a b c x)))
(def afoo (Foo. 1 2 3))
(.bar afoo 3) ; >> 9

(set! (.-foo (.-prototype js/String)) (fn [] "bar")

(Object.assign (.. RankProvider -prototype)
               #js {"getData" (fn [cbk] (this-as this (getData this cbk)))
                    "watch" (fn [offset limit] (this-as this (watch this offset limit)))})

onScreenshotReady

FRAGEN VIKTOR:
https://github.com/tradingview/trading_platform
Position einzeichnen

Sunspot count:
https://wwwbis.sidc.be/images/wolfmms.png

Cardinal square.
The Definitive Guide to Forecasting Using W.D. Gann's Square of Nine by Patrick Mikula 
drew square of 9 
Select the figure and modify its rotation angle so that its diagonals would cross or be as close as possible to the marked points.


The global globalThis property contains the global this value, which is akin to the global object.


https://stackoverflow.com/questions/32467299/clojurescript-convert-arbitrary-javascript-object-to-clojure-script-map

https://www.tradingview.com/u/citlacom/
Usually, in Financial Astrology is said that planets declination change modify their energy power and effect: in North is strengthen and in South is weaken.
Vesta (asteroid) speed provides a good indicator to determine the periods of dominant trend. During the direct phase (when the speed accelerated and reached highest value) we can observe that BTCUSD experienced a clear trend, perfect for trend following. 

It's said by financial astrologers that Moon in extreme declinations above or below 22, tends to produce more extreme effects of the Moon energy effects. My friend Peter advised that extreme declinations are very intense energy points and when the extreme is reached a reversal effect is expected. We are not quite sure if this rule is valid so this indicator is to validate the this empirical observations.
Open source script.
https://www.tradingview.com/script/YtHRc7IR-Financial-Astrology-Neptune-Longitude/
. The daily price trend is forecasted through this planets cycles (aspects, declination and speed phases)
https://www.tradingview.com/script/dWi5MI7l-Morun-Astro-Trend-MAs-cross-Strategy/
https://www.tradingview.com/script/nzvqcuSh-Detrended-Rhythm-Oscillator-DRO/



visible": true, // Plot line width. "linewidth": 2, // Plot type: // 1 - Histogram // 2 - Line // 3 - Cross // 4 - Area // 5 - Columns // 6 - Circles // 7 - Line With Breaks // 8 - Area With Breaks "plottype": 2,
"plots": [{"id": "plot_0", "type": "line"}],

Possible values of some properties:

linestyle: [0 (solid), 1 (dotted), 2 (dashed), 3 (large dashed)]
linewidth: [1, 2, 3, 4]
horzLabelsAlign: ["center", "left", "right"]
vertLabelsAlign: ["top", "middle", "bottom"]
leftEnd, rightEnd: [0 (Normal), 1 (Arrow)]
bars_pattern - mode: [0 (HL Bars, 1 (Line-Close), 2 (OC Bars), 3 (Line-Open), 4 (Line-High), 5 (Line-Low), 6 (Line-HL/2)]

nPlaces Volume indicator on the same pane with the main series

getVisibleRange()
Returns the object {from, to}. from and to are Unix timestamps in the timezone of the chart.

getVisiblePriceRange()
Returns the object {from, to}. from and to are boundaries of the price scale visible range in main series area.
Date.UTC(2018, 0, 1) / 1000 

symbol()

Returns the current symbol of the chart.

symbolExt()

property_name: 
transparency
linewidth
plottype. line histogram crossarea columns circles line_with_breaks area_with_breaks

this._context.select_sym(1); 

var c = PineJS.Std.close(this._context)-50; 
 var o = PineJS.Std.open(this._context)-50; 
var l = PineJS.Std.low(this._context)-50; 
var h = PineJS.Std.high(this._context)-50; 
 console.log('execute custom index!'); 
console.log('symbol: ', this._context['symbol']['time']); 
 return [o, c];

Custom indicator 
 widget.chart().createStudy('ShuBenRSI', false, false);


'plots': [{'id': 'plot_0', 'type': 'line'}], 
 'defaults': { 'styles': 
{'plot_0': {'linestyle': 0, 'visible': true ,  'linewidth': 1, 'plottype': 2, draw a line graph 
 'trackPrice': to true , 
'transparency': 40, 27 'color': '#880000' 28 } 29 }, 30 'precision': 3, precision EG: 608.4 31 is 'inputs': {} 32 }, 33 is 'styles': { 34 'plot_0': { 35 'title': 'ShuBenRSI', 36 'histogrambase': 0, 37 [ } 38 is }, 39 'inputs': [], 40 }, 41 is constructor: function () { 42 is the this.init = function (context, inputCallback) { 43 var host =win.loc; 44 var host1 = host.href.split('static'); 45 var fakeDataRSI =[]; 46 $.ajaxSetup({ async: false }); 47 $.post(host1[0] + 'cta_posPL_syetem/getChartData',{method:'getDataRSI'}, function (result) { 48 if(result.result_code == 'success'){ 49 fakeDataRSI =result.data; 50 } 51 52 }); 53 this.fakeData =fakeDataRSI; 54 this.count = 0; 55 this.time = 0; 56 this.rsi = 0; 57 this.infoList =[]; 58 console.log('init context:', context); 59 console.log(this.count); 60 this._context =context; 61 this._input =inputCallback; 62 var symbol ='p1905'; 63 var symbol = PineJS.Std.ticker(this._context); Get the selected product code 64 this._context.new_sym(symbol, PineJS.Std.period(this._context), PineJS.Std.period(this ._context )); 65 }; 66 this .main.main = function (context, inputCallback) { 67 if(this.count> 1 && this.time != this._context['symbol']['time']){ this.count += 1; 68 console.log('count:',this.count); 69 if(this.count<5)console.log('main fakeData:', this.fakeData [this.count]); 70 this._context =context; 71 this._input =inputCallback; 72 this._context.select_sym(1); 73 /* 74 //RSI calculation 75 76 //console.log(PineJS.Std .close(this._context)); 77 this.infoList.push(PineJS.Std.close(this._context)); 78 var upSum = 0;var downSum = 0; 79 if(this.count> 15){ 80 for(var i = 1; i <= 14; i++){ 81 var change = this.infoList[i]-this.infoList[i-1]; 82 change> 0? upSum += change: downSum -= change; 83 } 84 var rs = Math.round(upSum/14 *1000)/downSum; 85 this.rsi = Math.round(rs/(1 + rs) * 100 * 1000)/1000; 86 //console.log('current:', this._context['symbol']['time'],'pretime:',this.time); 87 //console.log('infoList:',this.infoList); 88 this.infoList.splice(0, 1); 89 this.time = this._context['symbol']['time']; 90 //console.log('index close: --- >', PineJS.Std.close(this._context)); 91 return [this.rsi]; 92 } 93 } 94 return [this.rsi]; 95 */ 96 var c = this.fakeData[this.count++]['close']; 97 console.log('rsi:',this.rsi); //console.log('execute custom index!'); 10698 99 return [c]; 100 /* 101 var c = PineJS.Std.close(this._context)-50; 102 var o = PineJS.Std.open(this._context)-50; 103 var l = PineJS. Std.low(this._context)-50; 104 var h = PineJS.Std.high(this._context)-50; 105 console.log('symbol:', this._context['symbol']['time' ]); 107 //return [o, c]; 108 */109 } 110 } 111 } 112 ]; 

Custom indicator curve

If the curve data you want to customize is not K-line data, you can request the data returned by the background from the background in the custom template. eg: 

VIEW IMAGE VIEW IMAGE

. 1 __customIndicators =[ 2 { . 3 name: 'ShuBenRSI', . 4 metainfo: { . 5 '_metainfoVersion': 40, . 6 'id': 'ShuBenRSI@tv-basicstudies-1', . 7 'scriptIdPart': '', . 8 'name': 'ShuBenRSI', . 9 when calling createStudy method, it is also used as the "name" parameter 10 'description': 'ShuBenRSI', . 11 the description will appear on the graph 12 is 'shortDescription': 'ShuBenRSI', 13 'is_hidden_study': to true , 14 'is_price_study': to false , 15 'isCustomIndicator': to true , 16 'plots': [{'id': 'plot_0', 'type': 'line'}], . 17 'defaults': { 18 is 'styles': { . 19 'plot_0': { 20 is 'linestyle': 0, 21 is 'visible': to true , 22 is 'linewidth': 1, 23 is 'plottype': 2, draw a line graph types: 2 24 'trackPrice': to true , 25 'transparency': 40, 26 is 'color': '#880000' 27 } 28 }, 29 'precision': 3, precision EG: 608.4 30 'inputs': {} 31 is }, 32 'styles': { 33 is 'plot_0': 34 is 'title': 'ShuBenRSI' , 35 'histogrambase': 0, 36 } 37 [ }, 38 is 'inputs': [], 39 }, 40 

constructor: fn () {this.init = 
fn [context inputCallback] {
this._context.new_sym(symbol, PineJS.Std.period(context), PineJS.Std.period(context))

this.main = 
(fn [context, inputCallback]
context.select_sym(1); 
#js [(PineJS.close context s)]


c = security(syminfo.tickerid, "5", close)

period
Resolution, e.g. '60' - 60 minutes, 'D' - daily, 'W' - weekly, 'M' - monthly, '5D' - 5 days, '12M' - one year, '3M' - one quarter.

The data source is not K-line data

Reprinted at: https://www.cnblogs.com/xsmile/p/10640536.html


location.top

Location value for plotshape, plotchar functions. Shape is plotted near the top chart border.
location.top

Location value for plotshape, plotchar functions. Shape is plotted near the top chart border.
na(close) ? close[
(def obj
  (let [obj #js {:text "foo"}
        setText (fn [text] (set! (.-text obj) text))
        getText (fn [] (.-text obj))]
    (set! (.-setText obj) setText)
    (set! (.-getText obj) getText)
    obj))

(.setText obj "hello")
(prn (.getText obj)) ;; "hello"


https://stackoverflow.com/questions/63655452/how-to-setup-tradingview-charting-library-chart-to-update-automatically/65652299#65652299
