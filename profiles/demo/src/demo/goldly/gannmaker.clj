(ns demo.goldly.gannmaker
  (:require
   [clojure.pprint]
   [ta.tradingview.chartmaker :refer [make-chart source-gann]]
   [demo.lib.gann :refer [make-root-box get-boxes-in-window]]
   [ta.data.date :refer [parse-date now-datetime]]
   [ta.data.date :refer [epoch-second->datetime ->epoch-second]]))

(def amazon
  {:ap 1.27
   :at (parse-date "1997-05-15")
   :bp 30.54
   :bt (parse-date "2002-05-28")})

(def root (make-root-box amazon))

(defn convert-box [{:keys [ap bp at bt]}]
  {:symbol "NasdaqNM:AMZN"
   :a-p (Math/pow 10 ap)
   :b-p (Math/pow 10 bp)
   :a-t (->epoch-second at)
   :b-t (->epoch-second bt)})

(defn gann->chart [box]
  (->  (convert-box box)
       (source-gann)))

(-> (get-boxes-in-window root (parse-date "2005-01-01") (parse-date "2021-12-31")
                         (Math/log10 284) (Math/log10 3482))
    (clojure.pprint/print-table))

(->>   (get-boxes-in-window root (parse-date "2005-01-01") (parse-date "2021-12-31")
                            (Math/log10 287) (Math/log10 3482))
      ;first
      ;convert-box
      ;(clojure.pprint/print-table)
      ;gann->chart
       (map gann->chart)
       (into [])
       (make-chart 111  "AMZN" "amazon gann 2005"))

{:bt #time/date-time "2017-07-05T00:00", :zoom 1
 :dt #time/duration "PT44136H", :at #time/date-time "2012-06-22T00:00"
 :dp 1.3810653117644456, :ap 1.4848690327204024, :bp 2.865934344484848, :idx-p 1, :idx-t 3}

Math/exp

(make-chart 333  "AMZN" "amazon ganns langfrist"
            [(source-gann
              {:symbol "NasdaqNM:AMZN"
               :a-p 1517.0  :a-t 1511879400
               :b-p 1794.0  :b-t 1515076200})
             (source-gann
              {:symbol "NasdaqNM:AMZN"
               :a-p 1300.0  :a-t 1511879400
               :b-p 1517.0  :b-t 1515076200})])
