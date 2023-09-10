(ns joseph.nav
  (:require
    [clojure.string :as str]
    [ta.multi.nav-trades :refer [portfolio]]
    [ta.helper.ds :refer [ds->map]]
    [joseph.trades :refer [load-trades-valid]]))

(defn calc-nav [symbol]
  (let [trades (load-trades-valid)
        trades (if (or (nil? symbol) (str/blank? symbol))
                 trades
                 (filter #(= symbol (:symbol %)) trades))
        ds-nav (portfolio trades)]
    {:nav ds-nav
     :trades trades}))


(defn calc-nav-browser [symbol]
  (let [{:keys [nav trades]} (calc-nav symbol)]
    {:nav (ds->map nav)
     :trades trades}))


(comment 
  (-> (calc-nav nil) 
      :nav)

  (-> (calc-nav "GOOGL")
      :trades)
  (-> (calc-nav "GOOGL")
      :nav)

  (-> (calc-nav-browser nil) 
      :nav)
  
;
  )


