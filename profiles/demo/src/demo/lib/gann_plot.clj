(ns demo.lib.gann-plot
  (:require
   [cljc.java-time.duration :as duration]
   [tick.core :as tick :refer [>>]]
   [tick.alpha.interval :as t.i]
   [ta.data.date :refer [parse-date now-datetime]]
   [demo.lib.svg :refer [svg-view]]
   [demo.lib.gann :refer [get-boxes-in-window root]]
   [goldly.scratchpad :refer [show! show-as clear!]]))

;; Gann Box (Box + Fan)

(defn add-fraction [{:keys [at dt] :as box} f]
  (let [d-f (duration/divided-by dt f)]
    (tick/>> at d-f)))

(defn remove-fraction [{:keys [bt dt] :as box} f]
  (let [d-f (duration/divided-by dt f)]
    (tick/<< bt d-f)))


(defn gann-plot [opts {:keys [ap bp at bt] :as box}]
  (let [line-box (fn [t0 p0 t1 p1]
                   [:line {:color "blue"} [t0 p0] [t1 p1]])
        line-fan (fn [t0 p0 t1 p1]
                   [:line {:color "green"} [t0 p0] [t1 p1]])]
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



       ;(line bt bp bt ap) ; c-1-1
     ]))

(comment
  (gann-plot nil root)

;
  )


(defn get-close-prices [symbol dt-start dt-end]
  [[(parse-date "2021-03-01") (Math/log10 50000)]
   [(parse-date "2021-07-01") (Math/log10 40000)]
   [(parse-date "2021-08-01") (Math/log10 60000)]])

(defn get-gann-spec [symbol dt-start dt-end]
  (let [close-series (get-close-prices symbol dt-start dt-end)  ; vec of float
        px-max (Math/log10 70000) ; (apply max close-series)
        px-min (Math/log10 100) ; (apply min close-series)
        boxes (get-boxes-in-window
               root dt-start dt-end
               px-min px-max)
        boxes-plotted (apply concat (map #(gann-plot {} %) boxes))
        series-plotted [:series {:color "red"} close-series]]
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
     (conj boxes-plotted series-plotted))))

(comment
  (show!
   (get-gann-spec
    "BTCUSD"
    (parse-date "2010-01-01")
    (parse-date "2021-12-31")))


 ; 
  )






