(ns juan.asset-pairs)

(def asset-pairs
  [; usd pairs
   {:fx "EUR/USD" :future "EU" :pip 0.001}
   {:fx "USD/CHF" :future "SF" :pip 0.001}
   {:fx "GBP/USD" :future "BP" :pip 0.001}
   {:fx "USD/SEK" :future "SEK" :pip 0.01}
   {:fx "USD/NOK" :future "NOK" :pip 0.01}
   {:fx "USD/CAD" :future "CD" :pip 0.001}
   {:fx "USD/JPY" :future "JY" :pip 0.1}
   {:fx "AUD/USD" :future "AD" :pip 0.001}
   {:fx "NZD/USD" :future "NE" :pip 0.001}
   ; usd pairs emerging   
   ;{:fx "BRL/USD" :future "BR"} ; no sentiment numbers
   {:fx "USD/MXN" :future "PX" :pip 0.01}
   ;{:fx "USD/RUB" :future "RU" :pip 0.1} ; no fxcm data
   {:fx "USD/ZAR" :future "RA" :pip 0.01}
   ; eur pairs
   ;{:fx "EUR/AUD" :future "EAD" :pip 0.001} ; fails on fix-quotes
   ;{:fx "EUR/CAD" :future "ECD" :pip 0.001} ; fails on fix-quotes
   {:fx "EUR/JPY" :future "RY" :pip 0.1}
   {:fx "EUR/CHF" :future "RF" :pip 0.001}
   {:fx "EUR/GBP" :future "RP" :pip 0.001}
   ; gbp pairs
   {:fx "GBP/JPY" :future "PJY" :pip 0.1}])
