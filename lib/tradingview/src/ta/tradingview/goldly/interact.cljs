(ns ta.tradingview.goldly.interact
  (:require
     [reagent.core :as r])
  )


(defn extract-range [r]
  (let [from (.-from r)
        to (.-to r)]
    {:from from :to to}))

(defonce state (r/atom {}))

(def tv-widget-atom (r/atom nil))

(defn chart-active []
  (.activeChart @tv-widget-atom))

(defn on-data-loaded [& _args]
  (println "tv data has been loaded (called after set-symbol)")
  )

(defn set-symbol 
  ([symbol interval]
      ;(println "tv set symbol:" symbol "interval: " interval)
   (set-symbol symbol interval on-data-loaded))
  ([symbol interval on-load-finished]
    ;(println "tv set symbol:" symbol "interval: " interval)
    (.setSymbol @tv-widget-atom symbol interval on-load-finished)
    nil))

;; STUDY / INDICAOR

(defn study-list []
  (let [studies-js (.getStudiesList @tv-widget-atom)]
      (.log js/console)
      (js->clj studies-js)))

; JSServer.studyLibrary.push.apply(JSServer.studyLibrary,e)

(defn add-study [study-name study-args]
  (let [chart (chart-active)
        study-args-js (-> study-args vec clj->js)]
    ;(println "create study: " study-name " args:" study-args)
    (.createStudy chart study-name false false study-args-js)
    nil))

(defn remove-all-studies []
  ;(println "remove-all-studies")
  (let [chart (chart-active)]
    (.removeAllStudies chart)
    nil))


; window.tvWidget.activeChart().dataReady(() => {

(defn wrap-chart-ready [f]
  ; It's now safe to call any other methods of the widget
  (.onChartReady @tv-widget-atom f))

; (wrap-chart-ready (fn [] (println "chart ready!")))

(defn wrap-header-ready [f]
  (.headerReady @tv-widget-atom f))

; (wrap-header-ready (fn [] (println "header ready!")))

(defn add-header-button [text tooltip on-click-fn]
  (let [options (clj->js nil)
        button (.createButton @tv-widget-atom options)]
    ;(println "button: " button)
    (set! (.-textContent button) text)
    (set! (.-title button) tooltip)
    (.addEventListener button "click" on-click-fn)))


(defn get-symbol []
  (let [i (.symbolInterval @tv-widget-atom)
        symbol (.-symbol i)
        interval (.-interval i)]
    ;(println "symbol: " symbol "interval: " interval)
    {:symbol symbol :interval interval}))

(defn on-crosshair-moved [f]
  (let [chart (chart-active)
        cross-hair (.crossHairMoved chart)]
    (.subscribe cross-hair nil f)))

(defn print-position [r]
  (let [price (.-price r)
        time (.-time r)
        position {:price price :time time}]
    ;(set! (.-bongo js/globalThis) r)
    ;(println "tv pos: " position)
    (swap! state assoc :position position)))

(defn demo-crosshair []
  (on-crosshair-moved print-position))


(defn get-range []
  (let [chart (chart-active)]
    (-> (.getVisibleRange chart)
        (extract-range))))

(defn set-range [{:keys [_from _to] :as range} opts] ; Date.UTC (2012, 2, 3) / 1000,
  (let [chart (chart-active)
        range-js (clj->js range)
        opts (or opts {})
        opts-js (clj->js opts)]
    (-> (.setVisibleRange chart range-js opts-js))))



(defn on-range-change [f]
  (let [chart (chart-active)
        visible-range (.onVisibleRangeChanged chart)
        on-change (fn [r]
                    (f (extract-range r)))]
    (.subscribe visible-range nil on-change)))

(defn track-range []
  (let [f (fn [{:keys [_from _to] :as vis-range}]
            ;(.log js/console "visible range changed from: " from "to: " to)
            (swap! state assoc :range vis-range))]
    (swap! state assoc :range (get-range))
    (on-range-change f)))

(defn show-features []
  (let [features (.getAllFeatures @tv-widget-atom)]
    (.keys js/Object features)))

(defn reset-data []
  ;(println "reset-data!")
  (let [chart (chart-active)]
    (.resetData chart)
    nil))

(defn refresh-marks [_f]
  (let [chart (chart-active)]
    ;(println "refreshing marks..")
    (.refreshMarks chart)
    nil))

;;

(defn add-shape [points shape]
  (let [chart (chart-active)
        points-js (clj->js points)
        shape-js (clj->js shape)]
    ;(println "ADDING SHAPE: " points shape)
    (let [id (.createMultipointShape chart points-js shape-js)]
      ;(println "SHAPE ADDED: " id)
      (.log js/console shape-js)
      (.log js/console points-js)
      id)))

(defn get-shape-properties [id]
  ; widget.activeChart () .getShapeById ('YGC4tE') .getProperties ()
  (let [chart (chart-active)
        shape (.getShapeById chart id)
        props (.getProperties shape)]
    (.log js/console "SHAPE PROPS: " props)
    ;props
    nil))






(defonce tv-data (r/atom nil))

(defn on-save [data]
  ;(println "chart saved to: window.data")
  (.log js/console data)
  (reset! tv-data data)
  (set! (.-data js/globalThis) data))

(defn save-chart []
  ;(println "saving chart..")
  (.save @tv-widget-atom on-save)
  ;nil
  "started saving chart..")

(defn get-chart []
  ;data.pa [0] .charts
  (let [d @tv-data
        ;pa (.-pa d)
        ;d-clj (js->clj d)
        ;d-clj (jsx->clj d)
        ;charts (:charts d-clj)
        ;charts-clj (jsx->clj charts)
        ]
    ;(println "get-chart: ")
    (.log js/console d)
    ;(println d-clj)
    ;(println charts-clj)
    ;d-clj
    (.stringify js/JSON d)))

(defn add-context-menu [menu]
  (let [;chart (chart-active)
        menu-js (clj->js menu)
        add-context-menu (fn [unixtime _price]
                           ;(println "adding menu: " menu)
                           ;(println "args: " unixtime)
                           menu-js)]
    ;(println "adding menu: phase1: " menu)
    ;(println "menu-js: " menu-js)
    (.onContextMenu @tv-widget-atom add-context-menu)))


(defn add-series [location [k v]] ; "CLJ" [:trade "column"]
  ;(println "adding col:" k "to: " location "value: " v)
  (if (= v "column")
    (add-study (str location "COL") [k]) ; v = plot type. this is ignored.
    (add-study location [k]) ; v = plot type. this is ignored.
    ))

(defn add-plot [location plot]
  ;(println "adding plot " plot " to: " location)
  ;[{:trade "flags"}]
  (doall
   (map #(add-series location %) plot)))

(defn add-algo-studies [plots]
  ;(println "add-algo-studies: " plots)
  (remove-all-studies)
  (let [plot-main (first plots)
        plots (rest plots)]
    (when plot-main
      (add-plot "CLJMAIN" plot-main))
    (doall
     (map #(add-plot "CLJ" %) plots))))



; in scratchpad:
; (show-tradingview-widget "scratchpadtest" {:feed :ta})
;@tv-widget-atom
