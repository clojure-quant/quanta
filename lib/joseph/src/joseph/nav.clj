(ns joseph.nav
  (:require
    [ta.multi.nav-trades :refer [portfolio]]
    [joseph.trades :refer [load-trades-valid]]))




(defn calc-nav []
  (let [trades (load-trades-valid)
        ds-nav (portfolio trades)
        ]
    ds-nav
    )
  
  )

(comment 
  (calc-nav)  
  
  )


