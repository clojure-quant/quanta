(ns ta.trade.roundtrip.roundtrip
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tablecloth.api :as tc]
   [quanta.trade.roundtrip :refer [sign-switch]]
   ))


(defn- adjust [val-vec side-vec]
  (dtype/emap sign-switch :float64 side-vec val-vec))

(defn add-performance [roundtrip-ds]
  (let [roundtrip-ds (tc/order-by roundtrip-ds [:exit-date] [:asc])
        {:keys [side qty entry-price exit-price exit-idx entry-idx]} roundtrip-ds
        _ (assert side)
        _ (assert entry-price)
        _ (assert exit-price)
        entry-volume (dfn/* qty entry-price)
        exit-volume (dfn/* qty exit-price)
        ret-volume (adjust (dfn/- exit-volume entry-volume) side)
        ret-abs (adjust (dfn/- exit-price entry-price) side)
        ret-prct (-> 100.0 (dfn/* ret-abs) (dfn// entry-price))
        ret-log (adjust (dfn/- (dfn/log10 exit-price) (dfn/log10 entry-price)) side)
        cum-ret-volume  (dfn/cumsum ret-volume)
        cum-ret-abs  (dfn/cumsum ret-abs)
        cum-ret-log  (dfn/cumsum ret-log)
        cum-ret-prct  (dfn/cumsum ret-prct)]
    (tc/add-columns roundtrip-ds
                    {:ret-abs ret-abs
                     :ret-prct ret-prct
                     :ret-log ret-log
                     :win? (dfn/> ret-abs 0.0)
                     :bars (if (and entry-idx exit-idx)
                             (dfn/- exit-idx entry-idx)
                             0)
                     :cum-ret-volume cum-ret-volume
                     :cum-ret-abs cum-ret-abs
                     :cum-ret-log cum-ret-log
                     :cum-ret-prct cum-ret-prct
                     :nav (dfn/+ (Math/log10 100.0) cum-ret-log)})))

(comment
  
  (def ds
    (tc/dataset {:side [:long :short :long :short]
                 :entry-idx [1 2 3 4]
                 :exit-idx [2 3 4 5]
                 :entry-date [:d1 :d2 :d3 :d4]
                 :exit-date [:d2 :d3 :d4 :d5]
                 :entry-price [1.0 2.0 3.0 4.0]
                 :exit-price [2.0 3.0 4.0 5.0]}))

  (add-performance ds)
   ;; => _unnamed [4 14]:
   ;;    
   ;;    |  :side | :entry-idx | :exit-idx | :entry-date | :exit-date | :entry-price | :exit-price | :ret-abs |    :ret-prct |    :ret-log | :win? | :bars | :cum-ret-log |       :nav |
   ;;    |--------|-----------:|----------:|-------------|------------|-------------:|------------:|---------:|-------------:|------------:|-------|------:|-------------:|-----------:|
   ;;    |  :long |          1 |         2 |         :d1 |        :d2 |            1 |           2 |        1 | 100.00000000 | -0.30103000 |  true |     1 |  -0.30103000 | 1.69897000 |
   ;;    | :short |          2 |         3 |         :d2 |        :d3 |            2 |           3 |       -1 | -50.00000000 |  0.17609126 | false |     1 |  -0.12493874 | 1.87506126 |
   ;;    |  :long |          3 |         4 |         :d3 |        :d4 |            3 |           4 |        1 |  33.33333333 | -0.12493874 |  true |     1 |  -0.24987747 | 1.75012253 |
   ;;    | :short |          4 |         5 |         :d4 |        :d5 |            4 |           5 |       -1 | -25.00000000 |  0.09691001 | false |     1 |  -0.15296746 | 1.84703254 |

  (-> (add-performance ds)
      (tc/info))

  (Math/pow 10 2.8132)

  (dfn/cumsum [1 2 3])
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