(ns ta.tradingview.goldly.indicator.clj)


(def clj-meta
  {:_metainfoVersion 51
   :id "clj@tv-basicstudies-1"
   :name "CLJ"
   :description "CLJ" ; this is used in the api
   :shortDescription "CLJ DESC"
   "isCustomIndicator" true
   "is_price_study" false ; plot in main chart-pane
   "isTVScript" false
   "isTVScriptStub" false
   "format" {"type" "price"
             "precision" 4}
   "plots" [{"id" "plot_0"
             "type" "line"}]
   "defaults" {"styles" {"plot_0" {"linestyle" 0
                                   "visible" true
                                   "linewidth" 1 ; Make the line thinner
                                   "plottype" 2 ; Plot type is Line
                                   "trackPrice" true ; Show price line (horizontal line with last price)
                                   "color" "#880000" ; Set the plotted line color to dark red
                                   }}}

   "inputs" [{"id" "col"
              "name" "col"
              "type" "text" ; "integer"
              "defval" "#close"}]

   "styles" {"plot_0" {"title" "algo column value" ; Output name will be displayed in the Style window
                       "histogramBase"  0}}})


(defn clj-study-runner [PineJS]
  (fn []
    (clj->js
     {:init (fn [context inputCallback]
              (let [main-symbol (-> PineJS .-Std (.ticker context))
                    col (or (inputCallback 0) "close")
                    symbol (str main-symbol "#" col)
                    p (-> PineJS .-Std (.period context)) ;PineJS.Std.period (this._context)
                    ]
                ;(println "CLJ INIT! PERIOD: " p "SYMBOL: " main-symbol "COL: " col) ; called 1x
              ;(.log js/console inputCallback)
              ;(.log js/console context)

              ;this._context = context;
              ;this._input = inputCallback;
                (.new_sym context symbol "D")
                nil))
      :main (fn [context _inputCallback]
               ;(println "CLJ MAIN!") ; called for EACH BAR.
              (.select_sym context 1) ;this._context.select_sym (1);
              (let [v (-> PineJS .-Std (.close context)) ;var v = PineJS.Std.close (this._context);
                    ;t (-> PineJS .-Std (.updatetime context)) ;var v = PineJS.Std.updatetime (this._context);
                     ;(this._context['symbol']['time'] !=NaN){
                    ;X (aget context "symbol")
                    ;t (aget X "time")
                     ;year (-> PineJS .-Std (.year context))
                     ;month (-> PineJS .-Std (.month context))
                     ;day (-> PineJS .-Std (.dayofmonth context))
                     ; updatetime
                    ;main-symbol (-> PineJS .-Std (.ticker context))
                    ]
                 ;(println "VALUE: " v "SYMBOL: " main-symbol "TIME:" t ) ;year "-" month "-" day
                 ;this._context = context;
                 ;this._input = inputCallback;
                #js [v]))})))




(defn study-clj [PineJS]
  (clj->js
   {:name "CLJ"
    :metainfo clj-meta
    :constructor (clj-study-runner PineJS)}))
