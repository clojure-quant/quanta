(ns quanta.trade.position.entry.size)


(defmulti positionsize
  (fn [[type _opts] _close] type))

(defmethod positionsize :fixed-qty
  [[_type fixed-qty] _close]
  fixed-qty)

(defmethod positionsize :fixed-amount
  [[_type fixed-qty] close]
  (/ fixed-qty close))