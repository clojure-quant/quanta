(ns ta.tradingview.template
  (:require
   [ta.tradingview.chartmaker :refer [create-study make-chart]]
   [ta.tradingview.template.trendline :as tl]
   [ta.tradingview.template.pitchfork :as pf]
   [ta.tradingview.template.gann :as g]))

(defn trendline [{:keys [symbol template ap bp at bt]
                  :or {template tl/trendline}}]
  (create-study template symbol
                [{:time_t at, :offset 0, :price ap}
                 {:time_t bt, :offset 0, :price bp}]))

(defn pitchfork [{:keys [symbol template ap bp cp at bt ct]
                  :or {template pf/pitchfork}}]
  (create-study template symbol
                [{:time_t at, :offset 0, :price ap}
                 {:time_t bt, :offset 0, :price bp}
                 {:time_t ct, :offset 0, :price cp}]))

(defn gann [{:keys [symbol template ap bp at bt]
             :or {template g/gann}}]
  (create-study template symbol
                [{:time_t at, :offset 0, :price ap}
                 {:time_t bt, :offset 0, :price bp}]))


(defn gann-vertical [symbol p-0 d-p n a-t b-t]
  (into []
        (for [i (range n)]
          (gann
           {:symbol symbol
            :ap (+ p-0 (* i d-p))  :at a-t
            :bp (+ p-0 (* (inc i) d-p)) :bt b-t}))))


(comment

  (def id-generated 123)

  (make-chart 77 77 id-generated "MSFT" "test-empty-MSFT"
              [(trendline {:symbol "MSFT"
                           :a-p 300.0
                           :b-p 330.0
                           :a-t (dt "2021-08-04T00:00:00")
                           :b-t (dt "2021-11-04T00:00:00")})])

  (def id-compare 1636726545)

  ; test: chart-meta-data
  (-> [(-> (load-chart 77 77 id-generated)  (dissoc :charts :timeScale :legs))
       (-> (load-chart 77 77 id-compare)  (dissoc :charts :timeScale :legs))]
      print-table)

  ; test: sources list
  (-> (load-chart 77 77 id-generated) sources-summary)
  (-> (load-chart 77 77 id-compare)   sources-summary)

  ; test: mainseries keys
  (-> (load-chart 77 77 id-generated)       (filter-type "MainSeries") keys-sorted)
  (-> (load-chart 77 77 id-compare)  (filter-type "MainSeries") keys-sorted)

   ; test: mainseries state
  (-> (load-chart 77 77 id-generated)       (filter-type "MainSeries") source-state-summary)
  (-> (load-chart 77 77 id-compare)  (filter-type "MainSeries") source-state-summary)


  ; test mainseries differences (in both ways)
  (differ/diff
   (-> (load-chart 77 77 id-generated) (filter-type "MainSeries"))
   (-> (load-chart 77 77 id-compare)  (filter-type "MainSeries")))

  (differ/diff
   (-> (load-chart 77 77 id-compare)  (filter-type "MainSeries"))
   (-> (load-chart 77 77 id-generated) (filter-type "MainSeries")))


  ; test study differences (in both ways)
  (differ/diff
   (-> (load-chart 77 77 id-generated) (filter-type "Study"))
   (-> (load-chart 77 77 id-compare)  (filter-type "Study")))

  (differ/diff
   (-> (load-chart 77 77 id-compare)  (filter-type "Study"))
   (-> (load-chart 77 77 id-generated) (filter-type "Study")))

    ; test study differences (in both ways)
  (diff-summary
   #(dissoc % :id :charts :symbol_type :exchange :timestamp :symbol :name :short_name :publish_request_id :legs)
   id-generated id-compare)

   ;; no differences in mainseries
  (diff-summary
   #(filter-type % "MainSeries")
   id-generated id-compare)

   ;; no differences in study (except for ids)
  (diff-summary
   #(filter-type % "Study")
   id-generated id-compare)

   ;; no differences in except ids of sources
  (diff-summary
   #(-> (get-pane %) (dissoc :sources))
   id-generated id-compare)

  (diff-summary
   #(dissoc % :symbol :name :short_name :publish_request_id :legs :id :exchange :timestamp :symbol_type)
         ;identity
   id-generated id-compare)


  (gann-vertical 1000.0 200.0 5 1511879400 1515076200)







;  
  )