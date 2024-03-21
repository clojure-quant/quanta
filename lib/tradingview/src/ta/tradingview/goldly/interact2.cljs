(ns ta.tradingview.goldly.interact2)

; to get all functions that the widget supports
;TradingView.widget.prototype

;; EVENTS

; window.tvWidget.activeChart().dataReady(() => {

(defn wrap-chart-ready [tv f]
  ; It's now safe to call any other methods of the widget
  (.onChartReady tv f))

; (wrap-chart-ready (fn [] (println "chart ready!")))

(defn wrap-header-ready [tv f]
  ; A promise that resolves if and when the header is ready to be used.
  ; headerReady() => Promise
  (.headerReady tv f))

;; DEBUG

(defn set-debug-mode [tv enabled?]
  (println "set-debug-mode: " enabled?)
  (.setDebugMode tv enabled?))

;; HELPER FNS

(defn chart-active [tv]
  (.activeChart tv))

(defn reset-data [tv]
  (let [chart (chart-active tv)]
    (println "reset-data! (of active chart)")
    (.resetData chart)
    nil))

;; SYMBOL

(defn- on-data-loaded [modus opts]
  ; used by set-symbol
  (println "tv data has been loaded (called after set-symbol)"))

(defn set-symbol
  ([tv symbol interval]
      ;(println "tv set symbol:" symbol "interval: " interval)
   (set-symbol tv symbol interval on-data-loaded))
  ([tv symbol interval on-load-finished]
   (println "tv set symbol:" symbol "interval: " interval)
   (.setSymbol tv symbol interval on-load-finished)
   nil))

;; STUDY / INDICAOR

(defn study-list [tv]
  (let [studies-js (.getStudiesList tv)]
    (.log js/console)
    (js->clj studies-js)))

(defn add-study [tv study-name study-args]
  ; JSServer.studyLibrary.push.apply(JSServer.studyLibrary,e)  
  (let [chart (chart-active tv)
        study-args-js (-> study-args vec clj->js)]
    (println "add study: " study-name " args:" study-args)
    (.createStudy chart study-name false false study-args-js)))

(defn remove-entity [tv entity-id]
  (let [chart (chart-active tv)]
    (println "remove entity-id: " entity-id)
    (.removeEntity chart entity-id)
    nil))

(defn remove-all-studies [tv]
  ;(println "remove-all-studies")
  (let [chart (chart-active tv)]
    (.removeAllStudies chart)
    nil))

; crosshair-moved

(defn- get-position [r]
  (let [price (.-price r)
        time (.-time r)]
    {:price price :time time}))

(defn on-crosshair-moved [tv f]
  (let [chart (chart-active tv)
        cross-hair (.crossHairMoved chart)
        wrapped-f (fn [r]
                    (let [r-clj (get-position r)]
                      (f r-clj)))]
    (.subscribe cross-hair nil wrapped-f)))

; RANGE

(defn- extract-range [r]
  (let [from (.-from r)
        to (.-to r)]
    {:from from :to to}))

(defn get-range [tv]
  ; getVisibleRange ()
  ; Returns the object {from, to} . from and to are Unix timestamps in the timezone of the chart.
  (let [chart (chart-active tv)]
    (-> (.getVisibleRange chart)
        (extract-range))))

; getVisiblePriceRange ()
; Returns the object {from, to} . from and to are boundaries of the price scale visible range in main series area.
; Date.UTC (2018, 0, 1) / 1000 

(defn set-range [tv range opts] ; Date.UTC (2012, 2, 3) / 1000,
  (println "set-visible-range " range opts)
  (let [chart (chart-active tv)
        range-js (clj->js range)
        opts (or opts {})
        opts-js (clj->js opts)]
    (-> (.setVisibleRange chart range-js opts-js))))

(defn on-range-change [tv f]
  (let [chart (chart-active tv)
        visible-range (.onVisibleRangeChanged chart)
        wrapped-f (fn [r]
                    (f (extract-range r)))]
    (.subscribe visible-range nil wrapped-f)))

(defn goto-date! [tv epoch]
  (let [{:keys [from to]} (get-range tv)
        diff (- to from)
        half (.round js/Math (/ diff 2))
        from (- epoch half)
        to (+ epoch half)]
    (println "goto-date! date: " epoch " from: " from " to: " to)
    (set-range tv
               {:from from :to to}
               {})))

;; features

(defn show-features [tv]
  (let [features (.getAllFeatures tv)]
    (.keys js/Object features)))

;; SHAPE

(defn add-shape [tv points shape]
  (println "ADDING SHAPE: " points shape)
  (let [chart (chart-active tv)
        points-js (clj->js points)
        shape-js (clj->js shape)
        id (.createMultipointShape chart points-js shape-js)]
    (.log js/console shape-js)
    (.log js/console points-js)
    id))

(defn get-shape-properties [tv id]
  ; widget.activeChart () .getShapeById ('YGC4tE') .getProperties ()
  (let [chart (chart-active tv)
        shape (.getShapeById chart id)
        props (.getProperties shape)]
    (.log js/console "SHAPE PROPS: " props)
    ;props
    nil))

;; MARKS

(defn refresh-marks [tv _f]
  (let [chart (chart-active tv)]
    ;(println "refreshing marks..")
    (.refreshMarks chart)
    nil))

;; SYMBOL

(defn get-symbol-and-interval [tv]
  (let [i (.symbolInterval tv)
        symbol (.-symbol i)
        interval (.-interval i)]
    ;(println "symbol: " symbol "interval: " interval)
    {:symbol symbol :interval interval}))

; .symbolExt ()

;; CHART

(defn save-chart [tv f]
  ;(println "saving chart..")
  (let [wrapped-f (fn [d]
                    (.stringify js/JSON d))]
    (.save tv wrapped-f)))

;; MENU

(defn add-header-button [tv text tooltip on-click-fn]
  (let [options (clj->js nil)
        ;var button = widget.createButton ();
        button (.createButton tv options)] ; 
    ;(println "button: " button)
    ; button.textContent = 'My custom button caption';
    (set! (.-textContent button) text)
    ; button.setAttribute('title', 'My custom button tooltip');
    (set! (.-title button) tooltip)
    ; button.addEventListener('click', function() { alert("My custom button pressed!"); });
    (.addEventListener button "click" on-click-fn)))

(defn add-context-menu [tv menu]
  (let [;chart (chart-active)
        menu-js (clj->js menu)
        add-context-menu (fn [unixtime _price]
                           ;(println "adding menu: " menu)
                           ;(println "args: " unixtime)
                           menu-js)]
    ;(println "adding menu: phase1: " menu)
    ;(println "menu-js: " menu-js)
    (.onContextMenu tv add-context-menu)))

;; LAYOUT

;; Widget does not support Layout. 
;; TradingTerminal does support it.

#_(defn set-layout [tv layout]
    (println "set-layout: " layout)
  ; layout A string representation of the new layout type. E.g. '2h' for two charts split vertically.
  ; ​"2h" | "2v" | "2-1" | "3s" | "3h" | "3v" | "4" | "6" | "8" | "1-2" | "3r" | "4h" | "4v" | "4s" | "5h" | "6h" | "7h" | "8h" | "1-3" | "2-2" | "2-3" | "1-4" | "5s" | "6c" | "8c" | "10c5" | "12c6" | "12c4" | "14c7" | "16c8" | "16c4"
    (.setLayout tv layout))
