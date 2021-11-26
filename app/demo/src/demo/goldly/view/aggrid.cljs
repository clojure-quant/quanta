



(defn table [data]
  [aggrid {:data data
           :box :lg
           :pagination :false
           :paginationAutoPageSize true}])