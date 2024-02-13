(ns ta.env.javelin.backtest
  (:require
   [ta.calendar.combined :refer [combined-event-seq]]
   [ta.env.javelin.calendar :as cal]))


(defn run-backtest [env w]
  (let [cals (cal/active-calendars env)
        event-seq (combined-event-seq w cals)]
    (doall (map #(cal/set-calendar! env %) event-seq))
    :backtest-finished
    ))

(comment 
  
 (require '[ta.env.javelin.env :as e]) 
 (require '[javelin.core-clj :refer [cell cell=]]) 
 (require '[ta.env.tools.window :as tw]) 
  
 (require '[modular.system])
 (def duckdb (:duckdb modular.system/system))
    

 (defn demo-backtest []
   (let [env (e/create-env duckdb)
         c (cal/get-calendar env [:us :h]) ; force creation of one calendar
         printer (cell= (println "now: " c)) ; track calendar time and print it.
         count-a (atom 0)
         inc-counter (fn [cell]
                       (swap! count-a inc))
         counter (cell= (inc-counter c))
         w (tw/trailing-years-window 1)]
        (run-backtest env w)  
        @count-a
     ))
  
  ;; this will print "now: DATE" for all hours in the last year
  ;; will return the number of hourly bars in the last year.
  (demo-backtest)


  
  
  
  
 ; 
  )

