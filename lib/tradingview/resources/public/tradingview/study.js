var equitystudy = function (PineJS) {
  return {
    name: 'Equity',
    metainfo: {
        _metainfoVersion: 51,
        id: 'Equity@tv-basicstudies-1',
        description: 'Equity',
        shortDescription: 'Equity',
        is_hidden_study: false,
        is_price_study: true,
        isCustomIndicator: true,
        format: {type: 'price',
                 // Precision is set to one digit, e.g. 777.7
                 precision: 1 },
        plots: [{id: 'plot_0', type: 'line'}],
        defaults: {
            styles: {
                plot_0: {
                    linestyle: 0,
                    visible: true,
                    // Make the line thinner
                    linewidth: 1,
                    // Plot type is Line
                    plottype: 2,
                    // Show price line
                    trackPrice: true,
                    // Set the plotted line color to dark red
                    color: '#880000'
                }
            },
            inputs: {}
        },
        styles: {
            plot_0: {
                // Output name will be displayed in the Style window
                title: 'Equity value',
                histogramBase: 0,
            }
        },
        inputs: [],
    },

    constructor: function() {
        this.init = function(context, inputCallback) {
            this._context = context;
            this._input = inputCallback;
            var symbol = 'TLT';
            this._context.new_sym(symbol, PineJS.Std.period(this._context));
        };

        this.main = function(context, inputCallback) {
            this._context = context;
            this._input = inputCallback;
            this._context.select_sym(1);
            var v = PineJS.Std.close(this._context);
            console.log("equity v: " + v)
            return [v];
        }
    }
}};


console.log("LOADING EQUITY-STUDY-JS DEMO !!")

function mystudy (m) {
 console.log("Creating mystudy with: " + m)
 return function () {
    //this.main = m;
    

 }}

 function bongo () {
    console.log("equity-study JS bongo!")
    this.main = function(ctx, inputCallback) {
        this._context = ctx;
        this._input = inputCallback;
        //console.log("main called! context hs been set!");
        //console.log("ctx: " + ctx)
        //console.log("input: " + inputCallback)
        return [100];
  
      };
 }


var C = class { // ...
              }
Object.defineProperty (C, 'name', {value: 'TheName'});

// test: 

console.log("TESTING: class name: " + (new C()).constructor.name )
// let itsName =  ;
// itsName === 'TheName' -> true



window.mystudy = mystudy;
window.bongo = bongo;

window.equitystudy = equitystudy;

// export default {
//	history: history,
//
//    getBars: