(ns ta.trade.position-pl
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]
   [ta.helper.ago :refer [xf-future]]))

(comment

  (Math/pow 10 2.8132)

  ; we want to operate on log-10. With them *10 = 1
  (->>  (Math/log10 13)
        (Math/pow 10))

  (defn log10 [a]
    (Math/log10 a))

  (->>  [0.01 0.1 1 10 100 100]
        (map log10))
  ; negative logs mean we have lost money
  ; so log-pl negative=loss positive=profit

  (let [lo (log10 5601.5)
        lc (log10 57159.0)
        d (- lc lo)]
    (Math/pow d 10))
     ; 1.09    1=*10
     ;          0.09 = + a little bit

  (let [p 120
        l 40
        plog (Math/log10 p)
        llog (Math/log10 l)
        diff (- plog llog)]
    [plog llog diff (Math/pow 10 diff)])

  (- (Math/log10 101) (Math/log10 100)) ; 1% 0.004

  (- (Math/log10 120) (Math/log10 100)) ; 20% 0.08
  (- (Math/log10 1200) (Math/log10 1000)) ; 20% 0.08
  (- (Math/log10 1000) (Math/log10 2000)) ; -0.3
  (- (Math/log10 2000) (Math/log10 1000)) ; +0.3

;   
  )

(defn roundtrip-pl [position chg-p]
  (case position
    :short (- 0 chg-p)
    :long chg-p
    0.0))

(defn position-pl [close position]
  (println "POSITION PL close: " close "pos: " position)
  (let [close-f1  (into [] xf-future close)
         _ (println "log ...")
        log-close (fun/log10 close)
        log-close-f1 (fun/log10 close-f1)
        d-log-c-f1 (fun/- log-close-f1 log-close)
        _ (println "roundtrip - pl ...")
        pl-log (dtype/emap roundtrip-pl :float64 position d-log-c-f1)]
    (println "posizion-pl done!")
    (println "pl-log: " pl-log)
    pl-log))

(comment 
  (require '[tablecloth.api :as tc])
  
  (def pos-ds (tc/dataset {:close [1 2 3 4 5 6 7]
                           :position [:long :short :flat :long :short :flat :flat]}))

  (position-pl (:close pos-ds) (:position pos-ds))

  
  
  
 ; 
  )