(ns juan.asset-pairs)

(def asset-pairs
  [; usd pairs
   {:fx "EUR/USD" :future "EU" :pip 0.001 :ctrader 1} 
   {:fx "USD/CHF" :future "SF" :pip 0.001 :ctrader 6}
   {:fx "GBP/USD" :future "BP" :pip 0.001 :ctrader 2}
   {:fx "USD/SEK" :future "SEK" :pip 0.01 :ctrader 66}
   {:fx "USD/NOK" :future "NOK" :pip 0.01 :ctrader 22}
   {:fx "USD/CAD" :future "CD" :pip 0.001 :ctrader 8}
   {:fx "USD/JPY" :future "JY" :pip 0.1 :ctrader 4}
   {:fx "AUD/USD" :future "AD" :pip 0.001 :ctrader 5}
   {:fx "NZD/USD" :future "NE" :pip 0.001 :ctrader 12}
   ; usd pairs emerging   
   ;{:fx "BRL/USD" :future "BR"} ; no sentiment numbers
   {:fx "USD/MXN" :future "PX" :pip 0.01 :ctrader 94}
   ;{:fx "USD/RUB" :future "RU" :pip 0.1} ; no fxcm data
   {:fx "USD/ZAR" :future "RA" :pip 0.01 :ctrader 45}
   ; eur pairs
   ;{:fx "EUR/AUD" :future "EAD" :pip 0.001} ; fails on fix-quotes
   ;{:fx "EUR/CAD" :future "ECD" :pip 0.001} ; fails on fix-quotes
   {:fx "EUR/JPY" :future "RY" :pip 0.1 :ctrader 3}
   {:fx "EUR/CHF" :future "RF" :pip 0.001 :ctrader 10}
   {:fx "EUR/GBP" :future "RP" :pip 0.001 :ctrader 9}
   ; gbp pairs
   {:fx "GBP/JPY" :future "PJY" :pip 0.1 :ctrader 7}])


