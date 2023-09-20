(ns demo.data-import.import-alphavantage
  (:require
   [ta.data.import :refer [import-series import-list]]))


  ; alphavantage has different api for stocks and fx
  ; so test both
  (import-series :alphavantage "MSFT" "D" :full)
  (import-series :alphavantage "EURUSD" "D" :full)

  
  ; import lists of symbols
  ; test is a symbol list (see app/resources/symbollist)
  (import-list ["MSFT" "EURUSD"] "D" :full)
  (import-list "test" "D" :full)
