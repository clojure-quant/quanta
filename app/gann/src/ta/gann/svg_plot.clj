(ns ta.gann.svg-plot
  (:require
   [cljc.java-time.duration :as duration]
   [tick.core :as tick :refer [>>]]
   [tick.alpha.interval :as t.i]
   [tablecloth.api :as tc]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.functional :as dfn]
   [ta.gann.svg-view :refer [svg-view]]
   ;  [ta.gann.gann :refer [get-boxes-in-window make-root-box zoom-out zoom-in load-root-box]]
   [ta.gann.window :as window]
   [ta.gann.db :refer [sanitize-root-box prepare-box]]))

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

(defn gann-svg [opts]
  (let [{:keys [symbol px-min px-max dt-start dt-end boxes close-series]
         :or {symbol "x"}} (window/get-gann-data opts)
        height (or (:height opts) 1000)
        width (or (:width opts) 1000)
        boxes-plotted (apply concat (map #(gann-plot {} %) boxes))
        series-plotted [:series {:color "red"} close-series]]
    (println "generating gann-svg width:" width "height:" height)
    ;[:div
     ;[:h1 (str "gann: " symbol " " dt-start " - " dt-end " box-count: " (count boxes))]
    (svg-view
     {:min-px px-min
      :max-px px-max
      :min-dt dt-start
      :max-dt dt-end
      :svg-width  width
      :svg-height height}
     ;(gann-plot {} (first boxes))
     ;(concat boxes-plotted series-plotted)
     ;boxes-plotted
     (conj boxes-plotted series-plotted))
  ;  ]
    ))

(defn sanitize-box [root-box]
  (-> root-box
      sanitize-root-box
      prepare-box))

(defn fix-opts-web-request [{:keys [root-box] :as opts}]
  (if root-box
    (assoc opts :root-box (sanitize-box root-box))
    opts))

(defn gann-svg-web [opts]
  (-> opts
      fix-opts-web-request
      gann-svg))

(defn get-boxes [opts]
  (->> opts
       fix-opts-web-request
       window/get-gann-data
       :boxes
       (map #(dissoc % :dt))))

(comment

  (gann-svg {:s "GLD"
             ;:dt-start "2021-01-01"
             ;:dt-end "2021-12-31"
             :height 500
             :width 500})

  (gann-svg-web {:s "GLD"
                  ;:dt-start "2021-01-01"
                  ;:dt-end "2021-12-31"
                 :height 500
                 :width 500
                 :root-box {:symbol "GLD"
                            :ap 77.24
                            :at "2000-08-18"
                            :bp 153.48
                            :bt "2002-10-10"}})

; 
  )








