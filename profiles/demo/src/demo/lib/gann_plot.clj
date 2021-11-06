(ns demo.lib.gann-plot
  (:require
   [cljc.java-time.duration :as duration]
   [tick.core :as tick :refer [>>]]
   [tick.alpha.interval :as t.i]
   [ta.data.date :refer [parse-date now-datetime]]
   [ta.warehouse :refer [load-symbol]]
   [tablecloth.api :as tc]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.functional :as dfn]
   [demo.lib.svg :refer [svg-view]]
   [demo.lib.gann :refer [get-boxes-in-window make-root-box zoom-out zoom-in]]
   [demo.lib.gann-data :as boxes :refer [load-root-box]]
   [goldly.scratchpad :refer [show! show-as clear!]]))

;; Gann Box (Box + Fan)

(defn add-fraction [{:keys [at dt] :as box} f]
  (let [d-f (duration/divided-by dt f)]
    (tick/>> at d-f)))

(defn remove-fraction [{:keys [bt dt] :as box} f]
  (let [d-f (duration/divided-by dt f)]
    (tick/<< bt d-f)))


(defn gann-plot [opts {:keys [ap bp at bt dp dt] :as box}]
  (let [line-box (fn [t0 p0 t1 p1]
                   [:line {:color "blue"} [t0 p0] [t1 p1]])
        line-fan (fn [t0 p0 t1 p1]
                   [:line {:color "green"} [t0 p0] [t1 p1]])
        circle (fn [p t f]
                 [:ellipse {:style {:stroke "red"}
                            :cy p
                            :cx t
                            :ry (/ (* dp 5.0) (float f))
                            :rx (duration/divided-by (cljc.java-time.duration/multiplied-by dt 5.0) f)}])]
    [(line-box at ap bt ap)
     (line-box at bp bt bp)
     (line-box at ap at bp)
     (line-box bt ap bt bp)

     (line-fan at ap bt bp) ; a-1-1
     (line-fan at bp bt ap) ; b-1-1

     (line-fan at ap (add-fraction box 2) bp)
     (line-fan at ap (add-fraction box 3) bp)
     (line-fan at ap (add-fraction box 4) bp)
     (line-fan at ap (add-fraction box 8) bp)

     (line-fan at bp (add-fraction box 2) ap)
     (line-fan at bp (add-fraction box 3) ap)
     (line-fan at bp (add-fraction box 4) ap)
     (line-fan at bp (add-fraction box 8) ap)

     (line-fan bt ap (remove-fraction box 2) bp)
     (line-fan bt ap (remove-fraction box 3) bp)
     (line-fan bt ap (remove-fraction box 4) bp)
     (line-fan bt ap (remove-fraction box 8) bp)

     (line-fan bt bp (remove-fraction box 2) ap)
     (line-fan bt bp (remove-fraction box 3) ap)
     (line-fan bt bp (remove-fraction box 4) ap)
     (line-fan bt bp (remove-fraction box 8) ap)

     #_[:ellipse {:style {:stroke "red"}
                  :cy ap
                  :cx at
                  :ry dp
                  :rx dt}]
     (circle ap at 5)
     (circle bp bt 5)
     (circle ap bt 5)
     (circle bp at 5)

     (circle ap at 4)
     (circle bp bt 4)
     (circle ap bt 4)
     (circle bp at 4)

     (circle ap at 3)
     (circle bp bt 3)
     (circle ap bt 3)
     (circle bp at 3)

     (circle ap at 2)
     (circle bp bt 2)
     (circle ap bt 2)
     (circle bp at 2)

     (circle ap at 1)
     (circle bp bt 1)
     (circle ap bt 1)
     (circle bp at 1)]))



(comment


  (gann-plot nil boxes/btc-box)

;
  )

;; get stock prices

(defn get-close-prices-test [symbol dt-start dt-end]
  [[(parse-date "2021-03-01") (Math/log10 50000)]
   [(parse-date "2021-07-01") (Math/log10 40000)]
   [(parse-date "2021-08-01") (Math/log10 60000)]])

(defn row-in-range [dt-start dt-end {:keys [date] :as row}]
  (and (tick/>= date dt-start)
       (tick/<= date dt-end)))

(defn get-prices [wh symbol dt-start dt-end]
  (let [ds (-> (load-symbol wh "D" symbol)
               (tc/select-rows
                (partial row-in-range dt-start dt-end))
               (tc/select-columns [:date :close]))
        ds-log (tc/add-columns ds {:close-log (dfn/log10 (:close ds))})]
    {:series (mapv (juxt :date :close-log) (tds/mapseq-reader ds-log))
     :px-min (apply min (:close-log ds-log))
     :px-max (apply max (:close-log ds-log))
     :count (count (:close-log ds-log))}))

(comment
  (-> (get-prices :crypto "BTCUSD" (parse-date "2021-01-01") (parse-date "2021-12-31"))
      (dissoc :series))

  (-> (get-prices :stocks "GLD" (parse-date "2021-01-01") (parse-date "2021-12-31"))
      (dissoc :series))

  (-> (get-boxes-in-window boxes/gld-box (parse-date "2021-01-01") (parse-date "2021-12-31")
                           (Math/log10 2000) (Math/log10 3000))
      (clojure.pprint/print-table))

;
  )


(defn get-gann-spec [{:keys [wh symbol dt-start dt-end root-box]
                      :or {root-box (load-root-box symbol)}
                      }]
  (let [data (get-prices wh symbol dt-start dt-end)  ; vec of float
        px-min (:px-min data)  ;(Math/log10 3000) 
        px-max (:px-max data) ; (Math/log10 70000) ; ; 
        close-series (:series data)
        boxes (if root-box 
                (get-boxes-in-window root-box dt-start dt-end px-min px-max)
                [])
        boxes-plotted (apply concat (map #(gann-plot {} %) boxes))
        series-plotted [:series {:color "red"} close-series]]
    [:div
     [:h1 (str "gann: " symbol " " dt-start " - " dt-end " box-count: " (count boxes))]
     (svg-view
      {:min-px px-min
       :max-px px-max
       :min-dt dt-start
       :max-dt dt-end
       :svg-width 1000
       :svg-height 1000}
     ;(gann-plot {} (first boxes))
     ;(concat boxes-plotted series-plotted)
     ;boxes-plotted
      (conj boxes-plotted series-plotted))]))

(comment

  root

  (show!
   (get-gann-spec {:wh :crypto
                   :symbol "BTCUSD"
                   :dt-start (parse-date "2021-01-01")
                   :dt-end (parse-date "2021-12-31")}))
  
  (show!
   (get-gann-spec {:wh :crypto
                   :symbol "BTCUSD"
                   :dt-start (parse-date "2021-01-01")
                   :dt-end (parse-date "2021-12-31")
                   :root-box (zoom-out boxes/btc-box)
                   }))
  
  (show!
   (get-gann-spec {:wh :crypto
                   :symbol "BTCUSD"
                   :dt-start (parse-date "2021-01-01")
                   :dt-end (parse-date "2021-12-31")
                   :root-box (zoom-in boxes/btc-box)}))

  (show!
   (get-gann-spec {:wh :crypto
                   :symbol "BTCUSD"
                   :dt-start (parse-date "2021-01-01")
                   :dt-end (parse-date "2021-12-31")
                   :root-box (zoom-in (zoom-in (zoom-in (zoom-in boxes/btc-box))))}))

  (show!
   (get-gann-spec {:wh :stocks
                   :symbol  "SPY"
                   :dt-start (parse-date "2021-01-01")
                   :dt-end (parse-date "2021-12-31")}))

   (show!
    (get-gann-spec {:wh :stocks
                    :symbol  "SPY"
                    :dt-start (parse-date "2020-01-01")
                    :dt-end (parse-date "2021-12-31")
                    :root-box (zoom-in boxes/sup-box)
                    }))

  
  (show!
   (get-gann-spec {:wh :stocks
                   :symbol  "GLD"
                   :dt-start (parse-date "2005-01-01")
                   :dt-end (parse-date "2021-12-31")
                   ;:root-box (zoom-in (zoom-in boxes/gld-box))
                   }))



  ; 
  )







