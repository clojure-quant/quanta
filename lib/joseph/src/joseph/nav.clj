(ns joseph.nav
  (:require
    [clojure.string :as str]
    [ta.multi.nav-trades :refer [portfolio]]
    [ta.helper.ds :refer [ds->map]]
    [joseph.trades :refer [load-trades-valid]]))

(defn calc-nav [symbol account]
  (let [trades (load-trades-valid)
        symbols (->> trades (map :symbol) (into #{}) (into []) (sort))
        accounts (->> trades (map :account) (into #{}) (into []) (sort))
        trades (if (or (nil? symbol) (str/blank? symbol))
                 trades
                 (filter #(= symbol (:symbol %)) trades))
        trades (if (or (nil? account) (str/blank? account))
                 trades
                 (filter #(= account (:account %)) trades))
        nav (portfolio trades)]
    {:nav (:eff nav)
     :warnings (:warnings nav)
     :trades trades
     :symbols symbols
     :accounts accounts
     }))


(defn calc-nav-browser [symbol account]
  (let [{:keys [nav] :as data} (calc-nav symbol account)]
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


