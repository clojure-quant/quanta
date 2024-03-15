https://quantocracy.com/
https://www.quantconnect.com/market/alpha/e219bc8c86278eb3d2d0d8ab6/v1.1


; multiple strategies
https://github.com/systematicinvestor/SIT/blob/master/R/bt.test.r


     :b [1 2 3 4 3 3]}
  ds/->dataset
  (ds/column-map
   :c (fn[a b]
        (cond
          (> a 3) 1
          (< b 3) -1
          :else 0))
   [:a :b]))

   Or see https://clojurians.zulipchat.com/#narrow/stream/236259-tech.2Eml.2Edataset.2Edev/topic/stupid.20column.20calc/near/244088467 for an alternative

   jsa5:33 PM
For your specific case, column-map seems like the exact fit. Another option is the more general add-column. Using the tablecloth dplyr / data.table like "wrapper" for TMD, this would look like:

(-> {:a [2 3 4 5 1 1]
     :b [1 2 3 4 3 3]}
  tc/dataset
  (tc/add-column
   :c #(let [a (:a %)
             b (:b %)]
        (mapv (fn[a b]
                (cond
                  (> a 3) 1
                  (< b 3) -1
                  :else 0))
              a b))))
=> _unnamed [6 3]:

Neanderthal is a core.matrix implementation that is 3000x faster than Java (if in the GPU).
https://neanderthal.uncomplicate.org/
Interop from R to Neanderthal has problems. Is this correct???

https://github.com/shark8me/clj-ml
shark8me/clj-ml: A machine learning library for Clojure built on top of Weka and friends

https://github.com/adamtornhill/kmeans-clj/blob/master/src/kmeans_clj/sample.clj


https://github.com/adamtornhill/zoo-time-series/blob/master/src/zoo_time_series/core.clj
https://www.adamtornhill.com/articles/clojure/zoochurn.htm

https://slugclojure.ahungry.com/package/MichaelDrogalis.onyx
https://github.com/nrfm/dependency
https://github.com/rm-hull/monet
https://github.com/rm-hull/clustering
https://slugclojure.ahungry.com/package/ztellman.automat
https://github.com/joshuaulrich/quantmod

##  $ xts                 : chr [1:27] "apply.daily" "apply.monthly" "apply.quarterly" "apply.weekly" ...
##  $ quantmod            : chr [1:25] "allReturns" "annualReturn" "ClCl" "dailyReturn" ...
##  $ TTR                 : chr [1:63] "adjRatios" "ADX" "ALMA" "aroon" ...
##  $ PerformanceAnalytics: chr [1:7] "Return.annualized" "Return.annualized.excess" "Return.clean" "Return.cumulative" ...

hurst index
https://r-forge.r-project.org/scm/viewvc.php/pkg/PerformanceAnalytics/R/HurstIndex.R?view=markup&root=returnanalytics

https://github.com/cgrand/xforms

TASK PIPELINE:
https://github.com/sunng87/papaline

;; http://www.nasdaq.com/screening/company-list.aspx
;; NASDAQTrader
;; ftp://ftp.nasdaqtrader.com/SymbolDirectory
;; ftp://ftp.nasdaqtrader.com/SymbolDirectory/nasdaqlisted.txt
;; ftp://ftp.nasdaqtrader.com/SymbolDirectory/otherlisted.txt
;; ftp://ftp.nasdaqtrader.com/symboldirectory
;; ftp://nasdaqtrader.com/SymbolDirectory/nasdaqlisted.txt
;; ftp://nasdaqtrader.com/SymbolDirectory/otherlisted.txt