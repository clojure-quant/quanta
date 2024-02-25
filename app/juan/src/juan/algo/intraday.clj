(ns juan.algo.intraday
  (:require
   [tablecloth.api :as tc]))


(defn ensure-date-unique [_env _spec bar-ds]
  (tc/unique-by bar-ds :date))
