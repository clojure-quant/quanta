(ns ta.notebook.clj
  (:require
   [taoensso.timbre :refer [debug info warnf error]]
   [ta.notebook.resource-mapper :refer [map-item default-mappings]]))

; form type predicates 

(defn hiccup? [original-form]
  ;(println "processing: " original-form)
  (and (seq? original-form)
       (keyword? (first original-form))))

(defn cljs-fn? [original-form]
  ;(println "processing: " original-form)
  (and (seq? original-form)
       (symbol? (first original-form))
       (not (resolve (first original-form)))))

; form converter

(defn map-last-arg [ctx args]
  (let [last-arg (last args)
        _ (info "last arg: " last-arg)
        last-arg-mapped (map-item ctx last-arg)
        _ (info "last arg mapped: " last-arg-mapped)
        but-last (vec (take (dec (count args)) args))]
    (conj but-last last-arg-mapped)))

(comment
  (def ctx-map-last {:resources (atom [])
                     :mapping-table default-mappings
                     :ns-nb "demo.map-last"})
  (map-last-arg ctx-map-last [1 2 3]) ; last arg unchanged
  (map-last-arg ctx-map-last [1 2 {:a 1}]) ; map => edn
  @(:resources ctx-map-last) ; has one item
  (map-last-arg ctx-map-last [1 2 {:a 1}]) ; map => edn
  @(:resources ctx-map-last) ; now it has two items
;  
  )
(defn convert-hiccup [{:keys [resources]} original-form]
  (info "converting hiccup")
  (vec original-form))

(defn eval-arg [arg]
  (info "evaling: " arg)
  (if (seq? arg)
    (eval arg)
    arg))

(defn convert-cljs-fn [{:keys [resources make-resources] :as ctx} original-form]
  (info "converting cljs form: " original-form)
  (let [[fun & args] original-form
        args-evaled (map eval args)
        args-mapped (if make-resources
                      (map-last-arg ctx args-evaled)
                      args-evaled)
        converted (-> (list (str fun))
                      (concat args-mapped)
                      vec)]
    (info "converted cljs form:" converted)
    converted))

; args-mapped (map-last-arg ctx args)

(defn process-form [ctx f]
  (cond
    (hiccup? f) (convert-hiccup ctx f)
    (cljs-fn? f) (convert-cljs-fn ctx f)
    :else f))

(comment
  (def ctx {:resources (atom [])
            :mapping-table default-mappings
            :ns-nb "demo.process-form"})
  (process-form ctx (list :p "hello world"))

  (require '[tick.alpha.api :as tick])
  (process-form ctx (list :p "report created: " (tick/now)))
  (process-form ctx (list :p "report created: " (str (tick/now))))
  (process-form ctx (list (symbol "text-clj") "report created: " (str (tick/now))))

  ; this creates a resource. ["1" :text]
  (process-form ctx (list (symbol "gamma-view") "hello world"))
  (p/loadr "demo.process-form" "1" :text)

  (def big-data {:a 3})
  (process-form ctx (list (symbol "gamma-view") big-data))
  (p/loadr "demo.process-form" "5" :edn)

  (def big-text "born to be alive")
  (process-form ctx (list (symbol "gamma-view") big-text))
  (process-form ctx (list (symbol "gamma-view") (str big-text ", and kicking!")))
  (p/loadr "demo.process-form" "2" :text)

  (require '[demo.playground.cljplot :refer [vega-clj]])
  (def data-clj {:A 28 :B 55 :C 43 :D 91 :E 81 :F 53 :G 19 :H 87 :I 52})
  (process-form ctx (list (symbol "gamma-view") (vega-clj data-clj)))

  (require '[tablecloth.api :as tc])
  (def ds1 (-> {:close [1 2 3 4 5]
                :open [1 2 3 4 5]}
               (tc/dataset)))

  (process-form ctx (list (symbol "line-plot") ds1))

;
  )

(defn walk-forms [ctx form]
  (info "walking ctx: " ctx " form: " form)
  (let [ctx (assoc ctx :resources (atom []))
        converted-forms (clojure.walk/prewalk
                         (partial process-form ctx)
                         form)]
    {:resources @(:resources ctx)
     :form converted-forms
     :ns-nb (:ns-nb ctx)}))

(defn walk-component [form]
  (-> (walk-forms {:make-resources false} form)
      :form))

(defmacro defn-component [name args form]
  `(defn ~name ~args
     (walk-component ~form)))

(comment

  (defmacro def-2 [name val]
    `(def ~name
       ~val))

  (defmacro defn-2 [name args f]
    `(defn ~name ~args
       ~f))

  (def-2 bongo 3)
  bongo

  (defn-2 hi [x] (println x))

  (hi "asdf")

  (macroexpand-1
   (defn-component willy [x]
     (println "willy:" x)))

  (willy 3)

;  
  )

;(macroexpand
;  (defn-component ds-table-text
;    (text "df")
;    )
; )

(defmacro create-view [ctx form]
  (walk-forms (eval ctx) form))

(comment

  (def ctx-view  {:mapping-table default-mappings
                  :ns-nb "test.create-view-simple"
                  :make-resources true})
  (create-view ctx-view (:h1 (str (tick/now))))
  (macroexpand (create-view ctx-view (:h1 (str (tick/now)))))

  (create-view ctx-view (text-x "long text"))
  (create-view ctx-view (text-x (str (tick/now))))
  (create-view (assoc ctx-view :make-resources false) (text-x (str (tick/now))))

  (require '[tick.alpha.api :as tick])
  (require '[tablecloth.api :as tc])
  (require '[ta.helper.ds :refer [ds->str]])
  (tick/now)
  (def ds1 (-> {:close [1 2 3 4 5]
                :open [1 2 3 4 5]}
               (tc/dataset)))
  (ds->str ds1)
  ; and: form of type seq
  ;      first type symbol
  ;      first type symbol unresolved  
  ; =>  exchange last  value in seq with (pack value)
  ;     quote entire expression.

  (def ctx-study  {:mapping-table default-mappings
                   :ns-nb "test.create-view-study"})

  (create-view
   ctx-study
   (:div
    (text (ds->str ds1))
    (list-plot {:cols [:close]} ds1)))

  (-> (create-view
       ctx-study
       (:div
        (:h1 "hello world")
        (:p "created:" (tick/now))
        (:p.text-blue-300 "now the plots")
        (list-plot {:cols [:close]} ds1)
        (text (ds->str ds1))
        [:p/vega {:spec {:a 1}} [1 2 3]]))
      :form
      ;:resources
      )

  (p/loadr "demo.study1" "1" :text)

  (require '[demo.playground.cljplot :refer [vega-clj]])
  (def data-clj {:A 28 :B 55 :C 43 :D 91 :E 81 :F 53 :G 19 :H 87 :I 52})
  (create-view
   ctx-study
   (img-plot (vega-clj data-clj)))

  (macroexpand

   (create-component
    (img-plot "asdf" data-clj)))

  (defn xx-plot [data]
    (create-component
     (img-plot (vega-clj data))))

  (create-view
   ctx-study
   (xx-plot data-clj))

  (def expected-result
    {:nb-ns "demo.study1"
     :plot-1 1
     :resources {"1" :edn
                 "2" :text}
     :form '(:div
             (:h1 "hello world")
             (:p.text-blue-300 "now the plots")
             (list-plot {:cols [:close]} ["1" :edn])
             (text (ds->str ["2" :text])))})

; clerk/table
  (defn table [data]
    (create-view *ns* (next-id)
                 (html-table data)))

  (eval (quote (+ 3 3)))

;  
  )