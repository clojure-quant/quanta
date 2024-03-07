

(def report-db-stats-datahike
  {:name :admin/db-stats-datahike
   :opts-fn (fn [] {:current {:cached false}
                    :options []})
   :calc-fn (fn [_]
              (load-db-stats))
   :ui-fn 'crb.web.report.table/table-ui
   :cols [{:path :type}
          {:path :count}]})