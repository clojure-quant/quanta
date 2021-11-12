(ns ta.tradingview.config)


(def tv-config {:db-path "tvdb/"
                :lists  ["crypto"
                         "fidelity-select"
                         "bonds"
                         "commodity-industry"
                         "commodity-sector"
                         "currency"
                         "equity-region"
                         "equity-region-country"
                         "equity-sector-industry"
                         "equity-style"
                         "test"]})




#_(def tv-routes
    ["tv/" {:get  {"config" :tv/config
                   "time" :tv/time
                   "search" :tv/search
                   "symbols" :tv/symbols
                   "history" :tv/history}
            "storage/1.1/" {"charts" {:get  :tv-db/load-chart
                                      :post :tv-db/save-chart
                                      :put  :tv-db/modify-chart
                                      :delete  :tv-db/delete-chart}
                            "study_templates" {:get :tv-db/load-template
                                               :post :tv-db/save-template
                                           ;:put :tv-db/modify-template
                                               :delete :tv-db/delete-template}}}])

