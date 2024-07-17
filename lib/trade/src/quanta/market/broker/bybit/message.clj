(ns quanta.market.broker.bybit.message)


(defn parse-bybit-trade [{:keys [s p v T]}]
  {:asset s
   :price p
   :size v
   :time T})



;({:asset BTCUSDT, :price 60007.77, :size 9.9E-5}
; {:asset BTCUSDT, :price 60007.94, :size 0.003858}
; {:asset BTCUSDT, :price 60008.02, :size 0.001843})
;({:asset BTCUSDT, :price 60008.97, :size 0.0049})

