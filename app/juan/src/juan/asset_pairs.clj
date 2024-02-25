(ns juan.asset-pairs)

(def asset-pairs
  [; usd pairs
   {:fx "EURUSD" :future "EU" :pip 0.001}
   {:fx "USDCHF" :future "SF" :pip 0.001}
   {:fx "GBPUSD" :future "BP" :pip 0.001}
   {:fx "USDSEK" :future "SEK" :pip 0.01}
   {:fx "USDNOK" :future "NOK" :pip 0.01} 
   {:fx "USDCAD" :future "CD" :pip 0.001}
   {:fx "USDJPY" :future "JY" :pip 0.1}
   {:fx "AUDUSD" :future "AD" :pip 0.001}
   {:fx "NZDUSD" :future "NE" :pip 0.001}
   ; usd pairs emerging   
   ;{:fx "BRLUSD" :future "BR"} ; no sentiment numbers
   {:fx "USDMXN" :future "PX" :pip 0.01} 
   ;{:fx "USDRUB" :future "RU" :pip 0.1} ; no fxcm data
   {:fx "USDZAR" :future "RA" :pip 0.01} 
   ; eur pairs
   {:fx "EURAUD" :future "EAD" :pip 0.001}
   {:fx "EURCAD" :future "ECD" :pip 0.001}
   {:fx "EURJPY" :future "RY" :pip 0.1}
   {:fx "EURCHF" :future "RF" :pip 0.001}
   {:fx "EURGBP" :future "RP" :pip 0.001}
   ; gbp pairs
   {:fx "GBPJPY" :future "PJY" :pip 0.1}
   ])
