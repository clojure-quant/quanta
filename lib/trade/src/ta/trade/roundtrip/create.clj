(ns ta.trade.roundtrip.create
  (:require
   [tablecloth.api :as tc]
   [ta.indicator.helper :refer [indicator]]))

(defn- new-signal [signal]
  (case signal
    :long :long
    :short :short
    :flat :flat
    nil))

(defn signal-action []
  (indicator
   [idx (volatile! 0)
    position (volatile! :flat)
    id (volatile! 0)
    entry (volatile! {:side :flat
                      :entry-idx 0
                      :id 1
                      })]
   (fn [[signal date price]]
     (let [prior-position @position
           new-position (or (new-signal signal) prior-position)
           chg? (not (= new-position prior-position))
           entry? (and chg? (contains? #{:long :short} signal))
           exit? (and chg? (contains? #{:long :short} prior-position))
           roundtrip (when exit?
                       (assoc @entry
                              :exit-idx @idx
                              :exit-price price
                              :exit-date date))
           #_result  #_{:signal signal
                    :entry? entry?
                    :exit? exit?
                    :position new-position
                    :roundtrip roundtrip}
           result roundtrip
           ]
       (when entry?
         (vswap! id inc)
         (vreset! entry {:id @id
                         :entry-idx @idx
                         :side new-position
                         :entry-date date
                         :entry-price price
                         }))
       (vswap! idx inc)
       (vreset! position new-position)
       result))))


(defn signal->roundtrips [signal-ds]
  (assert (:signal signal-ds) "to create roundtrips :signal column needs to be present!")
  (assert (:date  signal-ds) "to create roundtrips :date column needs to be present!")
  (assert (:close  signal-ds) "to create roundtrips :close column needs to be present!")
  (let [n (tc/row-count signal-ds)
        fun (signal-action)
        signal (:signal signal-ds)
        date (:date signal-ds)
        close (:close signal-ds)
        vec (fn [idx]
                    [(signal idx) (date idx) (close idx)])
        map-of-vecs (map vec (range n))
        roundtrips (into [] fun map-of-vecs)
        ]
    (tc/dataset roundtrips)
    ))

(comment

  (require '[tick.core :as t])

  (def ds (tc/dataset {:date [(t/instant "2020-01-01T00:00:00Z")
                              (t/instant "2020-01-02T00:00:00Z")
                              (t/instant "2020-01-03T00:00:00Z")
                              (t/instant "2020-01-04T00:00:00Z")
                              (t/instant "2020-01-05T00:00:00Z")
                              (t/instant "2020-01-06T00:00:00Z")
                              (t/instant "2020-01-07T00:00:00Z")
                              (t/instant "2020-01-08T00:00:00Z")
                              (t/instant "2020-01-09T00:00:00Z")
                              (t/instant "2020-01-10T00:00:00Z")
                              (t/instant "2020-01-11T00:00:00Z")
                              ]
                       :close [1 2 3 4 5 6 7 8 9 10 11]
                       :signal  [:long :hold :long :flat
                                :flat
                                :short :flat :flat
                                :long :short :flat]}))
  ds
  ;; => _unnamed [11 3]:
  ;;    
  ;;    |                :date | :close | :signal |
  ;;    |----------------------|-------:|---------|
  ;;    | 2020-01-01T00:00:00Z |      1 |   :long |
  ;;    | 2020-01-02T00:00:00Z |      2 |   :hold |
  ;;    | 2020-01-03T00:00:00Z |      3 |   :long |
  ;;    | 2020-01-04T00:00:00Z |      4 |   :flat |
  ;;    | 2020-01-05T00:00:00Z |      5 |   :flat |
  ;;    | 2020-01-06T00:00:00Z |      6 |  :short |
  ;;    | 2020-01-07T00:00:00Z |      7 |   :flat |
  ;;    | 2020-01-08T00:00:00Z |      8 |   :flat |
  ;;    | 2020-01-09T00:00:00Z |      9 |   :long |
  ;;    | 2020-01-10T00:00:00Z |     10 |  :short |
  ;;    | 2020-01-11T00:00:00Z |     11 |   :flat |

  (signal->roundtrips ds)
  ;; => _unnamed [4 8]:
  ;;    
  ;;    | :id | :entry-idx |  :side |          :entry-date | :entry-price | :exit-idx | :exit-price |           :exit-date |
  ;;    |----:|-----------:|--------|----------------------|-------------:|----------:|------------:|----------------------|
  ;;    |   1 |          0 |  :long | 2020-01-01T00:00:00Z |            1 |         3 |           4 | 2020-01-04T00:00:00Z |
  ;;    |   2 |          5 | :short | 2020-01-06T00:00:00Z |            6 |         6 |           7 | 2020-01-07T00:00:00Z |
  ;;    |   3 |          8 |  :long | 2020-01-09T00:00:00Z |            9 |         9 |          10 | 2020-01-10T00:00:00Z |
  ;;    |   4 |          9 | :short | 2020-01-10T00:00:00Z |           10 |        10 |          11 | 2020-01-11T00:00:00Z |

; 
  )