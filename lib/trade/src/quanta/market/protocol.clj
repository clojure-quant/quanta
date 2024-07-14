(ns quanta.market.protocol)

(defmulti connection
  (fn [{:keys [type]}]
    type))


(defmulti get-quote
  (fn [type connection asset]
    type))

