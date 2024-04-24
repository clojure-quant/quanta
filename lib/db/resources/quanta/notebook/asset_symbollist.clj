(ns quanta.notebook.asset-symbollist
  (:require 
    [ta.db.asset.symbollist :as sl]))

  (sl/load-list "resources/symbollist/bonds.edn")
  
  (sl/load-list-full "resources/symbollist/fidelity-select.edn")

 
