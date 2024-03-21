(ns ta.tradingview.handler.marks
  (:require
   [ring.util.response :as res]))

(def demo-marks
  {:id [0 1 2 3 4 5]
   :time [1568246400 1567900800 1567641600 1567641600 1566950400 1565654400]
   :label ["A" "B" "CORE" "D" "EURO" "F"]
   :text ["Today"
          "4 days back"
          "7 days back + Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
          "7 days back once again"
          "15 days back"
          "30 days back"]
   :color ["red" "blue" "green" "red" "blue" "green"]
   :labelFontColor ["white" "white" "red" "#FFFFFF" "white" "#000"]
   :minSize [14 28 7 40 7 14]})

(defn marks-handler [{:keys [query-params] :as req}]
  ; https://demo_feed.tradingview.com/marks?symbol=AAPL&from=1488810600&to=1491226200&resolution=D
  (let [{:keys [symbol resolution from to]} (clojure.walk/keywordize-keys query-params)
        from (Integer/parseInt from)
        to (Integer/parseInt to)
        ;marks (load-marks symbol resolution from to)
        ;_ (info "marks: " marks)
        marks demo-marks]
    (res/response marks)))