(ns ta.trade.data
  (:require
    [tick.core :as t]
    [tablecloth.api :as tc]))


(def bar-entry-ds (tc/dataset {:asset ["BTC" "BTC" "BTC"]
                          :close [1.0 2.0 3.0]
                          :low [1.0 2.0 3.0]
                          :high [1.0 2.0 3.0]
                          :date [(t/instant "1999-02-01T20:00:00Z")
                                 (t/instant "2000-02-01T20:00:00Z")
                                 (t/instant "2001-02-01T20:00:00Z")]
                          :entry [:flat :short :flat]}))


(def time-bar-entry-ds (tc/dataset {:asset ["BTC" "BTC" "BTC" "BTC" "BTC" "BTC"]
                               :close [1.0 2.999 3.01 3.01 3.01 3.01]
                               :low [1.0 2.999 3.01 3.01 3.01 3.01]
                               :high [1.0 2.999 3.01 3.01 3.01 3.01]
                               :date [(t/instant "2000-01-01T20:00:00Z")
                                      (t/instant "2000-02-01T20:00:00Z")
                                      (t/instant "2000-03-01T20:00:00Z")
                                      (t/instant "2000-04-01T20:00:00Z")
                                      (t/instant "2000-05-01T20:00:00Z")
                                      (t/instant "2000-06-01T20:00:00Z")]
                               :entry [:flat :short :flat :flat :flat :flat]}))

(def profit-bar-entry-ds (tc/dataset {:asset ["BTC" "BTC" "BTC" "BTC" "BTC" "BTC"]
                                    :close [1.0 2.999 1.01 3.01 3.01 3.01]
                                    :low [1.0 2.999 1.01 3.01 3.01 3.01]
                                    :high [1.0 2.999 1.01 3.01 3.01 3.01]
                                    :date [(t/instant "2000-01-01T20:00:00Z")
                                           (t/instant "2000-02-01T20:00:00Z")
                                           (t/instant "2000-03-01T20:00:00Z")
                                           (t/instant "2000-04-01T20:00:00Z")
                                           (t/instant "2000-05-01T20:00:00Z")
                                           (t/instant "2000-06-01T20:00:00Z")]
                                    :entry [:flat :short :flat :flat :flat :flat]}))