(ns demo.goldly.ui-test.vega-nav
  (:require
   [cljc.java-time.local-date-time :as ldt]
   [cljc.java-time.zone-offset :refer [utc]]
   [tick.goldly]
   [tick.core :as t]
   [demo.goldly.view.vega-nav :refer [vega-nav-plot]]
   [ta.viz.nav-vega :refer [portfolio-nav]]
   ))


(def vega-nav-plot-test-data
  [{:nav 100.0 :index 1}
   {:nav 120.0 :index 2}
   {:nav 150.0 :index 3}])

(vega-nav-plot vega-nav-plot-test-data)

(t/new-date 2020 2 2)
;(t/new-datetime 2020 2 2 0 0 0)
(t/instant "2000-01-01T00:00:00.001Z")
;(t/zoned-date-time "2000-01-01T00:00:00Z[Europe/Paris]")
(t/date-time "2018-01-01T00:00")

(defn d [s]
  (-> (str s "T00:00")
      (t/date-time)
      (t/inst)))

 {:open# 18,
 :long$ 751131.0,
 :short$ 116290.0,
 :net$ 634841.0,
 :pl-u 236591.0,
 :pl-r 3030.000000000001,
 :date #time/date-time "2022-06-01T00:00",
 :pl-r-cum -5629.500000000001}

(def demo-portfolio
  [{:close 100.0 :date (d "2018-01-01") :symbol "DAX"}
   {:close 120.0 :date (d "2019-01-01") :symbol "DAX"}
   {:close 150.0 :date (d "2020-01-01") :symbol "SPY"}])

(portfolio-nav demo-portfolio)