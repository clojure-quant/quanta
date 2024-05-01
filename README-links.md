
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



hurst index
https://r-forge.r-project.org/scm/viewvc.php/pkg/PerformanceAnalytics/R/HurstIndex.R?view=markup&root=returnanalytics

https://github.com/cgrand/xforms

;; http://www.nasdaq.com/screening/company-list.aspx
;; NASDAQTrader
;; ftp://ftp.nasdaqtrader.com/SymbolDirectory
;; ftp://ftp.nasdaqtrader.com/SymbolDirectory/nasdaqlisted.txt
;; ftp://ftp.nasdaqtrader.com/SymbolDirectory/otherlisted.txt
;; ftp://ftp.nasdaqtrader.com/symboldirectory
;; ftp://nasdaqtrader.com/SymbolDirectory/nasdaqlisted.txt
;; ftp://nasdaqtrader.com/SymbolDirectory/otherlisted.txt
