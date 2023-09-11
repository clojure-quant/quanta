(ns joseph.nav
  (:require
    [clojure.string :as str]
    [ta.multi.nav-trades :refer [portfolio]]
    [ta.helper.ds :refer [ds->map]]
    [joseph.trades :refer [load-trades-valid]]))

(defn calc-nav [symbol]
  (let [trades (load-trades-valid)
        symbols (->> trades (map :symbol) (into #{}) (into []))
        accounts (->> trades (map :account) (into #{}) (into []))
        trades (if (or (nil? symbol) (str/blank? symbol))
                 trades
                 (filter #(= symbol (:symbol %)) trades))
        ds-nav (portfolio trades)]
    {:nav ds-nav
     :trades trades
     :symbols symbols
     :accounts accounts
     }))


(defn calc-nav-browser [symbol]
  (let [{:keys [nav] :as data} (calc-nav symbol)]
    (merge data
           {:nav (ds->map nav)})))


(comment 
  (-> (calc-nav nil)
      :symbols)

  (-> (calc-nav nil) 
      :nav)

  (-> (calc-nav "GOOGL")
      :trades)
  (-> (calc-nav "GOOGL")
      :nav)

  (-> (calc-nav-browser nil) 
      :nav)
  
  (-> (calc-nav-browser nil)
      :symbols)
  
  (-> (calc-nav-browser nil)
      :accounts)
;
  )


